package com.likelion.remini.repository;

import com.likelion.remini.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);

    List<User> findByExpirationDateBetween(LocalDateTime start, LocalDateTime end);
}
