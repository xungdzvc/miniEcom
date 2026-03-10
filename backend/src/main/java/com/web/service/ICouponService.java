/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service;

import com.web.dto.request.coupon.CouponAddOrUpdateRequest;
import com.web.dto.response.coupon.CouponResponse;
import java.util.List;

/**
 *
 * @author ZZ
 */
public interface ICouponService {
    CouponResponse addOrUpdate(Long id,CouponAddOrUpdateRequest req);
    List<CouponResponse> getAll(); 
    int getCouponDiscount(String code);
    CouponResponse getById(Long id);
    void delete(Long id);
}
