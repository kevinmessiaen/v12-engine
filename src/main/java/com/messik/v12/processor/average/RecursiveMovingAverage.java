package com.messik.v12.processor.average;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RecursiveMovingAverage extends SimpleProcessor<Double, Double> {

    private final int period;
    private Double rma;
    private final double alpha;

    public RecursiveMovingAverage(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
        this.alpha = 1.0 / period;
    }

    @Override
    protected Double calculate(Double data) {
        if (rma == null) {
            rma = data;
            return rma;
        }
        rma = alpha * data + (1 - alpha) * rma;
        return rma;
    }

    @Override
    public DependencyProcessor mutate() {
        return new RecursiveMovingAverage(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RecursiveMovingAverage(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RecursiveMovingAverage that = (RecursiveMovingAverage) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "RecursiveMovingAverage{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
