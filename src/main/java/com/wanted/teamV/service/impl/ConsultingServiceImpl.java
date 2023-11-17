package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.TodayAmountInfoResDto;
import com.wanted.teamV.dto.res.TodayRecommendResDto;
import com.wanted.teamV.entity.Budget;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Spend;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.BudgetRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.SpendRepository;
import com.wanted.teamV.service.ConsultingService;
import com.wanted.teamV.type.RecommendSentence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultingServiceImpl implements ConsultingService {

    private final MemberRepository memberRepository;
    private final BudgetRepository budgetRepository;
    private final SpendRepository spendRepository;

    @Override
    public TodayRecommendResDto recommendTodaySpend(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDateTime today = LocalDateTime.now();
        int day = today.getDayOfMonth(), lastDay = today.toLocalDate().lengthOfMonth();
        int remainDays = lastDay - day;

        Double monthlyBudget = budgetRepository.getSumByMemberId(memberId);
        List<Budget> budgetList = budgetRepository.getBudgetsByMemberId(memberId);
        Map<String, Double> categoryBudget = budgetList.stream()
                .collect(Collectors.groupingBy(budget -> budget.getCategory().getName(),
                        Collectors.summingDouble(Budget::getBudget)));

        List<Spend> monthlySpendsUntilToday = getSpendsUntilToday(memberId, today);
        Double spendUntilYesterday = monthlySpendsUntilToday.stream()
                .mapToDouble(Spend::getAmount)
                .sum();

        Map<String, Double> categorySpend = monthlySpendsUntilToday.stream()
                .collect(Collectors.groupingBy(spend -> spend.getCategory().getName(),
                        Collectors.summingDouble(Spend::getAmount)));

        //TodayRecommendResDto -> availableTodaySpend
        Double availableTotalSpend = monthlyBudget - spendUntilYesterday;
        double dailyTodaySpend = Math.round((availableTotalSpend / remainDays) / 100.0) * 100.0;

        //TodayRecommendResDto -> categoryAmount
        Map<String, Double> categoryAmount = new HashMap<>();

        for (Map.Entry<String, Double> entry : categoryBudget.entrySet()) {
            String category = entry.getKey();
            Double budgetAmount = entry.getValue();
            Double spendAmount = categorySpend.getOrDefault(category, 0.0);

            Double remainingAmount = budgetAmount - spendAmount;
            double dailySpend = Math.round((remainingAmount / remainDays) / 100.0) * 100.0;
            if (dailySpend <= 0.0) dailySpend = 10000.0;
            categoryAmount.put(category, dailySpend);
        }

        //TodayRecommendResDto -> sentence
        RecommendSentence sentence = RecommendSentence.NORMAL;
        Double ratio = spendUntilYesterday / monthlyBudget;

        if (ratio < 0.05) {
            sentence = RecommendSentence.EXCELLENT;
        } else if (ratio >= 0.05 && ratio < 0.3) {
            sentence = RecommendSentence.GOOD;
        } else if (ratio >= 0.3 && ratio < 0.6) {
            sentence = RecommendSentence.NORMAL;
        } else if (ratio >= 0.6 && ratio < 0.8) {
            sentence = RecommendSentence.BAD;
        } else if (ratio >= 0.8) {
            sentence = RecommendSentence.VERY_BAD;
        }

        return TodayRecommendResDto.builder()
                .availableTodaySpend(dailyTodaySpend)
                .categoryTodaySpend(categoryAmount)
                .sentence(sentence.getContent())
                .build();
    }

    @Override
    public TodayAmountInfoResDto getTodaySpend(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);

        //TodayAmountInfoResDto -> categoryTotal, allSpendsTotal
        List<Spend> todaySpends = spendRepository.findSpendsByDateBetween(memberId, todayStart, todayEnd);

        Double todayTotalSpends = todaySpends.stream()
                .mapToDouble(Spend::getAmount)
                .sum();

        Map<String, Double> todaySpendByCategory = todaySpends.stream()
                .collect(Collectors.groupingBy(spend -> spend.getCategory().getName(),
                        Collectors.summingDouble(Spend::getAmount)));

        //TodayAmountInfoResDto -> risk
        List<Budget> budgetList = budgetRepository.getBudgetsByMemberId(memberId);

        Map<String, Double> monthlyBudgetByCategory = budgetList.stream()
                .collect(Collectors.groupingBy(budget -> budget.getCategory().getName(),
                        Collectors.summingDouble(Budget::getBudget)));

        Map<String, Double> riskPercentageByCategory = new HashMap<>();

        for (Map.Entry<String, Double> entry : monthlyBudgetByCategory.entrySet()) {
            String category = entry.getKey();
            Double monthlyBudget = entry.getValue();
            Double dailyBudget = monthlyBudget / todayStart.toLocalDate().lengthOfMonth();

            Double spendForCategory = todaySpends.stream()
                    .filter(spend -> spend.getCategory().getName().equals(category))
                    .mapToDouble(Spend::getAmount)
                    .sum();

            Double difference = (spendForCategory - dailyBudget) / dailyBudget * 100;
            difference = Math.max(0.0, Math.round(difference * 100.0) / 100.0);

            riskPercentageByCategory.put(category, difference);
        }

        return TodayAmountInfoResDto.builder()
                .todaySpendByCategory(todaySpendByCategory)
                .todayAllSpends(todayTotalSpends)
                .riskPercentageByCategory(riskPercentageByCategory)
                .build();
    }

    private List<Spend> getSpendsUntilToday(Long memberId, LocalDateTime today) {
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        LocalDateTime first = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime last = LocalDateTime.of(year, month, day - 1, 23, 59, 59);

        return spendRepository.findSpendsByDateBetween(memberId, first, last);
    }
}
