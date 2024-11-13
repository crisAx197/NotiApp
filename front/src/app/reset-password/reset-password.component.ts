import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  templateUrl: './reset-password.component.html',
  imports: [
    FormsModule,
    CommonModule
  ],
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  message: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }

  resetPassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      this.message = '';
      return;
    }

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
    if (!passwordRegex.test(this.newPassword)) {
      this.errorMessage = 'La contraseña debe tener entre 8 y 20 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un carácter especial';
      this.message = '';
      return;
    }

    const params = {
      token: this.token,
      newPassword: this.newPassword
    };

    this.http.post('http://localhost:8080/usuarios/reset-password', null, { params })
      .subscribe({
        next: (response: any) => {
          this.message = 'Contraseña cambiada exitosamente';
          this.errorMessage = '';
          setTimeout(() => this.router.navigate(['/login']), 2000);
        },
        error: (error) => {
          this.errorMessage = 'Hubo un error. Verifica el token y vuelve a intentar.';
          this.message = '';
        }
      });
  }
}
