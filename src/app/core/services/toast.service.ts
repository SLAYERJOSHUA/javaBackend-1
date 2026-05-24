import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  text: string;
  tone: 'ok' | 'error';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly message = signal<ToastMessage | null>(null);

  show(text: string, tone: 'ok' | 'error' = 'ok'): void {
    this.message.set({ text, tone });
    window.setTimeout(() => this.message.set(null), 3200);
  }
}
