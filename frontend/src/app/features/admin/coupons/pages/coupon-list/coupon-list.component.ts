import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { CouponAdminService } from '../../../../../shared/data-access/coupon-admin.service';
import { CouponAdminResponse } from '../../../../../shared/models/coupon-admin.model';

import { AdminPageHeaderComponent } from '../../../shared/ui/page-header/page-header.component';
import { AdminTableComponent } from '../../../shared/ui/admin-table/admin-table.component';
import { AdminToastService } from '../../../shared/services/admin-toast.service';
import { ConfirmService } from '../../../../../shared/services/confirm.service';

@Component({
  selector: 'app-coupon-list',
  standalone: true,
  imports: [
    CommonModule,
    AdminPageHeaderComponent,
    AdminTableComponent
  ],
  templateUrl: './coupon-list.component.html',
  styleUrls: ['./coupon-list.component.css']
})
export class CouponListComponent implements OnInit {
  isLoading = true;
  page = 1;
  pageSize = 5;
  totalPages = 1;

  coupons: CouponAdminResponse[] = [];

  constructor(
    private router: Router,
    private couponAdminService: CouponAdminService,
    private toast: AdminToastService,
    private confirm: ConfirmService
  ) {}

  ngOnInit(): void {
    this.loadCoupons();
  }

  loadCoupons(): void {
    this.isLoading = true;

    this.couponAdminService.getCouponsPaged(this.page - 1, this.pageSize)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.coupons = res.content;
          this.totalPages = res.totalPages;
        },
        error: (err) => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        }
      });
  }

  goAddCoupon(): void {
    this.router.navigate(['/admin/coupons/add']);
  }

  editCoupon(couponId: number): void {
    this.router.navigate([`/admin/coupons/${couponId}`]);
  }

  async deleteCoupon(coupon: CouponAdminResponse): Promise<void> {
    const ok = await this.confirm.confirm(
      `Bạn chắc chắn muốn xóa coupon "${coupon.couponCode}"?`,
      'Xác nhận xóa'
    );

    if (!ok) return;

    this.couponAdminService.deleteCoupon(coupon.id).subscribe({
      next: () => {
        this.toast.success(`Đã xóa coupon "${coupon.couponCode}".`);
        this.loadCoupons();
      },
      error: (err) => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền thực hiện thao tác này.');
        } else {
          this.toast.error('Không thể xóa coupon.');
        }
      }
    });
  }

  prevPage(): void {
    if (this.page > 1) {
      this.page--;
      this.loadCoupons();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages) {
      this.page++;
      this.loadCoupons();
    }
  }
}
