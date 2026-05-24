import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { ToastService } from '../../../core/services/toast.service';
import { AccountRequest } from '../../../models/account.model';
import { StatusClassPipe } from '../../../shared/pipes/status-class.pipe';

@Component({
  selector: 'app-account-approval',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, StatusClassPipe],
  templateUrl: './account-approval.component.html'
})
export class AccountApprovalComponent implements OnInit {
  private readonly api = inject(AccountService);
  private readonly toast = inject(ToastService);
  readonly requests = signal<AccountRequest[]>([]);
  readonly notes = signal<Record<number, string>>({});

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.getRequests().subscribe((requests) => this.requests.set(requests));
  }

  setNote(requestId: number, value: string): void {
    this.notes.update((current) => ({ ...current, [requestId]: value }));
  }

  approve(requestId: number): void {
    this.api.approveRequest(requestId, this.notes()[requestId]).subscribe(() => {
      this.toast.show('Account request approved');
      this.load();
    });
  }

  reject(requestId: number): void {
    this.api.rejectRequest(requestId, this.notes()[requestId]).subscribe(() => {
      this.toast.show('Account request rejected');
      this.load();
    });
  }
}
