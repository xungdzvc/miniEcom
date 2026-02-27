import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ConfirmService {
  /**
   * Lightweight confirm helper.
   *
   * If you later want a real modal, you can keep this API and swap implementation.
   */
  async confirm(message: string, title?: string): Promise<boolean> {
    const text = title ? `${title}\n\n${message}` : message;
    return Promise.resolve(window.confirm(text));
  }
}
