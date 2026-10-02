import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { CategoryService } from '../../../../../shared/data-access/category.service';
import { Category } from '../../../../../shared/models/cartegory.model';
import { AdminPageHeaderComponent } from '../../../shared/ui/page-header/page-header.component';
import { ConfirmService } from '../../../../../shared/services/confirm.service';
import { AdminToastService } from '../../../shared/services/admin-toast.service';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, AdminPageHeaderComponent],
  styleUrls: ['./category-list.component.css'],
  templateUrl: './category-list.component.html'
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  isLoading = true;
  selectedParentId: number | null = null;

  constructor(
    private categoryService: CategoryService,
    private router: Router,
    private toast: AdminToastService,
    private confirm: ConfirmService
  ) {}

  ngOnInit(): void {
    this.reload();
  }

  get rootCategories(): Category[] {
    return this.categories
      .filter(category => category.parentId == null)
      .sort((a, b) => a.name.localeCompare(b.name, 'vi'));
  }

  get selectedParent(): Category | null {
    if (this.selectedParentId == null) return null;
    return this.rootCategories.find(category => category.id === this.selectedParentId) ?? null;
  }

  get selectedChildren(): Category[] {
    if (this.selectedParentId == null) return [];
    return this.categories
      .filter(category => category.parentId === this.selectedParentId)
      .sort((a, b) => a.name.localeCompare(b.name, 'vi'));
  }

  childCount(parentId: number): number {
    return this.categories.filter(category => category.parentId === parentId).length;
  }

  reload(): void {
    this.isLoading = true;

    this.categoryService.getCategoriesForLayout()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: response => {
          const data = response?.data;
          this.categories = Array.isArray(data) ? data : [];

          const roots = this.rootCategories;
          const currentStillExists = roots.some(root => root.id === this.selectedParentId);

          if (!currentStillExists) {
            this.selectedParentId = roots[0]?.id ?? null;
          }
        },
        error: err => {
          this.categories = [];
          this.selectedParentId = null;
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        }
      });
  }

  selectParent(category: Category): void {
    this.selectedParentId = category.id;
  }

  addRootCategory(): void {
    this.router.navigate(['/admin/categories/add']);
  }

  addChildCategory(): void {
    if (!this.selectedParent) return;

    this.router.navigate(['/admin/categories/add'], {
      queryParams: { parentId: this.selectedParent.id }
    });
  }

  trackById(_index: number, category: Category): number {
    return category.id;
  }

  editCategory(category: Category, event?: Event): void {
    event?.stopPropagation();
    this.router.navigate([`/admin/categories/edit/${category.id}`]);
  }

  async deleteCategory(category: Category, event?: Event): Promise<void> {
    event?.stopPropagation();

    const children = this.childCount(category.id);
    const message = children > 0
      ? `Danh mục "${category.name}" đang có ${children} danh mục cấp 2. Bạn vẫn muốn gửi yêu cầu xóa?`
      : `Xóa danh mục "${category.name}"?`;

    const ok = await this.confirm.confirm(message, 'Xác nhận');
    if (!ok) return;

    this.categoryService.deleteCategory(category.id).subscribe({
      next: () => {
        this.toast.success('Đã xóa danh mục.');
        this.reload();
      },
      error: err => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền xóa danh mục này.');
        } else {
          this.toast.error(err?.error?.message ?? 'Không thể xóa danh mục.');
        }
      }
    });
  }
}
