import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-table',
  standalone: true,
  template: `
    <div class="admin-table-shell">
      <div class="admin-table-container">
        <table class="admin-table">
          <ng-content></ng-content>
        </table>
      </div>
    </div>
  `
})
export class AdminTableComponent {}
