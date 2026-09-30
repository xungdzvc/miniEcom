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
import com.web.mapper.ReviewMapper;
import com.web.repository.ProductRepository;
import com.web.repository.ReviewRepository;
import com.web.repository.UserRepository;
import com.web.security.SecurityUtil;
import com.web.service.IOrderService;
import com.web.service.IReviewService;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse addReview(ReviewRequest reviewRequest) {
        Long userId = SecurityUtil.getUserId();
        ReviewEntity review = reviewRepository.findByProductIdAndUserId(reviewRequest.getProductId(),userId);
        if(review != null){
            throw new MyException("Bạn đã đánh giá sản phẩm này rồi");
        }
        review = new ReviewEntity();
        if (reviewRequest.getRate() != null && reviewRequest.getRate() > 0 && reviewRequest.getRate()<=5) {
            if (!canRate( reviewRequest.getProductId())) {
                throw new MyException("Bạn chỉ có thể đánh giá khi đã mua sản phẩm");
            }
            review.setRate(reviewRequest.getRate());
        }

        review.setComment(reviewRequest.getComment());

        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new MyException("Tài khoản sảy ra lỗi "));

        review.setProduct(productRepository.findById(reviewRequest.getProductId()).orElseThrow(()-> new MyException("sản phẩm lỗi")));
        review.setUser(user);
        reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }
    
    @Override
    public boolean canRate(Long productId) {
        Long userId = SecurityUtil.getUserId();
        if(userId == null){
            return false;
        }
        return iOrderService.existsPurchaseByUserAndProduct(userId, productId);
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        List<ReviewEntity> reviewEntities = reviewRepository.findByProductId(productId);
        return reviewEntities.stream().map(reviewMapper::toResponse).collect(Collectors.toList());
    }

    


}
