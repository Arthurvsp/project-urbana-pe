package com.urbana.user.service;

import com.urbana.user.client.CardClient;
import com.urbana.user.dto.UserCreateDTO;
import com.urbana.user.dto.UserDTO;
import com.urbana.user.model.Role;
import com.urbana.user.model.User;
import com.urbana.user.repository.RoleRepository;
import com.urbana.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CardClient cardClient;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::convertToDTO).orElse(null);
    }

    public UserDTO createUser(UserCreateDTO createDTO) {
        // Cria o novo usuário (sem nenhuma validação de permissão)
        User user = new User();
        user.setName(createDTO.getName());
        user.setEmail(createDTO.getEmail());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        // Roles solicitadas (ou default USER)
        List<String> requestedRoles = createDTO.getRoles();
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            requestedRoles = List.of("USER");
        }

        // Processa as roles
        List<Role> roles = new ArrayList<>();
        for (String roleName : requestedRoles) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
            roles.add(role);
        }
        user.setRoles(roles);

        // Salva e retorna DTO
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            User updatedUser = userRepository.save(user);
            return convertToDTO(updatedUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        try {
            dto.setCards(cardClient.getCardsByUserId(user.getId()));
        } catch (Exception e) {
            dto.setCards(new ArrayList<>());  // Fallback se CARD down
        }
        return dto;
    }

    public void addCardToUser(Long userId, Long cardNumber) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getCards().contains(cardNumber)) {
                user.getCards().add(cardNumber);
                userRepository.save(user);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    // Novo: Remover cardNumber da lista do user
    public void removeCardFromUser(Long userId, Long cardNumber) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.getCards().remove(cardNumber);
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }
}