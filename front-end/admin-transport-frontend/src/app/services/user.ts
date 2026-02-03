import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const USER_API = 'http://localhost:8081/api/users';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(USER_API);
  }

  getUserById(id: number): Observable<any> {
    return this.http.get<any>(`${USER_API}/${id}`);
  }

  createUser(user: any): Observable<any> {
    return this.http.post<any>(USER_API, user);
  }

  updateUser(id: number, user: any): Observable<any> {
    return this.http.put<any>(`${USER_API}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${USER_API}/${id}`);
  }

  addCardToUser(userId: number, cardNumber: number): Observable<void> {
    return this.http.put<void>(`${USER_API}/${userId}/cards/${cardNumber}`, null);
  }

  removeCardFromUser(userId: number, cardNumber: number): Observable<void> {
    return this.http.delete<void>(`${USER_API}/${userId}/cards/${cardNumber}`);
  }
}