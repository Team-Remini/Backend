package com.likelion.remini.service;

import com.likelion.remini.repository.ReminiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminiService {

    private final ReminiRepository reminiRepository;

    /* 회고 관리 */



    /* 회고 조회 */



}
