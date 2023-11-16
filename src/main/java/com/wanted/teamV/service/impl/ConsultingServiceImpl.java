package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.TodayAmountInfoResDto;
import com.wanted.teamV.dto.res.TodayRecommendResDto;
import com.wanted.teamV.entity.Budget;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Spend;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.BudgetRepository;
import com.wanted.teamV.repository.CategoryRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.SpendRepository;
import com.wanted.teamV.service.ConsultingService;
import com.wanted.teamV.type.RecommendSentence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final SpendServiceImpl spendService;

    @Override
    public TodayRecommendResDto recommendTodaySpend(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Double monthlyBudget = budgetRepository.getSumByMemberId(memberId);
        List<Budget> budgetList = budgetRepository.getBudgetsByMemberId(memberId);
        Map<String, Double> categoryBudget = budgetList.stream()
                .collect(Collectors.groupingBy(budget -> budget.getCategory().getName(),
                        Collectors.summingDouble(Budget::getBudget)));

        LocalDateTime today = LocalDateTime.now();
        int day = today.getDayOfMonth(), lastDay = today.toLocalDate().lengthOfMonth();
        int remainDays = lastDay - day;

        List<Spend> monthlySpendsBeforeToday = getSpendsUntilToday(memberId, today);
        Map<String, Double> categorySpend = monthlySpendsBeforeToday.stream()
                .collect(Collectors.groupingBy(spend -> spend.getCategory().getName(),
                        Collectors.summingDouble(Spend::getAmount)));

        Double allSpends = monthlySpendsBeforeToday.stream()
                .mapToDouble(Spend::getAmount)
                .sum();

        //TodayRecommendResDto -> availableTodaySpend
        Double availableTotalSpend = monthlyBudget - allSpends;
        double dailyTodaySpend = Math.round((availableTotalSpend / remainDays) / 100.0) * 100.0;

        //TodayRecommendResDto -> categoryAmount
        Map<String, Double> categoryAmount = new HashMap<>();

        for (Map.Entry<String, Double> entry : categoryBudget.entrySet()) {
            String category = entry.getKey();
            Double budgetAmount = entry.getValue();
            Double spendAmount = categorySpend.getOrDefault(category, 0.0);

            Double remainingAmount = budgetAmount - spendAmount;
            if (remainingAmount <= 0.0) remainingAmount = 10000.0;
            double dailySpend = Math.round((remainingAmount / remainDays) / 100.0) * 100.0;
            categoryAmount.put(category, dailySpend);
        }

        //TodayRecommendResDto -> sentence
        RecommendSentence sentence = RecommendSentence.NORMAL;
        Double ratio = allSpends / monthlyBudget;
        System.out.println(allSpends);
        System.out.println(monthlyBudget);
        System.out.println(ratio);
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
    public TodayAmountInfoResDto getTodaySpend() {
        return null;
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
