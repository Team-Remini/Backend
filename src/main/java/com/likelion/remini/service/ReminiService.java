package com.likelion.remini.service;

import com.likelion.remini.domain.Like;
import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.User;
import com.likelion.remini.dto.ReminiDetailResponse;
import com.likelion.remini.exception.ReminiNotFoundException;
import com.likelion.remini.exception.UserNotFoundException;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.LikeRepository;
import com.likelion.remini.repository.ReminiRepository;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminiService {

    private final ReminiRepository reminiRepository;
    private final UserRepository userRepository;

    private final LikeRepository likeRepository;
    private final AuthTokensGenerator authTokensGenerator;

    /* 회고 관리 */



    /* 회고 조회 */

    public ReminiDetailResponse getDetail(Long reminiId) {

        Long userId = authTokensGenerator.extractMemberId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 userid로 찾을 수 없습니다 : " + userId));

        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(() -> new ReminiNotFoundException("회고를 찾을 수 없음"));

        Like like = likeRepository.findByUserAndRemini(user, remini);

        return ReminiDetailResponse.create(remini, user, like != null);
    }


}
