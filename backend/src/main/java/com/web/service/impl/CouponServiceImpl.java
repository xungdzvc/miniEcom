/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

import com.web.dto.request.coupon.CouponAddOrUpdateRequest;
import com.web.dto.response.coupon.CouponResponse;
import com.web.entity.CouponEntity;
import com.web.exception.MyException;
import com.web.repository.CouponRepository;
import com.web.service.ICouponService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {

    private final CouponRepository couponRepo;

    @Override
    public CouponResponse addOrUpdate(Long id, CouponAddOrUpdateRequest req) {
        if(req.getCouponCode() == null||req.getCouponCode().isEmpty()){
            throw new MyException("coupon code không thể để trống");
        }
        if(req.getDiscount() < 0||req.getDiscount() >100){
            throw new MyException("discount không thể nhỏ < 0 hoặc lớn hơn 100");
        }
        CouponEntity coupon = new CouponEntity();
        LocalDateTime now = LocalDateTime.now();
        if (id == null) {
            coupon.setCreatedAt(now);
        } else {
            coupon = couponRepo.findById(id).orElseThrow(() -> new MyException("không tồn tại coupon này"));
        }
        coupon.setUpdatedAt(now);
        coupon.setCouponCode(req.getCouponCode());
        coupon.setDiscount(req.getDiscount());
        couponRepo.save(coupon);

        CouponResponse couponResponse = new CouponResponse();
        convert(couponResponse, coupon);
        return couponResponse;
    }

    @Override
    public List<CouponResponse> getAll() {
        List<CouponEntity> coupons = couponRepo.findAll();
        List<CouponResponse> couponResponses = new ArrayList<>();
        for (CouponEntity couponEntity : coupons) {
            CouponResponse couponResponse = new CouponResponse();
            convert(couponResponse, couponEntity);
            couponResponses.add(couponResponse);
        }
        return couponResponses;
    }

    void convert(CouponResponse couponResponse, CouponEntity coupon) {
        couponResponse.setId(coupon.getId());
        couponResponse.setCouponCode(coupon.getCouponCode());
        couponResponse.setDiscount(coupon.getDiscount());
        couponResponse.setCreatedAt(coupon.getCreatedAt());
        couponResponse.setUpdatedAt(coupon.getUpdatedAt());
    }

    @Override
    public int getCouponDiscount(String code) {
        return couponRepo.findDiscountPercentByCouponCode(code).orElse(0);
    }

    @Override
    public CouponResponse getById(Long id) {
        CouponEntity coupon = couponRepo.findById(id).orElseThrow(()-> new MyException("Mã giảm giá không tồn tại"));
        CouponResponse couponResponse = new CouponResponse();
        convert(couponResponse, coupon);
        return couponResponse;
        
    }

    @Override
    public void delete(Long id) {
        couponRepo.deleteById(id);
    }
}
