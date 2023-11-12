package com.wanted.teamV.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetInfoResDto {
    private Long id;
    private String category;
    private String member;
    private int budget;
}
