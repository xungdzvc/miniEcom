package com.web.controller.admin;

import com.web.dto.StorageFileDTO;
import com.web.service.IStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class UploadController {

    private final IStorageService storageService;

    public UploadController(IStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        StorageFileDTO saved = storageService.save(file, "products");
        return ResponseEntity.ok(Map.of("key", saved.key(), "url", saved.url()));
    }

}
