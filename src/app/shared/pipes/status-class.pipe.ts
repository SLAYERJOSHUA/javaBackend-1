import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'statusClass', standalone: true })
export class StatusClassPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return (value || '').toLowerCase().replaceAll('_', '-');
  }
}
