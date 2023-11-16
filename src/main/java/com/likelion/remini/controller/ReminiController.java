package com.likelion.remini.controller;

import com.likelion.remini.dto.*;
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
import springfox.documentation.annotations.ApiIgnore;

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

        Long reminiId = reminiService.createRemini(reminiRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reminiId);
    }

    //회고 수정 api
    @PatchMapping("/{reminiId}")
    public ResponseEntity<Long> updateRemini(@PathVariable Long reminiId, @RequestBody ReminiUpdateRequestDTO reminiUpdateRequestDTO) {

        Long updatedReminiId = reminiService.updateRemini(reminiId, reminiUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedReminiId);
    }

    //회고 삭제 api
    @DeleteMapping("/{reminiId}")
    public ResponseEntity<Long> deleteRemini(@PathVariable Long reminiId){
        reminiService.deleteRemini(reminiId);
        return ResponseEntity.noContent().build();
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

        ReminiDetailResponse response = reminiService.getDetail(reminiId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/private")
    public ResponseEntity<Page<ReminiPageResponse>> getPrivatePage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPrivatePage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/temporary")
    public ResponseEntity<Page<ReminiPageResponse>> getTemporaryPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getTemporaryPage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<ReminiPageResponse>> getRecentPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<ReminiPageResponse>> getPopularPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByLikeCount(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ReminiPageResponse>> getCategoryPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize,
                                                                   @RequestParam(defaultValue = "kpt") String category) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPageByType(request, category);

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
