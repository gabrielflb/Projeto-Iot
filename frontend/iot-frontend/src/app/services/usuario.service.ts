import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
   private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient, private router: Router) { }
}
