import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-page-header',
  standalone: true,
  imports: [CommonModule],
  styleUrls: ['./page-header.component.css'],
  template: `
    <div class="admin-page-header">
      <div class="title">
        <h2 class="admin-page-title">{{ title }}</h2>
        <p *ngIf="subtitle" class="admin-page-subtitle">{{ subtitle }}</p>
      </div>

      <div class="actions">
        <ng-content></ng-content>
      </div>
    </div>
  `
})
export class AdminPageHeaderComponent {
  @Input() title = '';
  @Input() subtitle = '';
}
