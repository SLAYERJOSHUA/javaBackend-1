import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private pending = 0;
  readonly loading = signal(false);

  start(): void {
    this.pending += 1;
    this.loading.set(true);
  }

  stop(): void {
    this.pending = Math.max(0, this.pending - 1);
    this.loading.set(this.pending > 0);
  }
}
