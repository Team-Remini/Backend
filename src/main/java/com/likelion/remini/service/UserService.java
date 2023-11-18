package com.likelion.remini.service;

import com.likelion.remini.domain.State;
import com.likelion.remini.domain.User;
import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.exception.UserErrorResult;
import com.likelion.remini.exception.UserException;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;

    /* 회원 관리 */
    /**
     * 요청자의 사용자정보를 조회하는 메서드이다.
     *
     * @return userResponseDTO 요청자의 사용자 정보
     */
    @Transactional
    public UserResponseDTO getUserInfo(){
        User user = getUser();
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .nickName(user.getNickname())
                .state(user.getState())
                .expirationDate(user.getExpirationDate())
                .profileImageUrl(user.getProfileImageURL())
                .build();
        return userResponseDTO;
    }

    /**
     * 사용자의 구독 유형을 갱신하는 메서드이다.
     *
     * @param newState 사용자의 갱신되는 구독 유형
     */
    @Transactional
    public void updateUserState(State newState){
        User userToUpdate = getUser();
        //state가 standard -> premium : state 변경, 현재 시각 기준 + 1달이 expiration date로 설정
        //state가 premium -> standard : state 변경, expiration date null 설정
        //state가 premium -> premium(갱신) : 현재 시각 기준 + 1달이 expiration date로 설정
        if (userToUpdate.getState().equals(newState) && newState.equals(State.PREMIUM)){
            //state가 premium -> premium(갱신)
            userToUpdate.setExpirationDate();
            userRepository.save(userToUpdate);
        }else if(!userToUpdate.getState().equals(newState) && newState.equals(State.PREMIUM)){
            //state가 standard -> premium
            userToUpdate.standardToPremium();
            userToUpdate.setExpirationDate();
            userRepository.save(userToUpdate);
        }else if(!userToUpdate.getState().equals(newState) && newState.equals(State.STANDARD)){
            //state가 premium -> standard
            userToUpdate.premiumToStandard();
            userToUpdate.initializeExpirationDate();
            userRepository.save(userToUpdate);
        }

    }
    /**
     * 프리미엄 사용자의 구독 유형을 자동 갱신하는 메서드이다.
     *
     *
     */
    @Transactional
    public void automaticUpdatePremiumUserState(){
        LocalDateTime currentTime = LocalDateTime.now();
        List<Long> userIdList = userRepository.findUserIdListAfterExpiration(currentTime);
        for(Long userId : userIdList){
            User userToUpdate = userRepository.findById(userId)
                    .orElseThrow(()->new UserException(UserErrorResult.USER_NOT_FOUND));
            userToUpdate.setExpirationDate();
            userRepository.save(userToUpdate);
        }
    }



    private User getUser() {
        Long userId = authTokensGenerator.extractMemberId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }
}
