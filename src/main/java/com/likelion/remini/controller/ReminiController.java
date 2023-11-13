package com.likelion.remini.controller;

import com.likelion.remini.dto.LikeResponseDTO;
import com.likelion.remini.dto.ReminiRequestDTO;
import com.likelion.remini.dto.ReminiUpdateRequestDTO;
import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.service.ReminiService;
import io.swagger.annotations.Api;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
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
    //회고 생성 api
    @PostMapping
    public ResponseEntity<Long> createRemini(@RequestBody ReminiRequestDTO reminiRequestDTO){
        try {
            Long reminiId = reminiService.createRemini(reminiRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reminiId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //회고 수정 api
    @PatchMapping("/{reminiId}")
    public ResponseEntity<Long> updateRemini(@PathVariable Long reminiId, @RequestBody ReminiUpdateRequestDTO reminiUpdateRequestDTO) {
        try{
            Long updatedReminiId = reminiService.updateRemini(reminiId, reminiUpdateRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updatedReminiId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    //회고 삭제 api
    @DeleteMapping("/{reminiId}")
    public ResponseEntity<Long> deleteRemini(@PathVariable Long reminiId){
        try{
            reminiService.deleteRemini(reminiId);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //좋아요 생성 api
    @PostMapping("/{reminiId}/likes")
    public ResponseEntity<Long> createLike(@PathVariable Long reminiId){
        Long likesCount = reminiService.createLike(reminiId);
        return ResponseEntity.status(HttpStatus.CREATED).body(likesCount);
    }

    // 좋아요 여부 확인 API
    @GetMapping("/{reminiId}/likes")
    public ResponseEntity<LikeResponseDTO> hasLiked(@PathVariable Long reminiId){
        boolean hasLiked = reminiService.hasLiked(reminiId);
        return ResponseEntity.ok(new LikeResponseDTO(hasLiked));

    }

    //좋아요 취소 api
    @DeleteMapping("/{reminiId}/likes")
    public ResponseEntity<Void> unlike(@PathVariable Long reminiId){
        reminiService.unlike(reminiId);
        return ResponseEntity.noContent().build();
    }




    /* 회고 조회 */



}
