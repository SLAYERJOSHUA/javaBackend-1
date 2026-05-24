import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { PaymentService } from '../../core/services/payment.service';
import { Account } from '../../models/account.model';
import { Transaction } from '../../models/transaction.model';
import { Payment } from '../../models/payment.model';
import { StatusClassPipe } from '../../shared/pipes/status-class.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, StatusClassPipe],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly accountsApi = inject(AccountService);
  private readonly transactionsApi = inject(TransactionService);
  private readonly paymentsApi = inject(PaymentService);

  readonly user = this.auth.user;
  readonly accounts = signal<Account[]>([]);
  readonly transactions = signal<Transaction[]>([]);
  readonly payments = signal<Payment[]>([]);
  readonly totalBalance = computed(() => this.accounts().reduce((sum, account) => sum + Number(account.balance || 0), 0));
  readonly isAdmin = computed(() => this.user()?.role === 'ADMIN');

  ngOnInit(): void {
    const user = this.user();
    if (!user) return;
    const accounts$ = user.role === 'ADMIN' ? this.accountsApi.getAllAccounts() : this.accountsApi.getAccountsByUser(user.id);
    accounts$.subscribe((data) => this.accounts.set(Array.isArray(data) ? data : data.content));
    const transactions$ = user.role === 'ADMIN' ? this.transactionsApi.getAllTransactions() : this.transactionsApi.getTransactionsByUser(user.id);
    transactions$.subscribe((data) => this.transactions.set(Array.isArray(data) ? data : data.content));
    const payments$ = user.role === 'ADMIN' ? this.paymentsApi.getAllPayments() : this.paymentsApi.getPaymentsByUser(user.id);
    payments$.subscribe((data) => this.payments.set(Array.isArray(data) ? data : data.content));
  }

  barHeight(index: number): number {
    return 40 + ((this.transactions().filter((_, i) => i % 7 === index).length + index + 2) * 12);
  }
}
