package com.web.dto.request.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRequest {
    private String loaiThe;
    private Long menhGia;
    private String seri;
    private String maThe;
}
