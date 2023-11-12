package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findByMemberIdAndCategoryId(Long memberId, Long categoryId);

    @Modifying
    @Query("UPDATE Budget b SET b.budget = :money WHERE b.member.id = :memberId AND b.category.id = :categoryId")
    void updateBudget(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId, @Param("money") int money);

    @Query("SELECT sum(b.budget) FROM Budget b WHERE b.member.id = :memberId")
    Double getSumByMemberId(@Param("memberId") Long memberId);

    List<Budget> getBudgetsByMemberId(Long memberId);
}
