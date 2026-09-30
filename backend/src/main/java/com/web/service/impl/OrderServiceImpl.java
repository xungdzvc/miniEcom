package com.web.service.impl;

import com.web.dto.CartDTO;
import com.web.dto.CartItemDTO;
import com.web.dto.request.order.DirectCheckoutRequest;
import com.web.dto.response.order.OrderDetailResponse;
import com.web.dto.request.order.OrderCheckoutRequest;
import com.web.dto.response.order.OrderCheckoutResponse;
import com.web.dto.response.order.OrderListResponse;
import com.web.entity.*;
import com.web.enums.OrderStatus;
import com.web.enums.PaymentMethod;
import com.web.exception.MyException;
import com.web.mapper.CartMapper;
import com.web.mapper.OrderMapper;
import com.web.repository.*;
import com.web.security.SecurityUtil;
import com.web.service.ICouponService;
import com.web.service.IMailService;
import com.web.service.IOrderService;
import com.web.service.IProductService;
import com.web.util.MailTemplates;
import com.web.util.Utils;
import java.math.BigDecimal;

import java.time.*;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderItemRepository orderItemRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ICouponService couponService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final SystemBankAccountRepository systemBankAccountRepository;
    private final IMailService mailService;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final IProductService productService;
    private final Clock clock;
    @Value("${baseUrl.web}")
    private String baseUrl;

    @Value("${app.order.pending-ttl}")
    private Duration pendingTtl;



    @Retryable(value = OptimisticLockingFailureException.class, maxAttemptsExpression = "${app.retry.optimistic-lock.max-attempts:3}")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderCheckoutResponse checkoutByBankOrWallet(OrderCheckoutRequest orderCheckoutRequest) {
        if (orderCheckoutRequest.getPaymentMethod() == null) {
            throw new MyException("Chưa chọn phương thức thanh toán");
        }
        CartEntity cartEntity = cartRepository.findById(orderCheckoutRequest.getCartId())
                .orElseThrow(() -> new MyException("Giỏ hàng không tồn tại"));

        Long userId = SecurityUtil.getUserId();
        if (!Objects.equals(cartEntity.getUser().getId(), userId)) {
            throw new MyException("Bạn không thể thực hiện thao tác này");
        }
        if (cartEntity.getCartItems() == null || cartEntity.getCartItems().isEmpty()) {
            throw new MyException("Giỏ hàng trống");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new MyException("Nguười dùng không hợp lệ"));
        SystemBankAccountEntity bank = systemBankAccountRepository.findFristByIsDefaultTrue();
        if (bank == null) {
            throw new MyException("Tài khoản ngân hàng chưa được cấu hình vui lòng liên hệ ADMIN");
        }
        OrderEntity orderEntity = new OrderEntity();
        CartDTO cartDTO = cartMapper.toCartDTO(cartEntity);
        Instant now = clock.instant();

        orderEntity.setOrderDate(now);
        orderEntity.setExpiresAt(now.plus(pendingTtl));
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemDTO item : cartDTO.getItems()) {

            totalPrice = totalPrice.add(Utils.calsubPercent(item.getPrice(), item.getDiscount()).multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        int couponPercent = couponService.getCouponDiscount(orderCheckoutRequest.getCouponCode());
        couponPercent = Math.max(0, Math.min(100, couponPercent));
        BigDecimal finalPrice = Utils.calsubPercent(totalPrice, couponPercent);

        orderEntity.setOrderDate(now);
        orderEntity.setTotal(finalPrice);
        orderEntity.setUser(user);
        orderEntity.setPaymentMethod(orderCheckoutRequest.getPaymentMethod());
        for (CartItemDTO item : cartDTO.getItems()) {
            ProductEntity productEntity = productRepository.findByIdAndStatus(item.getProductId(), true);
            if (productEntity == null) {
                throw new MyException("Có sản phẩm không tồn tại hoặc đã ngừng kinh doanh");
            }

            if(productDetailRepository.reserveStock(productEntity.getProductDetail().getId() ,item.getQuantity()) == 0){
                 throw new MyException(productEntity.getName() + " Chỉ còn " + productEntity.getProductDetail().getQuantity() + " trong kho hãy giảm số lượng xuống hoặc chọn mặt hàng khác thay thế");
            }
//            if (productEntity.getProductDetail().getQuantity() < item.getQuantity()) {
//                throw new MyException(productEntity.getName() + " Chỉ còn " + productEntity.getProductDetail().getQuantity() + " trong kho hãy giảm số lượng xuống hoặc chọn mặt hàng khác thay thế");
//            }
            OrderItemEntity iOrder = new OrderItemEntity();
            iOrder.setProduct(productEntity);
            iOrder.setOrder(orderEntity);
            iOrder.setPrice(item.getPrice());
            iOrder.setQuantity(item.getQuantity());
            orderEntity.getOrderItems().add(iOrder);
        }

        boolean isBank = true;
        if (orderCheckoutRequest.getPaymentMethod() == PaymentMethod.ORDER_BANKING) {
            orderEntity.setStatus(OrderStatus.PENDING);
            orderEntity.setExpiresAt(now.plus(pendingTtl));
        } else {
            if (user.getCurrentBalance().compareTo(finalPrice) < 0) {
                throw new MyException("Số dư ví không đủ");
            }
            orderEntity.setStatus(OrderStatus.SUCCESS);
            user.wallet(orderEntity.getTotal());
            for (CartItemEntity item : cartEntity.getCartItems()) {
                productService.incrementSalesCount(item.getProduct(), item.getQuantity());
            }
            cartEntity.getCartItems().clear();
            cartRepository.save(cartEntity);
            userRepository.save(user);
            isBank = false;
        }
        orderRepository.saveAndFlush(orderEntity);
        OrderCheckoutResponse checkoutResponse = orderMapper.toOrderCheckoutResponse(orderEntity);
        if (isBank) {
            String transferContent = "HD" + now.atZone(Utils.getInstance().getZoneId()).getYear() + orderEntity.getId();
            checkoutResponse.setTransferContent(transferContent);
            checkoutResponse.setQRCodeUrl(Utils.getInstance().
                    buildVietQrQuickLink(
                            bank.getBankCode(),
                            bank.getAccountNumber(),
                            "qr_only", checkoutResponse.getTotal(), transferContent, bank.getAccountName()));
            mailService.sendHtml(
                    user.getEmail(),
                    "Hướng dẫn thanh toán đơn #" + orderEntity.getId(),
                    MailTemplates.bankPending(user, orderEntity, bank, checkoutResponse.getQRCodeUrl(), transferContent)
            );

        } else {
            String orderUrl = baseUrl + "/order/" + orderEntity.getId(); // đổi theo route của bạn
            mailService.sendHtml(
                    user.getEmail(),
                    "Thanh toán thành công đơn #" + orderEntity.getId(),
                    MailTemplates.paymentSuccess(user, orderEntity, orderUrl)
            );
        }

        return checkoutResponse;
    }

    @Override
    public OrderDetailResponse getOrderSuccessById(Long id) {
        OrderEntity orderEntity = orderRepository.findByIdAndStatus(id, OrderStatus.SUCCESS).orElseThrow(
                ()-> new MyException("Không tồn tại đơn hàng")
        );
        validationGetOrder(orderEntity);
        return orderMapper.toOrderDetailResponse(orderEntity);
    }

    private void validationGetOrder(OrderEntity orderEntity) {
        if (orderEntity == null) {
            throw new MyException("Đơn hàng chưa thanh toán hoặc không tồn tại");
        }
        if (!orderEntity.getUser().getId().equals(SecurityUtil.getUserId())) {
            throw new MyException("Đơn hàng này có vấn đề về chủ sở hữu");
        }

    }

    @Override
    public List<OrderListResponse> getListOrders() {
        Long id = SecurityUtil.getUserId();
        List<OrderListResponse> orders = new ArrayList<>();
        List<OrderEntity> orderEntities = orderRepository.findSuccessAndNotExpired(id, OrderStatus.SUCCESS, Instant.now());
        for (OrderEntity orderEntity : orderEntities) {
            OrderListResponse order = orderMapper.toOrderResponse(orderEntity);
            orders.add(order);
        }
        return orders;
    }

    @Override
    public OrderStatus getStatusById(Long id) {
        return orderRepository.findByIdAndUserId(id,SecurityUtil.getUserId()).orElseThrow(
                () -> new MyException("Đơn hàng không tồn tại")).getStatus();

    }

    @Override
    public String getDownloadUrl(Long id, Long orderItemId) {
        OrderEntity orderEntity = orderRepository.findByIdAndStatus(id, OrderStatus.SUCCESS).orElseThrow(()->
                new MyException("Đơn hàng chưa thanh toán hoặc không tồn tại"));

        if (!orderEntity.getUser().getId().equals(SecurityUtil.getUserId())) {
            throw new MyException("Đơn hàng này có vấn đề về chủ sở hữu");
        }
        OrderItemEntity item = orderEntity.getOrderItems().stream()
                .filter(i -> i.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new MyException("Sản phẩm không tồn tại trong đơn hàng"));

        return item.getProduct().getProductDetail().getDownloadUrl();
    }

    @Retryable(value = OptimisticLockingFailureException.class, maxAttemptsExpression  = "${app.retry.optimistic-lock.max-attempts:3}")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderCheckoutResponse checkoutByDirectBankOrWallet(DirectCheckoutRequest directCheckoutRequest) {
        ProductEntity product = productRepository.findById(directCheckoutRequest.getProductId()).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new MyException("Nguời dùng không hợp lệ"));
        if(productDetailRepository.reserveStock(product.getProductDetail().getId(),directCheckoutRequest.getQuantity()) == 0){
            throw new MyException(product.getName() + " Chỉ còn " + product.getProductDetail().getQuantity() + " trong kho hãy giảm số lượng xuống hoặc chọn mặt hàng khác thay thế");
        }
        OrderEntity order = new OrderEntity();


        Instant now = clock.instant();

        BigDecimal totalPrice = Utils.calsubPercent((product.getPrice().multiply(
                BigDecimal.valueOf(directCheckoutRequest.getQuantity()))),
                product.getProductDetail().getDiscount());

        int couponPercent = couponService.getCouponDiscount(directCheckoutRequest.getCouponCode());

        BigDecimal finalPrice = Utils.calsubPercent(totalPrice, couponPercent);
        boolean isBanking = directCheckoutRequest.getPaymentMethod().equals(PaymentMethod.ORDER_BANKING);
        if (user.getCurrentBalance().compareTo(finalPrice) < 0 && !isBanking) {
            throw new MyException("Số dư không đủ xin vui lòng nạp thêm tiền vào tài khoản");
        }
        SystemBankAccountEntity bank = systemBankAccountRepository.findFristByIsDefaultTrue();
        if (bank == null) {
            throw new MyException("Tài khoản ngân hàng chưa được cấu hình vui lòng liên hệ ADMIN");
        }
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setPrice(finalPrice);
        orderItem.setProduct(product);
        orderItem.setQuantity(directCheckoutRequest.getQuantity());
        order.getOrderItems().add(orderItem);

        order.setOrderDate(now);
        order.setExpiresAt(now.plus(pendingTtl));

        order.setUser(user);

        if (!isBanking) {
            order.setPaymentMethod(PaymentMethod.WALLET);
            order.setTotal(finalPrice);
            user.wallet(finalPrice);

            order.setStatus(OrderStatus.SUCCESS);
            userRepository.save(user);
            productService.incrementSalesCount(product, directCheckoutRequest.getQuantity());

        } else {
            order.setPaymentMethod(PaymentMethod.ORDER_BANKING);
            order.setTotal(finalPrice);
            order.setStatus(OrderStatus.PENDING);

        }
        orderRepository.saveAndFlush(order);
        String orderUrl = baseUrl + "/order/" + order.getId();
        OrderCheckoutResponse checkoutResponse = orderMapper.toOrderCheckoutResponse(order);
        if (isBanking) {
            String transferContent = "HD" + now.atZone(Utils.getInstance().getZoneId()).getYear()+ order.getId();
            checkoutResponse.setQRCodeUrl(Utils.getInstance().
                    buildVietQrQuickLink(
                            bank.getBankCode(),
                            bank.getAccountNumber(),
                            "qr_only", checkoutResponse.getTotal(), transferContent, bank.getAccountName()));
            mailService.sendHtml(
                    user.getEmail(),
                    "Hướng dẫn thanh toán đơn #" + order.getId(),
                    MailTemplates.bankPending(user, order, bank, checkoutResponse.getQRCodeUrl(), transferContent)
            );
        } else {
            mailService.sendHtml(
                    user.getEmail(),
                    "Thanh toán thành công đơn #" + order.getId(),
                    MailTemplates.paymentSuccess(user, order, orderUrl)
            );
        }

        return checkoutResponse;
    }

    @Override
    @Transactional
    public void makeExpiredOrder(Long orderId){
        int updated = orderRepository.makeExpiredByOrderId(orderId);
        if(updated ==0 ){
            return;
        }
        List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrderId(orderId);
        for(OrderItemEntity orderItem : orderItemEntities){
            productDetailRepository.releaseStock(orderItem.getProduct().getProductDetail().getId(),
                    orderItem.getQuantity());
        }


    }
    @Override
    public boolean existsPurchaseByUserAndProduct(Long userId, Long productId) {
        return orderRepository.existsPurchaseByUserAndProduct(userId, productId);
    }

}
