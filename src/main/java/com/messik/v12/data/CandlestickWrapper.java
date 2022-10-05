package com.messik.v12.data;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class CandlestickWrapper implements Comparable<CandlestickWrapper> {

    private final String fiat;
    private final String asset;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final double volume;

    public double getDurationSeconds() {
        return Duration.between(start, end).getSeconds();
    }

    @Override
    public int compareTo(CandlestickWrapper o) {
        return start.compareTo(o.start);
    }
}
