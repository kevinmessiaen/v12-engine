package com.messik.v12.processor.momentum;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RecursiveRelativeStrengthIndex extends SimpleProcessor<Double, Double> {

    private final int period;
    private final double alpha;
    private Double last;
    private double up;
    private double down;

    public RecursiveRelativeStrengthIndex(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
        this.alpha = 1.0 / period;
    }

    @Override
    protected Double calculate(Double value) {
        if (last == null) {
            last = value;
        }

        double change = value - last;
        last = value;

        up = alpha * Math.max(change, 0) + (1 - alpha) * up;
        down = alpha * -Math.min(change, 0) + (1 - alpha) * down;

        return down == 0 ? 100 : up == 0 ? 0 : 100 - (100 / (1 + up / down));

    }

    @Override
    public DependencyProcessor mutate() {
        return new RecursiveRelativeStrengthIndex(name, selector, Mutations.mutate(period, new int[] {1, 200}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RecursiveRelativeStrengthIndex(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RecursiveRelativeStrengthIndex that = (RecursiveRelativeStrengthIndex) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "RecursiveRelativeStrengthIndex{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
