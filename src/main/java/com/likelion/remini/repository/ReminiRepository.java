package com.likelion.remini.repository;

import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.Type;
import com.likelion.remini.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ReminiRepository extends JpaRepository<Remini, Long> {

    Page<Remini> findAllByUserAndInstantSave(PageRequest pageRequest, User user, Boolean instantSave);

    Page<Remini> findAllByTypeAndInstantSave(PageRequest pageRequest, Type type, Boolean instantSave);

    Page<Remini> findAllByInstantSave(PageRequest pageRequest, Boolean instantSave);

    Optional<Remini> findFirstByUserOrderByModifiedDateDesc(User user);
}
