package com.web.dto.response.cart;

 
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
 @Getter
@Setter
public class CartResponse {

    private long id;
    private long userId;
    private BigDecimal toltalPrice;
    private List<CartItemResponse> cartItems;
    
}
