import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-admin-page-header',
  standalone: true,
  imports: [CommonModule],
  styleUrls: ['./page-header.component.css'],
  template: `
    <header class="admin-page-header">
      <div class="admin-page-copy">
        <span class="admin-page-eyebrow">Quản trị hệ thống</span>
        <h1 class="admin-page-title">{{ title }}</h1>
        <p *ngIf="subtitle" class="admin-page-subtitle">{{ subtitle }}</p>
      </div>

      <div class="admin-page-actions">
        <ng-content></ng-content>
      </div>
    </header>
  `
})
export class AdminPageHeaderComponent {
  @Input() title = '';
  @Input() subtitle = '';
}
