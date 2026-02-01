package com.urbana.card.client;

import com.urbana.card.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "user")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);

    @PutMapping("/api/users/{userId}/cards/{cardNumber}")
    void addCardToUser(@PathVariable Long userId, @PathVariable Long cardNumber);

    @DeleteMapping("/api/users/{userId}/cards/{cardNumber}")
    void removeCardFromUser(@PathVariable Long userId, @PathVariable Long cardNumber);
}