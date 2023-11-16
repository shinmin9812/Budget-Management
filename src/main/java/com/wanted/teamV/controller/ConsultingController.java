package com.wanted.teamV.controller;

import com.wanted.teamV.component.AuthenticationPrincipal;
import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.res.TodayRecommendResDto;
import com.wanted.teamV.service.ConsultingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consults")
public class ConsultingController {

    private final ConsultingService consultingService;

    @GetMapping("/today-recommend")
    public ResponseEntity<TodayRecommendResDto> recommendTodaySpend(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        TodayRecommendResDto response = consultingService.recommendTodaySpend(loginMember.id());
        return ResponseEntity.ok(response);
    }
}
