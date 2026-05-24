import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LayoutComponent } from './shared/layout/layout.component';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then((m) => m.RegisterComponent) },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent) },
      { path: 'accounts', loadComponent: () => import('./features/accounts/accounts.component').then((m) => m.AccountsComponent), canActivate: [roleGuard], data: { roles: ['ACCOUNT_HOLDER', 'ADMIN'] } },
      { path: 'account-requests', loadComponent: () => import('./features/accounts/account-request-status/account-request-status.component').then((m) => m.AccountRequestStatusComponent), canActivate: [roleGuard], data: { roles: ['ACCOUNT_HOLDER'] } },
      { path: 'transactions', loadComponent: () => import('./features/transactions/transactions.component').then((m) => m.TransactionsComponent), canActivate: [roleGuard], data: { roles: ['ACCOUNT_HOLDER', 'ADMIN'] } },
      { path: 'payments', loadComponent: () => import('./features/payments/payments.component').then((m) => m.PaymentsComponent), canActivate: [roleGuard], data: { roles: ['ACCOUNT_HOLDER', 'ADMIN'] } },
      { path: 'support', loadComponent: () => import('./features/support/support.component').then((m) => m.SupportComponent) },
      { path: 'admin/users', loadComponent: () => import('./features/admin/user-management/user-management.component').then((m) => m.UserManagementComponent), canActivate: [roleGuard], data: { roles: ['ADMIN'] } },
      { path: 'admin/account-approvals', loadComponent: () => import('./features/admin/account-approval/account-approval.component').then((m) => m.AccountApprovalComponent), canActivate: [roleGuard], data: { roles: ['ADMIN'] } }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
