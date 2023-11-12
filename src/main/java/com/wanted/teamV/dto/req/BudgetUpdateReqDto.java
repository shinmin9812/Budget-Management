package com.wanted.teamV.dto.req;

public record BudgetUpdateReqDto(
        Long categoryId,
        int budget
) {
}
