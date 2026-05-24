import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api.config';
import { User, UserRole } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private readonly http: HttpClient) {}

  getAllUsers(page = 0, size = 50) {
    return this.http.get<{ content: User[] } | User[]>(`${API_BASE_URL}/api/users`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  searchUsers(name: string) {
    return this.http.get<User[]>(`${API_BASE_URL}/api/users/search`, { params: { name } });
  }

  getUsersByRole(role: UserRole) {
    return this.http.get<User[]>(`${API_BASE_URL}/api/users/role/${role}`);
  }

  getUserById(userId: number) {
    return this.http.get<User>(`${API_BASE_URL}/api/users/${userId}`);
  }

  updateProfile(userId: number, payload: Partial<User>) {
    return this.http.put<User>(`${API_BASE_URL}/api/users/${userId}/profile`, payload);
  }

  updateRole(userId: number, role: UserRole) {
    return this.http.put<User>(`${API_BASE_URL}/api/users/${userId}/role`, { role });
  }

  activateUser(userId: number) {
    return this.http.post<User>(`${API_BASE_URL}/api/users/${userId}/activate`, {});
  }

  deactivateUser(userId: number) {
    return this.http.delete<User>(`${API_BASE_URL}/api/users/${userId}`);
  }
}
