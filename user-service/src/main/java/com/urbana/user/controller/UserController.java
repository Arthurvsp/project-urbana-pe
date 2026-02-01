package com.urbana.user.controller;

import com.urbana.user.dto.UserCreateDTO;
import com.urbana.user.dto.UserDTO;
import com.urbana.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "API para gerenciamento de usuários")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Lista todos os usuários")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Consulta um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return userDTO != null ? ResponseEntity.ok(userDTO) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cria um novo usuário")
    @PostMapping
    public UserDTO createUser(@RequestBody UserCreateDTO createDTO) {
        return userService.createUser(createDTO);
    }

    @Operation(summary = "Alterar/atualizar um usuário existente")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove um usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adiciona um cardNumber à lista de um usuário")
    @PutMapping("/{userId}/cards/{cardNumber}")
    public ResponseEntity<Void> addCardToUser(@PathVariable Long userId, @PathVariable Long cardNumber) {
        userService.addCardToUser(userId, cardNumber);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove um cardNumber da lista de um usuário")
    @DeleteMapping("/{userId}/cards/{cardNumber}")
    public ResponseEntity<Void> removeCardFromUser(@PathVariable Long userId, @PathVariable Long cardNumber) {
        userService.removeCardFromUser(userId, cardNumber);
        return ResponseEntity.noContent().build();
    }
}