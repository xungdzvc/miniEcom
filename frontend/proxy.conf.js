module.exports = {
  // Cấu hình Proxy thông minh: Bắt tất cả request ("**")
  "**": {
    secure: false,
    changeOrigin: true,
    bypass: function (req, res, proxyOptions) {
      res.setHeader("Cross-Origin-Opener-Policy", "same-origin-allow-popups");
      
      // Bỏ dòng Embedder-Policy để tránh lỗi chặn ảnh
       res.setHeader("Cross-Origin-Embedder-Policy", "require-corp"); 

      // Quan trọng: Trả về false để Angular tự phục vụ file này, KHÔNG gửi về Spring Boot
      return false;
    }
  }
};