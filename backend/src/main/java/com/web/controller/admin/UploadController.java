package com.web.controller.admin;

import org.springframework.web.bind.annotation.*;
import com.web.minio.MinioService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.MinioClient;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private MinioService minioService;
    @Autowired
    private MinioClient minioClient;
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = minioService.upload(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
    @GetMapping("/file/{key}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String key) {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket("bucket-xungdeptry")
                    .object(key)
                    .build();

            InputStream stream = minioClient.getObject(args);
            byte[] bytes = stream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/*")
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

}
