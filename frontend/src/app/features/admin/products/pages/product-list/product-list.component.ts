import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { AdminPageHeaderComponent } from '../../../shared/ui/page-header/page-header.component';
import { AdminTableComponent } from '../../../shared/ui/admin-table/admin-table.component';
import { StatusBadgeComponent } from '../../../shared/ui/status-badge/status-badge.component';
import { ConfirmService } from '../../../../../shared/services/confirm.service';
import { AdminToastService } from '../../../shared/services/admin-toast.service';

import { ProductAdminList } from '../../../../../shared/models/core/product/product-admin-list.model';
import { ProductAdminService } from '../../../../../shared/data-access/products/admin-product.service';
import { environment } from '../../../../../../../enviroments/environment';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, AdminPageHeaderComponent, AdminTableComponent, StatusBadgeComponent,FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent implements OnInit {

  readonly fileBaseUrl = environment.fileBaseUrl;

  products: ProductAdminList[] = [];
  isLoading = true;
  page = 1;
  pageSize = 5;
  constructor(
    private productService: ProductAdminService,
    private router: Router,
    private toast: AdminToastService,
    private confirm: ConfirmService
  ) {}

  ngOnInit(): void {
    this.reload();
  }

  reload() {
    this.isLoading = true;

    this.productService.getAllProducts()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: res =>{this.products = res.data;},
        
        error: err => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        }
      });
  }
  q = '';

get filteredProducts() {
  const q = (this.q || '').trim().toLowerCase();
  if (!q) return this.products;

  return this.products.filter(p => {
    const id = String(p.id ?? '');
    const name = (p.name ?? '').toLowerCase();
    const cate = (p.categoryName ?? '').toLowerCase();
    return id.includes(q) || name.includes(q) || cate.includes(q);
  });
}
  goAddProduct() {
    this.router.navigate(['/admin/products/add']);
  }
  
  get totalPages() {
  return Math.max(1, Math.ceil(this.filteredProducts.length / this.pageSize));
}
onSearchChange() {
  this.page = 1;
}
get pagedProducts() {
  const start = (this.page - 1) * this.pageSize;
  return this.filteredProducts.slice(start, start + this.pageSize);
}
  nextPage() {
    if (this.page < this.totalPages) {
      this.page++;
    }
  }

  prevPage() {
    if (this.page > 1) {
      this.page--;
    }
  }
  reloadElastic() {
    this.isLoading = true;

    this.productService.rebuildElastic()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: res => {
          this.toast.success(res ?? 'Đã rebuild elastic thành công.');
        },
        error: err => {
          this.toast.error('Không thể rebuild elastic.');
        }
      });
  }

  editProduct(productId : number) {
    this.router.navigate([`/admin/products/${productId}`]);
  }

  async deleteProduct(product: ProductAdminList) {
    const ok = await this.confirm.confirm(
      `Bạn chắc chắn muốn xóa sản phẩm "${product.name}"?`,
      'Xác nhận'
    );
    if (!ok) return;

    this.productService.deleteProduct(product.id).subscribe({
      next: () => {
        this.products = this.products.filter(p => p.id !== product.id);
        this.toast.success('Đã xóa sản phẩm.');
      },
      error: err => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền xóa sản phẩm này.');
        } else {
          this.toast.error('Không thể xóa sản phẩm.');
        }
      }
    });
  }
  async changeStatus(product: ProductAdminList) {
    const newStatus = !product.status;
    const action = product.status === true  ? 'Ngừng kinh doanh' : 'mở bán';

    const ok = await this.confirm.confirm(
      `Bạn chắc chắn muốn ${action} sản phẩm "${product.name}"?`,
      'Xác nhận'
    );
    if (!ok) return;
    this.productService.changeStatus(product.id, { status: newStatus }).subscribe({
      next: () => {
        this.toast.success(`Đã ${action} sản phẩm "${product.name}".`);
        this.reload();
      },
      error: err => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền xóa sản phẩm này.');
        } else {
          this.toast.error('Không thể xóa sản phẩm.');
        }
      }
    });
  }

  async pin(product: ProductAdminList) {
    const newStatus = !product.pin;
    const action = product.pin === true  ? 'Bỏ ghim' : 'Ghim';

    const ok = await this.confirm.confirm(
      `Bạn chắc chắn muốn ${action} sản phẩm "${product.name}"?`,
      'Xác nhận'
    );
    if (!ok) return;
    this.productService.pin(product.id, { status: newStatus }).subscribe({
      next: () => {
        this.toast.success(`Đã ${action} sản phẩm "${product.name}".`);
        this.reload();
      },
      error: err => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền thực hiện hành động này.');
        } else {
          this.toast.error('Không thể thực hiện hành động này.');
        }
      }
    });
  }
}
