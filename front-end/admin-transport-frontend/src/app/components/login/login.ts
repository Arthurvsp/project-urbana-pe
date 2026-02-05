import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  private keycloak = inject(Keycloak);
  private router = inject(Router);

  async login(): Promise<void> {
    await this.keycloak.login();
    this.router.navigate(['/users']);
  }
}
