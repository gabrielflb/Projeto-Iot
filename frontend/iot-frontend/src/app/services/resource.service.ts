import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, switchMap, take } from 'rxjs';
import { Reserva } from '../entity/Reserva';
import { AuthService } from './auth.service';

export interface Resource {
  id: number;
  name: string;
  type: string;
  status: 'LIVRE' | 'OCUPADO';
  lastUpdated: string;
}

@Injectable({
  providedIn: 'root'
})
export class ResourceService {

  private apiUrl = 'http://localhost:8080/api/resources';
  private apiUrlReserva = 'http://localhost:8080/api/reserva';

  constructor(private http: HttpClient, private authService: AuthService) { }

  getResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(this.apiUrl);
  }

 releaseResource(resourceId: number): Observable<any> {
    return this.authService.currentUser$.pipe(
      take(1),
      switchMap(user => {
        if (!user) {
          throw new Error('Usuário não logado');
        }
        const headers = new HttpHeaders({
          'Email': user.email
        });
        return this.http.post(
          `${this.apiUrl}/${resourceId}/release`, 
          {}, 
          { headers, responseType: 'text' }
        );
      })
    );
  }

  deleteResource(resourceID: number): Observable<any>{
      return this.http.delete(`${this.apiUrl}/${resourceID}`)
  }

  getReservations():Observable<Reserva[]> {
    return this.http.get<Reserva[]>(this.apiUrlReserva);
  }

  reserveResource(resourceId: number): Observable<any>{
    return this.http.post(`${this.apiUrlReserva}/${resourceId}/reserva`, {});
  }

  createResource(newResource: { name: string; type: string; }): Observable<Resource> {
    return this.http.post<Resource>(this.apiUrl, newResource);
  }
  

}