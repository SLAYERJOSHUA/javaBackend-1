import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api.config';
import { Transaction, TransactionType, WorkStatus } from '../../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  constructor(private readonly http: HttpClient) {}

  createTransaction(payload: { accountId: number; userId: number; transactionType: TransactionType; amount: number; description: string }) {
    return this.http.post<Transaction>(`${API_BASE_URL}/api/transactions`, payload);
  }

  getAllTransactions(page = 0, size = 10) {
    return this.http.get<{ content: Transaction[] } | Transaction[]>(`${API_BASE_URL}/api/transactions`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  getTransactionsByUser(userId: number) {
    return this.http.get<Transaction[]>(`${API_BASE_URL}/api/transactions/user/${userId}`);
  }

  getTransactionsByAccount(accountId: number) {
    return this.http.get<Transaction[]>(`${API_BASE_URL}/api/transactions/account/${accountId}`);
  }

  updateStatus(transactionId: number, status: WorkStatus) {
    return this.http.put<Transaction>(`${API_BASE_URL}/api/transactions/${transactionId}/status`, { status });
  }

  filter(status?: WorkStatus, type?: TransactionType) {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (type) params = params.set('type', type);
    return this.http.get<Transaction[]>(`${API_BASE_URL}/api/transactions/filter`, { params });
  }
}
