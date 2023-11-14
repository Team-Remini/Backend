package com.likelion.remini.repository;

import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.User;
import com.likelion.remini.dto.ReminiPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReminiRepository extends JpaRepository<Remini, Long> {

    Page<Remini> findAllByUserAndInstantSave(PageRequest pageRequest, User user, Boolean instantSave);

    Page<Remini> findAllByTypeAndInstantSave(PageRequest pageRequest, String type, Boolean instantSave);

    Page<Remini> findAllByInstantSave(PageRequest pageRequest, Boolean instantSave);
}
