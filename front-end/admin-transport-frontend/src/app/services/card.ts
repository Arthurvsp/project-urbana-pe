import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const CARD_API = 'http://localhost:8082/api/cards';

@Injectable({
  providedIn: 'root'
})
export class CardService {
  private http = inject(HttpClient);

  getAllCards(): Observable<any[]> {
    return this.http.get<any[]>(CARD_API);
  }

  getCardsByUserId(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${CARD_API}/user/${userId}`);
  }

  addCard(card: any): Observable<any> {
    return this.http.post<any>(CARD_API, card);
  }

  removeCardByNumber(cardNumber: number): Observable<void> {
    return this.http.delete<void>(`${CARD_API}/number/${cardNumber}`);
  }

  toggleCardStatus(cardNumber: number): Observable<any> {
    return this.http.put<any>(`${CARD_API}/number/${cardNumber}/toggle`, null);
  }
}