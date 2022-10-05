package com.messik.v12.processor.average;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class SimpleMovingAverage extends SimpleProcessor<Double, Double> {

    private final int period;
    private final List<Double> values = new ArrayList<>();

    public SimpleMovingAverage(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
    }

    @Override
    protected Double calculate(Double data) {
        if (values.isEmpty()) {
            for (int i = 0; i < period; i ++) {
                values.add(data);
            }
        }

        values.add(data);
        values.remove(0);

        return values.stream().mapToDouble(d -> d).average().orElse(data);
    }

    @Override
    public DependencyProcessor mutate() {
        return new SimpleMovingAverage(name, selector, Mutations.mutate(period, new int[] {1, 200}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new SimpleMovingAverage(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SimpleMovingAverage that = (SimpleMovingAverage) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "SimpleMovingAverage{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
