import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
  selector: 'img[appImageFallback]',
  standalone: true
})
export class ImageFallbackDirective {
  @Input() fallbackSrc = '/product-placeholder.svg';

  constructor(private readonly el: ElementRef<HTMLImageElement>) {}

  @HostListener('error')
  onError(): void {
    const img = this.el.nativeElement;
    if (img.src.endsWith(this.fallbackSrc)) return;
    img.src = this.fallbackSrc;
  }
}
