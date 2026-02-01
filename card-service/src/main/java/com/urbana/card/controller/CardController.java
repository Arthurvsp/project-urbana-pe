package com.urbana.card.controller;

import com.urbana.card.dto.CardCreateDTO;
import com.urbana.card.dto.CardDTO;
import com.urbana.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Card Controller", description = "API para gerenciamento de cartões")
@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Operation(summary = "Lista todos os cartões")
    @GetMapping
    public List<CardDTO> getAllCards() {
        return cardService.getAllCards();
    }

    @Operation(summary = "Adiciona um cartão")
    @PostMapping
    public ResponseEntity<CardDTO> addCard(@RequestBody CardCreateDTO createDTO) {
        CardDTO addedCard = cardService.addCard(createDTO);
        return ResponseEntity.ok(addedCard);
    }

    @Operation(summary = "Remove um cartão pelo número do cartão")
    @DeleteMapping("/number/{cardNumber}")
    public ResponseEntity<Void> removeCardByNumber(@PathVariable Long cardNumber) {
        cardService.removeCardByNumber(cardNumber);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativa ou inativa um cartão pelo número do cartão")
    @PutMapping("/number/{cardNumber}/toggle")
    public ResponseEntity<CardDTO> toggleCardStatusByNumber(@PathVariable Long cardNumber) {
        CardDTO updatedCard = cardService.toggleCardStatusByNumber(cardNumber);
        return ResponseEntity.ok(updatedCard);
    }

    @Operation(summary = "Lista cartões de um usuário")
    @GetMapping("/user/{userId}")
    public List<CardDTO> getCardsByUserId(@PathVariable Long userId) {
        return cardService.getCardsByUserId(userId);
    }
}