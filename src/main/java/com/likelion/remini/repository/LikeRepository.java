package com.likelion.remini.repository;

import com.likelion.remini.domain.Like;
import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndRemini(User user, Remini remini);
    Optional<Like> findByUserAndRemini(User user, Remini remini);
}
