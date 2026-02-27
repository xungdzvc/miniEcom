package com.web.dto.response;

import java.time.LocalDateTime;

public interface ReponseProductAdminListView {
    Long getId();
    String getName();
    float getPrice();
    String getThumbnail();
    Long getViewCount();
    Long getSoldCount();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
