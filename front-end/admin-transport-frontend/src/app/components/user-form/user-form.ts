// user-form.ts (adicionado Validators.required para password no create, e roles default)
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../../services/user';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.css']
})
export class UserFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  public route = inject(ActivatedRoute);
  public router = inject(Router);
  userForm: FormGroup;
  isEdit = false;
  userId?: number;
  showPassword = false;

  constructor() {
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(8)]],  // ← Required será adicionado dinamicamente no ngOnInit
      roles: [['USER']]  // ← Default para novo usuário
    });
  }

  ngOnInit(): void {
    this.userId = +this.route.snapshot.paramMap.get('id')!;
    if (this.userId) {
      this.isEdit = true;
      this.loadUser();
    } else {
      // Para create, tornar password required
      this.userForm.get('password')?.addValidators(Validators.required);
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  loadUser(): void {
    this.userService.getUserById(this.userId!).subscribe(user => {
      this.userForm.patchValue(user);
    });
  }

  saveUser(): void {
    if (this.userForm.valid) {
      const userData = this.userForm.value;
      if (this.isEdit) {
        // Para update, remover password se vazio
        if (!userData.password) {
          delete userData.password;
        }
        this.userService.updateUser(this.userId!, userData).subscribe(() => {
          this.router.navigate(['/users']);
        });
      } else {
        this.userService.createUser(userData).subscribe(() => {
          this.router.navigate(['/users']);
        });
      }
    } else {
      console.log('Form inválido:', this.userForm.errors);  // ← Para debug, remova depois
    }
  }

  cancel(): void {
    this.router.navigate(['/users']);
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
}