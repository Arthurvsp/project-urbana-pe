package com.urbana.card.repository;

import com.urbana.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Long userId);

    boolean existsByCardNumber(Long cardNumber);

    Optional<Card> findByCardNumber(Long cardNumber);

    void deleteByCardNumber(Long cardNumber);
}