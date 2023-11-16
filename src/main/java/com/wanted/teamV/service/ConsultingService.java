package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.TodayAmountInfoResDto;
import com.wanted.teamV.dto.res.TodayRecommendResDto;

public interface ConsultingService {

    TodayRecommendResDto recommendTodaySpend(Long memberId);

    TodayAmountInfoResDto getTodaySpend(Long memberId);
}
