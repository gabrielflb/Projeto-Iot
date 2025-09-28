import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, of } from 'rxjs'; 
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Usuario } from '../entity/Usuario'; 

interface DecodedToken {
  sub: string;
  exp: number;
  iat: number;
  scope?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';
  private tokenKey = 'iot_token';

  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
  }

  public tryLoadUserFromToken(): void {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken: DecodedToken = jwtDecode(token);
        const isExpired = Date.now() >= decodedToken.exp * 1000;
        if (isExpired) {
          this.logout(); 
        } else {
          this.loadUserProfile(decodedToken.sub).subscribe();
        }
      } catch (error) {
        console.error("Token inválido no localStorage", error);
        this.logout();
      }
    }
  }

  login(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          const token = response.acessToken;
          this.setToken(token);
          const decodedToken: DecodedToken = jwtDecode(token);
          this.loadUserProfile(decodedToken.sub).subscribe();
        })
      );
  }

  loadUserProfile(userId: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/usuario/${userId}`).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
      }),
      catchError(() => {
        this.logout();
        return of(null as any); 
      })
    );
  }
  
  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
  
  isAdmin(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    const decodedToken: DecodedToken = jwtDecode(token);
    // console.log('Token Decodificado:', decodedToken); 
    return decodedToken.scope?.includes('ROLE_ADMIN') || false;
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuario`, userData);
  }
}