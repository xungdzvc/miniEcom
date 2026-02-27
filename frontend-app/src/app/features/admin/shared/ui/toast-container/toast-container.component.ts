import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AdminToastService } from '../../services/admin-toast.service';

@Component({
  selector: 'app-admin-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.css'],
})
export class AdminToastContainerComponent {
  constructor(public toast: AdminToastService) {}

  trackById(_: number, t: { id: string }) {
    return t.id;
  }
}
