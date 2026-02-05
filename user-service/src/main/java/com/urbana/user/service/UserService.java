package com.urbana.user.service;

import com.urbana.user.client.CardClient;
import com.urbana.user.dto.UserCreateDTO;
import com.urbana.user.dto.UserDTO;
import com.urbana.user.dto.UserUpdateDTO;
import com.urbana.user.model.Role;
import com.urbana.user.model.User;
import com.urbana.user.repository.RoleRepository;
import com.urbana.user.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Keycloak keycloakAdmin;

    @Value("${keycloak.target.realm}")
    private String targetRealm;

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
        // Criar usuário no Keycloak
        RealmResource realmResource = keycloakAdmin.realm(targetRealm);
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(createDTO.getEmail());
        userRep.setEmail(createDTO.getEmail());
        userRep.setFirstName(createDTO.getName());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);

        Response response = realmResource.users().create(userRep);
        if (response.getStatus() != 201) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar usuário no Keycloak: " + response.getStatusInfo().getReasonPhrase());
        }

        // Extrair ID do usuário criado no Keycloak (do Location header)
        String keycloakUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Definir senha no Keycloak
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(createDTO.getPassword());
        realmResource.users().get(keycloakUserId).resetPassword(credential);

        // Atribuir roles no Keycloak
        List<String> requestedRoles = createDTO.getRoles();
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            requestedRoles = List.of("USER");
        }
        for (String roleName : requestedRoles) {
            RoleRepresentation roleRep = realmResource.roles().get(roleName).toRepresentation();
            realmResource.users().get(keycloakUserId).roles().realmLevel().add(List.of(roleRep));
        }

        // Agora, salvar no banco local da mesma forma que antes, incluindo password encoded, mais keycloak_id
        User user = new User();
        user.setName(createDTO.getName());
        user.setEmail(createDTO.getEmail());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setKeycloakId(keycloakUserId);

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

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (updateDTO.getName() != null && !updateDTO.getName().isEmpty()) {
                user.setName(updateDTO.getName());
            }
            if (updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()) {
                user.setEmail(updateDTO.getEmail());
            }
            if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
            }

            String keycloakId = user.getKeycloakId();
            if (keycloakId != null) {
                RealmResource realm = keycloakAdmin.realm(targetRealm);
                UserRepresentation userRep = realm.users().get(keycloakId).toRepresentation();

                if (updateDTO.getName() != null && !updateDTO.getName().isEmpty()) {
                    userRep.setFirstName(updateDTO.getName());
                }
                if (updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()) {
                    userRep.setEmail(updateDTO.getEmail());
                }
                realm.users().get(keycloakId).update(userRep);

                if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
                    CredentialRepresentation cred = new CredentialRepresentation();
                    cred.setTemporary(false);
                    cred.setType(CredentialRepresentation.PASSWORD);
                    cred.setValue(updateDTO.getPassword());
                    realm.users().get(keycloakId).resetPassword(cred);
                }

                if (updateDTO.getRoles() != null && !updateDTO.getRoles().isEmpty()) {
                    List<RoleRepresentation> currentRoles = realm.users().get(keycloakId).roles().realmLevel().listAll();
                    if (!currentRoles.isEmpty()) {
                        realm.users().get(keycloakId).roles().realmLevel().remove(currentRoles);
                    }

                    for (String roleName : updateDTO.getRoles()) {
                        RoleRepresentation roleRep = realm.roles().get(roleName).toRepresentation();
                        realm.users().get(keycloakId).roles().realmLevel().add(List.of(roleRep));
                    }
                    
                    List<Role> newRoles = new ArrayList<>();
                    for (String roleName : updateDTO.getRoles()) {
                        Role role = roleRepository.findByName(roleName);
                        if (role == null) {
                            role = new Role();
                            role.setName(roleName);
                            roleRepository.save(role);
                        }
                        newRoles.add(role);
                    }
                    user.setRoles(newRoles);
                }
            }

            User updatedUser = userRepository.save(user);
            return convertToDTO(updatedUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Deletar do Keycloak
            if (user.getKeycloakId() != null) {
                RealmResource realmResource = keycloakAdmin.realm(targetRealm);
                realmResource.users().get(user.getKeycloakId()).remove();
            }
            userRepository.deleteById(id);
        }
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
            dto.setCards(new ArrayList<>());
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