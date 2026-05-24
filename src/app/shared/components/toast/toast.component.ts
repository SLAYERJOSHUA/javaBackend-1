import { Component, inject } from '@angular/core';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  styleUrl: './toast.component.css',
  template: `
    @if (toast.message(); as message) {
      <div class="toast" [class.bad]="message.tone === 'error'">{{ message.text }}</div>
    }
  `
})
export class ToastComponent {
  readonly toast = inject(ToastService);
}
