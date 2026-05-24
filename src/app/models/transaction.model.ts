export type TransactionType = 'TRANSFER' | 'DEPOSIT' | 'WITHDRAWAL';
export type WorkStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export interface Transaction {
  transactionId: number;
  transactionNumber: string;
  accountId: number;
  userId: number;
  transactionType: TransactionType;
  amount: number;
  status: WorkStatus;
  description: string;
  createdAt?: string;
  updatedAt?: string;
}
