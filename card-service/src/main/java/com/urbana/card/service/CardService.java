package com.urbana.card.service;

import com.urbana.card.client.UserClient;
import com.urbana.card.dto.CardCreateDTO;
import com.urbana.card.dto.CardDTO;
import com.urbana.card.dto.UserDTO;
import com.urbana.card.model.Card;
import com.urbana.card.model.CardType;
import com.urbana.card.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserClient userClient;

    public List<CardDTO> getAllCards() {
        return cardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CardDTO convertToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setName(card.getName());
        dto.setStatus(card.isStatus());
        dto.setCardType(card.getCardType().name());
        dto.setUserId(card.getUserId());
        return dto;
    }

    private Card convertToEntity(CardCreateDTO dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setName(dto.getName());
        card.setStatus(dto.isStatus());
        card.setCardType(CardType.valueOf(dto.getCardType()));
        return card;
    }

    public List<CardDTO> getCardsByUserId(Long userId) {
        return cardRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CardDTO addCard(CardCreateDTO createDTO) {
        UserDTO user = userClient.getUserById(createDTO.getUserId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        if (cardRepository.existsByCardNumber(createDTO.getCardNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de cartão já existe");
        }

        if (createDTO.getName() == null || createDTO.getName().isEmpty()) {
            createDTO.setName(user.getName());
        }

        Card card = convertToEntity(createDTO);
        card.setUserId(createDTO.getUserId());
        Card savedCard = cardRepository.save(card);
        userClient.addCardToUser(createDTO.getUserId(), savedCard.getCardNumber());
        return convertToDTO(savedCard);
    }

    @Transactional
    public void removeCardByNumber(Long cardNumber) {
        Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            Long userId = card.getUserId();
            cardRepository.deleteByCardNumber(cardNumber);
            userClient.removeCardFromUser(userId, cardNumber);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado");
        }
    }

    public CardDTO toggleCardStatusByNumber(Long cardNumber) {
        Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            card.setStatus(!card.isStatus());
            Card updatedCard = cardRepository.save(card);
            return convertToDTO(updatedCard);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão com número " + cardNumber + " não encontrado");
    }
}