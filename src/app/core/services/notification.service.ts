import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api.config';
import { Notification } from '../../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private readonly http: HttpClient) {}

  getNotifications(userId: number) {
    return this.http.get<Notification[]>(`${API_BASE_URL}/api/notifications/user/${userId}`);
  }

  getUnreadNotifications(userId: number) {
    return this.http.get<Notification[]>(`${API_BASE_URL}/api/notifications/user/${userId}/unread`);
  }

  markAsRead(notificationId: number) {
    return this.http.put<Notification>(`${API_BASE_URL}/api/notifications/${notificationId}/read`, {});
  }
}
