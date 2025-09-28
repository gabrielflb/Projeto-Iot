import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'iot-frontend';
  constructor(private authService: AuthService) {} 
  ngOnInit(): void {
    this.authService.tryLoadUserFromToken();
  }
}
