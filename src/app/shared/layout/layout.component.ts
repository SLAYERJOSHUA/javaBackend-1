import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LoadingService } from '../../core/services/loading.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  styleUrl: './layout.component.css',
  templateUrl: './layout.component.html'
})
export class LayoutComponent {
  private readonly auth = inject(AuthService);
  readonly loading = inject(LoadingService).loading;
  readonly user = this.auth.user;
  readonly links = computed(() => {
    const user = this.user();
    if (!user) return [];
    const base = [
      { label: 'Dashboard', path: '/dashboard' },
      { label: 'My Accounts', path: '/accounts', roles: ['ACCOUNT_HOLDER', 'ADMIN'] },
      { label: 'Transactions', path: '/transactions', roles: ['ACCOUNT_HOLDER', 'ADMIN'] },
      { label: 'Payments', path: '/payments', roles: ['ACCOUNT_HOLDER', 'ADMIN'] },
      { label: 'Support', path: '/support' },
      { label: 'Users', path: '/admin/users', roles: ['ADMIN'] },
      { label: 'Account Approval', path: '/admin/account-approvals', roles: ['ADMIN'] }
    ];
    return base.filter((link) => !link.roles || link.roles.includes(user.role));
  });

  logout(): void {
    this.auth.logout();
  }
}
