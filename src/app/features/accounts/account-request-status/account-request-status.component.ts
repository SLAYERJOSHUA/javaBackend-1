import { DatePipe, CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { AccountService } from '../../../core/services/account.service';
import { AccountRequest } from '../../../models/account.model';
import { StatusClassPipe } from '../../../shared/pipes/status-class.pipe';

@Component({
  selector: 'app-account-request-status',
  standalone: true,
  imports: [DatePipe, CurrencyPipe, StatusClassPipe],
  template: `
    <section class="panel">
      <div class="panel-head"><h2>Account Request Status</h2></div>
      @for (request of requests(); track request.requestId) {
        <article class="panel">
          <h3>Request #{{ request.requestId }}</h3>
          <p><span class="status {{ request.status | statusClass }}">{{ request.status }}</span></p>
          <p><strong>Account Type:</strong> {{ request.accountType }}</p>
          <p><strong>Initial Deposit:</strong> {{ request.initialDeposit | currency:'INR' }}</p>
          <p><strong>Submitted:</strong> {{ request.submittedAt || request.createdAt | date:'medium' }}</p>
          <p><strong>Document:</strong> {{ request.documentName || 'Uploaded document' }}</p>
        </article>
      } @empty {
        <div class="empty">No account request found.</div>
      }
    </section>
  `
})
export class AccountRequestStatusComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly api = inject(AccountService);
  readonly requests = signal<AccountRequest[]>([]);

  ngOnInit(): void {
    const userId = this.auth.user()?.id;
    this.api.getRequests().subscribe((requests) => this.requests.set(userId ? requests.filter((request) => request.userId === userId) : requests));
  }
}
