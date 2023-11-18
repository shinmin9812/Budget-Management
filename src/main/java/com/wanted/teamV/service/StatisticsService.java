package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.StatisticsResDto;

public interface StatisticsService {

    StatisticsResDto getMonthlyStatistics(Long memberId);

    Double getDayStatistics(Long memberId);

}
