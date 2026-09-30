package com.web.controller.user;


import com.web.dto.request.reviews.ReviewRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.security.SecurityUtil;
import com.web.security.ratelimit.RateLimited;
import com.web.service.IProductService;
import com.web.service.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final IProductService productService;
    private final IReviewService reviceService;

 
    @GetMapping("/{productId}/list")
    public ApiResponse<?> getReviewsByProductId(@PathVariable Long productId){
        return ApiResponse.success(productService.getReviewsByProductId(productId),"Lấy danh sách đánh giá thành công");
    }
    @RateLimited("review")
    @PostMapping("/{productId}")
    public ApiResponse<?> addReviewByProductId(@Valid @RequestBody ReviewRequest reviewRequest){
        return ApiResponse.success(reviceService.addReview(reviewRequest),"Thêm đánh giá thành công");
    }
    
    @GetMapping("/{productId}/can-rate")
    public ApiResponse<?> canRate(@PathVariable Long productId){
        return ApiResponse.success(reviceService.canRate(productId),"Kiểm tra quyền đánh giá thành công");
    }

}
