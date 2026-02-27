import { Injectable } from '@angular/core';

export type ThemeMode = 'light' | 'dark' | 'system';
const KEY = 'theme_mode';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private media = window.matchMedia?.('(prefers-color-scheme: dark)');

  getMode(): ThemeMode {
    return (localStorage.getItem(KEY) as ThemeMode) || 'system';
  }

  setMode(mode: ThemeMode) {
    localStorage.setItem(KEY, mode);
    this.apply(mode);
  }

  apply(mode: ThemeMode) {
    const root = document.documentElement;

    if (mode === 'system') {
      const isDark = !!this.media?.matches;
      root.setAttribute('data-theme', isDark ? 'dark' : 'light');
      root.setAttribute('data-theme-mode', 'system');
      return;
    }

    root.setAttribute('data-theme', mode);
    root.setAttribute('data-theme-mode', mode);
  }

  watchSystemChanges(enable: boolean) {
    if (!this.media) return;

    const handler = () => {
      if (this.getMode() === 'system') this.apply('system');
    };

    // add/remove listener (tương thích)
    if (enable) {
      // @ts-ignore
      this.media.addEventListener?.('change', handler) ?? this.media.addListener(handler);
    } else {
      // @ts-ignore
      this.media.removeEventListener?.('change', handler) ?? this.media.removeListener(handler);
    }
  }
}
