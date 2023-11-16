package com.wanted.teamV.dto.res;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TodayAmountInfoResDto {
    private Map<String, Double> todaySpendByCategory;
    private Double todayAllSpends;
    private Map<String, Double> riskPercentageByCategory;
}
