import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../../services/user';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.css'],
})
export class UserFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  public route = inject(ActivatedRoute);
  public router = inject(Router);
  private keycloak = inject(KeycloakService);

  userForm: FormGroup;
  isEdit = false;
  userId?: number;
  isAdmin = false;

  constructor() {
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      roles: [['USER']],
    });
  }

  async ngOnInit(): Promise<void> {
    this.isAdmin = await this.keycloak.isUserInRole('ADMIN');
    this.userId = +this.route.snapshot.paramMap.get('id')!;
    if (this.userId) {
      this.isEdit = true;
      this.loadUser();
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  loadUser(): void {
    this.userService.getUserById(this.userId!).subscribe((user) => {
      this.userForm.patchValue({
        name: user.name,
        email: user.email,
        roles: user.roles || ['USER'],
      });
    });
  }

  saveUser(): void {
    if (this.userForm.valid) {
      let userData = this.userForm.value;

      if (!this.isAdmin) {
        delete userData.roles;
      }

      if (this.isEdit && (!userData.password || userData.password.trim() === '')) {
        delete userData.password;
      }

      if (this.isEdit) {
        this.userService.updateUser(this.userId!, userData).subscribe(() => {
          this.router.navigate(['/users']);
        });
      } else {
        this.userService.createUser(userData).subscribe(() => {
          this.router.navigate(['/users']);
        });
      }
    }
  }

  cancel(): void {
    this.router.navigate(['/users']);
  }

  logout(): void {
    this.keycloak.logout();
  }
}
