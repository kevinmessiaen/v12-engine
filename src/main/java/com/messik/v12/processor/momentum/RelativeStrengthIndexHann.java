package com.messik.v12.processor.momentum;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RelativeStrengthIndexHann extends SimpleProcessor<Double, Double> {

    private static final double TWO_PI = 2.0 * Math.PI;

    private final int period;
    private Double last = null;
    private final List<Double> changes = new ArrayList<>();

    public RelativeStrengthIndexHann(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
    }

    @Override
    protected Double calculate(Double value) {
        if (last == null) {
            last = value;
            for (int i = 0; i < period; i ++) {
                changes.add(0.0);
            }
        }

        changes.add(value - last);
        changes.remove(0);

        var cd = 0.0;
        var cu = 0.0;
        for (int i = 0; i < period; i ++) {
            var change = changes.get(i);
            var absChange = Math.abs(change);
            var cosPart = Math.cos(TWO_PI * (period - i) / (period + 1));

            if (change < 0) {
                cu += (1 - cosPart) * absChange;
            } else if (change > 0) {
                cd += (1 - cosPart) * absChange;
            }
        }
        last = value;
        return (cu - cd) / (cu + cd);
    }

    @Override
    public DependencyProcessor mutate() {
        return new RelativeStrengthIndexHann(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RelativeStrengthIndexHann(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RelativeStrengthIndexHann that = (RelativeStrengthIndexHann) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "RelativeStrengthIndexHann{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
