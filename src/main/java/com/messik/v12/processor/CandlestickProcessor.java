package com.messik.v12.processor;

import com.messik.v12.data.CandlestickWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CandlestickProcessor implements DependencyProcessor {

    public static final CandlestickProcessor CANDLESTICK_PROCESSOR = new CandlestickProcessor();
    private CandlestickProcessor() {

    }

    @Override
    public Set<String> provide() {
        return Set.of("open", "high", "low", "close", "volume");
    }

    @Override
    public Set<String> require() {
        return Collections.emptySet();
    }

    @Override
    public void process(Map<String, Object> data) {
        CandlestickWrapper candlestick = (CandlestickWrapper) data.get("data");
        data.put("open", candlestick.getOpen());
        data.put("high", candlestick.getHigh());
        data.put("low", candlestick.getLow());
        data.put("close", candlestick.getClose());
        data.put("volume", candlestick.getVolume());
    }

    @Override
    public DependencyProcessor mutate() {
        return this;
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

}
