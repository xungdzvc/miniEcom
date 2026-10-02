import { environment } from '../../../environments/environment';

/**
 * Giữ nguyên public path do backend trả về.
 *
 * Ví dụ:
 * - /uploads/products/abc.png -> http://localhost:8081/uploads/products/abc.png (local)
 * - /api/files/product/abc.png -> http://localhost:8081/api/files/product/abc.png
 * - https://... -> giữ nguyên
 * - blob:/data: -> giữ nguyên
 *
 * Frontend không tự đổi /uploads/products thành /api/files để tránh làm sai
 * contract URL đang hoạt động của backend.
 */
export function resolveFileUrl(path?: string | null): string {
  if (!path) return '/product-placeholder.svg';

  const raw = path.trim().replace(/\\/g, '/');

  if (/^(https?:|data:|blob:)/i.test(raw)) {
    return raw;
  }

  const normalized = raw.startsWith('/') ? raw : `/${raw}`;
  const base = (environment.fileBaseUrl ?? '').replace(/\/$/, '');

  return base ? `${base}${normalized}` : normalized;
}
