import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ReservaHistorico } from '../entity/ReservaHistorico'; 

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/reserva';

  constructor(private http: HttpClient) { }

  createReservation(resourceId: number, durationMinutes: number): Observable<any> {
    const body = { resourceId, durationMinutes };
    return this.http.post(this.apiUrl, body);
  }
   getMyHistory(): Observable<ReservaHistorico[]> {
    return this.http.get<ReservaHistorico[]>(`${this.apiUrl}/history`);
  }
}