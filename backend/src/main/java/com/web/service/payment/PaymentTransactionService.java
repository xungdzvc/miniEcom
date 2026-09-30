package com.web.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.request.payment.*;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.payment.OrderPaymentResponse;
import com.web.dto.response.payment.TopupPaymentResponse;
import com.web.dto.response.payment.TopupResponse;
import com.web.entity.*;
import com.web.enums.*;
import com.web.exception.MyException;
import com.web.repository.*;
import com.web.security.SecurityUtil;
import com.web.service.IMailService;
import com.web.service.IPaymentTransactionService;
import com.web.service.IProductService;
import com.web.service.IUserService;
import com.web.util.MailTemplates;
import com.web.util.Utils;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PaymentTransactionService implements IPaymentTransactionService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final IUserService userService;
    private final SystemBankAccountRepository systemBankAccountRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final TopupIntentRepository topupIntentRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final TopupPaymentRepository topupPaymentRepository;
    private final IProductService productService;
    private final IMailService mailService;
    private final Clock clock;
    private static final int CURRENT_YEAR = java.time.Year.now().getValue();
    private static final Pattern ORDER_PATTERN = Pattern.compile(
            "\\bHD" + CURRENT_YEAR + "(\\d{1,12})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOPUP_PATTERN = Pattern.compile(
            "\\bNAP(\\d{1,12})\\b", Pattern.CASE_INSENSITIVE);

    @Value("${partnerId}")
    private String partnerId;

    @Value("${partnerKey}")
    private String partnerKey;

    @Value("${urlApiCharging}")
    private String urlApiCharging;

    @Value("${baseUrl.web}")
    private String baseUrl;

    @Value("${app.payment.topup-ttl}")
    private Duration topupTtl;


    @Override
    public void processTransaction(WebhookRequest webhookRequest) {
        if (transactionRepository.existsByPaymentRef(webhookRequest.getReferenceCode())) {
            throw new MyException("Giao dịch đã tồn tại");
        }

        PaymentTransactionEntity transaction = buildTransactionFromWebhook(webhookRequest);
        transactionRepository.save(transaction);

        Long orderId = extractOrderId(transaction.getTransactionContent());
        if (orderId != null) {
            handleBankOrderPayment(webhookRequest, orderId);
            return;
        }

        Long topupId = extractTopupId(transaction.getTransactionContent());
        if (topupId != null) {
            handleBankTopupPayment(webhookRequest, topupId);
            return;
        }

        transaction.setPaymentStatus(PaymentStatus.UNMATCH);
        transactionRepository.save(transaction);
    }
    @Transactional
    @Override
    public OrderPaymentResponse handleBankOrderPayment(WebhookRequest webhookRequest, Long orderId) {
        OrderEntity order = orderRepository.findByIdAndStatus(orderId,OrderStatus.PENDING)
                .orElseThrow(() -> new MyException("Đơn hàng không tồn tại"));


        PaymentTransactionEntity transaction = transactionRepository
                .findByPaymentRef(webhookRequest.getReferenceCode());

        if (transaction == null) {
            transaction = buildTransactionFromWebhook(webhookRequest);
            transactionRepository.save(transaction);
        }

        if (transaction.getAmount().compareTo(order.getTotal()) == -1) {
            transaction.setPaymentStatus(PaymentStatus.WRONG_AMOUNT);
            transactionRepository.save(transaction);
            throw new MyException("Số tiền chuyển khoản không đủ");
        }

        OrderPaymentEntity orderPayment = new OrderPaymentEntity();
        orderPayment.setOrder(order);
        orderPayment.setTransaction(transaction);
        orderPayment.setTransferContent(webhookRequest.getContent());
        orderPayment.setAmount(transaction.getAmount());
        orderPayment.setPaymentMethod(PaymentMethod.ORDER_BANKING);
        orderPayment.setStatus(PaymentStatus.SUCCESS);
        orderPaymentRepository.save(orderPayment);

        transaction.setMatchType(MatchType.ORDER);
        transaction.setMatchRef("HD" + CURRENT_YEAR + orderId);
        transaction.setPaymentStatus(PaymentStatus.SUCCESS);
        transactionRepository.save(transaction);

        for (OrderItemEntity item : order.getOrderItems()) {
            productService.incrementSalesCount(item.getProduct(), item.getQuantity());
        }
        order.setStatus(OrderStatus.SUCCESS);
        order.setPaymentMethod(PaymentMethod.ORDER_BANKING);
        orderRepository.save(order);

        UserEntity user = order.getUser();
        String orderUrl = baseUrl + "/order/" + orderId;
        mailService.sendHtml(
                user.getEmail(),
                "Thanh toán thành công đơn #" + orderId,
                MailTemplates.paymentSuccess(user, order, orderUrl));

        return toOrderPaymentResponse(orderPayment);
    }
    @Transactional
    @Override
    public TopupPaymentResponse handleBankTopupPayment(WebhookRequest webhookRequest, Long topupId) {
        TopupIntentEntity topupIntent = topupIntentRepository
                .findByIdAndStatusAndNotExpiredAt(topupId, PaymentStatus.PENDING,clock.instant()).orElseThrow(()-> new MyException("Topup nạp tiền không hợp lệ hoặc đã hết hạn"));


        PaymentTransactionEntity transaction = transactionRepository
                .findByPaymentRef(webhookRequest.getReferenceCode());
        if (transaction != null) {
            transaction.setPaymentStatus(PaymentStatus.UNMATCH);
            transactionRepository.save(transaction);
        }else{
            transaction = buildTransactionFromWebhook(webhookRequest);
            transactionRepository.save(transaction);
        }
        if(!webhookRequest.getTransferAmount().equals(topupIntent.getAmount())){
            throw new MyException("Số tiền không hợp lệ ");
        }

        UserEntity user = userRepository.findById(topupIntent.getUser().getId()).orElseThrow(() -> new MyException("Người dùng không hợp lệ"));



        TopupPaymentEntity topupPayment = new TopupPaymentEntity();
        topupPayment.setUser(user);
        topupPayment.setTransaction(transaction);
        topupPayment.setAmount(topupIntent.getAmount());
        topupPayment.setPaymentMethod(PaymentMethod.TOPUP);
        topupPayment.setStatus(PaymentStatus.SUCCESS);
        topupPaymentRepository.save(topupPayment);

        transaction.setMatchType(MatchType.TOPUP);
        transaction.setMatchRef("NAP" + topupId);
        transaction.setPaymentStatus(PaymentStatus.SUCCESS);
        transactionRepository.save(transaction);

        userService.deposit(user.getId(), topupIntent.getAmount());

        topupIntent.setStatus(PaymentStatus.SUCCESS);
        topupIntentRepository.save(topupIntent);

        return toTopupPaymentResponse(topupPayment, transaction);
    }

    @Override
    public TopupResponse requestTopUp(TopupRequest request) {
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không hợp lệ"));;


        Instant now = clock.instant();

        TopupIntentEntity intent = new TopupIntentEntity();
        intent.setUser(user);
        intent.setExpiredAt(now.plus(topupTtl));
        intent.setStatus(PaymentStatus.PENDING);
        intent.setAmount(request.getAmount());
        topupIntentRepository.save(intent);

        SystemBankAccountEntity bank = systemBankAccountRepository.findFristByIsDefaultTrue();
        if (bank == null) {
            throw new MyException("Tài khoản ngân hàng chưa được cấu hình, vui lòng liên hệ ADMIN");
        }

        String matchRef = "NAP" + intent.getId();
        TopupResponse response = new TopupResponse();
        response.setTopupId(intent.getId());
        response.setAmount(intent.getAmount());
        response.setStatus(intent.getStatus());
        response.setExpiresAt(intent.getExpiredAt());
        response.setQRCodeUrl(Utils.getInstance().buildVietQrQuickLink(
                bank.getBankCode(),
                bank.getAccountNumber(),
                "qr_only",
                request.getAmount(),
                matchRef,
                bank.getAccountName()));
        return response;
    }

    @Override
    public PaymentStatus getTopupStatus(Long topupId) {
        TopupIntentEntity intent = topupIntentRepository.findByIdAndUserId(topupId,SecurityUtil.getUserId())
                .orElseThrow(() -> new MyException("Yêu cầu nạp tiền không tồn tại"));
        return intent.getStatus();
    }

    @Override
    public ApiResponse<?> sendCard(CardRequest request) {
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không hợp lệ"));;

        String sign = Utils.MD5Hash(partnerKey + request.getMaThe() + request.getSeri());
        String requestId = String.valueOf(new Random().nextInt(111111, 999999));

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("request_id", requestId)
                    .addFormDataPart("code", request.getMaThe())
                    .addFormDataPart("serial", request.getSeri())
                    .addFormDataPart("telco", request.getLoaiThe())
                    .addFormDataPart("amount", request.getMenhGia().toString())
                    .addFormDataPart("command", "charging")
                    .addFormDataPart("partner_id", partnerId)
                    .addFormDataPart("sign", sign)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(urlApiCharging)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                ObjectMapper mapper = new ObjectMapper();
                String responseString = response.body().string();

                JsonNode jsonNode = mapper.readTree(responseString);

                int status = jsonNode.get("status").asInt();
                BigDecimal amount = jsonNode.get("amount").decimalValue();
                String returnedCode = jsonNode.get("code").asText();
                String returnedSerial = jsonNode.get("serial").asText();
                String telco = jsonNode.get("telco").asText();

                PaymentStatus paymentStatus;
                String message;

                paymentStatus = switch (status) {
                    case 99 -> {
                        message = "Gửi thẻ thành công, chờ xử lí";
                        yield PaymentStatus.PENDING;
                    }
                    case 1 -> {
                        message = "Nạp tiền thành công";
                        yield PaymentStatus.SUCCESS;
                    }
                    case 2 -> {
                        message = "Thẻ nạp sai mệnh giá. Bạn sẽ bị trừ 50% giá trị thực";
                        yield PaymentStatus.WRONG_AMOUNT;
                    }
                    case 3 -> {
                        message = "Thẻ cào lỗi, vui lòng kiểm tra lại seri hoặc mã thẻ";
                        yield PaymentStatus.FAILED;
                    }
                    case 4 -> {
                        message = "Hệ thống nạp thẻ bảo trì, xin vui lòng thử lại sau";
                        yield PaymentStatus.FAILED;
                    }
                    case 5 -> {
                        message = "Gửi thẻ thất bại";
                        yield PaymentStatus.FAILED;
                    }
                    default -> {
                        message = "Hệ thống xảy ra lỗi, xin vui lòng thử lại sau";
                        yield PaymentStatus.FAILED;
                    }
                };

                PaymentTransactionEntity transaction = new PaymentTransactionEntity();
                transaction.setPaymentType(PaymentType.CARD);
                transaction.setPaymentRef(requestId);
                transaction.setCardCode(returnedCode);
                transaction.setCardSerial(returnedSerial);
                transaction.setCardType(telco);
                transaction.setAmount(amount);
                transaction.setPaymentStatus(paymentStatus);
                transactionRepository.save(transaction);

                TopupPaymentEntity topupPayment = new TopupPaymentEntity();
                topupPayment.setUser(user);
                topupPayment.setTransaction(transaction);
                topupPayment.setAmount(amount);
                topupPayment.setPaymentMethod(PaymentMethod.TOPUP);
                topupPayment.setCardType(telco);
                topupPayment.setCardCode(returnedCode);
                topupPayment.setCardSerial(returnedSerial);
                topupPayment.setStatus(paymentStatus);
                topupPaymentRepository.save(topupPayment);

                if (paymentStatus.equals(PaymentStatus.SUCCESS) || paymentStatus.equals(PaymentStatus.WRONG_AMOUNT)) {
                    user.deposit(amount);
                }

                return ApiResponse.success(new CardSubmissResponse(transaction.getId(),transaction.getPaymentStatus(),
                        transaction.getAmount(),message), message);
            }

        } catch (Exception e) {
            throw new MyException("Lỗi khi nạp thẻ: " + e.getMessage());
        }
    }
    @Transactional
    @Override
    public ApiResponse<?> handleCardCallback(CardCallBackRequest request) {
        PaymentStatus paymentStatus;
        String message;

        paymentStatus = switch (request.getStatus()) {
            case 1 -> {
                message = "Nạp tiền thành công";
                yield PaymentStatus.SUCCESS;
            }
            case 2 -> {
                message = "Thẻ nạp sai mệnh giá. Bạn sẽ bị trừ 50% giá trị thực";
                yield PaymentStatus.WRONG_AMOUNT;
            }
            case 3 -> {
                message = "Thẻ cào lỗi, vui lòng kiểm tra lại seri hoặc mã thẻ";
                yield PaymentStatus.FAILED;
            }
            case 4 -> {
                message = "Hệ thống nạp thẻ bảo trì, xin vui lòng thử lại sau";
                yield PaymentStatus.FAILED;
            }
            case 5 -> {
                message = "Gửi thẻ thất bại";
                yield PaymentStatus.FAILED;
            }
            default -> {
                message = "Hệ thống xảy ra lỗi, xin vui lòng thử lại sau";
                yield PaymentStatus.FAILED;
            }
        };

        TopupPaymentEntity topupPayment = topupPaymentRepository
                .findByCardCodeAndCardSerialAndStatus(
                        request.getCode(), request.getSerial(), PaymentStatus.PENDING)
                .orElse(null);

        if (topupPayment == null) {
            return ApiResponse.error("Không tồn tại giao dịch hoặc đã được xử lý");
        }

        Long userId = topupPayment.getUser().getId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new MyException("Người dùng không hợp lệ"));;
        if(paymentStatus.equals(PaymentStatus.SUCCESS) || paymentStatus.equals(PaymentStatus.WRONG_AMOUNT)){
            userService.deposit(userId, request.getAmount());
        }


        topupPayment.setStatus(paymentStatus);
        topupPaymentRepository.save(topupPayment);

        PaymentTransactionEntity transaction = topupPayment.getTransaction();
        transaction.setPaymentStatus(paymentStatus);
        transactionRepository.save(transaction);

        return ApiResponse.success(new CardSubmissResponse(request.getTrans_id(),paymentStatus,
                request.getAmount(),message), "Cộng tiền thành công");
    }

    @Override
    public List<TopupPaymentResponse> getUserTopupHistory(Long userId) {
        return topupPaymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(tp -> toTopupPaymentResponse(tp, tp.getTransaction()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderPaymentResponse> getOrderPayments(Long orderId) {
        return orderPaymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::toOrderPaymentResponse)
                .collect(Collectors.toList());
    }

    private PaymentTransactionEntity buildTransactionFromWebhook(WebhookRequest webhook) {
        PaymentTransactionEntity transaction = new PaymentTransactionEntity();
        transaction.setPaymentType(PaymentType.BANK);
        transaction.setPaymentRef(webhook.getReferenceCode());
        transaction.setAmount(webhook.getTransferAmount());
        transaction.setBankAccount(webhook.getAccountNumber());
        transaction.setTransactionContent(webhook.getContent());
        transaction.setPaymentName(webhook.getGateway());
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        return transaction;
    }

    private Long extractOrderId(String content) {
        if (content == null) {
            return null;
        }
        return Utils.getInstance().extractId(ORDER_PATTERN, content);
    }

    private Long extractTopupId(String content) {
        if (content == null) {
            return null;
        }
        return Utils.getInstance().extractId(TOPUP_PATTERN, content);
    }

    private OrderPaymentResponse toOrderPaymentResponse(OrderPaymentEntity entity) {
        OrderPaymentResponse response = new OrderPaymentResponse();
        response.setId(entity.getId());
        response.setOrderId(entity.getOrder().getId());
        response.setTransactionId(entity.getTransaction().getId());
        response.setTransferContent(entity.getTransferContent());
        response.setAmount(entity.getAmount());
        response.setPaymentMethod(entity.getPaymentMethod());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    private TopupPaymentResponse toTopupPaymentResponse(TopupPaymentEntity entity,
            PaymentTransactionEntity transaction) {
        TopupPaymentResponse response = new TopupPaymentResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setTransactionId(entity.getTransaction().getId());
        response.setAmount(entity.getAmount());
        response.setPaymentMethod(entity.getPaymentMethod());
        response.setCardType(entity.getCardType());
        response.setCardCode(entity.getCardCode());
        response.setCardSerial(entity.getCardSerial());
        response.setStatus(entity.getStatus());
        response.setPaymentType(transaction.getPaymentType());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
