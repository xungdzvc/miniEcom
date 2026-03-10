package com.web.util;

import com.web.entity.OrderEntity;
import com.web.entity.SystemBankAccountEntity;
import com.web.entity.UserEntity;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author ZZ
 */
public class MailTemplates {

    private static final NumberFormat VND = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static String money(long vnd) {
        return VND.format(vnd) + " đ";
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String base(String title, String bodyHtml) {
        return """
    <!doctype html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width,initial-scale=1">
      <meta name="x-apple-disable-message-reformatting">
               <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
    </head>
    <body style="margin:0;padding:0;background:#f6f7fb;">
      <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f6f7fb;">
        <tr>
          <td align="center" style="padding:20px 12px;">
            <table role="presentation" width="640" cellpadding="0" cellspacing="0"
                   style="width:100%%;max-width:640px;background:#ffffff;border-radius:16px;overflow:hidden;
                          box-shadow:0 10px 28px rgba(15,23,42,.08);
                          font-family:Inter,Segoe UI,Roboto,Arial,sans-serif;color:#111827;">
              <!-- Header -->
              <tr>
                <td style="background:#0b1220;padding:18px 20px;">
                  <div style="font-size:18px;font-weight:800;color:#ffffff;letter-spacing:.2px;">XungLord</div>
                  <div style="margin-top:6px;font-size:13px;line-height:18px;color:rgba(255,255,255,.82);">
                    %s
                  </div>
                </td>
              </tr>

              <!-- Content -->
              <tr>
                <td style="padding:18px 20px;font-size:14px;line-height:20px;">
                  %s
                </td>
              </tr>

              <!-- Footer -->
              <tr>
                <td style="padding:14px 20px;background:#f8fafc;border-top:1px solid #eef2ff;
                           font-size:12px;line-height:18px;color:#6b7280;text-align:center;">
                  Email tự động. Nếu cần hỗ trợ, bạn có thể reply email này.
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </body>
    </html>
    """.formatted(escape(title), bodyHtml);
    }

    public static String bankPending(UserEntity user, OrderEntity order, SystemBankAccountEntity bank,
            String qrUrl, String transferContent) {

        String customerName = escape(user.getFullName() != null ? user.getFullName() : user.getUsername());
        String expires = order.getExpiresAt() != null ? order.getExpiresAt().format(DT) : "";

        String itemsHtml = order.getOrderItems().stream()
                .map(oi -> """
                <tr>
                  <td style="padding:10px 0;border-bottom:1px solid #eef2ff;">
                    %s <span style="color:#6b7280;">× %d</span>
                  </td>
                </tr>
            """.formatted(escape(oi.getProduct().getName()), oi.getQuantity()))
                .reduce("", String::concat);

        String qrBlock = (qrUrl != null && !qrUrl.isBlank())
                ? "<img alt='QR' src='" + escape(qrUrl) + "' width='180' height='180' style='display:block;margin:0 auto;border-radius:12px;border:1px solid #e5e7eb;'/>"
                : "<div style='color:#6b7280;font-size:12px;'>QR chưa sẵn sàng</div>";

        String body = """
    <div style="font-size:18px;font-weight:800;line-height:24px;margin:0 0 8px;">Hướng dẫn thanh toán</div>
    <div style="color:#6b7280;margin:0 0 14px;">
      Chào <b>%s</b>, đơn <b>#%d</b> đã được tạo. Vui lòng thanh toán trước <b>%s</b>.
    </div>

    <!-- Two columns (stackable) -->
    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;">
      <tr>
        <!-- Left -->
        <td valign="top" style="padding:0 10px 12px 0;">
          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                 style="background:#f8fafc;border:1px solid #e5e7eb;border-radius:14px;">
            <tr>
              <td style="padding:14px;">
                <div style="font-weight:800;margin-bottom:10px;">Thông tin chuyển khoản</div>

                <div style="line-height:22px;">
                  <div><b>Ngân hàng:</b> %s</div>
                  <div><b>Số tài khoản:</b> %s</div>
                  <div><b>Chủ tài khoản:</b> %s</div>
                  <div><b>Số tiền:</b> %s</div>
                  <div style="margin-top:8px;">
                    <b>Nội dung:</b>
                    <span style="display:inline-block;background:#111827;color:#fff;padding:4px 10px;border-radius:999px;font-weight:800;">
                      %s
                    </span>
                  </div>
                </div>

                <div style="margin-top:10px;color:#ef4444;font-size:12px;line-height:18px;">
                  Lưu ý: chuyển đúng <b>nội dung</b> để hệ thống xác nhận nhanh.
                </div>
              </td>
            </tr>
          </table>
        </td>

        <!-- Right -->
        <td valign="top" style="padding:0 0 12px 10px;">
          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                 style="background:#f8fafc;border:1px solid #e5e7eb;border-radius:14px;">
            <tr>
              <td style="padding:14px;text-align:center;">
                <div style="font-weight:800;margin-bottom:10px;">QR thanh toán</div>
                %s
                
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>

    <!-- Items -->
    <div style="margin-top:8px;font-weight:800;">Sản phẩm</div>
    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;margin-top:6px;">
      %s
    </table>

    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin-top:12px;">
      <tr>
        <td style="padding-top:10px;border-top:1px dashed #e5e7eb;color:#6b7280;">Tổng cuối cùng</td>
        <td align="right" style="padding-top:10px;border-top:1px dashed #e5e7eb;font-size:18px;font-weight:900;">
          %s
        </td>
      </tr>
    </table>
    """.formatted(
                customerName, order.getId(), escape(expires),
                escape(bank.getBankCode()),
                escape(bank.getAccountNumber()),
                escape(bank.getAccountName()),
                money(order.getTotal()),
                escape(transferContent),
                qrBlock,
                itemsHtml,
                money(order.getTotal())
        );

        return base("Thanh toán đơn #" + order.getId(), body);
    }

    public static String paymentSuccess(UserEntity user, OrderEntity order, String orderUrl) {
        String customerName = escape(user.getFullName() != null ? user.getFullName() : user.getUsername());
        String created = order.getOrderDate() != null ? order.getOrderDate().format(DT) : "";

        String itemsHtml = order.getOrderItems().stream()
                .map(oi -> """
                <tr>
                  <td style="padding:10px 0;border-bottom:1px solid #eef2ff;">
                    %s <span style="color:#6b7280;">× %d</span>
                  </td>
                </tr>
            """.formatted(escape(oi.getProduct().getName()), oi.getQuantity()))
                .reduce("", String::concat);

        String safeUrl = escape(orderUrl);

        String body = """
    <div style="font-size:18px;font-weight:900;line-height:24px;margin:0 0 8px;">Thanh toán thành công ✅</div>
    <div style="color:#6b7280;margin:0 0 14px;">
      Chào <b>%s</b>, bạn đã thanh toán thành công cho đơn <b>#%d</b> lúc <b>%s</b>.
    </div>

    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
           style="background:#f8fafc;border:1px solid #e5e7eb;border-radius:14px;">
      <tr>
        <td style="padding:14px;">
          <div style="font-weight:900;margin-bottom:10px;">Sản phẩm</div>
          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
            %s
          </table>

          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin-top:12px;">
            <tr>
              <td style="padding-top:10px;border-top:1px dashed #e5e7eb;color:#6b7280;">Tổng cuối cùng</td>
              <td align="right" style="padding-top:10px;border-top:1px dashed #e5e7eb;font-size:18px;font-weight:900;">
                %s
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>

    <div style="text-align:center;margin-top:16px;">
      <a href="%s"
         style="display:inline-block;background:#2563eb;color:#ffffff;text-decoration:none;
                padding:12px 18px;border-radius:12px;font-weight:900;">
        Xem đơn hàng
      </a>
      <div style="margin-top:10px;font-size:12px;color:#6b7280;line-height:18px;">
        Nếu nút không bấm được, mở link:
        <a href="%s" style="color:#2563eb;text-decoration:underline;">%s</a>
      </div>
    </div>
    """.formatted(
                customerName, order.getId(), escape(created),
                itemsHtml,
                money(order.getTotal()),
                safeUrl,
                safeUrl, safeUrl
        );

        return base("Thanh toán thành công đơn #" + order.getId(), body);
    }
}
