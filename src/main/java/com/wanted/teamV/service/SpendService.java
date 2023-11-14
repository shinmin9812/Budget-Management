package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.SpendCreateReqDto;
import com.wanted.teamV.dto.req.SpendUpdateReqDto;
import com.wanted.teamV.dto.res.SpendInfoResDto;
import com.wanted.teamV.dto.res.SpendListResDto;

import java.time.LocalDateTime;

public interface SpendService {
    SpendInfoResDto createSpend(Long memberId, SpendCreateReqDto createReqDto);

    SpendInfoResDto updateSpend(Long memberId, Long spendId, SpendUpdateReqDto updateReqDto);

    SpendInfoResDto getSpendDetail(Long memberId, Long spendId);

    SpendListResDto getAllSpends(Long memberId, LocalDateTime startDate, LocalDateTime endDate, Long categoryId, Integer min, Integer max);

    void deleteSpend(Long memberId, Long spendId);

    Double getExcludingTotal(Long memberId);
}
