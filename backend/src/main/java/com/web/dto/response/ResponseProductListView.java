package com.web.dto.response;

public interface ResponseProductListView {
    Long getId();
    String getName();
    float getPrice();
    String getThumbnail();
    Long getViewCount();
    Long getSoldCount();

}
