package com.wanted.teamV.controller;

import com.wanted.teamV.component.AuthenticationPrincipal;
import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/monthly")
    public ResponseEntity<StatisticsResDto> getMonthlyStatistics(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        StatisticsResDto response = statisticsService.getMonthlyStatistics(loginMember.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day")
    public ResponseEntity<Double> getDayStatistics(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        Double response = statisticsService.getDayStatistics(loginMember.id());
        return ResponseEntity.ok(response);
    }
}
