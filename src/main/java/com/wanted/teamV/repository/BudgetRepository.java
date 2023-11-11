package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findByMemberIdAndCategoryId(Long memberId, Long categoryId);

    @Modifying
    @Query("UPDATE Budget b SET b.budget = :money WHERE b.member.id = :memberId AND b.category.id = :categoryId")
    void updateBudget(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId, @Param("money") int money);
}
