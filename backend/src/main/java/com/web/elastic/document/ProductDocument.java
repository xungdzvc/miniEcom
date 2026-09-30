package com.web.elastic.document;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDocument {
    @Id
    private Long id;

    private String name;

    private String categoryName;

    private BigDecimal price;
    
    private String slug;
    
    private String thumbnail;
}
