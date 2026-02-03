import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../../services/user';

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
  users: any[] = [];
  searchTerm: string = '';

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(users => {
      this.users = users;
    });
  }

  filterUsers(): any[] {
    return this.users.filter(user => 
      user.name.toLowerCase().includes(this.searchTerm.toLowerCase()) || 
      user.email.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  manageCards(userId: number): void {
    this.router.navigate(['/users', userId, 'cards']);
  }

  editUser(userId: number): void {
    this.router.navigate(['/users/edit', userId]);
  }

  deleteUser(userId: number): void {
    if (confirm('Tem certeza que deseja excluir?')) {
      this.userService.deleteUser(userId).subscribe(() => {
        this.loadUsers();
      });
    }
  }

  newUser(): void {
    this.router.navigate(['/users/new']);
  }
}