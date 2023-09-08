package com.vmlg.bank.bank.repositores;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.vmlg.bank.bank.domain.user.User;

public interface UserRepository extends JpaRepository<User,UUID> {
    Optional<User> findUserByDocument(String document);
    Optional<User> findUserById(UUID id);
    UserDetails findUserByEmail(String email);
}
