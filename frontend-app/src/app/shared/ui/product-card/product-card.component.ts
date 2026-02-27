import { Component, Input } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ProductViewerListDetail } from '../../models/core/product/product-viewer-list.model'; 
import { environment } from '../../../enviroments/environment';
import { Router } from '@angular/router';
import { ProductService } from '../../data-access/products/product.service';
import { take } from 'rxjs/operators';
@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css'],
})
export class ProductCardComponent {
  @Input({ required: true }) product!: ProductViewerListDetail;
  @Input() ctaText: string = 'Xem chi tiết';
  fileBaseUrl = environment.fileBaseUrl;

  constructor(private router: Router,private productService : ProductService){}
  formatNumber(v?: number) {
    return (v ?? 0).toLocaleString();
  }

  
  viewDetail(slug : string){
    this.productService.updateViewProductBySlug(slug)
    .pipe(take(1))
    .subscribe({
    error: (e) => console.log('update view failed', e)
  });
    this.router.navigate(['/products', slug]);
  }
  hasDiscount(): boolean {
    if(!this.product) return false;
    return !!this.product.discount && this.product.discount > 1;
  }

  calcNewPrice(price?: number | null, discount?: number | null) {
  const p = Number(price ?? 0);
  const d = Number(discount ?? 0);
  return Math.max(0, Math.round(p * (1 - d / 100)));
  }

  calcSaveAmount(price?: number | null, discount?: number | null) {
    const p = Number(price ?? 0);
    const d = Number(discount ?? 0);
    return Math.max(0, Math.round(p * (d / 100)));
  }

  calcDiscountPercent(price?: number | null, discount?: number | null) {
    return Math.max(0, Math.round(Number(discount ?? 0)));
  }

}
