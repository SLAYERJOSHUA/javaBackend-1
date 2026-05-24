import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { AuthService } from '../../core/services/auth.service';
import { PaymentService } from '../../core/services/payment.service';
import { ToastService } from '../../core/services/toast.service';
import { Account } from '../../models/account.model';
import { Payment, PaymentMethod } from '../../models/payment.model';
import { WorkStatus } from '../../models/transaction.model';
import { StatusClassPipe } from '../../shared/pipes/status-class.pipe';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, StatusClassPipe],
  templateUrl: './payments.component.html'
})
export class PaymentsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly accountsApi = inject(AccountService);
  private readonly api = inject(PaymentService);
  private readonly toast = inject(ToastService);
  readonly accounts = signal<Account[]>([]);
  readonly payments = signal<Payment[]>([]);
  readonly isAdmin = computed(() => this.auth.user()?.role === 'ADMIN');
  readonly form = this.fb.nonNullable.group({
    accountId: [0, Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    paymentMethod: ['CARD', Validators.required],
    description: ['', Validators.required]
  });

  ngOnInit(): void { this.load(); }

  load(): void {
    const user = this.auth.user();
    if (!user) return;
    const accounts$ = user.role === 'ADMIN' ? this.accountsApi.getAllAccounts() : this.accountsApi.getAccountsByUser(user.id);
    accounts$.subscribe((data) => this.accounts.set(Array.isArray(data) ? data : data.content));
    const payments$ = user.role === 'ADMIN' ? this.api.getAllPayments() : this.api.getPaymentsByUser(user.id);
    payments$.subscribe((data) => this.payments.set(Array.isArray(data) ? data : data.content));
  }

  submit(): void {
    const user = this.auth.user();
    if (!user || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.api.makePayment({ ...value, userId: user.id, paymentMethod: value.paymentMethod as PaymentMethod }).subscribe(() => {
      this.toast.show('Payment processed');
      this.form.reset({ accountId: 0, amount: 0, paymentMethod: 'CARD', description: '' });
      this.load();
    });
  }

  updateStatus(id: number, status: WorkStatus): void {
    this.api.updateStatus(id, status).subscribe(() => {
      this.toast.show('Payment status updated');
      this.load();
    });
  }
}
