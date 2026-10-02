package com.web.controller.admin;

import com.web.dto.StorageFileDTO;
import com.web.service.IStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/uploads")
public class UploadController {

    private final IStorageService storageService;

    @PostMapping()
    public ResponseEntity<StorageFileDTO> upload(@RequestParam("file") MultipartFile file) {
        StorageFileDTO result = storageService.save(file, "product");
        return ResponseEntity.ok(result);
    }

}
