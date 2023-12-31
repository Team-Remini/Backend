package com.likelion.remini.service;

import com.likelion.remini.domain.*;
import com.likelion.remini.dto.*;
import com.likelion.remini.exception.ReminiErrorResult;
import com.likelion.remini.exception.ReminiException;
import com.likelion.remini.exception.UserErrorResult;
import com.likelion.remini.exception.UserException;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.LikeRepository;
import com.likelion.remini.repository.ReminiRepository;
import com.likelion.remini.repository.SectionRepository;
import com.likelion.remini.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReminiService {

    private final ReminiRepository reminiRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final LikeRepository likeRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final PresignedUrlService presignedUrlService;
    private final Long ANONYMOUS_USER_ID;

    /**
     * 의존성 주입을 위한 생성자.
     * ANONYMOUS_USER_ID에 @Value 어노테이션으로 값을 주입하기 위해
     * 별도의 생성자를 통해 정의하였다.
     */
    @Autowired
    public ReminiService(ReminiRepository reminiRepository,
                         UserRepository userRepository,
                         SectionRepository sectionRepository,
                         LikeRepository likeRepository,
                         AuthTokensGenerator authTokensGenerator,
                         PresignedUrlService presignedUrlService,
                         @Value("${jwt.anonymous-user-id}") Long ANONYMOUS_USER_ID) {
        this.reminiRepository = reminiRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.likeRepository = likeRepository;
        this.authTokensGenerator = authTokensGenerator;
        this.presignedUrlService = presignedUrlService;
        this.ANONYMOUS_USER_ID = ANONYMOUS_USER_ID;
    }

    /* 회고 관리 */
    /**
     * 회고를 생성하는 메서드이다.
     *
     * @param reminiRequestDTO 회고 생성 시 요구되는 정보
     * @return 회고 Id, presignedUploadUrl(이미지 업로드 url)
     */
    @Transactional
    public ReminiResponse createRemini(ReminiRequestDTO reminiRequestDTO) {
        User user = getUser();
        List<String> sectionTexts = reminiRequestDTO.getSectionTexts();
        List<Section> sections = new ArrayList<>();

        //받아온 sectionTexts를 각 section 객체로 생성 이때 remini_id는 null
        if (sectionTexts != null && !sectionTexts.isEmpty()) {
            for (String sectionText : sectionTexts) {
                Section section = Section.builder()
                        .text(sectionText)
                        .build();
                sections.add(section);
            }
        }

        // 회고 사진 파일명을 고유한 값으로 설정
        String uuid = UUID.randomUUID().toString();

        //remini 객체 생성, section과 연관관계 설정
        Remini remini = Remini.builder()
                .user(user)
                .type(reminiRequestDTO.getType())
                .title(reminiRequestDTO.getTitle())
                .reminiImage(uuid)
                .instantSave(reminiRequestDTO.getInstantSave())
                .step(reminiRequestDTO.getStep())
                .likesCount(0L)
                .sectionList(sections)
                .build();
        //section의 remini_id 속성에 연관된 remini의 remini_id 부여
        List<Section> sectionList = remini.getSectionList();
        for(Section section: sectionList){
            section.setRemini(remini);
        }

        Remini savedRemini = reminiRepository.save(remini);
        String presignedUploadUrl = presignedUrlService.getPresignedUploadUrl(remini.getReminiImage());

        return new ReminiResponse(savedRemini.getReminiId(), presignedUploadUrl);
    }
    /**
     * 회고를 수정하는 메서드이다.
     *
     * @param reminiId, reminiRequestDTO 회고 생성 시 요구되는 정보
     * @return reminiId, presignedUploadUrl(이미지 업로드 url)
     */
    @Transactional
    public ReminiResponse updateRemini(Long reminiId, ReminiUpdateRequestDTO reminiUpdateRequestDTO){
        User user = getUser();
        Remini reminiToUpdate = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));
        //사용자 소유한 회고인지 확인, 필요없을 시 삭제해도 ㄱㅊ
        if (!reminiToUpdate.getUser().getId().equals(user.getId())) {
            throw new ReminiException(ReminiErrorResult.NON_OWNER);
        }
        //회고 관련된 기존 연관 section 객체 삭제
        sectionRepository.deleteAllByRemini(reminiToUpdate);
        //수정 사항 반영, section 객체는 새로 생성
        reminiToUpdate.update(reminiUpdateRequestDTO);

        String presignedUploadUrl = presignedUrlService.getPresignedUploadUrl(reminiToUpdate.getReminiImage());

        return new ReminiResponse(reminiId, presignedUploadUrl);
    }

    /**
     * 주어진 ID를 가진 회고를 삭제하는 메서드이다.
     *
     * @param reminiId 회고 ID
     *
     */
    @Transactional
    public void deleteRemini(Long reminiId){
        Remini reminiToDelete = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));
        //remini 관련 section 삭제
        sectionRepository.deleteAllByRemini(reminiToDelete);
        //remini 삭제
        reminiRepository.delete(reminiToDelete);
    }

    /**
     * 주어진 ID를 가진 회고의 좋아요를 생성하는 메서드이다.
     *
     * @param reminiId 회고 ID
     * @return 회고 좋아요 개수
     */
    @Transactional
    public Long createLike(Long reminiId){
        User user = getUser();
        Remini reminiToLike = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));

        //좋아요를 누르지 않았다면
        if(!hasLiked(reminiId)){
            //like 객체 생성
            Like like = Like.builder()
                    .user(user)
                    .remini(reminiToLike)
                    .build();
            likeRepository.save(like);
            //likesCount +1
            reminiToLike.incrementLikesCount();
            //likesCount 반환
            return reminiToLike.getLikesCount();
        }
        else{
            // 좋아요 누른 상태로 해당 api 호출 시 -1 반환
            return -1L;
        }

    }
    /**
     * 좋아요 여부를 반환하는 메서드이다.
     *
     * @param reminiId 회고 아이디
     * @return true/false 좋아요 여부
     */
    public boolean hasLiked(Long reminiId){
        User user = getUser();
        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));

        return likeRepository.existsByUserAndRemini(user,remini);
    }
    /**
     * 좋아요를 취소하는 메서드이다.
     *
     * @param reminiId 회고 아이디
     * @return likesCount 회고의 좋아요 개수
     */
    @Transactional
    public Long unlike(Long reminiId){
        User user = getUser();
        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));

        Like like = likeRepository.findByUserAndRemini(user,remini)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid, reminiid로 찾을 수 없습니다 : " + user.getId()+ "," + reminiId));
        if(like != null){
            likeRepository.delete(like);

            remini.decrementLikesCount();
            return remini.getLikesCount();
        }
        else{
            // 좋아요 누르지 않은 상태로 해당 api 호출 시 -1 반환
            return -1L;
        }
    }

    public String getUploadUrl(Long reminiId) {

        User user = getUser();
        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(() -> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));

        // 회고 작성자가 아닌 경우, 이미지 등록 및 수정 불가
        if (!user.equals(remini.getUser())) {
            throw new ReminiException(ReminiErrorResult.NON_OWNER);
        }

        return presignedUrlService.getPresignedUploadUrl(remini.getReminiImage());
    }

    /* 회고 조회 */

    /**
     * 주어진 ID를 가진 회고의 상세 정보를 조회하는 메서드이다.
     *
     * @param reminiId 회고 ID
     * @return 회고 상세 정보
     */
    public ReminiDetailResponse getDetail(Long reminiId) {

        User user = getUserOrAnonymous();

        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(() -> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));

        String reminiImage = presignedUrlService.getPresignedUrl(remini.getReminiImage());

        boolean isLiked = likeRepository.findByUserAndRemini(user, remini).isPresent();

        return ReminiDetailResponse.create(remini, user, reminiImage, isLiked);
    }

    /**
     * 요청자가 임시저장한 회고 목록을 조회하는 메서드이다.
     *
     * @param request 페이지 요청 정보
     * @return 페이지 형태의 임시저장 회고 목록
     */
    public Page<ReminiPageResponse> getTemporaryPage(PageRequest request) {

        User user = getUser();

        Page<Remini> reminiPage = reminiRepository.findAllByUserAndInstantSave(request, user, true);

        return new PageImpl<>(
                reminiPage.stream()
                        .map(v -> ReminiPageResponse.of(v, presignedUrlService.getPresignedUrl(v.getReminiImage())))
                        .collect(Collectors.toList()),
                request,
                reminiPage.getTotalElements()
        );
    }

    /**
     * 요청자가 작성 완료한 회고 목록을 조회하는 메서드이다.
     *
     * @param request 페이지 요청 정보
     * @return 페이지 형태의 작성 완료된 회고 목록
     */
    public Page<ReminiPageResponse> getPrivatePage(PageRequest request) {

        User user = getUser();

        Page<Remini> reminiPage = reminiRepository.findAllByUserAndInstantSave(request, user, false);

        List<Long> likedList = likeRepository.findReminiIdList(user, reminiPage.getContent());

        return new PageImpl<>(
                reminiPage.stream()
                        .map(v -> ReminiPageResponse.of(v, presignedUrlService.getPresignedUrl(v.getReminiImage()),
                                likedList.contains(v.getReminiId())))
                        .collect(Collectors.toList()),
                request,
                reminiPage.getTotalElements()
        );
    }

    /**
     * 지정된 페이지 요청에 따른 회고 목록을 조회하는 메서드이다.
     *
     * @param request 페이지 요청 정보
     * @return 페이지 형태의 회고 목록
     */
    public Page<ReminiPageResponse> getPage(PageRequest request) {

        User user = getUserOrAnonymous();

        Page<Remini> reminiPage = reminiRepository.findAllByInstantSave(request, false);

        List<Long> likedList = likeRepository.findReminiIdList(user, reminiPage.getContent());

        return new PageImpl<>(
                reminiPage.stream()
                        .map(v -> ReminiPageResponse.of(v, presignedUrlService.getPresignedUrl(v.getReminiImage()),
                                likedList.contains(v.getReminiId())))
                        .collect(Collectors.toList()),
                request,
                reminiPage.getTotalElements()
        );
    }

    /**
     * 주어진 카테고리에 맞는 회고 목록을 조회하는 메서드이다.
     *
     * @param request 페이지 요청 정보
     * @param type 회고 카테고리
     * @return 페이지 형태의 동일 카테고리 회고 목록
     */
    public Page<ReminiPageResponse> getPageByType(PageRequest request, Type type) {

        User user = getUserOrAnonymous();

        Page<Remini> reminiPage = reminiRepository.findAllByTypeAndInstantSave(request, type, false);

        List<Long> likedList = likeRepository.findReminiIdList(user, reminiPage.getContent());

        return new PageImpl<>(
                reminiPage.stream()
                        .map(v -> ReminiPageResponse.of(v, presignedUrlService.getPresignedUrl(v.getReminiImage()),
                                likedList.contains(v.getReminiId())))
                        .collect(Collectors.toList()),
                request,
                reminiPage.getTotalElements()
        );
    }

    private User getUser() {
        Long userId = authTokensGenerator.extractMemberId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 로그인한 사용자 엔티티를 반환하거나, 익명 사용자인 경우 익명사용자 전용 엔티티를 반환하는 메서드이다.
     *
     * @return 사용자 엔티티
     */
    private User getUserOrAnonymous() {
        Long userId;

        try {
            userId = authTokensGenerator.extractMemberId();
        } catch (UserException e) {
            // 익명 사용자 관련 UserException이 아닌 경우, 예외를 다시 던진다.
            if (!e.getErrorResult().equals(UserErrorResult.ANONYMOUS_USER)) {
                throw e;
            }
            userId = ANONYMOUS_USER_ID;
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

}