/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.dto.response.reviews;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ZZ
 */

@Setter
@Getter
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String fullName;
    private String userName;
    private Integer rate;
    private String comment;
    private String userAvatar;
    private LocalDateTime createdAt;
}
