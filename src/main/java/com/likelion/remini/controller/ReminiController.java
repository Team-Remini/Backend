package com.likelion.remini.controller;

import com.likelion.remini.domain.User;
import com.likelion.remini.dto.ReminiDetailResponse;
import com.likelion.remini.exception.ReminiNotFoundException;
import com.likelion.remini.exception.UserNotFoundException;
import com.likelion.remini.service.ReminiService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/remini")
@RequiredArgsConstructor
@Api(tags = "Remini", description = "회고 관련 API")
public class ReminiController {

    private final ReminiService reminiService;

    /* 회고 관리 */


    /* 회고 조회 */
    @GetMapping("{reminiId}")
    public ResponseEntity<ReminiDetailResponse> getDetail(@PathVariable Long reminiId) {
        ReminiDetailResponse response = null;

        try {
            response = reminiService.getDetail(reminiId);
        } catch (UserNotFoundException | ReminiNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
