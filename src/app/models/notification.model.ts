export interface Notification {
  notificationId: number;
  userId: number;
  title: string;
  message: string;
  type?: string;
  isRead: boolean;
  createdAt?: string;
}
