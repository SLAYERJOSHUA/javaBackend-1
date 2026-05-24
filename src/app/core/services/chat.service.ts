import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api.config';
import { ChatMessage } from '../../models/chat.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  constructor(private readonly http: HttpClient) {}

  sendMessage(payload: Partial<ChatMessage>) {
    return this.http.post<ChatMessage>(`${API_BASE_URL}/api/chat/send`, payload);
  }

  getMessages() {
    return this.http.get<ChatMessage[]>(`${API_BASE_URL}/api/chat/messages`);
  }

  getSessionMessages(chatSessionId: string) {
    return this.http.get<ChatMessage[]>(`${API_BASE_URL}/api/chat/${chatSessionId}`);
  }

  getUnrepliedMessages() {
    return this.http.get<unknown[]>(`${API_BASE_URL}/api/chat/unreplied`);
  }
}
