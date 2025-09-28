import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'] // Pode usar o mesmo estilo do login
})
export class RegisterComponent {
  userData = {
    nome: '',
    email: '',
    senha: ''
  };
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.userData.nome || !this.userData.email || !this.userData.senha) {
      this.errorMessage = 'Por favor, preencha todos os campos.';
      return;
    }

    this.authService.register(this.userData).subscribe({
      next: () => {
        this.successMessage = 'Cadastro realizado com sucesso! Redirecionando para o login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        // Pega a mensagem de erro do backend (ex: "E-mail já cadastrado")
        this.errorMessage = err.error.message || 'Ocorreu um erro no cadastro. Tente novamente.';
        console.error(err);
      }
    });
  }
}