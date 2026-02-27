package com.web.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;
import java.util.UUID;
import io.minio.PutObjectArgs;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {
        try {
            // Tên lưu file: random UUID + tên file thật
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Upload file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return  fileName;


            // URL public để FE hiển thị
        //    return String.format("%s/%s/%s", "https://minio1.webtui.vn:9000", bucket, fileName);

        } catch (Exception e) {
            throw new RuntimeException("Upload file thất bại: " + e.getMessage());
        }
    }
}
