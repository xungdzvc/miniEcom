import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AdminStatCardComponent } from '../shared/ui/admin-stat-card/admin-stat-card.component';
import { DashboardStateService } from '../../../core/services/dashboard-state.service';
import { DashboardSummaryComponent } from '../../../shared/models/dashboard-summary.model';
import { finalize } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, AdminStatCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  isLoading = true;

  constructor(
    private dashboardService : DashboardStateService,
    private router : Router){}
  // TODO: bạn sẽ nối backend thật sau
    summary: DashboardSummaryComponent ={
      productsCount : 0,
      categoriesCount : 0,
      usersCount : 0,
      totalProductAcitve : 0,
      totalProductInActive : 0,
      totalClickToWebsite : 0,
      
    };

  ngOnInit(): void {
    
    this.loadHome();
    
  }

  loadHome(){
    this.isLoading = true;
    this.dashboardService.getDashboardSummary()
        .pipe(finalize(()=>this.isLoading = false))
        .subscribe({
          next : res =>{
            this.summary = res.data ?? this.summary;
          },
          error: err => {
            const code = err?.status ?? 500;
            this.router.navigate(['/error', code])
          
        }
          
        });
    
  }
  

  formatNumber(value?: number): string {
  return value != null ? value.toLocaleString() : '0';
  }
}
