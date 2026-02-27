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

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, AdminPageHeaderComponent, AdminTableComponent, StatusBadgeComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent implements OnInit {

  readonly fileBaseUrl = 'http://localhost:8080/files/';

  products: ProductAdminList[] = [];
  isLoading = true;

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

  goAddProduct() {
    this.router.navigate(['/admin/products/add']);
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
    this.productService.updateProduct(product.id, { status: newStatus }).subscribe({
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
}
