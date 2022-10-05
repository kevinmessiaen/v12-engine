package com.messik.v12.processor.average;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class HullMovingAverage implements DependencyProcessor {

    private final String name;
    private final String selector;
    private final String shortWmaSelector;
    private final String longWmaSelector;
    private final String currentValueSelector;
    private final int period;
    private final WeightedMovingAverage shortWma;
    private final WeightedMovingAverage longWma;
    private final WeightedMovingAverage wma;


    public HullMovingAverage(String name, String selector, int period) {
        this.name = name;
        this.selector = selector;
        this.shortWmaSelector = name + "_short_wma";
        this.longWmaSelector = name + "_long_wma";
        this.currentValueSelector = name + "_current_value";
        this.shortWma = new WeightedMovingAverage(shortWmaSelector, selector,period / 2);
        this.longWma = new WeightedMovingAverage(longWmaSelector, selector, period);
        this.wma = new WeightedMovingAverage(name, currentValueSelector, (int) Math.round(Math.sqrt(period)));
        this.period = period;
    }


    @Override
    public Set<String> provide() {
        return Set.of(name, shortWmaSelector, longWmaSelector, currentValueSelector);
    }

    @Override
    public Set<String> require() {
        return Collections.singleton(selector);
    }

    @Override
    public void process(Map<String, Object> data) {
        shortWma.process(data);
        longWma.process(data);

        var shortValue = (double) data.get(shortWmaSelector);
        var longValue = (double) data.get(longWmaSelector);

        var currentValue = 2 * shortValue - longValue;
        data.put(currentValueSelector, currentValue);
        wma.process(data);
    }

    @Override
    public DependencyProcessor mutate() {
        return new HullMovingAverage(name, selector, Mutations.mutate(period, new int[] {2, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new HullMovingAverage(name, selector, period);
    }

    @Override
    public String toString() {
        return "HullMovingAverage{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
