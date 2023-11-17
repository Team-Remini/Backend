package com.likelion.remini.controller;

import com.likelion.remini.dto.*;
import com.likelion.remini.exception.UserNotFoundException;
import com.likelion.remini.service.ReminiService;
import io.swagger.annotations.*;
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

    @PostMapping
    @ApiOperation(value = "회고 생성")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiRequestDTO", value="저장할 회고 정보", required = true, paramType = "body", dataTypeClass = ReminiRequestDTO.class)
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "회고 생성 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Long> createRemini(@RequestBody ReminiRequestDTO reminiRequestDTO){

        Long reminiId = reminiService.createRemini(reminiRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reminiId);
    }

    @PatchMapping("/{reminiId}")
    @ApiOperation(value = "회고 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiId", value="수정할 회고 ID", example = "1", required = true, paramType = "path", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "reminiUpdateRequestDTO", value="수정할 회고 정보", required = true, paramType = "body", dataTypeClass = ReminiUpdateRequestDTO.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "회고 수정 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Long> updateRemini(@PathVariable Long reminiId, @RequestBody ReminiUpdateRequestDTO reminiUpdateRequestDTO) {

        Long updatedReminiId = reminiService.updateRemini(reminiId, reminiUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedReminiId);
    }

    @DeleteMapping("/{reminiId}")
    @ApiOperation(value = "회고 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiId", value="삭제할 회고 ID", example = "1", required = true, paramType = "path", dataTypeClass = Long.class),
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "회고 삭제 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Long> deleteRemini(@PathVariable Long reminiId){
        reminiService.deleteRemini(reminiId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reminiId}/likes")
    @ApiOperation(value = "좋아요 생성")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiId", value="좋아할 회고 ID", example = "1", required = true, paramType = "path", dataTypeClass = Long.class),
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "좋아요 생성 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Long> createLike(@PathVariable Long reminiId){
        Long likesCount = reminiService.createLike(reminiId);
        return ResponseEntity.status(HttpStatus.CREATED).body(likesCount);
    }

    // 좋아요 여부 확인 API
    @GetMapping("/{reminiId}/likes")
    @ApiIgnore(value = "회고 목록 및 상세조회에서 확인하도록 변경")
    public ResponseEntity<LikeResponseDTO> hasLiked(@PathVariable Long reminiId){
        boolean hasLiked = reminiService.hasLiked(reminiId);
        return ResponseEntity.ok(new LikeResponseDTO(hasLiked));
    }

    @DeleteMapping("/{reminiId}/likes")
    @ApiOperation(value = "좋아요 취소")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiId", value="안 좋아할 회고 ID", example = "1", required = true, paramType = "path", dataTypeClass = Long.class),
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "좋아요 취소 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Void> unlike(@PathVariable Long reminiId){
        reminiService.unlike(reminiId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reminiId}/image")
    @ApiOperation(value = "사진 업로드 URL 발급", notes = "특정 회고에 대한 사진 업로드용 URL을 발급받는다.\n" +
            "- 발급 받은 URL로 파일을 PUT 요청으로 보낸다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiId", value="업로드할 사진이 속한 회고 ID", example = "1", required = true, paramType = "path", dataTypeClass = Long.class),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "사진 업로드 URL 발급 성공"),
            @ApiResponse(code = 400, message = "작성자가 아닌 사용자가 사진 업로드 시도"),
            @ApiResponse(code = 400, message = "사진 업로드 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청된 회고를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<String> getUploadUrl(@PathVariable Long reminiId) {
        String uploadUrl = reminiService.getUploadUrl(reminiId);

        return new ResponseEntity<>(uploadUrl, HttpStatus.OK);
    }

    @ApiOperation(value = "회고 상세 조회", notes = "주어진 ID의 회고 상세 정보를 조회한다.\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reminiId", value="조회할 회고 ID", example = "1", required = true, paramType = "path", dataTypeClass = Long.class),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "회고 상세 조회 성공"),
            @ApiResponse(code = 400, message = "사진 조회 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청된 회고를 찾을 수 없음"),
            @ApiResponse(code = 404, message = "요청자 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    @GetMapping("{reminiId}")
    public ResponseEntity<ReminiDetailResponse> getDetail(@PathVariable Long reminiId) {

        ReminiDetailResponse response = reminiService.getDetail(reminiId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/private")
    @ApiOperation(value = "개인 회고 목록 조회", notes = "요청하는 사용자가 작성한 회고 목록을 최신순으로 조회한다.\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value="조회할 페이지 (0부터 시작)", example = "0", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value="한 페이지에 조회할 개수", example = "12", paramType = "query", dataTypeClass = Integer.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 400, message = "사진 조회 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청자 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Page<ReminiPageResponse>> getPrivatePage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPrivatePage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/temporary")
    @ApiOperation(value = "임시 회고 목록 조회", notes = "요청하는 사용자가 작성한 임시회고 목록을 최신순으로 조회한다.\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value="조회할 페이지 (0부터 시작)", example = "0", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value="한 페이지에 조회할 개수", example = "12", paramType = "query", dataTypeClass = Integer.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 400, message = "사진 조회 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청자 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Page<ReminiPageResponse>> getTemporaryPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getTemporaryPage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/recent")
    @ApiOperation(value = "최신 회고 목록 조회", notes = "회고 목록을 최신순으로 조회한다.\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value="조회할 페이지 (0부터 시작)", example = "0", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value="한 페이지에 조회할 개수", example = "12", paramType = "query", dataTypeClass = Integer.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 400, message = "사진 조회 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청자 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Page<ReminiPageResponse>> getRecentPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByCreateDate(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/popular")
    @ApiOperation(value = "인기 회고 목록 조회", notes = "회고 목록을 인기순으로 조회한다.\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value="조회할 페이지 (0부터 시작)", example = "0", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value="한 페이지에 조회할 개수", example = "12", paramType = "query", dataTypeClass = Integer.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 400, message = "사진 조회 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청자 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<Page<ReminiPageResponse>> getPopularPage(@RequestParam(defaultValue = "0") int pageNumber,
                                                                   @RequestParam(defaultValue = "12") int pageSize) {
        PageRequest request = getPageRequestByLikeCount(pageNumber, pageSize);

        Page<ReminiPageResponse> response = reminiService.getPage(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category")
    @ApiOperation(value = "카테고리별 회고 목록 조회", notes = "회고 목록을 카테고리별로 최신순으로 조회한다.\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value="조회할 페이지 (0부터 시작)", example = "0", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value="한 페이지에 조회할 개수", example = "12", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "category", value="조회할 회고 카테고리", example = "kpt", paramType = "query", dataTypeClass = String.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 400, message = "사진 조회 URL 생성 실패"),
            @ApiResponse(code = 404, message = "요청자 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
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
