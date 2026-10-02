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
  allCategories: Category[] = [];
  currentCategory: Category | null = null;
  currentCategoryHasChildren = false;
  presetParentId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private categoryService: CategoryService,
    private notify: NotificationService
  ) {}

  ngOnInit(): void {
    this.presetParentId = this.readPresetParentId();

    this.form = this.fb.group({
      name: ['', Validators.required],
      parentId: [this.presetParentId],
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
          this.allCategories = Array.isArray(rootsPayload) ? rootsPayload : [];

          if (!category) {
            // Khi tạo mới: chỉ danh mục cấp 1 (parentId = null) được phép làm cha.
            this.rootCategories = this.allCategories.filter(
              (item) => item.parentId == null
            );

            const canUsePreset = this.presetParentId != null &&
              this.rootCategories.some(item => item.id === this.presetParentId);

            this.form.patchValue({ parentId: canUsePreset ? this.presetParentId : null });
            return;
          }

          const data: Category = category?.data ?? category;
          this.currentCategory = data;
          this.currentCategoryHasChildren = this.allCategories.some(
            (item) => item.parentId === data.id
          );

          // Dropdown cha chỉ chứa category cấp 1 và không bao giờ chứa chính category đang sửa.
          // Nếu category cấp 1 hiện tại đang có con, không cho chuyển nó xuống cấp 2 ở FE
          // để tránh vô tình tạo cấu trúc cấp 3.
          this.rootCategories = this.currentCategoryHasChildren
            ? []
            : this.allCategories.filter(
                (item) => item.parentId == null && item.id !== data.id
              );

          this.form.patchValue({
            name: data.name ?? '',
            parentId: data.parentId ?? null,
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



  private readPresetParentId(): number | null {
    const raw = this.route.snapshot.queryParamMap.get('parentId');
    if (raw == null || raw.trim() === '') return null;

    const value = Number(raw);
    return Number.isFinite(value) ? value : null;
  }

  get isCurrentCategoryLevel2(): boolean {
    return this.currentCategory?.parentId != null;
  }

  get emptyParentLabel(): string {
    if (!this.isEdit) {
      return '— Không có (tạo danh mục cấp 1) —';
    }

    if (this.isCurrentCategoryLevel2) {
      return '— Tách thành danh mục cấp 1 —';
    }

    return '— Giữ là danh mục cấp 1 —';
  }

  get parentHint(): string {
    if (this.currentCategoryHasChildren) {
      return 'Danh mục này đang có danh mục con nên được giữ ở cấp 1 để tránh tạo cấp 3.';
    }

    if (this.isCurrentCategoryLevel2) {
      return 'Chỉ có thể chuyển sang một danh mục cha cấp 1 khác hoặc tách thành danh mục cấp 1.';
    }

    return 'Chỉ danh mục cấp 1 (parentId = null) được hiển thị trong danh sách cha.';
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
