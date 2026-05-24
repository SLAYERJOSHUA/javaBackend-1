import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { AuthResponse, LoginRequest, RegisterRequest, User, UserRole } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'token';
  private readonly refreshKey = 'refreshToken';
  private readonly userKey = 'user';
  readonly user = signal<User | null>(this.readUser());
  readonly isLoggedIn = computed(() => !!this.token && !!this.user());

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  get token(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  get refreshTokenValue(): string | null {
    return localStorage.getItem(this.refreshKey);
  }

  login(payload: LoginRequest) {
    return this.http.post<AuthResponse>(`${API_BASE_URL}/api/auth/login`, payload).pipe(tap((response) => this.setSession(response)));
  }

  register(payload: RegisterRequest) {
    return this.http.post<User>(`${API_BASE_URL}/api/auth/register`, payload);
  }

  refreshToken() {
    return this.http.post<{ token: string; expiresIn: number }>(`${API_BASE_URL}/api/auth/refresh`, { token: this.refreshTokenValue }).pipe(
      tap((response) => localStorage.setItem(this.tokenKey, response.token))
    );
  }

  changePassword(oldPassword: string, newPassword: string) {
    return this.http.post<{ message: string }>(`${API_BASE_URL}/api/auth/change-password`, { oldPassword, newPassword });
  }

  hasRole(roles: UserRole[]): boolean {
    const role = this.user()?.role;
    return !!role && roles.includes(role);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshKey);
    localStorage.removeItem(this.userKey);
    this.user.set(null);
    void this.router.navigateByUrl('/login');
  }

  private setSession(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.refreshKey, response.refreshToken);
    localStorage.setItem(this.userKey, JSON.stringify(response.user));
    this.user.set(response.user);
  }

  private readUser(): User | null {
    try {
      const raw = localStorage.getItem(this.userKey);
      return raw ? JSON.parse(raw) as User : null;
    } catch {
      return null;
    }
  }
}
