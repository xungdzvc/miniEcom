package com.web.util;

import com.web.entity.ProductEntity;
import com.web.entity.ProductImageEntity;
import com.web.exception.MyException;
import com.web.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.*;

public class Utils {

    private static Utils instance;
    private final static ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    public ZoneId getZoneId(){
        return BUSINESS_ZONE;
    }
    public Long extractId(Pattern pattern, String content) {
        if (content == null) {
            return null;
        }
        Matcher m = pattern.matcher(content);
        if (!m.find()) {
            return null;
        }
        return Long.valueOf(m.group(1));
    }

    public static void replaceImage(
            List<String> urls,
                        ProductEntity productEntity
    ) {

        List<ProductImageEntity> currentImages = productEntity.getProductImages();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
            productEntity.setProductImages(currentImages);
        }
        if (urls == null) {
            return;
        }
        currentImages.clear();
        for (String url : urls) {
            ProductImageEntity img = new ProductImageEntity();
            img.setImageUrl(url);
            img.setProduct(productEntity);
            currentImages.add(img);
        }
    }

    public String buildVietQrQuickLink(
            String bankId, // ví dụ: "970415" hoặc "ICB"...
            String accountNo, // số tài khoản nhận
            String template, // "compact", "compact2", "qr_only", "print"
            BigDecimal amount,
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
        if (input == null) {
            return "";
        }
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
            if (!dir.exists()) {
                dir.mkdirs();
            }

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

    public static BigDecimal calsubPercent(BigDecimal amount, int percent) {
        if(amount == null){
            throw new IllegalArgumentException("Số tiền không thể trống");
        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Số tiền không thể nhỏ hơn 0");
        }

        if(percent < 0 || percent > 100){
            throw new IllegalArgumentException("Chiết khẩu phải nằm trong khoảng 0-> 100");
        }
        return amount.multiply(BigDecimal.valueOf(100 - percent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    public static void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    public static BigDecimal getMonthRevenue(OrderRepository orderRepository) { 
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);

        Instant startTime = startDate.atStartOfDay(BUSINESS_ZONE)
                .toInstant();

        Instant endTime = endDate.atStartOfDay(BUSINESS_ZONE)
                .toInstant();

        return orderRepository.sumRevenueBetween(startTime, endTime);
    }

    public static BigDecimal getQuarterRevenue(OrderRepository orderRepository) {  
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        int quarterStartMonth =
                ((today.getMonthValue() - 1) / 3) * 3 + 1;

        LocalDate firstDayOfQuarter = LocalDate.of(
                today.getYear(),
                quarterStartMonth,
                1
        );
        Instant start = firstDayOfQuarter
                .atStartOfDay(BUSINESS_ZONE)
                .toInstant();

        LocalDate firstDayOfNextQuarter =
                firstDayOfQuarter.plusMonths(3);

        Instant end =
                firstDayOfNextQuarter
                        .atStartOfDay(BUSINESS_ZONE)
                        .toInstant();

        return orderRepository.sumRevenueBetween(start, end);
    }

    public static BigDecimal getYearRevenue(OrderRepository orderRepository) {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        LocalDate startDate  = LocalDate.of(
                today.getYear(),
                1,
                1
        );

        LocalDate endDate = startDate.plusYears(1);
        Instant startTime = startDate
                .atStartOfDay(BUSINESS_ZONE)
                .toInstant(); 
        Instant endTime = endDate
                .atStartOfDay(BUSINESS_ZONE)
                .toInstant();
        return orderRepository.sumRevenueBetween(startTime, endTime);
    }

    public static String getClientIp(HttpServletRequest request) {

        String cfIp = request.getHeader("CF-Connecting-IP");

        if (cfIp != null && !cfIp.isBlank()) {
            return cfIp;
        }

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0];
        }

        return request.getRemoteAddr();
    }

}
