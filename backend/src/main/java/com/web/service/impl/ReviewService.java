/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

import com.web.dto.request.reviews.ReviewRequest;
import com.web.dto.response.reviews.ReviewResponse;
import com.web.entity.ReviewEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.repository.OrderRepository;
import com.web.repository.ProductRepository;
import com.web.repository.ReviewRepository;
import com.web.repository.UserRepository;
import com.web.security.SecurityUtil;
import com.web.service.IOrderService;
import com.web.service.IReviewService;
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
public class ReviewService implements IReviewService {
    
    private final IOrderService iOrderService;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public ReviewResponse addReview(ReviewRequest reviewRequest) {
        Long userId = SecurityUtil.getUserId();
        ReviewEntity review = new ReviewEntity();
        if (reviewRequest.getRate() != null && reviewRequest.getRate() > 0) {
            if (!canRate( reviewRequest.getProductId())) {
                throw new MyException("Bạn chỉ có thể đánh giá khi đã mua sản phẩm");
            }
            review.setRate(reviewRequest.getRate());
        }

        review.setComment(reviewRequest.getComment());
        review.setCreatedAt(LocalDateTime.now());

        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new MyException("Tài khoản sảy ra lỗi "));

        review.setProduct(productRepository.findById(reviewRequest.getProductId()).orElseThrow(()-> new MyException("sản phẩm lỗi")));
        review.setUser(user);
        reviewRepository.save(review);
        return toResponse(review);
    }
    
    @Override
    public boolean canRate(Long productId) {
        Long userId = SecurityUtil.getUserId();
        return iOrderService.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        List<ReviewEntity> reviewEntities = reviewRepository.findByProductId(productId);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for(ReviewEntity review : reviewEntities){
            reviewResponses.add(toResponse(review));
        }
        return reviewResponses;
    }

    ReviewResponse toResponse(ReviewEntity review){
        ReviewResponse response = new ReviewResponse();
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setProductId(review.getProduct().getId());
        return response;
    }


}
