package com.likelion.remini.service;

import com.likelion.remini.jwt.AuthTokens;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.jwt.OAuthInfoResponse;
import com.likelion.remini.jwt.OAuthLoginParams;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.likelion.remini.domain.User;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long userId = findOrCreateUser(oAuthInfoResponse);
        return authTokensGenerator.generate(userId);
    }

    private Long findOrCreateUser(OAuthInfoResponse oAuthInfoResponse) {
        return userRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(User::getId)
                .orElseGet(() -> newUser(oAuthInfoResponse));
    }

    private Long newUser(OAuthInfoResponse oAuthInfoResponse) {
        User user = User.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .profileImageURL((oAuthInfoResponse.getProfileImageURL()))
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .state("standard")
                .build();

        return userRepository.save(user).getId();
    }
}