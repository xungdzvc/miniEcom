import { AuthUser } from "../../shared/models/core/user/user-auth.model";

export type AuthSession = {
  accessToken: string;
  refreshToken?: string;
  user: AuthUser;
};

const KEY = 'auth_session';

export class AuthStorage {

  static set(data: { accessToken: string; user: AuthUser }) {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
  }

  static setUser(user: AuthUser) {
    if (user === undefined || user === null) {
      localStorage.removeItem('user');
      return;
    }
    localStorage.setItem('user', JSON.stringify(user));
  }

  static getUser(): AuthUser | null {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
  }
  
  static get(): { accessToken: string; user: AuthUser } | null {
    const token = localStorage.getItem('accessToken');
    const userRaw = localStorage.getItem('user');

    if (!token || !userRaw) return null;

    try {
      const user = JSON.parse(userRaw);
      return { accessToken: token, user };
    } catch {
      this.clear();
      return null;
    }
  }

  
  static clear() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
  }
}

