package com.likelion.remini.controller;

import com.likelion.remini.service.ReminiService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/remini")
@RequiredArgsConstructor
@Api(tags = "Remini", description = "회고 관련 API")
public class ReminiController {

    private final ReminiService reminiService;

    /* 회고 관리 */



    /* 회고 조회 */



}
