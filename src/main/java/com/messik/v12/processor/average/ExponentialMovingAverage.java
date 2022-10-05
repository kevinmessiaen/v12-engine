package com.messik.v12.processor.average;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExponentialMovingAverage extends SimpleProcessor<Double, Double> {

    private final int period;
    private Double ema;
    private final double k;

    public ExponentialMovingAverage(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
        this.k = 2.0 / (period + 1.0);
    }

    @Override
    protected Double calculate(Double data) {
        if (ema == null) {
            ema = data;
            return ema;
        }
        ema = ((data - ema) * k) + ema;
        return ema;
    }

    @Override
    public DependencyProcessor mutate() {
        return new ExponentialMovingAverage(name, selector, Mutations.mutate(period, new int[] {1, 200}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new ExponentialMovingAverage(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExponentialMovingAverage that = (ExponentialMovingAverage) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "ExponentialMovingAverage{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
