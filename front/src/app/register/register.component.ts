import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import {AuthGoogleService} from '../auth-google.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  nombre: string = '';
  correo: string = '';
  edad: number | null = null;
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router, private authGoogleService: AuthGoogleService) {}

  validatePassword(password: string): boolean {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
    return passwordRegex.test(password);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  register_google(){
    this.authGoogleService.login();
  };

  register() {
    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (!this.validatePassword(this.password)) {
      this.errorMessage =
        'La contraseña debe tener entre 8 y 20 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales';
      return;
    }

    const userData = {
      nombre: this.nombre,
      correo: this.correo,
      edad: this.edad,
      password: this.password,
    };


    this.authService.register(userData).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: () => {
        this.errorMessage = 'Error en el registro. Intente nuevamente.';
      },
    });
  }
}
