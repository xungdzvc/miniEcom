import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdminToastContainerComponent } from '../shared/ui/toast-container/toast-container.component';
import { ThemeService } from '../../../core/services/theme.service';
import { AdminStyleLoaderService } from '../shared/services/admin-style-loader.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminToastContainerComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  sidebarOpen = true;
  theme: 'light' | 'dark' = 'light';

  constructor(
    private themeService: ThemeService,
    private adminStyles: AdminStyleLoaderService
  ) {
    const mode = this.themeService.getMode();
    this.themeService.apply(mode);
    this.themeService.watchSystemChanges(true);
    this.theme = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
  }

  ngOnInit(): void {
    this.adminStyles.enable();
  }

  ngOnDestroy(): void {
    this.adminStyles.disable();
  }

  toggleSidebar() { this.sidebarOpen = !this.sidebarOpen; }

  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';
    this.themeService.setMode(this.theme);
  }
}
