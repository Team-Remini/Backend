package com.likelion.remini.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {
    List<Long> findUserIdListAfterAlarm(LocalDateTime currentTime);
    List<Long> findUserIdListAfterExpiration(LocalDateTime currentTime);
}
