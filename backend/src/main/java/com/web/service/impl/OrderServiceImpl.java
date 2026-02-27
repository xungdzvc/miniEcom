package com.web.service.impl;

import com.web.dto.CartDTO;
import com.web.dto.CartItemDTO;
import com.web.dto.OrderDTO;
import com.web.dto.request.order.DirectCheckoutRequest;
import com.web.dto.response.order.OrderDetailResponse;
import com.web.dto.request.order.OrderCheckoutRequest;
import com.web.dto.response.order.OrderCheckoutResponse;
import com.web.dto.response.order.OrderListResponse;
import com.web.entity.*;
import com.web.enums.OrderStatus;
import com.web.enums.PaymentMethod;
import com.web.enums.PaymentStatus;
import com.web.enums.PaymentType;
import com.web.exception.MyException;
import com.web.mapper.CartMapper;
import com.web.mapper.OrderMapper;
import com.web.repository.BankAccountRepository;
import com.web.repository.CartRepository;
import com.web.repository.CouponRepository;
import com.web.repository.OrderRepository;
import com.web.repository.ProductRepository;
import com.web.repository.SystemBankAccountRepository;
import com.web.repository.UserRepository;
import com.web.security.SecurityUtil;
import com.web.service.ICartService;
import com.web.service.IOrderService;
import com.web.util.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    private final SystemBankAccountRepository systemBankAccountRepository;

    private final ICartService cartService;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public OrderCheckoutResponse checkoutByBankOrWallet(OrderCheckoutRequest orderCheckoutRequest) {
        CartEntity cartEntity = cartRepository.findById(orderCheckoutRequest.getCartId())
                .orElseThrow(() -> new MyException("Giỏ hàng không tồn tại"));

        if (cartEntity.getCartItems() == null) {
            throw new MyException("Giỏ hàng trống");
        }
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findUserById(userId);
        if(user == null){
            throw new MyException("Nguười dùng không hợp lệ");
        }
        SystemBankAccountEntity bank = systemBankAccountRepository.findFristByIsDefaultTrue();
        if (bank == null) {
            throw new MyException("Tài khoản ngân hàng chưa được cấu hình vui lòng liên hệ ADMIN");
        }
        OrderEntity orderEntity = new OrderEntity();
        CartDTO cartDTO = cartMapper.toCartDTO(cartEntity);
        LocalDateTime now = LocalDateTime.now();
        long totalPrice = 0;

        for (CartItemDTO item : cartDTO.getItems()) {
            totalPrice += Utils.calsubPercent(item.getPrice(), item.getDiscount()) * item.getQuantity();
        }

        int couponPercent = cartService.getCouponDiscount(orderCheckoutRequest.getCouponCode());

        long finalPrice = Utils.calsubPercent(totalPrice, couponPercent);

        orderEntity.setOrderDate(now);
        orderEntity.setTotal(finalPrice);
        orderEntity.setUser(cartEntity.getUser());
        orderEntity.setPaymentMethod(orderCheckoutRequest.getPaymentMethod());
        for (CartItemDTO item : cartDTO.getItems()) {
            ProductEntity productEntity = productRepository.findByIdAndStatusTrue(item.getProductId());
            if (productEntity == null) {
                throw new MyException("Có sản phẩm không tồn tại hoặc đã ngừng kinh doanh");
            }
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
            orderEntity.setExpiresAt(now.plusMinutes(15));
        } else {
            if (user.getVnd() < finalPrice) {
                throw new MyException("Số dư ví không đủ");
            }
            orderEntity.setStatus(OrderStatus.SUCCESS);
            user.subVnd(orderEntity.getTotal());
            cartEntity.getCartItems().clear();
            cartRepository.save(cartEntity);
            userRepository.save(user);
            isBank = false;
        }
        orderRepository.saveAndFlush(orderEntity);
        OrderCheckoutResponse checkoutResponse = new OrderCheckoutResponse();
        
        checkoutResponse = orderMapper.toOrderCheckoutResponse(orderEntity);
        if (isBank) {
            checkoutResponse.setQRCodeUrl(Utils.getInstance().
                    buildVietQrQuickLink(
                            bank.getBankCode(),
                            bank.getAccountNumber(),
                            "qr_only", checkoutResponse.getTotal(), "HD" + now.getYear() + orderEntity.getId(), bank.getAccountName()));
  
        }

        return checkoutResponse;
    }

    @Override
    public OrderCheckoutResponse updateOrder(OrderDTO orderDTO) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public OrderDetailResponse getOrderSuccessById(Long id) {
        OrderEntity orderEntity = orderRepository.findByIdAndStatus(id, OrderStatus.SUCCESS);
        if (orderEntity == null) {
            throw new MyException("Đơn hàng chưa thanh toán hoặc không tồn tại");
        }
        if (!orderEntity.getUser().getId().equals(SecurityUtil.getUserId())) {
            throw new MyException("Đơn hàng này có vấn đề về chủ sở hữu");
        }
        OrderDetailResponse order = orderMapper.toOrderDetailResponse(orderEntity);
        return order;
    }

    @Override
    public List<OrderListResponse> getListOrders() {
        Long id = SecurityUtil.getUserId();
        List<OrderListResponse> orders = new ArrayList<>();
        List<OrderEntity> orderEntities = orderRepository.findSuccessOrNotExpired(id, OrderStatus.SUCCESS, LocalDateTime.now());
        for (OrderEntity orderEntity : orderEntities) {
            OrderListResponse order = orderMapper.toOrderResponse(orderEntity);
            orders.add(order);
        }
        return orders;
    }

    @Override
    public OrderStatus getStatusById(Long id) {
        return orderRepository.getStatusById(id).getStatus();
    }

    @Override
    public List<OrderDetailResponse> getOrderByUserId(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDownloadUrl(Long id, Long orderItemId) {
        OrderEntity orderEntity = orderRepository.findByIdAndStatus(id, OrderStatus.SUCCESS);
        if (orderEntity == null) {
            throw new MyException("Đơn hàng chưa thanh toán hoặc không tồn tại");
        }
        if (!orderEntity.getUser().getId().equals(SecurityUtil.getUserId())) {
            throw new MyException("Đơn hàng này có vấn đề về chủ sở hữu");
        }
        OrderItemEntity item = orderEntity.getOrderItems().stream()
                .filter(i -> i.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new MyException("Sản phẩm không tồn tại trong đơn hàng"));

        return item.getProduct().getProductDetail().getDownloadUrl();
    }
    @Transactional
    @Override
    public OrderCheckoutResponse checkoutByDirectBankOrWallet(DirectCheckoutRequest directCheckoutRequest) {
        ProductEntity product = productRepository.findById(directCheckoutRequest.getProductId()).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        Long userId = SecurityUtil.getUserId();
        UserEntity user = userRepository.findUserById(userId);
        if(user == null){
            throw new MyException("Nguời dùng không hợp lệ");
        }

        OrderEntity order = new OrderEntity();
        LocalDateTime now = LocalDateTime.now();
        long totalPrice = Utils.calsubPercent(product.getPrice() * directCheckoutRequest.getQuantity(), product.getProductDetail().getDiscount());
        int couponPercent = cartService.getCouponDiscount(directCheckoutRequest.getCouponCode());

        long finalPrice = Utils.calsubPercent(totalPrice, couponPercent);
        boolean isBanking = false;
        if (directCheckoutRequest.getPaymentMethod().equals(PaymentMethod.ORDER_BANKING)) {
            isBanking = true;
        }
        if (user.getVnd() < finalPrice && !isBanking) {
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
        order.getOrderItems().add(orderItem);

        order.setOrderDate(now);
        order.setExpiresAt(now.plusMinutes(15));

        order.setUser(user);

        if (!isBanking) {
            order.setPaymentMethod(PaymentMethod.WALLET);
            order.setTotal(finalPrice);
            user.subVnd(finalPrice);
            order.setStatus(OrderStatus.SUCCESS);
            userRepository.save(user);
        } else {
            order.setPaymentMethod(PaymentMethod.ORDER_BANKING);
            order.setTotal(finalPrice);
            order.setStatus(OrderStatus.PENDING);

        }
        orderRepository.saveAndFlush(order);
        OrderCheckoutResponse checkoutResponse = orderMapper.toOrderCheckoutResponse(order);
        if (isBanking) {
            checkoutResponse.setQRCodeUrl(Utils.getInstance().
                    buildVietQrQuickLink(
                            bank.getBankCode(),
                            bank.getAccountNumber(),
                            "qr_only", checkoutResponse.getTotal(), "HD" + now.getYear() + order.getId(), bank.getAccountName()));
        }

        return checkoutResponse;
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        List<OrderEntity> orders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.SUCCESS);
        for (OrderEntity order : orders) {
            long is = order.getOrderItems().stream().filter(orderItem
                    -> orderItem.getProduct().getId().equals(productId)).count();
            if (is >= 1) {
                return true;
            }
        }
        return false;

    }

}
