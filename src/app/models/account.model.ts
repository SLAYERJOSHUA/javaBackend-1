export type AccountType = 'SAVINGS' | 'CHECKING' | 'INVESTMENT';
export type AccountStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
export type AccountRequestStatus = 'UNDER_REVIEW' | 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Account {
  accountId: number;
  accountNumber: string;
  accountType: AccountType;
  balance: number;
  userId: number;
  status: AccountStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface AccountRequest {
  requestId: number;
  userId: number;
  accountType: AccountType;
  initialDeposit: number;
  purpose?: string;
  documentName?: string;
  status: AccountRequestStatus;
  submittedAt?: string;
  createdAt?: string;
  reviewNotes?: string;
}
