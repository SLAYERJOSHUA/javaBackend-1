import { Directive, ElementRef, Input, OnChanges, Renderer2, inject } from '@angular/core';

@Directive({ selector: '[appHighlightStatus]', standalone: true })
export class HighlightStatusDirective implements OnChanges {
  @Input() appHighlightStatus = '';
  private readonly element = inject(ElementRef<HTMLElement>);
  private readonly renderer = inject(Renderer2);

  ngOnChanges(): void {
    const value = this.appHighlightStatus.toLowerCase().replaceAll('_', '-');
    this.renderer.setAttribute(this.element.nativeElement, 'class', `status ${value}`);
  }
}
