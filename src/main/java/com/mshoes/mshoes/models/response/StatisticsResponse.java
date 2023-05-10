package com.mshoes.mshoes.models.response;

import lombok.Data;

@Data
public class StatisticsResponse {
    private long firstValue;
    private long lastValue;

    public StatisticsResponse(long firstValue, long lastValue) {
        this.firstValue = firstValue;
        this.lastValue = lastValue;
    }
}
