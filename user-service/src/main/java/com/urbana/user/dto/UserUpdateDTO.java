package com.urbana.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO para atualização de um usuário existente")
public class UserUpdateDTO {
    @Schema(description = "Nome do usuário", example = "Arthur Atualizado")
    private String name;

    @Schema(description = "Email do usuário", example = "arthur.atualizado@example.com")
    private String email;

    @Schema(description = "Senha do usuário (opcional, para resetar)", example = "novaSenha123")
    private String password;

    @Schema(description = "Lista de roles para o usuário (opcional, ex: ['ADMIN', 'USER'])", example = "[\"ADMIN\", \"USER\"]")
    private List<String> roles;

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}