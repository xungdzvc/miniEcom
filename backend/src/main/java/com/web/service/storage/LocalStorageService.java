/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.storage;

import com.web.dto.StorageFileDTO;
import com.web.exception.MyException;
import com.web.service.IStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ZZ
 */
@Service
public class LocalStorageService implements IStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;
    @Value("${app.upload-url-prefix:/uploads}")
    private String urlPrefix;

    private static final Set<String> allowed = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

    @Override
    public StorageFileDTO save(MultipartFile file, String folder) {
        System.out.println("WORKING_DIR = " + System.getProperty("user.dir"));
        System.out.println("UPLOAD_DIR_ABS = " + java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize());
        if (file == null || file.isEmpty()) {
            throw new MyException("File rỗng");
        }
        String contentType = file.getContentType();

        if (contentType == null || !allowed.contains(contentType)) {
            throw new MyException("Ảnh không hợp lệ, chỉ cho phép ảnh jpg, png, webp, gif");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot).toLowerCase();
        }

        String filename = UUID.randomUUID() + ext;

        try {
            Path dir = Paths.get(uploadDir, folder).normalize();
            Files.createDirectories(dir);

            Path target = dir.resolve(filename).normalize();
            if (!target.startsWith(dir)) {
                throw new RuntimeException("Tên file không hợp lệ");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String key = folder + "/" + filename;              // lưu DB
            String url = urlPrefix + "/" + key;                // hiển thị
            return new StorageFileDTO(key, url);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file", e);
        }
    }

    @Override
    public void delete(String key) {
        try {

            if (key.startsWith("/uploads/")) {
                key = key.substring("/uploads/".length());
            }
            Path target = Paths.get(uploadDir,key).toAbsolutePath().normalize();
            
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xoá file", e);
        }
    }

    @Override
    public String getPublicUrl(String key) {
        return urlPrefix + "/" + key;
    }

}
