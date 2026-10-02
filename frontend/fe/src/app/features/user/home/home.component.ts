import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { ProductCardComponent } from '../../../shared/ui/product-card/product-card.component';
import { ProductViewerListDetail } from '../../../shared/models/core/product/product-viewer-list.model';
import { ProductService } from '../../../shared/data-access/products/product.service';

import { StepsComponent } from '../../../shared/steps/steps.component';
import { FaqComponent } from '../../../shared/faq/faq.component';
import { CtaComponent } from '../../../shared/cta/cta.component';
import { SupportLauncherComponent } from '../../../shared/support-launcher/support-launcher.component';
import { RevealOnScrollDirective } from '../../../shared/directives/reveal-on-scroll.directive';
type SortKey = 'newest' | 'views' | 'sales' | 'free';

@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [
    CommonModule,
    ProductCardComponent,
    RouterLink,
    StepsComponent,
    FaqComponent,
    CtaComponent,
    SupportLauncherComponent,
    RevealOnScrollDirective
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  constructor(private productService: ProductService, private router: Router) {}

  // data
  isLoading = true;
  products: ProductViewerListDetail[] = [];
  productsView: any[] = [];

  

  // filter/sort (nếu bạn muốn bật lại tabs)
  sort: SortKey = 'newest';
  activeCategory: string | null = null;

  // pagination
  pageSize = 8;
  currentPage = 1;
  totalPages = 1;
  totalElements = 0;

  ngOnInit(): void {
    this.loadProduct();
  }

  private resetPage() {
    this.currentPage = 1;
  }

  loadProduct() {
    this.isLoading = true;

    this.productService
      .getAllProductsForViewer(this.currentPage - 1, this.pageSize)
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

  // ----- categories (nếu bạn cần tabs)
  get categories(): string[] {
    const set = new Set((this.products ?? []).map(p => p.categoryName).filter(Boolean) as string[]);
    return ['Tất cả', ...Array.from(set)];
  }

  setCategory(cat: string) {
    this.activeCategory = cat === 'Tất cả' ? null : cat;
    this.resetPage();
    this.loadProduct();
  }

  setSort(k: SortKey) {
    this.sort = k;
    this.resetPage();
    this.loadProduct();
  }

  // ----- list sau filter/sort
  get featured(): ProductViewerListDetail[] {
  let list = [...(this.products ?? [])];

  // Lọc theo category tab
  if (this.activeCategory) {
    list = list.filter(p => p.categoryName === this.activeCategory);
  }

  // Filter Free (nếu chọn free)
  if (this.sort === 'free') {
    list = list.filter(p => (p.price ?? 0) === 0 || (p.discount ?? 0) === 100);
  }

  // ✅ Sort: PIN trước, rồi mới sort theo tiêu chí đang chọn
  list.sort((a: any, b: any) => {
    const pinDiff = Number(!!b.pin) - Number(!!a.pin);
    if (pinDiff !== 0) return pinDiff;

    switch (this.sort) {
      case 'newest':
        return (b.id ?? 0) - (a.id ?? 0);
      case 'views':
        return (b.viewCount ?? 0) - (a.viewCount ?? 0);
      case 'sales':
        return (b.saleCount ?? 0) - (a.saleCount ?? 0);
      case 'free':
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

  get pagedFeatured(): ProductViewerListDetail[] {
    return this.featured;
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPagesSafe }, (_, i) => i + 1);
  }

  goToPage(page: number) {
    const safe = Math.min(Math.max(page, 1), this.totalPagesSafe);
    if (safe === this.currentPage) return;
    this.currentPage = safe;
    this.loadProduct();
  }
}