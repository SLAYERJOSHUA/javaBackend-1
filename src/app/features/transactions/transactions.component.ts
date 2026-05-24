import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { TransactionService } from '../../core/services/transaction.service';
import { Account } from '../../models/account.model';
import { Transaction, TransactionType, WorkStatus } from '../../models/transaction.model';
import { StatusClassPipe } from '../../shared/pipes/status-class.pipe';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, StatusClassPipe],
  templateUrl: './transactions.component.html'
})
export class TransactionsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly accountsApi = inject(AccountService);
  private readonly api = inject(TransactionService);
  private readonly toast = inject(ToastService);
  readonly accounts = signal<Account[]>([]);
  readonly transactions = signal<Transaction[]>([]);
  readonly isAdmin = computed(() => this.auth.user()?.role === 'ADMIN');
  readonly form = this.fb.nonNullable.group({
    accountId: [0, Validators.required],
    transactionType: ['TRANSFER', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: ['', Validators.required]
  });

  ngOnInit(): void { this.load(); }

  load(): void {
    const user = this.auth.user();
    if (!user) return;
    const accounts$ = user.role === 'ADMIN' ? this.accountsApi.getAllAccounts() : this.accountsApi.getAccountsByUser(user.id);
    accounts$.subscribe((data) => this.accounts.set(Array.isArray(data) ? data : data.content));
    const transactions$ = user.role === 'ADMIN' ? this.api.getAllTransactions() : this.api.getTransactionsByUser(user.id);
    transactions$.subscribe((data) => this.transactions.set(Array.isArray(data) ? data : data.content));
  }

  submit(): void {
    const user = this.auth.user();
    if (!user || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.api.createTransaction({ ...value, userId: user.id, transactionType: value.transactionType as TransactionType }).subscribe(() => {
      this.toast.show('Transaction created');
      this.form.reset({ accountId: 0, transactionType: 'TRANSFER', amount: 0, description: '' });
      this.load();
    });
  }

  updateStatus(id: number, status: WorkStatus): void {
    this.api.updateStatus(id, status).subscribe(() => {
      this.toast.show('Transaction status updated');
      this.load();
    });
  }
}
