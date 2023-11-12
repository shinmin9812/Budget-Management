package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.BudgetInfoResDto;
import com.wanted.teamV.entity.Category;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.BudgetRepository;
import com.wanted.teamV.repository.CategoryRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceImplTest {

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Test
    @DisplayName("예산 설정 - 성공")
    public void updateBudget() throws Exception {
        //given
        Long categoryId = 1L, memberId = 1L;
        int money = 40000;

        Category category = Category.builder()
                .name("식품")
                .build();

        Member member = Member.builder()
                .account("test")
                .password("test1234!@#$")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(budgetRepository.findByMemberIdAndCategoryId(memberId, categoryId)).thenReturn(null);

        //when
        BudgetInfoResDto result = budgetService.updateBudget(categoryId, memberId, money);

        //then
        //System.out.println(result);
        assertNotNull(result);
    }

    @Test
    @DisplayName("예산 설정 - 실패(사용자가 없는 경우)")
    public void updateBudget_member_not_found() throws Exception {
        //given
        Long categoryId = 1L, memberId = 999L;
        int money = 40000;

        Category category = Category.builder()
                .name("식품")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(memberRepository.findById(memberId)).thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //when & then
        assertThrows(CustomException.class, () -> budgetService.updateBudget(categoryId, memberId, money));
    }

    @Test
    @DisplayName("예산 설정 - 실패(카테고리가 없는 경우)")
    public void updateBudget_category_not_found() throws Exception {
        //given
        Long categoryId = 999L, memberId = 1L;
        int money = 40000;

        when(categoryRepository.findById(categoryId)).thenThrow(new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        //when & then
        assertThrows(CustomException.class, () -> budgetService.updateBudget(categoryId, memberId, money));
    }

}
