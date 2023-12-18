package com.likelion.remini.service;

import com.likelion.remini.domain.State;
import com.likelion.remini.domain.User;
import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.exception.UserErrorResult;
import com.likelion.remini.exception.UserException;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
                .toBeState(user.getToBeState())
                .expirationDate(user.getExpirationDate())
                .profileImageUrl(user.getProfileImageURL())
                .alarmTime(user.getAlarmTime())
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
        //state가 premium(toBeState = standard) -> premium(toBeState= premium) 해지 취소
        //state가 standard -> premium : state 변경, 현재 시각 기준 + 1달이 expiration date로 설정
        //state가 premium -> standard : state 변경, expiration date null 설정
        //state가 premium -> premium(갱신) : 현재 시각 기준 + 1달이 expiration date로 설정
        if (userToUpdate.getState().equals(newState) && userToUpdate.getToBeState().equals(State.STANDARD) && newState.equals(State.PREMIUM)){
            userToUpdate.toBeStateStandardToPremium();
            userRepository.save(userToUpdate);
        } else if (userToUpdate.getState().equals(newState) && newState.equals(State.PREMIUM)){
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
            userRepository.save(userToUpdate);
        }

    }

    /**
     * 프리미엄 사용자의 구독자 중 toBeState가 stadard인 사용자
     * 유형을 만료일자 넘길 시 자동 갱신하는 메서드이다.
     *
     */
    @Transactional
    @Scheduled(cron = "0 0 9 * * *") // 매일 9시마다 실행
    public void automaticUpdatePremiumUserState(){
        LocalDateTime currentTime = LocalDateTime.now();
        List<User> userList = userRepository.findUserListAfterExpirationAndToBeStateStandard(currentTime);
        for(User userToUpdate : userList){
            userToUpdate.premiumExpired();
            userRepository.save(userToUpdate);
        }
    }

    /**
     * 탈퇴하기 기능
     *
     */
    @Transactional
    public void deleteUser(){
        User user = getUser();
        userRepository.delete(user);
    }


    private User getUser() {
        Long userId = authTokensGenerator.extractMemberId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }
}
