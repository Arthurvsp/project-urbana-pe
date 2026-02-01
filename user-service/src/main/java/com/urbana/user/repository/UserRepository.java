package com.urbana.user.repository;

import com.urbana.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users.users WHERE email = ?1", nativeQuery = true)
    User findByEmailNative(String email);

    @Query(value = """
        SELECT COUNT(*) > 0 
        FROM users.users u 
        JOIN users.user_roles ur ON u.id = ur.user_id 
        JOIN users.roles r ON ur.role_id = r.id 
        WHERE r.name = 'ADMIN'
        """, nativeQuery = true)
    boolean existsAdminUser();
}