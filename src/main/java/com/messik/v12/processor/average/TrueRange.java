package com.messik.v12.processor.average;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

public class TrueRange extends SimpleProcessor<CandlestickWrapper, Double> {

    public TrueRange(String name, String selector) {
        super(name, selector);
    }

    @Override
    protected Double calculate(CandlestickWrapper value) {
        double high = value.getHigh();
        double low = value.getLow();
        double previousClose = value.getOpen();

        return Math.max(high - low, Math.max(high - previousClose, previousClose - low));
    }

    @Override
    public DependencyProcessor mutate() {
        return this;
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}


