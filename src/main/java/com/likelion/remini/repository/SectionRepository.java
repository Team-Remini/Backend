package com.likelion.remini.repository;

import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    // Remini 엔티티와 연관된 Section 엔티티 중에서 Remini 객체에 해당하는 것을 모두 삭제
    void deleteAllByRemini(Remini remini);
}
