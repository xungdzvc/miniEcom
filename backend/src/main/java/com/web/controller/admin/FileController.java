package com.web.controller.admin;

import com.web.dto.StorageResourceDTO;
import com.web.service.IStorageService;
import io.minio.GetObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;

import java.io.InputStream;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final IStorageService storageService;

    @GetMapping("{folder}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String folder, @PathVariable String filename) {
        String key = folder + "/" + filename;
        return storageService.load(key)
                .map(this::toResponse)
                .orElseGet(()-> ResponseEntity.notFound().build());

    }

    @GetMapping("/uploads/products/{filename}")
    public ResponseEntity<Resource> getLegacyProductFile(
            @PathVariable String filename
    ) {

        String key = "products/" + filename;

        return storageService.load(key)
                .map(this::toResponse)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    private ResponseEntity<Resource> toResponse(StorageResourceDTO file){
        return ResponseEntity.ok()
                .contentType(file.mediaType())
                .contentLength(file.contentType())
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(file.resource());
    }



}
