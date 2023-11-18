package com.likelion.remini.controller;

import com.likelion.remini.domain.State;
import com.likelion.remini.dto.ReminiUpdateRequestDTO;
import com.likelion.remini.dto.StateUpdateRequest;
import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Api(tags = "User", description = "사용자 관련 API")
public class UserController {
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
    @ApiOperation(value = "구독 모델 변경")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value="변경할 구독 모델", required = true, paramType = "body", dataTypeClass = StateUpdateRequest.class)
    })
    public ResponseEntity<Long> updateUserState(@RequestBody StateUpdateRequest request){
        userService.updateUserState(request.getState());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
