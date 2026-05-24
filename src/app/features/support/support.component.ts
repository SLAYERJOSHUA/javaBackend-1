import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ChatService } from '../../core/services/chat.service';
import { ToastService } from '../../core/services/toast.service';
import { ChatMessage } from '../../models/chat.model';

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  styleUrl: './support.component.css',
  templateUrl: './support.component.html'
})
export class SupportComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly api = inject(ChatService);
  private readonly toast = inject(ToastService);
  readonly messages = signal<ChatMessage[]>([]);
  readonly form = this.fb.nonNullable.group({ message: ['', Validators.required] });
  readonly userId = this.auth.user()?.id || 0;

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.getMessages().subscribe((messages) => this.messages.set(messages));
  }

  submit(): void {
    const user = this.auth.user();
    if (!user || this.form.invalid) return;
    this.api.sendMessage({
      senderId: user.id,
      senderRole: user.role,
      receiverId: user.role === 'ADMIN' ? 2 : 1,
      receiverRole: user.role === 'ADMIN' ? 'ACCOUNT_HOLDER' : 'CUSTOMER_SUPPORT',
      message: this.form.getRawValue().message,
      chatSessionId: `session_${user.id}`
    }).subscribe(() => {
      this.toast.show('Message sent');
      this.form.reset();
      this.load();
    });
  }
}
