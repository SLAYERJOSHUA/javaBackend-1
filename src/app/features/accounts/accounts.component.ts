import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { ToastService } from '../../core/services/toast.service';
import { Account, AccountType } from '../../models/account.model';
import { AccountNumberPipe } from '../../shared/pipes/account-number.pipe';
import { StatusClassPipe } from '../../shared/pipes/status-class.pipe';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CurrencyPipe, ReactiveFormsModule, AccountNumberPipe, StatusClassPipe],
  templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly api = inject(AccountService);
  private readonly toast = inject(ToastService);

  readonly accounts = signal<Account[]>([]);
  readonly showRequestForm = signal(false);
  readonly activeAccountExists = computed(() => this.accounts().some((account) => account.status === 'ACTIVE'));
  readonly form = this.fb.nonNullable.group({
    accountType: ['SAVINGS', Validators.required],
    initialDeposit: [0, [Validators.required, Validators.min(0)]],
    purpose: ['', [Validators.required, Validators.minLength(10)]],
    documentName: ['']
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const user = this.auth.user();
    if (!user) return;
    const request = user.role === 'ADMIN' ? this.api.getAllAccounts() : this.api.getAccountsByUser(user.id);
    request.subscribe((data) => this.accounts.set(Array.isArray(data) ? data : data.content));
  }

  submitRequest(): void {
    const user = this.auth.user();
    if (!user || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.api.createRequest({ ...value, accountType: value.accountType as AccountType, userId: user.id }).subscribe(() => {
      this.toast.show('Account request submitted');
      this.form.reset({ accountType: 'SAVINGS', initialDeposit: 0, purpose: '', documentName: '' });
      this.showRequestForm.set(false);
    });
  }

  generatePin(accountId: number): void {
    this.api.generatePin(accountId).subscribe((response) => this.toast.show(`PIN generated: ${response.pin}`));
  }

  refreshBalance(accountId: number): void {
    this.api.getBalance(accountId).subscribe((response) => {
      this.toast.show(`Current balance: ${response.balance}`);
      this.load();
    });
  }
}
