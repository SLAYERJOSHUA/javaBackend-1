import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'accountNumber', standalone: true })
export class AccountNumberPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return 'Not available';
    return `${value.slice(0, 3)} ${'*'.repeat(Math.max(0, value.length - 7))} ${value.slice(-4)}`;
  }
}
