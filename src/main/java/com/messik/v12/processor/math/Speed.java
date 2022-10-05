package com.messik.v12.processor.math;

import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

public class Speed extends SimpleProcessor<Double, Double> {

    private Double lastValue;

    public Speed(String name, String selector) {
        super(name, selector);
    }

    @Override
    protected Double calculate(Double value) {
        if (lastValue == null) {
            lastValue = value;
        }

        return ((value / lastValue) - 1) * 100;
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
        return "Speed{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", lastValue=" + lastValue +
                '}';
    }
}
