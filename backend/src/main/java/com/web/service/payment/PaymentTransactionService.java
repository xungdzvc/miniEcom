/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.payment;

import com.google.api.client.util.SecurityUtils;
import com.web.dto.request.payment.CardCallBackRequest;
import com.web.dto.request.payment.CardRequest;
import com.web.dto.request.payment.WebhookRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.payment.TopupResponse;
import com.web.dto.response.user.UserTopupResponse;
import com.web.entity.*;
import com.web.enums.MatchType;
import com.web.enums.OrderStatus;
import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import com.web.enums.PaymentType;
import com.web.exception.MyException;
import com.web.repository.*;
import com.web.security.SecurityUtil;
import com.web.service.IMailService;
import com.web.service.IPaymentTransactionService;
import com.web.util.MailTemplates;
import com.web.util.Utils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ZZ
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentTransactionService implements IPaymentTransactionService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SystemBankAccountRepository systemBankAccountRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TopupIntentRepository topupIntentRepository;
    private final CartRepository cartRepository;
    private final IMailService mailService;
    private static final int CURRENT_YEAR = java.time.Year.now().getValue();
    private static final Pattern ORDER_PATTERN = Pattern.compile(
            "\\bHD" + CURRENT_YEAR + "(\\d{1,12})\\b",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern TOPUP_PATTERN = Pattern.compile(
            "\\bNAP(\\d{1,12})\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Value("${partnerId}")
    private String partnerId;

    @Value("${partnerKey}")
    private String partnerKey;

    @Value("${urlApiCharging}")
    private String urlApiCharging;
    
    @Value("${baseUrl.web}")
    private String baseUrl;
    @Override
    public void processTransaction(WebhookRequest webhookRequest) {
        PaymentTransactionEntity paymentTransactionEntity = paymentTransactionRepository.findByPaymentRef(webhookRequest.getReferenceCode());

        if (paymentTransactionEntity == null) {
            paymentTransactionEntity = new PaymentTransactionEntity();
            paymentTransactionEntity.setAmount(webhookRequest.getTransferAmount());
            paymentTransactionEntity.setBankAccount(webhookRequest.getAccountNumber());
            paymentTransactionEntity.setTransactionContent(webhookRequest.getContent());
            paymentTransactionEntity.setPaymentName(webhookRequest.getGateway());
            paymentTransactionEntity.setPaymentType(PaymentType.BANK);
            paymentTransactionEntity.setPaymentRef(webhookRequest.getReferenceCode());
            paymentTransactionEntity.setCreatedAt(LocalDateTime.now());

            Long orderId = Utils.getInstance().extractId(ORDER_PATTERN, paymentTransactionEntity.getTransactionContent());

            if (orderId != null) {
                handleOrderPayment(paymentTransactionEntity, orderId);
                return;
            }

            Long topupId = Utils.getInstance().extractId(TOPUP_PATTERN, paymentTransactionEntity.getTransactionContent());
            if (topupId != null) {
                handleTopUpPayment(paymentTransactionEntity, topupId);
                return;
            }
        } else {
            throw new MyException("Giao dịch đã tồn tại");
        }
    }

    @Override
    public void handleOrderPayment(PaymentTransactionEntity paymentTransactionEntity, Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new MyException("Đơn hàng không hợp lệ"));

        if (paymentTransactionEntity.getAmount() < orderEntity.getTotal()) {
            paymentTransactionEntity.setPaymentStatus(PaymentStatus.WRONG_AMOUNT);
            paymentTransactionRepository.save(paymentTransactionEntity);
            return;

        }
        CartEntity cartEntity = cartRepository.findByUserId(orderEntity.getUser().getId());
        cartEntity.getCartItems().clear();
        UserEntity user = cartEntity.getUser();
        paymentTransactionEntity.setOrderId(orderId);
        paymentTransactionEntity.setMatchType(MatchType.ORDER);
        paymentTransactionEntity.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentTransactionEntity.setMatchRef("HD" + CURRENT_YEAR + orderId);
        paymentTransactionEntity.setOrderId(orderId);
        orderEntity.setPaymentMethod(PaymentMethod.ORDER_BANKING);
        orderEntity.setStatus(OrderStatus.SUCCESS);
        String orderUrl = baseUrl + "/order/" + orderEntity.getId() + "/detail"; // đổi theo route của bạn
        mailService.sendHtml(
                user.getEmail(),
                "Thanh toán thành công đơn #" + orderEntity.getId(),
                MailTemplates.paymentSuccess(user, orderEntity, orderUrl)
        );
        cartRepository.save(cartEntity);
        orderRepository.save(orderEntity);
        paymentTransactionRepository.save(paymentTransactionEntity);

    }

    @Override
    public void handleTopUpPayment(PaymentTransactionEntity paymentTransactionEntity, Long topupId) {

        TopupIntentEntity topupIntent = topupIntentRepository.findByIdAndStatus(topupId, PaymentStatus.PENDING);
        if (topupIntent == null) {
            paymentTransactionEntity.setPaymentStatus(PaymentStatus.UNMATCH);
        }
        UserEntity userEntity = userRepository.findUserById(topupIntent.getUserId());
        if (userEntity == null) {
            throw new MyException("Người dùng không hợp lệ");
        }
        userEntity.addBlance(topupIntent.getAmount());
        paymentTransactionEntity.setMatchType(MatchType.TOPUP);
        paymentTransactionEntity.setUserId(userEntity.getId());
        paymentTransactionEntity.setMatchRef("NAP" + topupId);
        paymentTransactionEntity.setPaymentStatus(PaymentStatus.SUCCESS);
        topupIntent.setStatus(PaymentStatus.SUCCESS);
        topupIntentRepository.save(topupIntent);
        userRepository.save(userEntity);
        paymentTransactionRepository.save(paymentTransactionEntity);

    }

    @Override
    public void handleTopUpWallet(PaymentTransactionEntity paymentTransactionEntity, Long userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            throw new MyException("Nguười dùng không hợp lệ");
        }
        paymentTransactionEntity.setMatchType(MatchType.TOPUP);
        paymentTransactionEntity.setUserId(userId);
        paymentTransactionEntity.setPaymentStatus(PaymentStatus.PENDING);
        paymentTransactionEntity.setMatchRef("NAP" + userId);
        paymentTransactionEntity.setPaymentType(PaymentType.CARD);
        paymentTransactionEntity.setPaymentName("");
        paymentTransactionRepository.save(paymentTransactionEntity);

    }

    @Override
    public ApiResponse<?> sendCard(CardRequest cardRequest) {
        Long userId = SecurityUtil.getUserId();
        String sign = Utils.MD5Hash(partnerKey + cardRequest.getMaThe() + cardRequest.getSeri());
        String requestId = String.valueOf(new Random().nextInt(111111, 999999));
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("request_id", requestId)
                    .addFormDataPart("code", cardRequest.getMaThe())
                    .addFormDataPart("serial", cardRequest.getSeri())
                    .addFormDataPart("telco", cardRequest.getLoaiThe())
                    .addFormDataPart("amount", cardRequest.getMenhGia().toString())
                    .addFormDataPart("command", "charging")
                    .addFormDataPart("partner_id", partnerId)
                    .addFormDataPart("sign", sign)
                    .build();
            Request request = new Request.Builder().url(urlApiCharging).post(body).addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();
            Object objResponse = JSONValue.parse(response.body().string());
            JSONObject jsonObject = (JSONObject) objResponse;
            int status = Integer.parseInt(jsonObject.get("status").toString());
            long amount = cardRequest.getMenhGia();
            String code = jsonObject.get("code").toString();
            String seri = jsonObject.get("serial").toString();
            String telco = jsonObject.get("telco").toString();
            String message;
            PaymentStatus paymentStatus = null;
            switch (status) {
                case 99:
                    message = "Gửi thẻ thành công chờ xử lí";
                    paymentStatus = paymentStatus.PENDING;
                    break;
                case 1:
                    message = "Nạp tiền thành công";
                    paymentStatus = paymentStatus.SUCCESS;
                    break;
                case 2:
                    message = "Thẻ nạp sai mệnh giá. Bạn sẽ bị trừ 50% giá trị thực";
                    paymentStatus = paymentStatus.WRONG_AMOUNT;
                    break;
                case 3:
                    message = "Thẻ cào lỗi vui lòng kiểm tra lại seri hoặc mã thẻ";
                    paymentStatus = paymentStatus.FAILED;
                    break;
                case 4:
                    message = "Hệ thống nạp thẻ bảo trì xin vui lòng thử lại sau";
                    paymentStatus = paymentStatus.FAILED;
                    break;
                case 5:
                    message = "Gửi thẻ thất bại ";
                    paymentStatus = paymentStatus.FAILED;
                    break;
                default:
                    message = "Hệ thống xảy ra lỗi xin vui lòng thử lại sau";
                    paymentStatus = paymentStatus.FAILED;
                    break;
            }
            PaymentTransactionEntity payment = new PaymentTransactionEntity();
            payment.setCardCode(code);
            payment.setCardSerial(seri);
            payment.setCardType(telco);
            payment.setAmount(amount);
            payment.setPaymentStatus(paymentStatus);
            payment.setPaymentRef(requestId);
            handleTopUpWallet(payment, userId);
            ApiResponse.success(null, message);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        return ApiResponse.error("Có lỗi xảy ra");
    }

    @Override
    public List<UserTopupResponse> getTopup(Long userId) {
        List<PaymentTransactionEntity> paymentTransactionEntities = paymentTransactionRepository.findAllByUserId(userId);
        List<UserTopupResponse> lUserTopupResponse = new ArrayList<>();
        for (PaymentTransactionEntity paymentEntity : paymentTransactionEntities) {
            UserTopupResponse userTopupResponse = new UserTopupResponse();
            userTopupResponse.setAmount(paymentEntity.getAmount());
            userTopupResponse.setId(paymentEntity.getId());
            userTopupResponse.setPaymentType(paymentEntity.getPaymentType());
            userTopupResponse.setPaymentStatus(paymentEntity.getPaymentStatus());
            userTopupResponse.setCardCode(paymentEntity.getCardCode());
            userTopupResponse.setCardSerial(paymentEntity.getCardSerial());
            userTopupResponse.setCardType(paymentEntity.getCardType());
            userTopupResponse.setCreatedAt(paymentEntity.getCreatedAt());
            lUserTopupResponse.add(userTopupResponse);
        }
        return lUserTopupResponse;

    }

    @Override
    public ApiResponse callBack(CardCallBackRequest cardCallBackRequest) {
        PaymentTransactionEntity payment = paymentTransactionRepository
                .findByPaymentStatusAndCardCodeAndCardSerial(
                        PaymentStatus.PENDING,
                        cardCallBackRequest.getCode(),
                        cardCallBackRequest.getSerial());
        if (payment == null) {
            return ApiResponse.error("Không tồn tại giao dịch này");
        }
        Long userId = Utils.getInstance().extractId(TOPUP_PATTERN, payment.getMatchRef());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            throw new MyException("Nguười dùng không hợp lệ");
        }
        userEntity.addBlance(cardCallBackRequest.getAmount());
        userRepository.save(userEntity);
        paymentTransactionRepository.save(payment);
        return ApiResponse.error("Cộng tiền thành công");
    }

    @Override
    public TopupResponse requestTopUp(Long amount) {
        Long userId = SecurityUtil.getUserId();
        LocalDateTime now = LocalDateTime.now();

        TopupIntentEntity topup = new TopupIntentEntity();
        topup.setUserId(userId);
        topup.setCreatedAt(now);
        topup.setExpiredAt(now.plusMinutes(15));

        topup.setStatus(PaymentStatus.PENDING);
        topup.setAmount(amount);

        topupIntentRepository.save(topup);
        SystemBankAccountEntity bank = systemBankAccountRepository.findFristByIsDefaultTrue();
        if (bank == null) {
            throw new MyException("Tài khoản ngân hàng chưa được cấu hình vui lòng liên hệ ADMIN ");
        }
        String matchRef = "NAP" + topup.getId();
        TopupResponse response = new TopupResponse();
        response.setTopupId(topup.getId());
        response.setAmount(topup.getAmount());
        response.setQRCodeUrl(Utils.getInstance().
                buildVietQrQuickLink(
                        bank.getBankCode(),
                        bank.getAccountNumber(),
                        "qr_only", amount, matchRef, bank.getAccountName()));
        response.setExpiresAt(topup.getExpiredAt());
        return response;
    }

    @Override
    public PaymentStatus getStatusByTopupId(Long topupId) {
        TopupIntentEntity topupIntentEntity = topupIntentRepository.findById(topupId).get();
        return topupIntentEntity.getStatus();
    }

}
