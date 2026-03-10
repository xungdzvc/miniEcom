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
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, AdminPageHeaderComponent, AdminTableComponent, StatusBadgeComponent,  FormsModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserAdminListComponent implements OnInit {
  isLoading = true;
  page = 1;
  pageSize = 5;
  q ='';
  users : UserAdminResponse[] = [];
  constructor(
    private router: Router,
    private userAdminService: UserAdminService,
    private toast: AdminToastService,
    private confirm: ConfirmService,
    private userService: UserAdminService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }
    onSearchChange() {
  this.page = 1;
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

  makeStaff(user: any) {

  this.userService.makeStaff(user.id, true).subscribe({
    next: () => {
      user.role = 'ROLE_STAFF';
      this.toast.success("Đã bổ nhiệm staff");
    },
    error: () => {
      this.toast.error("Không thể bổ nhiệm staff");
    }
  });

}


removeStaff(user: any) {

  this.userService.removeStaff(user.id, false).subscribe({
    next: () => {
      user.role = 'ROLE_USER';
      this.toast.success("Đã gỡ staff");
    },
    error: () => {
      this.toast.error("Không thể gỡ staff");
    }
  });

}
  
  get pagedUsers() {
  
    const start = (this.page - 1) * this.pageSize;
    const end = start + this.pageSize;
  
    return this.filteredUsers.slice(start, end);
  }
  get filteredUsers() {
  const q = (this.q || '').trim().toLowerCase();
  if (!q) return this.users;

  return this.users.filter(user => {
    const id = String(user.id ?? '');
    const username = (user.username ?? '').toLowerCase();
    const email = (user.email ?? '').toLowerCase();
    return id.includes(q) || username.includes(q) || email.includes(q);
  });
}
  get totalPages() {
    return Math.ceil(this.users.length / this.pageSize);
  }
  nextPage() {
    if (this.page < this.totalPages) {
      this.page++;
    }
  }
  
  prevPage() {
    if (this.page > 1) {
      this.page--;
    }
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