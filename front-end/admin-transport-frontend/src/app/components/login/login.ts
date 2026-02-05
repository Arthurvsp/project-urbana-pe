import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit {
  private keycloak = inject(KeycloakService);
  private router = inject(Router);

  async ngOnInit(): Promise<void> {
    await this.login();
  }

  async login(): Promise<void> {
    await this.keycloak.login({
      redirectUri: window.location.origin + '/users'
    });
    this.router.navigate(['/users']);
  }
}