import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { AdminStatCardComponent } from '../shared/ui/admin-stat-card/admin-stat-card.component';
import { DashboardStateService } from '../../../core/services/dashboard-state.service';

export interface DashboardSummaryComponent {
  totalProducts: number;
  activeProducts: number;
  inActiveProducts: number;
  totalCategories: number;
  totalUsers: number;

  monthRevenue: number;
  quarterRevenue: number;
  yearRevenue: number;

  newUsersToday: number;
  newUsersThisMonth: number;
}

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, AdminStatCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  isLoading = true;

  summary: DashboardSummaryComponent = {
    totalProducts: 0,
    activeProducts: 0,
    inActiveProducts: 0,
    totalCategories: 0,
    totalUsers: 0,
    monthRevenue: 0,
    quarterRevenue: 0,
    yearRevenue: 0,
    newUsersToday: 0,
    newUsersThisMonth: 0,
  };

  constructor(
    private dashboardService: DashboardStateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHome();
  }

  loadHome(): void {
    this.isLoading = true;

    this.dashboardService
      .getDashboardSummary()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.summary = res.data ?? this.summary;
        },
        error: (err) => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        },
      });
  }

  formatNumber(value?: number): string {
    return (value ?? 0).toLocaleString('vi-VN');
  }

  formatCurrency(value?: number): string {
    return (value ?? 0).toLocaleString('vi-VN') + ' đ';
  }

  get totalProductsSafe(): number {
    return this.summary.totalProducts || 0;
  }

  get activeRate(): number {
    if (!this.totalProductsSafe) return 0;
    return Math.round((this.summary.activeProducts / this.totalProductsSafe) * 100);
  }

  get inactiveRate(): number {
    if (!this.totalProductsSafe) return 0;
    return Math.round((this.summary.inActiveProducts / this.totalProductsSafe) * 100);
  }

  get revenueGrowthQuarterVsMonth(): number {
    const month = this.summary.monthRevenue || 0;
    const quarter = this.summary.quarterRevenue || 0;
    if (month <= 0) return 0;
    return ((quarter - month) / month) * 100;
  }

  get revenueGrowthYearVsQuarter(): number {
    const quarter = this.summary.quarterRevenue || 0;
    const year = this.summary.yearRevenue || 0;
    if (quarter <= 0) return 0;
    return ((year - quarter) / quarter) * 100;
  }

  get revenueGrowthQuarterVsMonthText(): string {
    return `${this.revenueGrowthQuarterVsMonth.toFixed(0)}%`;
  }

  get revenueGrowthYearVsQuarterText(): string {
    return `${this.revenueGrowthYearVsQuarter.toFixed(0)}%`;
  }

  get overviewCards() {
    return [
      {
        title: 'Tổng sản phẩm',
        icon: '📦',
        value: this.formatNumber(this.summary.totalProducts),
        subtitle: 'Tổng số sản phẩm trong hệ thống',
        trend: 'neutral' as const,
      },
      {
        title: 'Đang hoạt động',
        icon: '✅',
        value: this.formatNumber(this.summary.activeProducts),
        subtitle: `Chiếm ${this.activeRate}% tổng sản phẩm`,
        trend: 'up' as const,
      },
      {
        title: 'Ngừng hoạt động',
        icon: '⛔',
        value: this.formatNumber(this.summary.inActiveProducts),
        subtitle: `Chiếm ${this.inactiveRate}% tổng sản phẩm`,
        trend: 'down' as const,
      },
      {
        title: 'Danh mục',
        icon: '🗂️',
        value: this.formatNumber(this.summary.totalCategories),
        subtitle: 'Tổng số danh mục',
        trend: 'neutral' as const,
      },
      {
        title: 'Người dùng',
        icon: '👥',
        value: this.formatNumber(this.summary.totalUsers),
        subtitle: 'Tổng tài khoản đã đăng ký',
        trend: 'up' as const,
      },
    ];
  }

  get revenueCards() {
    return [
      {
        title: 'Doanh thu tháng',
        icon: '📅',
        value: this.formatCurrency(this.summary.monthRevenue),
        subtitle: 'Từ đầu tháng đến hiện tại',
        trend: 'up' as const,
      },
      {
        title: 'Doanh thu quý',
        icon: '📊',
        value: this.formatCurrency(this.summary.quarterRevenue),
        subtitle: `So với tháng: ${this.revenueGrowthQuarterVsMonthText}`,
        trend: 'up' as const,
      },
      {
        title: 'Doanh thu năm',
        icon: '💰',
        value: this.formatCurrency(this.summary.yearRevenue),
        subtitle: `So với quý: ${this.revenueGrowthYearVsQuarterText}`,
        trend: 'up' as const,
      },
    ];
  }

  get userCards() {
    return [
      {
        title: 'Đăng ký hôm nay',
        icon: '🆕',
        value: this.formatNumber(this.summary.newUsersToday),
        subtitle: 'Số người dùng mới trong ngày',
        trend: 'up' as const,
      },
      {
        title: 'Đăng ký tháng này',
        icon: '📈',
        value: this.formatNumber(this.summary.newUsersThisMonth),
        subtitle: 'Số người dùng mới từ đầu tháng',
        trend: 'up' as const,
      },
    ];
  }

  get revenueChartItems() {
    return [
      { label: 'Tháng', value: this.summary.monthRevenue || 0 },
      { label: 'Quý', value: this.summary.quarterRevenue || 0 },
      { label: 'Năm', value: this.summary.yearRevenue || 0 },
    ];
  }

  get maxRevenueValue(): number {
    return Math.max(...this.revenueChartItems.map((item) => item.value), 1);
  }

  calcBarWidth(value: number): string {
    return `${Math.max((value / this.maxRevenueValue) * 100, 8)}%`;
  }
}