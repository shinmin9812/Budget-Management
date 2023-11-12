package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.BudgetInfoResDto;
import com.wanted.teamV.entity.Budget;
import com.wanted.teamV.entity.Category;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.BudgetRepository;
import com.wanted.teamV.repository.CategoryRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public BudgetInfoResDto updateBudget(Long categoryId, Long memberId, int money) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Budget budget = budgetRepository.findByMemberIdAndCategoryId(memberId, categoryId);

        BudgetInfoResDto response;

        if (budget != null) {
            budgetRepository.updateBudget(budget.getMember().getId(), budget.getCategory().getId(), money);
            response = BudgetInfoResDto.builder()
                    .id(budget.getId())
                    .member(budget.getMember().getAccount())
                    .category(budget.getCategory().getName())
                    .budget(money)
                    .build();

        } else {
            Budget newBudget = Budget.builder()
                    .category(category)
                    .member(member)
                    .budget(money)
                    .build();

            budgetRepository.save(newBudget);

            response = BudgetInfoResDto.builder()
                    .id(newBudget.getId())
                    .member(newBudget.getMember().getAccount())
                    .category(newBudget.getCategory().getName())
                    .budget(newBudget.getBudget())
                    .build();
        }

        return response;
    }

    @Override
    public Map<String, Double> recommendBudgets(Long memberId, int money) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<Member> memberList = memberRepository.findAll();

        Map<String, Double> averageRatios = calculateAverageRatios(memberList);

        Map<String, Double> result = new HashMap<>();
        Double balance = 0.0;

        result.put("총액", Double.valueOf(money));

        for (Map.Entry<String, Double> entry : averageRatios.entrySet()) {
            String category = entry.getKey();
            Double avgRatio = entry.getValue();

            Double recommendBudget = avgRatio * money;

            recommendBudget = Math.floor(recommendBudget / 100) * 100;
            balance += recommendBudget;
            result.put(category, recommendBudget);
        }

        result.put("잔액", Double.valueOf(money) - balance);

        return result;
    }

    private Map<Member, Map<String, Double>> getCategoriesRatioByMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Budget> memberBudgets = budgetRepository.getBudgetsByMemberId(memberId);
        if (memberBudgets.isEmpty()) {
            throw new CustomException(ErrorCode.NO_BUDGET_FOUND);
        }

        Double totalBudget = budgetRepository.getSumByMemberId(memberId);
        if (totalBudget == 0.0) {
            throw new CustomException(ErrorCode.ZERO_TOTAL_BUDGET);
        }

        Map<String, Double> ratios = new HashMap<>();
        Double etcRatio = 0.0;

        for (Budget b : memberBudgets) {
            Double ratio = b.getBudget() / totalBudget;

            if (ratio < 0.1) {
                etcRatio += ratio;
            } else {
                ratios.put(b.getCategory().getName(), b.getBudget() / totalBudget);
            }
        }

        ratios.put("기타", etcRatio);

        Map<Member, Map<String, Double>> result = new HashMap<>();
        result.put(member, ratios);

        return result;
    }

    private Map<String, Double> calculateAverageRatios(List<Member> memberList) {
        Map<String, Double> averageRatios = new HashMap<>();

        for (Member m : memberList) {
            Map<String, Double> ratios = getCategoriesRatioByMember(m.getId()).get(m);

            ratios.forEach((category, ratio) ->
                    averageRatios.merge(category, ratio, Double::sum));
        }

        averageRatios.replaceAll((category, totalRatio) -> totalRatio / memberList.size());
        return averageRatios;
    }
}
