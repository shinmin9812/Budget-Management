package com.wanted.teamV.dto.res;

import com.wanted.teamV.entity.Category;
import com.wanted.teamV.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetInfoResDto {
    private Long id;
    private Category category;
    private Member member;
    private int budget;
}
