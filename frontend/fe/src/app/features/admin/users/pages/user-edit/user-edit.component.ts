import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  FormArray
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { NotificationService } from '../../../../../core/services/notification.service';
import { UserAdminService } from '../../../../../shared/data-access/user-admin.service';
import { RoleService } from '../../../../../shared/data-access/role.service';

import { FormLayoutComponent } from '../../../shared/form/form-layout/form-layout.component';
import { FormFieldComponent } from '../../../shared/form/form-field/form-field.component';
import { FormActionsComponent } from '../../../shared/form/form-actions/form-actions.component';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormLayoutComponent,
    FormFieldComponent,
    FormActionsComponent
  ],
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {
  form!: FormGroup;

  userId!: number;
  isLoading = false;

  roles: Array<{ id: number; name: string }> = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserAdminService,
    private roleService: RoleService,
    private notify: NotificationService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: [{ value: '', disabled: true }],
      address: [''],
      phoneNumber: [''],
      isActive: [true],
      vnd: [0],
      totalVnd: [0],
      roleIds: this.fb.array([], Validators.required)
    });

    this.loadRoles();
    this.loadUser();
  }

  get roleIds(): FormArray {
    return this.form.get('roleIds') as FormArray;
  }

  get roleUserId(): number | null {
    return this.roles.find(r => r.name === 'ROLE_USER')?.id ?? null;
  }

  get editableRoles(): Array<{ id: number; name: string }> {
    return this.roles.filter(
      r => r.name === 'ROLE_STAFF' || r.name === 'ROLE_MANAGER'
    );
  }

  loadRoles(): void {
    this.roleService.getAllRoles().subscribe({
      next: (res: any) => {
        this.roles = res?.data ?? [];
      },
      error: () => {
        this.notify.error('Không tải được danh sách vai trò');
      }
    });
  }

  loadUser(): void {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));

    if (!this.userId) {
      this.notify.error('ID người dùng không hợp lệ');
      this.router.navigate(['/admin/users']);
      return;
    }

    this.isLoading = true;

    this.userService.getUserById(this.userId).subscribe({
      next: (res: any) => {
        this.isLoading = false;

        const u = res?.data ?? res;

        this.form.patchValue({
          fullName: u.fullName ?? '',
          email: u.email ?? '',
          username: u.username ?? '',
          address: u.address ?? '',
          phoneNumber: u.phoneNumber ?? '',
          isActive: u.isActive ?? true,
          vnd: u.vnd ?? 0,
          totalVnd: u.totalVnd ?? 0
        });

        this.setRoleIdsFromIds(u.roleIds ?? []);
      },
      error: () => {
        this.isLoading = false;
        this.notify.error('Không tải được user');
        this.router.navigate(['/admin/users']);
      }
    });
  }

  setRoleIdsFromIds(roleIds: number[]): void {
    this.roleIds.clear();

    roleIds.forEach((roleId) => {
      this.roleIds.push(this.fb.control(roleId));
    });
  }

  isChecked(roleId: number): boolean {
    return this.roleIds.value.includes(roleId);
  }

  toggleRole(roleId: number, checked: boolean): void {
    const role = this.roles.find(r => r.id === roleId);

    // Không cho đổi ROLE_USER
    if (role?.name === 'ROLE_USER') {
      return;
    }

    if (checked) {
      if (!this.isChecked(roleId)) {
        this.roleIds.push(this.fb.control(roleId));
      }
    } else {
      const i = this.roleIds.value.findIndex((id: number) => id === roleId);
      if (i >= 0) {
        this.roleIds.removeAt(i);
      }
    }

    this.roleIds.markAsTouched();
    this.roleIds.updateValueAndValidity();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.roleIds.markAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    let roleIds: number[] = [...(raw.roleIds ?? [])];

    // luôn đảm bảo có ROLE_USER
    const userRoleId = this.roleUserId;
    if (userRoleId && !roleIds.includes(userRoleId)) {
      roleIds = [userRoleId, ...roleIds];
    }

    const payload = {
      fullName: raw.fullName?.trim(),
      email: raw.email?.trim(),
      username: raw.username,
      address: raw.address?.trim(),
      phoneNumber: raw.phoneNumber?.trim(),
      isActive: !!raw.isActive,
      vnd: Number(raw.vnd ?? 0),
      totalVnd: Number(raw.totalVnd ?? 0),
      roleIds
    };

    this.isLoading = true;

    this.userService.updateUser(this.userId, payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.notify.success('Cập nhật user thành công');
        this.router.navigate(['/admin/users']);
      },
      error: (err) => {
        this.isLoading = false;
        this.notify.error(err?.error?.message || 'Cập nhật thất bại');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/admin/users']);
  }

  getNameOfRole(roleName: String): String {
    if(roleName === 'ROLE_USER') return 'USER';
    if(roleName === 'ROLE_STAFF') return 'STAFF';
    if(roleName === 'ROLE_MANAGER') return 'MANAGER';
    return roleName;
  }
}