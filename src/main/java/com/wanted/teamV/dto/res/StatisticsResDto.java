package com.wanted.teamV.dto.res;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StatisticsResDto {
    private Double totalSpend;
    private Map<String, Double> categorySpendPercentage;
}
