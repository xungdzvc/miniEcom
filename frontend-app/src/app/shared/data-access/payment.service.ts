import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../enviroments/environment';
@Injectable({ providedIn: 'root' })
export class PaymentService {
  private api = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  chargeWithCard(cardData: any) {
    return this.http.post<any>(`${this.api}/charging`, cardData);
  }
  createTopUp(amount: number) {
    return this.http.post<any>(`${this.api}/topup`, { amount });
  }
  getTopupStatus(topupId: number) {
    return this.http.get<any>(`${this.api}/topup/status/${topupId}`);
  }


}
