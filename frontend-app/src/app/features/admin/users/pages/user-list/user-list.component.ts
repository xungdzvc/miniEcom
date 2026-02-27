import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { UserAdminService } from "../../../../../shared/data-access/user-admin.service";
import { UserAdminResponse } from "../../../../../shared/models/core/user/user-admin-list.model";
import { finalize } from "rxjs";
import { AdminPageHeaderComponent } from '../../../shared/ui/page-header/page-header.component';
import { AdminTableComponent } from '../../../shared/ui/admin-table/admin-table.component';
import { StatusBadgeComponent } from '../../../shared/ui/status-badge/status-badge.component';
import { AdminToastService } from '../../../shared/services/admin-toast.service';
import { ConfirmService } from '../../../../../shared/services/confirm.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, AdminPageHeaderComponent, AdminTableComponent, StatusBadgeComponent],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserAdminListComponent implements OnInit {
  isLoading = true;
  users : UserAdminResponse[] = [];
  constructor(
    private router: Router,
    private userAdminService: UserAdminService,
    private toast: AdminToastService,
    private confirm: ConfirmService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }
  
  loadUsers(){
    this.isLoading = true;
    this.userAdminService.getUsers().pipe(
        finalize(() =>this.isLoading = false)
        
    )
    .subscribe({
        next: res =>{this.users = res.data;},
        error : err =>{
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        
        }
    })
  }

  editUser(userId : number){
    this.router.navigate([`/admin/users/${userId}`]);
  }
  goAddUser(){
    this.router.navigate(['/admin/users/add']);
  }
  async changeStatus(user : UserAdminResponse){
    const nextStatus = !user.status;
    const actionText = nextStatus ? 'mở khóa' : 'khóa';

    const ok = await this.confirm.confirm(
      `Bạn chắc chắn muốn ${actionText} người dùng "${user.username}"?`,
      'Xác nhận'
    );
    if (!ok) return;

    this.userAdminService.changeStatus(user.id, nextStatus).subscribe({
        next :() =>{
            user.status = nextStatus;
            this.toast.success(`Đã ${actionText} người dùng "${user.username}".`);
        },
        error:(err) =>{
            console.error(err);
            if (err?.status === 403) {
              this.toast.warning('Bạn không có quyền thực hiện thao tác này.');
            } else {
              this.toast.error(`Không thể ${actionText} người dùng.`);
            }
        }
    })
  }
}