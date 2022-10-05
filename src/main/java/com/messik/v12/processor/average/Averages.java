package com.messik.v12.processor.average;

import java.util.Set;

public class Averages {

    public static final TrueRange TRUE_RANGE = new TrueRange("true_range", "data");
    public static final Average HLC3 = new Average("hlc3", Set.of("high", "low", "close"));
    public static final Average OHLC4 = new Average("ohlc4", Set.of("open", "high", "low", "close"));
    public static final Average HL2 = new Average("hl2", Set.of("high", "low"));
    public static final Average HEIKIN_ASHI_CLOSE = heikinAshiClose("ha_close", "open", "high", "low", "close");


    public static Average heikinAshiClose(String name, String open, String high, String low, String close) {
        return new Average(name, Set.of(open, high, low, close));
    }

}
