package com.likelion.remini.repository;

import com.likelion.remini.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {
    List<User> findUserListAfterAlarm(LocalDateTime currentTime);
    List<User> findUserListAfterExpirationAndToBeStateStandard(LocalDateTime currentTime);
}
