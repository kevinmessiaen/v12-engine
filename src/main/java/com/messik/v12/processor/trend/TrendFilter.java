package com.messik.v12.processor.trend;

import com.messik.v12.processor.DependencyProcessor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TrendFilter implements DependencyProcessor {

    private final String name;
    private final String signalSelector;
    private final String valueSelector;

    public TrendFilter(String name, String signalSelector, String valueSelector) {
        this.name = name;
        this.signalSelector = signalSelector;
        this.valueSelector = valueSelector;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(signalSelector, valueSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var signal = (double) data.get(signalSelector);
        var value =  (double) data.get(valueSelector);

        data.put(name, value > signal ? 1 : -1);
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
