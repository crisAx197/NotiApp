import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthGoogleService } from '../auth-google.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router, private authGoogleService: AuthGoogleService) {}

  goToRegister() {
    this.router.navigate(['/register']);
  }

  goToForgotPassword() {
    this.router.navigate(['/forgot-password']);
  }

  login_google(){
    this.authGoogleService.login();
  }

  login() {
    this.authService.login(this.email, this.password).subscribe({
      next: response => {
        if (response.status === 200) {
          this.router.navigate(['/home']);
        }
      },
      error: err => {
        this.errorMessage = 'Correo o contraseña incorrectos';
      }
    });
  }
}
