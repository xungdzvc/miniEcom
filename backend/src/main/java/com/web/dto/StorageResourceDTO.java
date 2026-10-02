package com.web.dto;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record StorageResourceDTO(
        Resource resource,
        MediaType mediaType,
        long contentType
) {
}
