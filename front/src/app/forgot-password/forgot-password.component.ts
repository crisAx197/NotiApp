import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
  imports: [CommonModule, FormsModule]  // Asegúrate de incluir FormsModule aquí
})
export class ForgotPasswordComponent {
  correo: string = '';
  message: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }

  recoverPassword() {
    this.http.post('http://localhost:8080/usuarios/recover-password', null, {
      params: { correo: this.correo }
    }).subscribe({
      next: (response: any) => {
        this.message = response.message;
        this.errorMessage = '';
      },
      error: (error) => {
        this.errorMessage = error.error.message || 'Ocurrió un error al recuperar la contraseña.';
        this.message = '';
      }
    });
  }
}
