package com.urbana.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para criação de um novo cartão")
public class CardCreateDTO {
    @Schema(description = "Número do cartão", example = "1234567890", required = true)
    private Long cardNumber;

    @Schema(description = "Nome no cartão", example = "Arthur Victor", required = true)
    private String name;

    @Schema(description = "Status inicial (default: true)", example = "true")
    private boolean status = true;

    @Schema(description = "Tipo do cartão", example = "COMUM", required = true)
    private String cardType;

    @Schema(description = "ID do usuário associado", example = "1", required = true)
    private Long userId;

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}