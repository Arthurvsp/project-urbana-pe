import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.LoginComponent) },
  { path: 'users', loadComponent: () => import('./components/user-list/user-list').then(m => m.UserListComponent), canActivate: [authGuard] },
  { path: 'users/new', loadComponent: () => import('./components/user-form/user-form').then(m => m.UserFormComponent), canActivate: [authGuard] },
  { path: 'users/edit/:id', loadComponent: () => import('./components/user-form/user-form').then(m => m.UserFormComponent), canActivate: [authGuard] },
  { path: 'users/:id/cards', loadComponent: () => import('./components/card-management/card-management').then(m => m.CardManagementComponent), canActivate: [authGuard] },
  { path: '**', redirectTo: '/login' }
];