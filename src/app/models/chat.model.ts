import { UserRole } from './user.model';

export interface ChatMessage {
  id: number;
  senderId: number;
  senderRole: UserRole;
  receiverId: number;
  receiverRole: UserRole;
  message: string;
  chatSessionId: string;
  isRead: boolean;
  createdAt?: string;
}
