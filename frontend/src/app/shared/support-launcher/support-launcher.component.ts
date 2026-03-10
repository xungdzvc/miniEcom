import { Component, HostListener } from '@angular/core';

@Component({
  selector: 'app-support-launcher',
  templateUrl: './support-launcher.component.html',
  styleUrls: ['./support-launcher.component.css'],
})
export class SupportLauncherComponent {
  open = false;

  zaloUrl = 'https://zalo.me/0388001659';
  telegramUrl = 'https://t.me/xunglordcom_suport';
  facebookUrl = 'https://www.facebook.com/pham.vanxung.9';

  toggle() { this.open = !this.open; }
  close() { this.open = false; }

  // Click ngoài panel để đóng
  @HostListener('document:click', ['$event'])
  onDocClick(event: MouseEvent) {
    if (!this.open) return;
    const target = event.target as HTMLElement;
    const panel = target.closest('.support-panel');
    const fab = target.closest('.support-fab');
    if (!panel && !fab) this.close();
  }

  // ESC để đóng
  @HostListener('document:keydown.escape')
  onEsc() {
    this.close();
  }
}