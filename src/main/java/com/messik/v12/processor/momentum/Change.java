package com.messik.v12.processor.momentum;

import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

public class Change extends SimpleProcessor<Double, Double> {

    public static final Change HIGH_CHANGE = new Change("high_change", "high");
    public static final Change LOW_CHANGE = new Change("low_change", "low");

    private Double last;

    public Change(String name, String selector) {
        super(name, selector);
    }

    @Override
    protected Double calculate(Double data) {
        var change = last == null ? 0.0 : data - last;
        last = data;
        return change;
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
