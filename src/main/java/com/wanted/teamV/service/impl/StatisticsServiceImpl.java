package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Spend;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.SpendRepository;
import com.wanted.teamV.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final MemberRepository memberRepository;
    private final SpendRepository spendRepository;

    @Override
    public StatisticsResDto getMonthlyStatistics(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        LocalDateTime today = LocalDateTime.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        LocalDateTime beforeLastMonth = LocalDateTime.of(year, month - 2, day, 0, 0, 0);
        LocalDateTime lastMonth = LocalDateTime.of(year, month - 1, day, 0, 0, 0);

        List<Spend> lastSpends = spendRepository.findSpendsByDateBetween(memberId, beforeLastMonth, lastMonth);
        List<Spend> thisSpends = spendRepository.findSpendsByDateBetween(memberId, lastMonth, today);

        //StatisticsResDto -> totalSpend
        Double lastAllSpend = getSumBySpends(lastSpends);
        Double thisAllSpend = getSumBySpends(thisSpends);

        //StatisticsResDto -> categorySpendPercentage
        Map<String, Double> lastMonthCategorySpend = getCategorySpends(lastSpends);
        Map<String, Double> thisMonthCategorySpend = getCategorySpends(thisSpends);

        Map<String, Double> categorySpendRatio = new HashMap<>();
        for (Map.Entry<String, Double> entry : thisMonthCategorySpend.entrySet()) {
            String category = entry.getKey();
            Double spendLastMonth = lastMonthCategorySpend.getOrDefault(category, 0.0);
            Double spendThisMonth = entry.getValue();

            Double categoryRatio;

            if (spendLastMonth != 0) {
                categoryRatio = ((spendThisMonth - spendLastMonth) / spendLastMonth) * 100.0;
            } else {
                categoryRatio = 0.0;
            }

            double roundRatio = Math.round(categoryRatio);
            categorySpendRatio.put(category, roundRatio);
        }

        return StatisticsResDto.builder()
                .totalSpend(thisAllSpend - lastAllSpend)
                .categorySpendPercentage(categorySpendRatio)
                .build();
    }

    @Override
    public Double getDayStatistics(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startOfDay = today.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = today.toLocalDate().atTime(LocalTime.MAX);

        DayOfWeek dayOfWeek = today.getDayOfWeek();
        LocalDateTime lastWeekSameDay = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(dayOfWeek));

        List<Spend> todaySpends = spendRepository.findSpendsByDateBetween(memberId, startOfDay, endOfDay);
        List<Spend> lastWeekDaySpends = spendRepository.findSpendsByDateBetween(
                memberId,
                lastWeekSameDay.toLocalDate().atStartOfDay(),
                lastWeekSameDay.toLocalDate().atTime(LocalTime.MAX)
        );

        Double todaySpending = getSumBySpends(todaySpends);
        Double lastWeekDaySpending = getSumBySpends(lastWeekDaySpends);

        Double spendingRatio;
        if (lastWeekDaySpending != 0) {
            spendingRatio = ((todaySpending - lastWeekDaySpending) / lastWeekDaySpending * 100.0);
        } else {
            spendingRatio = 0.0;
        }

        if (spendingRatio.isNaN()) {
            spendingRatio = 0.0;
        }

        return spendingRatio;
    }

    private Double getSumBySpends(List<Spend> lists) {
        return lists.stream()
                .mapToDouble(Spend::getAmount)
                .sum();
    }

    private Map<String, Double> getCategorySpends(List<Spend> lists) {
        return lists.stream()
                .collect(Collectors.groupingBy(spend -> spend.getCategory().getName(),
                        Collectors.summingDouble(Spend::getAmount)));
    }
}
