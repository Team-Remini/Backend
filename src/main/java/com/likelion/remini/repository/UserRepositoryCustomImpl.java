package com.likelion.remini.repository;

import com.likelion.remini.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findUserIdListAfterAlarm(LocalDateTime currentTime){
        QUser qUser = QUser.user;

        return queryFactory
                .select(qUser.id)
                .from(qUser)
                .where(qUser.alarmTime.isNotNull(), qUser.alarmTime.after(currentTime))
                .fetch();
    }

    @Override
    public List<Long> findUserIdListAfterExpiration(LocalDateTime currentTime){
        QUser qUser = QUser.user;

        return queryFactory
                .select(qUser.id)
                .from(qUser)
                .where(qUser.expirationDate.isNotNull().and(qUser.expirationDate.after(currentTime)))
                .fetch();
    }


}
