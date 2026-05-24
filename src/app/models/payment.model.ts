import { WorkStatus } from './transaction.model';

export type PaymentMethod = 'CARD' | 'BANK_TRANSFER' | 'WALLET';

export interface Payment {
  paymentId: number;
  paymentNumber: string;
  accountId: number;
  userId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  status: WorkStatus;
  description: string;
  createdAt?: string;
  updatedAt?: string;
}
