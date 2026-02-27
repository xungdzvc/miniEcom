import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ConfirmService {
  async confirm(message: string, title?: string): Promise<boolean> {
    const text = title ? `${title}\n\n${message}` : message;
    return Promise.resolve(window.confirm(text));
  }
}
