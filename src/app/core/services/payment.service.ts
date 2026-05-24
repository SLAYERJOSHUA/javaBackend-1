import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api.config';
import { Payment, PaymentMethod } from '../../models/payment.model';
import { WorkStatus } from '../../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  constructor(private readonly http: HttpClient) {}

  makePayment(payload: { accountId: number; userId: number; amount: number; paymentMethod: PaymentMethod; description: string }) {
    return this.http.post<Payment>(`${API_BASE_URL}/api/payments`, payload);
  }

  getAllPayments(page = 0, size = 10) {
    return this.http.get<{ content: Payment[] } | Payment[]>(`${API_BASE_URL}/api/payments`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  getPaymentsByUser(userId: number) {
    return this.http.get<Payment[]>(`${API_BASE_URL}/api/payments/user/${userId}`);
  }

  getPaymentsByAccount(accountId: number) {
    return this.http.get<Payment[]>(`${API_BASE_URL}/api/payments/account/${accountId}`);
  }

  updateStatus(paymentId: number, status: WorkStatus) {
    return this.http.put<Payment>(`${API_BASE_URL}/api/payments/${paymentId}/status`, { status });
  }

  filter(status?: WorkStatus, method?: PaymentMethod) {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (method) params = params.set('method', method);
    return this.http.get<Payment[]>(`${API_BASE_URL}/api/payments/filter`, { params });
  }
}
