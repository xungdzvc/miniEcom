import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AdminStyleLoaderService {
  private readonly linkId = 'admin-styles-link';

  enable(): void {
    if (!document.getElementById(this.linkId)) {
      const link = document.createElement('link');
      link.id = this.linkId;
      link.rel = 'stylesheet';
      link.href = 'admin/admin-styles.css';
      document.head.appendChild(link);
    }
    document.body.classList.add('admin-scope');
  }

  disable(): void {
    document.getElementById(this.linkId)?.remove();
    document.body.classList.remove('admin-scope');
  }
}
