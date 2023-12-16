package com.likelion.remini.repository;

import com.likelion.remini.domain.QUser;
import com.likelion.remini.domain.State;
import com.likelion.remini.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findUserListAfterAlarm(LocalDateTime currentTime){
        QUser qUser = QUser.user;

        return queryFactory
                .select(qUser)
                .from(qUser)
                .where(qUser.alarmTime.loe(currentTime))
                .fetch();
    }

    @Override
    public List<User> findUserListAfterExpirationAndToBeStateStandard(LocalDateTime currentTime){
        QUser qUser = QUser.user;

        return queryFactory
                .select(qUser)
                .from(qUser)
                .where(qUser.expirationDate.loe(currentTime).and(qUser.toBeState.eq(State.STANDARD)))
                .fetch();
    }


}
