import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { distinctUntilChanged, finalize, map } from 'rxjs';

import { ProductCardComponent } from '../../../../shared/ui/product-card/product-card.component';
import { ProductViewerListDetail } from '../../../../shared/models/core/product/product-viewer-list.model';
import { environment } from '../../../../../environments/environment';
import { ProductService } from '../../../../shared/data-access/products/product.service';

type SortKey = 'newest' | 'views' | 'sales' | 'free';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent {
  constructor(
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  fileBaseUrl = environment.fileBaseUrl;

  // data
  products: any[] = [];
  productsView: any[] = [];
  isLoading = true;

  // filter/sort
  sort: SortKey = 'newest';
  activeCategory: string | null = null;      // filter theo tên category (UI tabs)
  activeCategoryId: number | null = null;    // lấy từ query param categoryId

  // pagination
  pageSize = 8;        // ✅ 8 sản phẩm / trang
  currentPage = 1;

  // -----------------------------
  // Derived UI data
  // -----------------------------

  get categories(): string[] {
    // lấy danh mục từ dataset hiện tại
    const set = new Set((this.products ?? []).map(p => p.categoryName).filter(Boolean) as string[]);
    return ['Tất cả', ...Array.from(set)];
  }
  private sortPinnedFirst(list: any[] = []) {
    return [...list].sort((a, b) => Number(!!b.pin) - Number(!!a.pin));
  }
  get filtered(): ProductViewerListDetail[] {
  let list = [...(this.products ?? [])];

  // Lọc theo tab categoryName
  if (this.activeCategory) {
    list = list.filter(p => p.categoryName === this.activeCategory);
  }

  // Filter theo sort key "free"
  if (this.sort === 'free') {
    list = list.filter(p => (p.price ?? 0) === 0 || (p.discount ?? 0) === 100);
  }

  // ✅ PIN luôn ưu tiên đầu tiên (stable)
  list.sort((a: any, b: any) => {
    const pinDiff = Number(!!b.pin) - Number(!!a.pin);
    if (pinDiff !== 0) return pinDiff;

    // ✅ sau đó mới sort theo lựa chọn
    switch (this.sort) {
      case 'newest':
        return (b.id ?? 0) - (a.id ?? 0);
      case 'views':
        return (b.viewCount ?? 0) - (a.viewCount ?? 0);
      case 'sales':
        return (b.saleCount ?? 0) - (a.saleCount ?? 0);
      case 'free':
        // free đã lọc rồi → ưu tiên newest trong free
        return (b.id ?? 0) - (a.id ?? 0);
      default:
        return 0;
    }
  });

  return list;
}

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filtered.length / this.pageSize));
  }

  get pagedProducts(): ProductViewerListDetail[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filtered.slice(start, start + this.pageSize);
  }
  trackById = (_: number, item: any) => item.id;

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  // -----------------------------
  // Actions
  // -----------------------------

  private resetPage() {
    this.currentPage = 1;
  }

  setCategory(cat: string) {
    this.activeCategory = cat === 'Tất cả' ? null : cat;
    this.resetPage();
  }

  setSort(k: SortKey) {
    this.sort = k;
    this.resetPage();
  }

  goToPage(page: number) {
    const safe = Math.min(Math.max(page, 1), this.totalPages);
    this.currentPage = safe;
  }

  // -----------------------------
  // Lifecycle
  // -----------------------------

  ngOnInit(): void {
    // Theo dõi query param categoryId để load lại data
    this.route.queryParamMap
      .pipe(
        map(params => {
          const id = params.get('categoryId');
          return id ? Number(id) : null;
        }),
        distinctUntilChanged()
      )
      .subscribe(categoryId => {
        this.activeCategoryId = categoryId;
        this.isLoading = true;
        this.resetPage();
        this.loadProduct();
      });
  }

  loadProduct() {
    const req$ = this.activeCategoryId
      ? this.productService.getProductByCategoryId(this.activeCategoryId)
      : this.productService.getAllProductsForViewer();
    
    req$
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: res => {
          this.products = res?.data ?? [];
          this.resetPage();
        },
        error: err => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        },
      });
  }
}