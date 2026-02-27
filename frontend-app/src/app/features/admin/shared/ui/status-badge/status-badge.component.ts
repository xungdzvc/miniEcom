import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span
      class="admin-badge"
      [class.admin-badge-success]="on"
      [class.admin-badge-danger]="!on"
    >
      {{ on ? onText : offText }}
    </span>
  `
})
export class StatusBadgeComponent {
  @Input({ required: true }) on!: boolean;
  @Input() onText = 'Hoạt động';
  @Input() offText = 'Ngừng';
}
