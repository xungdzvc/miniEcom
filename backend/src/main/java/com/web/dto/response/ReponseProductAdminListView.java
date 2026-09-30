package com.web.dto.response;

import java.time.Instant;

public interface ReponseProductAdminListView {
    Long getId();
    String getName();
    float getPrice();
    String getThumbnail();
    Long getViewCount();
    Long getSoldCount();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}
