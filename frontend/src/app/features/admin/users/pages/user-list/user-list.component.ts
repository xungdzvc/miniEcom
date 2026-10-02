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
  page = 1;
  pageSize = 5;
  totalPages = 1;
  users: UserAdminResponse[] = [];

  constructor(
    private router: Router,
    private userAdminService: UserAdminService,
    private toast: AdminToastService,
    private confirm: ConfirmService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userAdminService.getUsersPaged(this.page - 1, this.pageSize)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: res => {
          // Hỗ trợ cả response phân trang trực tiếp và response bọc trong `data`.
          // Quan trọng: không bao giờ gán undefined vào mảng vì template dùng `.length`.
          const pageData = (res as any)?.data ?? res;
          this.users = Array.isArray(pageData)
            ? pageData
            : (Array.isArray(pageData?.content) ? pageData.content : []);
          this.totalPages = Array.isArray(pageData)
            ? 1
            : Number(pageData?.totalPages ?? 1);
        },
        error: err => {
          const code = err?.status ?? 500;
          this.router.navigate(['/error', code]);
        }
      });
  }

  prevPage(): void {
    if (this.page > 1) {
      this.page--;
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages) {
      this.page++;
      this.loadUsers();
    }
  }

  editUser(userId: number): void {
    this.router.navigate([`/admin/users/${userId}`]);
  }

  goAddUser(): void {
    this.router.navigate(['/admin/users/add']);
  }

  makeStaff(user: UserAdminResponse): void {
    this.userAdminService.makeStaff(user.id, true).subscribe({
      next: () => {
        user.role = 'ROLE_STAFF';
        this.toast.success('Đã bổ nhiệm staff');
      },
      error: () => {
        this.toast.error('Không thể bổ nhiệm staff');
      }
    });
  }

  removeStaff(user: UserAdminResponse): void {
    this.userAdminService.removeStaff(user.id, false).subscribe({
      next: () => {
        user.role = 'ROLE_USER';
        this.toast.success('Đã gỡ staff');
      },
      error: () => {
        this.toast.error('Không thể gỡ staff');
      }
    });
  }

  async changeStatus(user: UserAdminResponse): Promise<void> {
    const nextStatus = !user.status;
    const actionText = nextStatus ? 'mở khóa' : 'khóa';

    const ok = await this.confirm.confirm(
      `Bạn chắc chắn muốn ${actionText} người dùng "${user.username}"?`,
      'Xác nhận'
    );
    if (!ok) return;

    this.userAdminService.changeStatus(user.id, nextStatus).subscribe({
      next: () => {
        user.status = nextStatus;
        this.toast.success(`Đã ${actionText} người dùng "${user.username}".`);
      },
      error: (err) => {
        console.error(err);
        if (err?.status === 403) {
          this.toast.warning('Bạn không có quyền thực hiện thao tác này.');
        } else {
          this.toast.error(`Không thể ${actionText} người dùng.`);
        }
      }
    });
  }
}
