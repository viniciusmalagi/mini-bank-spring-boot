package com.vmlg.bank.bank.repositores;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vmlg.bank.bank.domain.user.User;

public interface UserRepository extends JpaRepository<User,UUID> {
    Optional<User> findUserByDocument(String document);

    Optional<User> findUserById(UUID id);
}
