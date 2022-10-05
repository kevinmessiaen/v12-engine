package com.messik.v12.processor.momentum;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RelativeStrengthIndex extends SimpleProcessor<Double, Double> {

    private final int period;
    private Double last = null;
    private final List<Double> gains = new ArrayList<>();
    private final List<Double> losses = new ArrayList<>();

    public RelativeStrengthIndex(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
    }

    @Override
    protected Double calculate(Double value) {
        if (last == null) {
            for (int i = 0; i < period; i++) {
                gains.add(0.0);
                losses.add(0.0);
            }
        } else if (value > last) {
            addValues(value - last, 0.0);
        } else if (value < last) {
            addValues(0.0, last - value);
        } else {
            addValues(0.0, 0.0);
        }
        last = value;
        return rsi();
    }

    private void addValues(double v, double v2) {
        gains.add(v);
        losses.add(v2);
        gains.remove(0);
        losses.remove(0);
    }

    private double rsi() {
        return 100 - 100 / (1 + rs());
    }

    private double rs() {
        double loses = ad();
        return loses == 0.0 ? 0.0 : au() / loses;
    }

    private double au() {
        return gains.stream().mapToDouble(v -> v).average().orElse(0.0);
    }

    private double ad() {
        return losses.stream().mapToDouble(v -> v).average().orElse(0.0);
    }

    @Override
    public DependencyProcessor mutate() {
        return new RelativeStrengthIndex(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RelativeStrengthIndex(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RelativeStrengthIndex that = (RelativeStrengthIndex) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "RelativeStrengthIndex{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
