import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../enviroments/environment';
@Injectable({ providedIn: 'root' })
export class SearchService {
  private api = environment.apiBaseUrl + '/search';

  constructor(private http: HttpClient) {}

  search(keyword: string, sort: string) {
    return this.http.get<any[]>(this.api, {
      params: { keyword, sort }
    });
  }
}
