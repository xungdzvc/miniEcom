export interface AuthUser {
  id: number;
  fullName: string;
  email: string;
  username: string;
  phoneNumber: string;
  address?: string;
  active: boolean;
  totalVnd: number;
  vnd: number;
  roles: string;
  googleId?: string;
}