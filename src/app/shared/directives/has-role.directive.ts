import { Directive, Input, TemplateRef, ViewContainerRef, effect, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { UserRole } from '../../models/user.model';

@Directive({ selector: '[appHasRole]', standalone: true })
export class HasRoleDirective {
  private readonly auth = inject(AuthService);
  private readonly view = inject(ViewContainerRef);
  private readonly template = inject(TemplateRef<unknown>);
  private roles: UserRole[] = [];

  @Input() set appHasRole(value: UserRole | UserRole[]) {
    this.roles = Array.isArray(value) ? value : [value];
    this.render();
  }

  constructor() {
    effect(() => {
      this.auth.user();
      this.render();
    });
  }

  private render(): void {
    this.view.clear();
    if (this.roles.length === 0 || this.auth.hasRole(this.roles)) {
      this.view.createEmbeddedView(this.template);
    }
  }
}
