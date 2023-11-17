package com.likelion.remini.controller;

import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Api(tags = "User", description = "사용자 관련 API")
public class userController {
    private final UserService userService;
    /* 회원 관리 */
    //사용자 정보 조회 api
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserInfo(){
        UserResponseDTO userResponseDTO = userService.getUserInfo();
        return ResponseEntity.ok(userResponseDTO);
    }
    //구독 모델 변경 api
    @PatchMapping("/state")
    public ResponseEntity<Long> updateUserState(@RequestBody String newState){
        userService.updateUserState(newState);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
