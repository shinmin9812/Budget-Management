package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.BudgetInfoResDto;

import java.util.Map;

public interface BudgetService {
    BudgetInfoResDto updateBudget(Long categoryId, Long memberId, int money);

    Map<String, Double> recommendBudgets(Long memberId, int money);
}
