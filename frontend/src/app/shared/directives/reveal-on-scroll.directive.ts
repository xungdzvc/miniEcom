import { AfterViewInit, Directive, ElementRef, Input, OnDestroy } from '@angular/core';

@Directive({
  selector: '[revealOnScroll]'
})
export class RevealOnScrollDirective implements AfterViewInit, OnDestroy {
  @Input() revealDelay = 0;            // ms
  @Input() revealOnce = true;          // chỉ animate 1 lần
  @Input() revealThreshold = 0.15;     // % visible

  private io?: IntersectionObserver;

  constructor(private el: ElementRef<HTMLElement>) {}

  ngAfterViewInit(): void {
    const node = this.el.nativeElement;
    node.style.opacity = '0';
    node.style.transform = 'translateY(14px)';
    node.style.transition = `opacity .5s ease ${this.revealDelay}ms, transform .5s ease ${this.revealDelay}ms`;

    this.io = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        node.style.opacity = '1';
        node.style.transform = 'translateY(0)';
        if (this.revealOnce) this.io?.disconnect();
      } else if (!this.revealOnce) {
        node.style.opacity = '0';
        node.style.transform = 'translateY(14px)';
      }
    }, { threshold: this.revealThreshold });

    this.io.observe(node);
  }

  ngOnDestroy(): void {
    this.io?.disconnect();
  }
}