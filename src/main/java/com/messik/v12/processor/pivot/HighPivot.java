package com.messik.v12.processor.pivot;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

import java.util.ArrayList;
import java.util.List;

public class HighPivot extends SimpleProcessor<Double, Double> {

    private final int period;
    private final List<Double> history = new ArrayList<>();

    public HighPivot(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
    }

    @Override
    protected Double calculate(Double data) {
        if (history.isEmpty()) {
            for (int i = 0; i < period; i ++) {
                history.add(data);
            }
        }

        history.add(data);
        history.remove(0);

        return history.stream().mapToDouble(d -> d).max().orElse(data);
    }

    @Override
    public DependencyProcessor mutate() {
        return new HighPivot(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new HighPivot(name, selector, period);
    }

    @Override
    public String toString() {
        return "HighPivot{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
