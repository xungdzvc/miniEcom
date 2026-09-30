/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.controller.admin;

import com.web.dto.request.coupon.CouponAddOrUpdateRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.ICouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ZZ
 */
@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor

public class CouponAdminController {

    private final ICouponService couponService;

    @GetMapping
    public ApiResponse<?> getAllCounpon() {
        return ApiResponse.success(couponService.getAll(),"Lấy thành công danh sách Mã Giảm Giá");
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCouponById(@PathVariable Long id) {
        return ApiResponse.success(couponService.getById(id));
    }
    
    @PatchMapping("/{id}")
    public ApiResponse<?> updateCoupon(@PathVariable Long id,@Valid @RequestBody CouponAddOrUpdateRequest req) {
        return ApiResponse.success(couponService.addOrUpdate(id, req),"Cập nhật Mã Giảm Giá thành công");
    }

    @PostMapping()
    public ApiResponse<?> addCoupon(@Valid @RequestBody CouponAddOrUpdateRequest req) {
        return ApiResponse.success(couponService.addOrUpdate(null, req),"Tạo Mã Giảm Giá thành công");
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);
        return ApiResponse.success(null,"Xoá Mã Giảm Giá thành công");
    }
}
