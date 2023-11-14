package com.likelion.remini.service;

import com.likelion.remini.domain.Like;
import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.Section;
import com.likelion.remini.dto.ReminiRequestDTO;
import com.likelion.remini.dto.ReminiUpdateRequestDTO;
import com.likelion.remini.dto.UserResponseDTO;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.LikeRepository;
import com.likelion.remini.domain.User;
import com.likelion.remini.dto.ReminiDetailResponse;
import com.likelion.remini.exception.ReminiNotFoundException;
import com.likelion.remini.exception.UserNotFoundException;
import com.likelion.remini.repository.ReminiRepository;
import com.likelion.remini.repository.UserRepository;
import com.likelion.remini.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminiService {

    private final ReminiRepository reminiRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final LikeRepository likeRepository;
    private final AuthTokensGenerator authTokensGenerator;


    /* 회고 관리 */
    //회고 등록 api
    @Transactional
    public Long createRemini(ReminiRequestDTO reminiRequestDTO) {
        Long userId = authTokensGenerator.extractMemberId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
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
        //remini 객체 생성, section과 연관관계 설정
        Remini remini = Remini.builder()
                .user(user)
                .type(reminiRequestDTO.getType())
                .title(reminiRequestDTO.getTitle())
                .reminiImageUrl(reminiRequestDTO.getReminiImage())
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

        return savedRemini.getReminiId();
    }
    //회고 수정 api
    @Transactional
    public Long updateRemini(Long reminiId, ReminiUpdateRequestDTO reminiUpdateRequestDTO){
        Long userId = authTokensGenerator.extractMemberId();
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        Remini reminiToUpdate = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new IllegalArgumentException("해당 reminiid로 찾을 수 없습니다 : " + reminiId));
        //사용자 소유한 회고인지 확인, 필요없을 시 삭제해도 ㄱㅊ
        if (!reminiToUpdate.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 회고를 수정할 권한이 없습니다.");
        }
        //회고 관련된 기존 연관 section 객체 삭제
        sectionRepository.deleteAllByRemini(reminiToUpdate);
        //수정 사항 반영, section 객체는 새로 생성
        reminiToUpdate.update(reminiUpdateRequestDTO);
        return reminiId;
    }

    //회고 삭제 api
    @Transactional
    public void deleteRemini(Long reminiId){
        Remini reminiToDelete = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new IllegalArgumentException("해당 reminiid로 찾을 수 없습니다 : " + reminiId));
        //remini 관련 section 삭제
        sectionRepository.deleteAllByRemini(reminiToDelete);
        //remini 삭제
        reminiRepository.delete(reminiToDelete);
    }

    //좋아요 api
    //좋아요 생성 api
    @Transactional
    public Long createLike(Long reminiId){
        Long userId = authTokensGenerator.extractMemberId();
        Remini reminiToLike = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new IllegalArgumentException("해당 reminiid로 찾을 수 없습니다 : " + reminiId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
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
    //좋아요 여부 api
    public boolean hasLiked(Long reminiId){
        Long userId = authTokensGenerator.extractMemberId();
        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new IllegalArgumentException("해당 reminiid로 찾을 수 없습니다 : " + reminiId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        return likeRepository.existsByUserAndRemini(user,remini);
    }
    //좋아요 취소 api (추가로 구현한 부분 지워도 무방)
    @Transactional
    public Long unlike(Long reminiId){
        Long userId = authTokensGenerator.extractMemberId();
        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(()-> new IllegalArgumentException("해당 reminiid로 찾을 수 없습니다 : " + reminiId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        Like like = likeRepository.findByUserAndRemini(user,remini)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid, reminiid로 찾을 수 없습니다 : " + userId+ "," + reminiId));
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

    /* 회고 조회 */

    public ReminiDetailResponse getDetail(Long reminiId) {

        Long userId = authTokensGenerator.extractMemberId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 userid로 찾을 수 없습니다 : " + userId));

        Remini remini = reminiRepository.findById(reminiId)
                .orElseThrow(() -> new ReminiNotFoundException("회고를 찾을 수 없음"));

        Like like = likeRepository.findByUserAndRemini(user, remini);

        return ReminiDetailResponse.create(remini, user, like != null);
    }


}