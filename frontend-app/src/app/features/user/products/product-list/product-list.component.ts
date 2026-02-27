import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCardComponent } from '../../../../shared/ui/product-card/product-card.component';
import { ProductViewerListDetail } from '../../../../shared/models/core/product/product-viewer-list.model';
import { environment } from '../../../../enviroments/environment';
import { ProductService } from '../../../../shared/data-access/products/product.service';
import { finalize } from 'rxjs';
import { Router } from '@angular/router';
type SortKey = 'newest' | 'views' | 'sales' | 'free';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent {
  constructor(private productService : ProductService,
    private router :Router
  ){}
  sort: SortKey = 'newest';
  activeCategory: string | null = null;
  fileBaseUrl = environment.fileBaseUrl;
  products: ProductViewerListDetail[] = []
  isLoading = true;
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

  get filtered(): ProductViewerListDetail[] {
    let list = [...this.products];
    if (this.activeCategory) list = list.filter(p => p.categoryName === this.activeCategory);
    if (this.sort === 'free') list = list.filter(p => p.price === 0 || (p.discount === 100));
    if (this.sort === 'newest') list.sort((a, b) => b.id - a.id);
    if (this.sort === 'views') list.sort((a, b) => (b.viewCount ?? 0) - (a.viewCount ?? 0));
    if (this.sort === 'sales') list.sort((a, b) => (b.saleCount ?? 0) - (a.saleCount ?? 0));

    return list;
  }
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
}
