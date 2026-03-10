import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, of } from 'rxjs';

import { CategoryService } from '../../../../../shared/data-access/category.service';
import { NotificationService } from '../../../../../core/services/notification.service';

import { FormLayoutComponent } from '../../../shared/form/form-layout/form-layout.component';
import { FormFieldComponent } from '../../../shared/form/form-field/form-field.component';
import { FormActionsComponent } from '../../../shared/form/form-actions/form-actions.component';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormLayoutComponent,
    FormFieldComponent,
    FormActionsComponent,
  ],
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css'],
})
export class CategoryFormComponent implements OnInit {
  form!: FormGroup;

  isEdit = false;
  categoryId: number | null = null;

  // để show UI loading nếu muốn
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private categoryService: CategoryService,
    private notify: NotificationService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
    });

    // Nếu có :id -> edit, không có -> add
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const idParam = params.get('id');
          this.isEdit = !!idParam;
          this.categoryId = idParam ? Number(idParam) : null;

          if (!this.isEdit) return of(null);

          this.isLoading = true;
          return this.categoryService.getCategoryById(this.categoryId!);
        })
      )
      .subscribe({
        next: (data: any) => {
          this.isLoading = false;
          if (!data) return;

          // tùy API bạn trả về data hay trực tiếp category
          const category = data?.data ?? data;

          this.form.patchValue({
            name: category.name,
          });

          // reset pristine để nút lưu không active ngay từ đầu (nếu bạn dùng dirty)
          this.form.markAsPristine();
        },
        error: () => {
          this.isLoading = false;
          this.notify.error('Không tải được dữ liệu danh mục.');
          this.router.navigate(['/admin/categories']);
        },
      });
  }

  cancel() {
    this.router.navigate(['/admin/categories']);
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = { name: this.form.value.name };

    this.isLoading = true;

    const req$ = this.isEdit
      ? this.categoryService.updateCategory(this.categoryId!, payload)
      : this.categoryService.addCategory(payload);

    req$.subscribe({
      next: () => {
        this.isLoading = false;

        if (this.isEdit) {
          this.notify.success('Cập nhật danh mục thành công!');
          this.form.markAsPristine();
          // hoặc quay về list
          this.router.navigate(['/admin/categories']);
        } else {
          this.notify.success('Thêm danh mục thành công!');
          this.form.reset();
        }
      },
      error: () => {
        this.isLoading = false;
        this.notify.error(this.isEdit ? 'Lỗi khi cập nhật!' : 'Lỗi khi thêm!');
      },
    });
  }
}
