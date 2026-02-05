import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../../services/user';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './user-list.html',
  styleUrls: ['./user-list.css']
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  public router = inject(Router);
  private keycloak = inject(KeycloakService);
  private cdr = inject(ChangeDetectorRef); 

  users: any[] = [];
  filteredUsers: any[] = [];
  searchTerm: string = '';

  ngOnInit(): void {
    this.searchTerm = ''; 
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        console.log('[UserList] Usuários recebidos do backend:', data);
        this.users = data || [];
        this.applyFilter();
        this.cdr.detectChanges(); 
        console.log('[UserList] this.users após carregar:', this.users);
        console.log('[UserList] filteredUsers após aplicar filtro:', this.filteredUsers);
      },
      error: (err) => {
        console.error('[UserList] Erro ao carregar usuários:', err);
        alert('Não foi possível carregar a lista de usuários. Veja o console.');
        this.cdr.detectChanges();
      }
    });
  }

  applyFilter(): void {
    const term = (this.searchTerm || '').trim().toLowerCase();
    
    if (!term) {
      this.filteredUsers = [...this.users];
    } else {
      this.filteredUsers = this.users.filter(user => 
        (user.name || '').toLowerCase().includes(term) ||
        (user.email || '').toLowerCase().includes(term)
      );
    }

    this.cdr.detectChanges(); 
    console.log('[UserList] Filtro aplicado. Termo:', term);
    console.log('[UserList] Usuários filtrados:', this.filteredUsers);
  }

  get displayedUsers(): any[] {
    return this.filteredUsers;
  }

  manageCards(userId: number): void {
    this.router.navigate(['/users', userId, 'cards']);
  }

  editUser(userId: number): void {
    this.router.navigate(['/users/edit', userId]);
  }

  deleteUser(userId: number): void {
    if (confirm('Tem certeza que deseja excluir este usuário?')) {
      this.userService.deleteUser(userId).subscribe({
        next: () => {
          this.loadUsers();
          alert('Usuário excluído com sucesso.');
        },
        error: (err) => {
          console.error('Erro ao excluir:', err);
          alert('Erro ao excluir o usuário.');
        }
      });
    }
  }

  newUser(): void {
    this.router.navigate(['/users/new']);
  }

  logout(): void {
    this.keycloak.logout();
  }
}