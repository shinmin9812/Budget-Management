package com.wanted.teamV.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spend {
    @Id
    @Column(name = "spend_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(nullable = false)
    int amount;

    @Column(nullable = false)
    String memo;

    @Column(nullable = false)
    Boolean isExcluded = true;

    @Column(nullable = false)
    LocalDateTime date;

    @Builder
    public Spend(Member member, Category category, int amount, String memo, LocalDateTime date) {
        this.member = member;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
        this.date = date;
    }

    public Spend(Member member, Category category, int amount, String memo, Boolean isExcluded, LocalDateTime date) {
        this.member = member;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
        this.isExcluded = isExcluded;
        this.date = date;
    }
}
