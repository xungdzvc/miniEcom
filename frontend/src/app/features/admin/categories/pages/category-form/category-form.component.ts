import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { finalize, switchMap } from 'rxjs/operators';

import { CategoryService } from '../../../../../shared/data-access/category.service';
import { Category } from '../../../../../shared/models/cartegory.model';
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
  isLoading = false;
  rootCategories: Category[] = [];

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
      parentId: [null],
    });

    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const idParam = params.get('id');
          this.isEdit = !!idParam;
          this.categoryId = idParam ? Number(idParam) : null;
          this.isLoading = true;

          const roots$ = this.categoryService.getCategoriesForLayout();

          if (!this.isEdit || !this.categoryId) {
            return forkJoin({ roots: roots$, category: of(null) });
          }

          return forkJoin({
            roots: roots$,
            category: this.categoryService.getCategoryById(this.categoryId),
          });
        })
      )
      .subscribe({
        next: ({ roots, category }: any) => {
          this.isLoading = false;
          const rootsPayload = roots?.data ?? roots;
          const allCategories: Category[] = Array.isArray(rootsPayload) ? rootsPayload : [];

          // Chỉ danh mục cấp 1 mới có thể làm cha. Backend cũng kiểm tra tối đa 2 cấp.
          this.rootCategories = allCategories.filter((item) =>
            item?.id !== this.categoryId && (item?.parentId == null)
          );

          if (!category) return;

          const data = category?.data ?? category;
          this.form.patchValue({
            name: data?.name ?? '',
            parentId: data?.parentId ?? null,
          });
          this.form.markAsPristine();
        },
        error: () => {
          this.isLoading = false;
          this.notify.error('Không tải được dữ liệu danh mục.');
          this.router.navigate(['/admin/categories']);
        },
      });
  }


  trackByCategoryId(_index: number, category: Category): number {
    return category.id;
  }

  cancel(): void {
    this.router.navigate(['/admin/categories']);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const parentValue = this.form.value.parentId;
    const payload = {
      name: String(this.form.value.name ?? '').trim(),
      parentId: parentValue === '' || parentValue == null ? null : Number(parentValue),
    };

    this.isLoading = true;

    const req$ = this.isEdit
      ? this.categoryService.updateCategory(this.categoryId!, payload)
      : this.categoryService.addCategory(payload);

    req$.pipe(finalize(() => (this.isLoading = false))).subscribe({
      next: () => {
        this.notify.success(this.isEdit ? 'Cập nhật danh mục thành công!' : 'Thêm danh mục thành công!');
        this.router.navigate(['/admin/categories']);
      },
      error: (err) => {
        this.notify.error(err?.error?.message ?? (this.isEdit ? 'Lỗi khi cập nhật!' : 'Lỗi khi thêm!'));
      },
    });
  }
}
