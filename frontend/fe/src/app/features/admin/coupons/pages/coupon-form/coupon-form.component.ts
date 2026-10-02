import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';

import { CouponAdminService } from '../../../../../shared/data-access/coupon-admin.service';
import { CouponAdminRequest } from '../../../../../shared/models/coupon-admin.model';

import { AdminPageHeaderComponent } from '../../../shared/ui/page-header/page-header.component';
import { AdminToastService } from '../../../shared/services/admin-toast.service';

@Component({
  selector: 'app-coupon-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AdminPageHeaderComponent],
  templateUrl: './coupon-form.component.html',
  styleUrls: ['./coupon-form.component.css']
})
export class CouponFormComponent implements OnInit {
  form!: FormGroup;
  isLoading = false;
  isSubmitting = false;
  isEditMode = false;
  couponId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private couponAdminService: CouponAdminService,
    private toast: AdminToastService
  ) {}

  ngOnInit(): void {
    this.buildForm();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.couponId = +id;
      this.loadCoupon(this.couponId);
    }
  }

  buildForm(): void {
    this.form = this.fb.group({
      couponCode: ['', [Validators.required, Validators.maxLength(100)]],
      discount: [0, [Validators.required, Validators.min(1), Validators.max(100)]],
      createdAt: [''],
      updatedAt: ['']
    });
  }

  loadCoupon(id: number): void {
    this.isLoading = true;

    this.couponAdminService.getCouponById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          const coupon = res.data;
          this.form.patchValue({
            couponCode: coupon.couponCode,
            discount: coupon.discount,
            createdAt: this.toDatetimeLocal(coupon.createdAt),
            updatedAt: this.toDatetimeLocal(coupon.updatedAt)
          });
        },
        error: (err) => {
          console.error(err);
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        }
      });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const nowIso = new Date().toISOString();

    const payload: CouponAdminRequest = {
      couponCode: (this.form.value.couponCode || '').trim(),
      discount: Number(this.form.value.discount),
      createdAt: this.isEditMode
        ? this.toIsoString(this.form.value.createdAt)
        : nowIso,
      updatedAt: nowIso
    };

    this.isSubmitting = true;
    const request$ = this.isEditMode && this.couponId
      ? this.couponAdminService.updateCoupon(this.couponId, payload)
      : this.couponAdminService.createCoupon(payload);

    

    request$
      .pipe(finalize(() => (this.isSubmitting = false)))
      .subscribe({
        next: () => {
          this.toast.success(
            this.isEditMode
              ? 'Cập nhật coupon thành công.'
              : 'Tạo coupon thành công.'
          );
          this.router.navigate(['/admin/coupons']);
        },
        error: (err) => {
          console.error(err);
          if (err?.status === 400) {
            this.toast.warning('Dữ liệu coupon không hợp lệ.');
          } else if (err?.status === 409) {
            this.toast.warning('Mã coupon đã tồn tại.');
          } else {
            this.toast.error(
              this.isEditMode
                ? 'Không thể cập nhật coupon.'
                : 'Không thể tạo coupon.'
            );
          }
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/admin/coupons']);
  }

  private toDatetimeLocal(value: string | null): string {
    if (!value) return '';
    const date = new Date(value);
    const offset = date.getTimezoneOffset();
    const local = new Date(date.getTime() - offset * 60000);
    return local.toISOString().slice(0, 16);
  }

  private toIsoString(value: string | null): string | null {
    if (!value) return null;
    return new Date(value).toISOString();
  }
}