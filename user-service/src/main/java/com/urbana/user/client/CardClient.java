package com.urbana.user.client;

import com.urbana.user.dto.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "card")
public interface CardClient {
    @GetMapping("/api/cards/user/{userId}")
    List<CardDTO> getCardsByUserId(@PathVariable Long userId);
}