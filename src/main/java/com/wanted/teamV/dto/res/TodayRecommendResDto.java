package com.wanted.teamV.dto.res;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TodayRecommendResDto {
    private Double availableTodaySpend;
    private Map<String, Double> categoryTodaySpend;
    private String sentence;
}
