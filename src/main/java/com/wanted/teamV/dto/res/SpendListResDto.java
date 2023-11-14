package com.wanted.teamV.dto.res;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SpendListResDto {
    private List<SpendInfoResDto> spendList;
    private Map<String, Double> categoryTotal;
    private Double allSpendsTotal;
}
