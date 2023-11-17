package com.likelion.remini.service;

import com.likelion.remini.domain.User;
import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.jwt.AuthTokensGenerator;
import org.springframework.mail.javamail.JavaMailSender;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;

    /* 회원 관리 */
    //사용자 정보 조회 api
    @Transactional
    public UserResponseDTO getUserInfo(){
        Long userId = authTokensGenerator.extractMemberId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .nickName(user.getNickname())
                .state(user.getState())
                .expirationDate(user.getExpirationDate())
                .profileImageUrl(user.getProfileImageURL())
                .build();
        return userResponseDTO;
    }

    //구독 모델 변경 api
    @Transactional
    public void updateUserState(String newState){
        Long userId = authTokensGenerator.extractMemberId();
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        //state가 standard -> premium : state 변경, 현재 시각 기준 + 1달이 expiration date로 설정
        //state가 premium -> standard : state 변경, expiration date null 설정
        //state가 premium -> premium(갱신) : 현재 시각 기준 + 1달이 expiration date로 설정
        if (userToUpdate.getState().equals(newState) && newState.equals("premium")){
            //state가 premium -> premium(갱신)
            userToUpdate.setExpirationDate();
            userRepository.save(userToUpdate);
        }else if(!userToUpdate.getState().equals(newState) && newState.equals("premium")){
            //state가 standard -> premium
            userToUpdate.standardToPremium();
            userToUpdate.setExpirationDate();
            userRepository.save(userToUpdate);
        }else if(!userToUpdate.getState().equals(newState) && newState.equals("standard")){
            //state가 premium -> standard
            userToUpdate.premiumToStandard();
            userToUpdate.initializeExpirationDate();
            userRepository.save(userToUpdate);
        }

    }


}
