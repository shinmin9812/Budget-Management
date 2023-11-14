package com.wanted.teamV.dto.req;

import java.time.LocalDateTime;

public record SpendUpdateReqDto(
        Long categoryId,
        int amount,
        String memo,
        LocalDateTime date,
        Boolean isExcluded
) {
}
