import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  private http = inject(HttpClient);
  private router = inject(Router);
  credentials = { email: '', password: '' };
  error: string = '';

  login(): void {
    this.http.post<any>('http://localhost:8081/api/auth/login', this.credentials).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.error = 'Credenciais inválidas';
      }
    });
  }
}