package com.urbana.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO para representação de um usuário")
public class UserDTO {
    @Schema(description = "ID do usuário (gerado automaticamente)", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "teste", required = true)
    private String name;

    @Schema(description = "Email do usuário", example = "teste@example.com", required = true)
    private String email;

    @Schema(description = "Senha do usuário (opcional para updates)", example = "123")
    private String password;

    private List<String> roles;

    private List<CardDTO> cards;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardDTO> cards) {
        this.cards = cards;
    }
}