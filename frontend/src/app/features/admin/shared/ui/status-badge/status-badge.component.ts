import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  styleUrls: ['./status-badge.component.css'],
  template: `
    <span
      class="status-badge"
      [class.status-badge-on]="on"
      [class.status-badge-off]="!on"
    >
      <span class="status-dot" aria-hidden="true"></span>
      {{ on ? onText : offText }}
    </span>
  `
})
export class StatusBadgeComponent {
  @Input({ required: true }) on!: boolean;
  @Input() onText = 'Hoạt động';
  @Input() offText = 'Ngừng';
}
