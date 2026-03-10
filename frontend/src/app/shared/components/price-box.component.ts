import { Component, Input } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-price-box',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './price-box.component.html',
  styleUrls: ['./price-box.component.css'],
})
export class PriceBoxComponent {
  @Input() price: number | null | undefined = 0;
  @Input() discount: number | null | undefined = 0;

  // option
  @Input() showBadge = true; // hiện -xx%
  @Input() showHint = true;  // hiện "Giá tốt nhất hiện tại" nếu không giảm

  hasDiscount(): boolean {
    const d = Number(this.discount ?? 0);
    return d > 1;
  }

  calcNewPrice(): number {
    const p = Number(this.price ?? 0);
    const d = Number(this.discount ?? 0);
    return Math.max(0, Math.round(p * (1 - d / 100)));
  }

  calcSaveAmount(): number {
    const p = Number(this.price ?? 0);
    const d = Number(this.discount ?? 0);
    return Math.max(0, Math.round(p * (d / 100)));
  }

  calcDiscountPercent(): number {
    return Math.max(0, Math.round(Number(this.discount ?? 0)));
  }
}
