import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { distinctUntilChanged, finalize, map } from 'rxjs';

import { ProductCardComponent } from '../../../../shared/ui/product-card/product-card.component';
import { ProductViewerListDetail } from '../../../../shared/models/core/product/product-viewer-list.model';
import { ProductService } from '../../../../shared/data-access/products/product.service';
import { CategoryService } from '../../../../shared/data-access/category.service';
import { Category } from '../../../../shared/models/cartegory.model';

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
    private route: ActivatedRoute,
    private categoryService: CategoryService
  ) {}


  // data
  products: any[] = [];
  productsView: any[] = [];
  isLoading = true;

  // filter/sort
  sort: SortKey = 'newest';
  categories: Category[] = [];
  activeCategoryId: number | null = null;

  // pagination
  pageSize = 8;
  currentPage = 1;
  totalPages = 1;
  totalElements = 0;

  // -----------------------------
  // Derived UI data
  // -----------------------------

  private sortPinnedFirst(list: any[] = []) {
    return [...list].sort((a, b) => Number(!!b.pin) - Number(!!a.pin));
  }
  get filtered(): ProductViewerListDetail[] {
  let list = [...(this.products ?? [])];

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

  get totalPagesSafe(): number {
    return Math.max(1, this.totalPages || 1);
  }

  get pagedProducts(): ProductViewerListDetail[] {
    return this.filtered;
  }

  get pagedProductsSafe(): ProductViewerListDetail[] {
    return this.pagedProducts ?? [];
  }
  trackById = (_: number, item: any) => item.id;

  get pages(): number[] {
    return Array.from({ length: this.totalPagesSafe }, (_, i) => i + 1);
  }

  // -----------------------------
  // Actions
  // -----------------------------

  private resetPage() {
    this.currentPage = 1;
  }

  setCategory(categoryId: number | null) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { categoryId: categoryId ?? null },
      queryParamsHandling: 'merge'
    });
  }

  setSort(k: SortKey) {
    this.sort = k;
    this.resetPage();
    this.loadProduct();
  }

  goToPage(page: number) {
    const safe = Math.min(Math.max(page, 1), this.totalPagesSafe);
    if (safe === this.currentPage) return;
    this.currentPage = safe;
    this.loadProduct();
  }

  // -----------------------------
  // Lifecycle
  // -----------------------------

  ngOnInit(): void {
    this.loadCategories();

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


  private loadCategories(): void {
    this.categoryService.getCategoriesForLayout().subscribe({
      next: res => {
        this.categories = res?.data ?? [];
      },
      error: () => {
        this.categories = [];
      }
    });
  }

  loadProduct(): void {
    this.isLoading = true;

    const req$ = this.activeCategoryId
      ? this.productService.getProductByCategoryId(this.activeCategoryId, this.currentPage - 1, this.pageSize)
      : this.productService.getAllProductsForViewer(this.currentPage - 1, this.pageSize);

    req$
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: res => {
          this.products = res?.content ?? [];
          this.totalPages = Math.max(1, Number(res?.totalPages ?? 1));
          this.totalElements = Number(res?.totalElements ?? this.products.length ?? 0);
          this.currentPage = Number(res?.number ?? this.currentPage - 1) + 1;
        },
        error: err => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        },
      });
  }
}