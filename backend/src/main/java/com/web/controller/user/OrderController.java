package com.web.controller.user;

import com.web.dto.request.order.DirectCheckoutRequest;
import com.web.dto.request.order.OrderCheckoutRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IOrderService;
import com.web.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/order")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    @PostMapping("/checkout")
    public ApiResponse<?> checkoutByBankOrWallet(@RequestBody OrderCheckoutRequest orderCheckoutRequest) {
        return ApiResponse.success(iOrderService.checkoutByBankOrWallet(orderCheckoutRequest));
    }

    @GetMapping("/status/{id}")
    public ApiResponse<?> getStatusById(@PathVariable Long id) {
        return ApiResponse.success(iOrderService.getStatusById(id));
    }

    @GetMapping()
    public ApiResponse<?> getOrders() {
        return ApiResponse.success(iOrderService.getListOrders());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOrder(@PathVariable Long id) {
        return ApiResponse.success(iOrderService.getOrderSuccessById(id));
    }
    
    @GetMapping("/{id}/detail")
    public ApiResponse<?> getDetailOrder(@PathVariable Long id){
        return ApiResponse.success(iOrderService.getOrderSuccessById(id));
    }
    
    @GetMapping("/{id}/item/{orderItemId}/download")
    public ApiResponse<?> getDownloadUrl(@PathVariable Long id, @PathVariable Long orderItemId){
        return ApiResponse.success(iOrderService.getDownloadUrl(id,orderItemId));
    }

    @PostMapping("/checkout/direct")
    public ApiResponse<?> checkoutByDirectWallet(@RequestBody DirectCheckoutRequest directCheckoutRequest) {
        return ApiResponse.success(iOrderService.checkoutByDirectBankOrWallet(directCheckoutRequest));
    }
    
    

}
