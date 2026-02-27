import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { CategoryService } from '../../../../../shared/data-access/category.service';
import { Category } from '../../../../../shared/models/cartegory.model';

import { AdminPageHeaderComponent } from '../../../shared/ui/page-header/page-header.component';
import { AdminTableComponent } from '../../../shared/ui/admin-table/admin-table.component';
import { ConfirmService } from '../../../../../shared/services/confirm.service';
import { AdminToastService } from '../../../shared/services/admin-toast.service';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, AdminPageHeaderComponent, AdminTableComponent],
  styleUrls: ['./category-list.component.css'],
  templateUrl : './category-list.component.html'
})
export class CategoryListComponent implements OnInit {

  public categories: Category[] = [];
  @Output() onAddCategory = new EventEmitter<void>();  // 👈 RẤT QUAN TRỌNG

  isLoading = true;

  constructor(
    private categoryService: CategoryService,
    private router : Router,
    private toast: AdminToastService,
    private confirm: ConfirmService 
  ) {}

  ngOnInit(): void {
    this.reload();
  }

  reload() {
  this.isLoading = true;

    this.categoryService.getAllCategories()
      .pipe(
        finalize(() => {
          this.isLoading = false; 
        })
      )
      .subscribe({
        next: data => {
          this.categories = data;
        },
        error: err => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
          
        }
      });
}


  addCategoryClick() {
      this.router.navigate(['/admin/categories/add']);

  }

  trackById(index: number, category: Category) {
  return category.id;
}
  editCategory(category: Category) {
    this.router.navigate([`/admin/categories/edit/${category.id}`]);
    
  }

  async deleteCategory(category: Category) {
    const ok = await this.confirm.confirm(
      `Xóa danh mục "${category.name}"?`,
      'Xác nhận'
    );
    if (!ok) return;

    this.categoryService.deleteCategory(category.id).subscribe({
      next: () => {
        this.categories = this.categories.filter(c => c.id !== category.id);
        this.toast.success('Đã xóa danh mục.');
      },
      error: err => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền xóa danh mục này.');
        } else {
          this.toast.error('Không thể xóa danh mục.');
        }
      }
    });
  }
}