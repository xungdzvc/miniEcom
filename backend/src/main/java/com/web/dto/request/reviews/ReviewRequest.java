/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.request.reviews;

import java.time.Instant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */
@Setter
@Getter
public class ReviewRequest {


    @NotNull(message = "mã sản phẩm không thể trống")
    @Positive(message = "mã sản phẩm không hợp lệ")
    private Long productId;
    @Min(value = 1,message = "Đánh giá phải từ 1 sao")
    @Min(value = 5,message = "Đánh giá không được quá 5 sao")
    private Integer rate;

    private String comment;
}
