package com.likelion.remini.repository;

import com.likelion.remini.domain.QLike;
import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LikeRepositoryCustomImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findReminiIdList(User user, List<Remini> reminiList) {
        QLike qLike = QLike.like;

        return queryFactory
                .select(qLike.remini.reminiId)
                .from(qLike)
                .where(qLike.user.eq(user), qLike.remini.in(reminiList))
                .fetch();
    }
}
