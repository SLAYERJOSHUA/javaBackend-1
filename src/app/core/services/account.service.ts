import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api.config';
import { Account, AccountRequest, AccountType } from '../../models/account.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  constructor(private readonly http: HttpClient) {}

  createAccount(userId: number, accountType: AccountType, balance: number) {
    return this.http.post<Account>(`${API_BASE_URL}/api/accounts`, { userId, accountType, balance });
  }

  createOwnAccount(userId: number, accountType: AccountType, initialDeposit: number) {
    return this.http.post<Account>(`${API_BASE_URL}/api/accounts/self`, { userId, accountType, initialDeposit });
  }

  createRequest(payload: Partial<AccountRequest>) {
    return this.http.post<AccountRequest>(`${API_BASE_URL}/api/accounts/request`, payload);
  }

  getAllAccounts(page = 0, size = 10) {
    return this.http.get<{ content: Account[] } | Account[]>(`${API_BASE_URL}/api/accounts`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  getAccountsByUser(userId: number) {
    return this.http.get<Account[]>(`${API_BASE_URL}/api/accounts/user/${userId}`);
  }

  getActiveAccountsByUser(userId: number) {
    return this.http.get<Account[]>(`${API_BASE_URL}/api/accounts/user/${userId}/active`);
  }

  getBalance(accountId: number) {
    return this.http.get<{ balance: number }>(`${API_BASE_URL}/api/accounts/${accountId}/balance`);
  }

  generatePin(accountId: number) {
    return this.http.post<{ pin: string }>(`${API_BASE_URL}/api/accounts/${accountId}/generate-pin`, {});
  }

  getRequests() {
    return this.http.get<AccountRequest[]>(`${API_BASE_URL}/api/accounts/requests`);
  }

  approveRequest(requestId: number, reviewNotes?: string) {
    return this.http.post<AccountRequest>(`${API_BASE_URL}/api/accounts/requests/${requestId}/approve`, { reviewNotes });
  }

  rejectRequest(requestId: number, reviewNotes?: string) {
    return this.http.post<AccountRequest>(`${API_BASE_URL}/api/accounts/requests/${requestId}/reject`, { reviewNotes });
  }
}
