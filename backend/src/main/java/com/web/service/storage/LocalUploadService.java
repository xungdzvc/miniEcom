package com.web.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalUploadService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.upload-url-prefix:/uploads}")
    private String urlPrefix;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    public String saveImage(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) throw new RuntimeException("File rỗng");

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException("Chỉ cho phép upload ảnh (jpg/png/webp/gif)");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = "";

        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot).toLowerCase();

        String filename = UUID.randomUUID() + ext;

        try {
            Path dir = Paths.get(uploadDir, subFolder).normalize();
            Files.createDirectories(dir);

            Path target = dir.resolve(filename).normalize();

            // chặn path traversal
            if (!target.startsWith(dir)) throw new RuntimeException("Tên file không hợp lệ");

            // ghi file
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // trả về path tương đối để lưu DB
            // vd: products/uuid.jpg
            return subFolder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file: " + e.getMessage(), e);
        }
    }

    public String toPublicUrl(String relativePath) {
        // /uploads/products/uuid.jpg
        return urlPrefix + "/" + relativePath.replace("\\", "/");
    }
}