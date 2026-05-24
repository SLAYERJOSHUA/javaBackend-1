import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../core/services/toast.service';
import { UserService } from '../../../core/services/user.service';
import { User, UserRole } from '../../../models/user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [DatePipe, FormsModule],
  templateUrl: './user-management.component.html'
})
export class UserManagementComponent implements OnInit {
  private readonly api = inject(UserService);
  private readonly toast = inject(ToastService);
  readonly users = signal<User[]>([]);
  readonly query = signal('');
  readonly roleFilter = signal('');
  readonly filtered = computed(() => {
    const query = this.query().toLowerCase();
    const role = this.roleFilter();
    return this.users().filter((user) => {
      const haystack = `${user.firstName} ${user.lastName} ${user.username} ${user.email} ${user.phone || ''}`.toLowerCase();
      return (!query || haystack.includes(query)) && (!role || user.role === role);
    });
  });

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.getAllUsers().subscribe((data) => this.users.set(Array.isArray(data) ? data : data.content));
  }

  setRole(user: User, role: UserRole): void {
    this.api.updateRole(user.id, role).subscribe(() => {
      this.toast.show('Role updated');
      this.load();
    });
  }

  toggle(user: User): void {
    const request = user.isActive ? this.api.deactivateUser(user.id) : this.api.activateUser(user.id);
    request.subscribe(() => {
      this.toast.show(user.isActive ? 'User deactivated' : 'User activated');
      this.load();
    });
  }
}
