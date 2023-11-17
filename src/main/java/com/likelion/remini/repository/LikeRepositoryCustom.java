package com.likelion.remini.repository;

import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.User;

import java.util.List;

public interface LikeRepositoryCustom {

    List<Long> findReminiIdList(User user, List<Remini> remini);
}
