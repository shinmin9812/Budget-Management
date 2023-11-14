package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Spend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpendRepository extends JpaRepository<Spend, Long> {

    @Modifying
    @Query("UPDATE Spend s SET s.category.id = :categoryId, s.amount = :amount, s.memo = :memo, s.date = :date, " +
            "s.isExcluded = :isExcluded WHERE s.id = :spendId AND s.member.id = :memberId")
    void updateSpend(@Param("memberId") Long memberId, @Param("spendId") Long spendId, @Param("categoryId") Long categoryId,
                     @Param("amount") int amount, @Param("memo") String memo, @Param("date") LocalDateTime date,
                     @Param("isExcluded") Boolean isExcluded);

    @Query("SELECT SUM(s.amount) FROM Spend s WHERE s.member.id = :memberId AND s.isExcluded = true")
    Double getSumAmountByMember(@Param("memberId") Long memberId);

    @Query("SELECT s FROM Spend s WHERE s.member.id = :memberId AND s.date BETWEEN :startDate and :endDate")
    List<Spend> findSpendsByDateBetween(@Param("memberId") Long memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Spend s WHERE s.member.id = :memberId AND s.category.id = :categoryId AND s.date BETWEEN :startDate and :endDate")
    List<Spend> findSpendsByCategoryAndDateBetween(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId,
                                                   @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Spend s WHERE s.member.id = :memberId AND s.amount BETWEEN :min and :max AND s.date BETWEEN :startDate and :endDate")
    List<Spend> findSpendsByAmountBetweenAndDateBetween(@Param("memberId") Long memberId, @Param("min") int min, @Param("max") int max,
                                                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Spend s WHERE s.member.id = :memberId AND s.category.id = :categoryId AND s.amount BETWEEN :min and :max AND s.date BETWEEN :startDate and :endDate")
    List<Spend> findSpendsByCategoryAndAmountBetweenAndDateBetween(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId,
                                                                   @Param("min") int min, @Param("max") int max,
                                                                   @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
