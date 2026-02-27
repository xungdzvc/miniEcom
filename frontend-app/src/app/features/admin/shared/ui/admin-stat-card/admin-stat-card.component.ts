import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-admin-stat-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-stat-card.component.html',
  styleUrls: ['./admin-stat-card.component.css'],
})
export class AdminStatCardComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) value!: string | number;

  // Optional
  @Input() subtitle?: string;
  @Input() icon?: string;   // emoji hoặc text
  @Input() trend?: 'up' | 'down' | 'neutral';
  @Input() hint?: string;

  get trendClass(): string {
    if (this.trend === 'up') return 'trend-up';
    if (this.trend === 'down') return 'trend-down';
    return 'trend-neutral';
  }
}
