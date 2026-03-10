import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

interface ErrorData {
  title: string;
  message: string;
  icon: string;
  color: string;
}

interface MousePosition {
  x: number;
  y: number;
}

@Component({
  selector: 'app-elite-error-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.css']
})
export class ErrorPageComponent implements OnInit, OnDestroy {
  code: number = 404;
  mounted: boolean = false;
  mousePos: MousePosition = { x: 0, y: 0 };
  orbs: number[] = Array(8).fill(0);
  errorData: ErrorData = {
    title: 'Không tìm thấy trang',
    message: 'Đường dẫn bạn truy cập không tồn tại hoặc đã bị xóa.',
    icon: '🔍',
    color: '#00d4ff'
  };

  private mountTimeout?: number;

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Get error code from route params
    this.route.paramMap.subscribe(params => {
      const codeParam = params.get('code');
      if (codeParam) {
        this.code = Number(codeParam);
        this.updateErrorData();
      }
    });

    // Trigger mount animation
    this.mountTimeout = window.setTimeout(() => {
      this.mounted = true;
    }, 100);
  }

  ngOnDestroy(): void {
    if (this.mountTimeout) {
      window.clearTimeout(this.mountTimeout);
    }
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent): void {
    const rect = document.documentElement.getBoundingClientRect();
    const x = (event.clientX - rect.width / 2) / rect.width;
    const y = (event.clientY - rect.height / 2) / rect.height;
    this.mousePos = { x, y };
  }

  private updateErrorData(): void {
    const errorDataMap: { [key: number]: ErrorData } = {
      403: {
        title: 'Truy cập bị từ chối',
        message: 'Rất tiếc, bạn không có quyền truy cập vào khu vực này.',
        icon: '🔒',
        color: '#ff3366'
      },
      404: {
        title: 'Không tìm thấy trang',
        message: 'Đường dẫn bạn truy cập không tồn tại hoặc đã bị xóa.',
        icon: '🔍',
        color: '#00d4ff'
      },
      500: {
        title: 'Lỗi máy chủ',
        message: 'Hệ thống đang gặp sự cố. Vui lòng thử lại sau.',
        icon: '⚡',
        color: '#ffd700'
      }
    };

    this.errorData = errorDataMap[this.code] || {
      title: 'Đã có lỗi xảy ra',
      message: 'Vui lòng kiểm tra lại đường truyền hoặc liên hệ quản trị viên.',
      icon: '⚠️',
      color: '#ff6b6b'
    };
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }
}
