package com.wanted.teamV.dto.req;

import java.time.LocalDateTime;

public record SpendCreateReqDto(
        Long categoryId,
        int amount,
        String memo,
        LocalDateTime date
) {
}
