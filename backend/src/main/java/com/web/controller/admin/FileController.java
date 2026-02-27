package com.web.controller.admin;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;

import java.io.InputStream;

@RestController
@RequestMapping("/files")
public class FileController {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public FileController(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            byte[] bytes = stream.readAllBytes();

            // Lấy content-type
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            String contentType = stat.contentType();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
