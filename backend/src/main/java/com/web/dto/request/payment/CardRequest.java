package com.web.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CardRequest {
    @NotBlank(message = "Loại thẻ không được bỏ trống")
    private String loaiThe;
    @NotNull(message = "Giá trị không được để trống")
    @Positive(message = "Giá trị phải lớn hơn 0")
    private BigDecimal menhGia;

    @NotBlank(message = "Sere không được bỏ trống")
    private String seri;

    @NotBlank(message = "Mã thẻ không được bỏ trống")
    private String maThe;
}
