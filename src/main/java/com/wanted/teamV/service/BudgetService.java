package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.BudgetInfoResDto;

public interface BudgetService {
    BudgetInfoResDto updateBudget(Long categoryId, Long memberId, int money);

}
