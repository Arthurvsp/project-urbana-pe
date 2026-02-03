import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { inject } from '@angular/core';
import { CardService } from '../../services/card';
import { UserService } from '../../services/user';

@Component({
  selector: 'app-card-management',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './card-management.html',
  styleUrls: ['./card-management.css']
})
export class CardManagementComponent implements OnInit {
  public route = inject(ActivatedRoute);
  private userService = inject(UserService);
  private cardService = inject(CardService);
  public router = inject(Router);
  userId: number;
  user: any;
  cards: any[] = [];
  newCard: any = { cardNumber: null, name: '', status: true, cardType: 'COMUM', userId: null };
  showNewCardForm = false; 

  constructor() {
    this.userId = +this.route.snapshot.paramMap.get('id')!;
    this.newCard.userId = this.userId;
  }

  ngOnInit(): void {
    this.loadUser();
    this.loadCards();
  }

  loadUser(): void {
    this.userService.getUserById(this.userId).subscribe(user => {
      this.user = user;
    });
  }

  loadCards(): void {
    this.cardService.getCardsByUserId(this.userId).subscribe(cards => {
      this.cards = cards;
    });
  }

  toggleNewCardForm(): void { 
    this.showNewCardForm = !this.showNewCardForm;
  }

  addNewCard(): void {
    this.cardService.addCard(this.newCard).subscribe(() => {
      this.loadCards();
      this.newCard = { cardNumber: null, name: '', status: true, cardType: 'COMUM', userId: this.userId };
      this.showNewCardForm = false;  
    });
  }

  toggleStatus(card: any): void {
    this.cardService.toggleCardStatus(card.cardNumber).subscribe(updatedCard => {
      const index = this.cards.findIndex(c => c.id === updatedCard.id);
      this.cards[index] = updatedCard;
    });
  }

  removeCard(cardNumber: number): void {
    if (confirm('Tem certeza?')) {
      this.cardService.removeCardByNumber(cardNumber).subscribe(() => {
        this.loadCards();
      });
    }
  }

  saveChanges(): void {
    this.router.navigate(['/users']);
  }
}