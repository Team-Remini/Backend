package com.likelion.remini.controller;

import com.likelion.remini.dto.*;
import com.likelion.remini.exception.ReminiNotFoundException;
import com.likelion.remini.exception.UserNotFoundException;
import com.likelion.remini.service.ReminiService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

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

    // 사진 업로드 URL api
    @GetMapping("/{reminiId}/image")
    public ResponseEntity<String> getUploadUrl(@PathVariable Long reminiId) {
        String uploadUrl = reminiService.getUploadUrl(reminiId);

        return new ResponseEntity<>(uploadUrl, HttpStatus.OK);
    }

    /* 회고 조회 */
    @GetMapping("{reminiId}")
    public ResponseEntity<ReminiDetailResponse> getDetail(@PathVariable Long reminiId) {
        ReminiDetailResponse response;

        try {
            response = reminiService.getDetail(reminiId);
        } catch (UserNotFoundException | ReminiNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/private")
    public ResponseEntity<Page<ReminiPageResponse>> getPrivatePage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response;

        try {
            response = reminiService.getPrivatePage(request);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/temporary")
    public ResponseEntity<Page<ReminiPageResponse>> getTemporaryPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response;

        try {
            response = reminiService.getTemporaryPage(request);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<ReminiPageResponse>> getRecentPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response;

        try {
            response = reminiService.getPage(request);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<ReminiPageResponse>> getPopularPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByLikeCount(pageNumber, pageSize);

        Page<ReminiPageResponse> response;

        try {
            response = reminiService.getPage(request);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ReminiPageResponse>> getCategoryPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize,
                                                                   @RequestParam(defaultValue = "kpt") String category) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response;

        try {
            response = reminiService.getPageByType(request, category);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private PageRequest getPageRequestByCreateDate(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("createdDate"),
                Sort.Order.asc("title")
        ));
    }

    private PageRequest getPageRequestByLikeCount(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("likesCount"),
                Sort.Order.asc("title")
        ));
    }
}
