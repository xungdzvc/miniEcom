package com.web.util;

import com.web.exception.MyException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.*;

public class Utils {

    private static Utils instance;
    public static Utils getInstance() {
            if(instance == null) {
                instance  = new Utils();
            }
            return instance;
    }

    public Long extractId(Pattern pattern, String content) {
        if (content == null) return null;
        Matcher m = pattern.matcher(content);
        if (!m.find()) return null;
        return Long.valueOf(m.group(1));
    }
    public String buildVietQrQuickLink(
            String bankId,      // ví dụ: "970415" hoặc "ICB"...
            String accountNo,   // số tài khoản nhận
            String template,    // "compact", "compact2", "qr_only", "print"
            float amount,
            String addInfo,
            String accountName
    ) {
        String addInfoEnc = URLEncoder.encode(addInfo, StandardCharsets.UTF_8);
        String nameEnc = URLEncoder.encode(accountName, StandardCharsets.UTF_8);

        return String.format(
                "https://img.vietqr.io/image/%s-%s-%s.png?amount=%f&addInfo=%s&accountName=%s",
                bankId, accountNo, template, amount, addInfoEnc, nameEnc
        );
    }

    public static String MD5Hash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String slugify(String input) {
        if (input == null) return "";
        String s = input.trim().toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");     // bỏ dấu
        s = s.replace("đ", "d");
        s = s.replaceAll("[^a-z0-9]+", "-");   // non-alnum -> -
        s = s.replaceAll("(^-+|-+$)", "");     // trim -
        return s;
    }
    public String saveFile(MultipartFile file, String uploadDir, Long productId, String productName) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Loại bỏ dấu tiếng Việt và khoảng trắng trong tên sản phẩm
            String cleanName = removeVietnameseAccents(productName).replaceAll("\\s+", "_").toLowerCase();

            // Lấy phần mở rộng file (jpg, png,...)
            String extension = "";
            String originalName = file.getOriginalFilename();
            int lastDot = originalName.lastIndexOf('.');
            if (lastDot > 0) {
                extension = originalName.substring(lastDot);
            }

            // Tạo tên file mới
            String fileName = productId + "_" + cleanName + extension;

            // Lưu file
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL để lưu DB
            return "/" + uploadDir + fileName;
        } catch (IOException e) {
            throw new MyException("Failed to save file: " + file.getOriginalFilename());
        }
    }

    /**
     * Hàm loại bỏ dấu tiếng Việt
     */
    private String removeVietnameseAccents(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public static long calsubPercent(long value,int percent){
        return (value - (value * percent/100));
    }


    

}
