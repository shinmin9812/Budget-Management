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
                    .member(budget.getMember())
                    .category(budget.getCategory())
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
                    .member(newBudget.getMember())
                    .category(newBudget.getCategory())
                    .budget(newBudget.getBudget())
                    .build();
        }

        return response;
    }

}
