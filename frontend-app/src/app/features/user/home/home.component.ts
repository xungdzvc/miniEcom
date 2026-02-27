import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCardComponent } from '../../../shared/ui/product-card/product-card.component';
import { ProductViewerListDetail } from '../../../shared/models/core/product/product-viewer-list.model';
import { ProductService } from '../../../shared/data-access/products/product.service';
import { finalize } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import { StepsComponent } from '../../../shared/steps/steps.component';
import { FaqComponent } from '../../../shared/faq/faq.component';
import { CtaComponent } from '../../../shared/cta/cta.component';
type SortKey = 'newest' | 'views'   | 'sales' | 'free';

@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [CommonModule, ProductCardComponent, RouterLink,StepsComponent,FaqComponent,CtaComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit{
  sort: SortKey = 'newest';
  activeCategory: string | null = null;
  isLoading = true;
  products: ProductViewerListDetail[] = [];
  constructor(private productService : ProductService, private router : Router){}

  ngOnInit(): void {
    if(!this.isLoading) return;
    this.loadProduct();
       
  }

  loadProduct(){
    this.productService.getAllProductsForViewer().pipe(
        finalize(()=> this.isLoading = false)
       ).subscribe({
        next : res =>{
          this.products = res.data;
        },
        error: err => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        }
       })
  }
  get categories(): string[] {
    const set = new Set(this.products.map(p => p.categoryName));
    return ['Tất cả', ...Array.from(set)];
  }

  setCategory(cat: string) {
    this.activeCategory = cat === 'Tất cả' ? null : cat;
  }

  setSort(k: SortKey) {
    this.sort = k;
  }

  get featured(): ProductViewerListDetail[] {
    let list = [...this.products];

    if (this.activeCategory) list = list.filter(p => p.categoryName === this.activeCategory);

    if (this.sort === 'free') list = list.filter(p => p.price === 0 || (p.discount === 100));
    if (this.sort === 'newest') list.sort((a, b) => b.id - a.id); // UI-only
    if (this.sort === 'views') list.sort((a, b) => (b.viewCount ?? 0) - (a.viewCount ?? 0));
    if (this.sort === 'sales') list.sort((a, b) => (b.saleCount ?? 0) - (a.saleCount ?? 0));

    return list.slice(0, 8);
  }
}
