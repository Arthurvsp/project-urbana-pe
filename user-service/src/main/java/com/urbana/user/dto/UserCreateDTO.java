package com.urbana.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO para criação de um novo usuário")
public class UserCreateDTO {
    @Schema(description = "Nome do usuário", example = "Arthur", required = true)
    private String name;

    @Schema(description = "Email do usuário", example = "arthur@example.com", required = true)
    private String email;

    @Schema(description = "Senha do usuário", example = "senhaSegura123", required = true)
    private String password;

    @Schema(description = "Lista de roles para o usuário (ex: ['ADMIN', 'USER']). Default: ['USER']", example = "[\"USER\"]")
    private List<String> roles;

    // Getters e Setters
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