/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service;

import com.web.dto.request.reviews.ReviewRequest;
import com.web.dto.response.reviews.ReviewResponse;

import java.util.List;

/**
 *
 * @author ZZ
 */
public interface IReviewService {
    ReviewResponse addReview(ReviewRequest reviewRequest);
    boolean canRate( Long productId);
    List<ReviewResponse> getReviewsByProductId(Long productId);
}
