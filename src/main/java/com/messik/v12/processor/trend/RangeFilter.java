package com.messik.v12.processor.trend;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RangeFilter extends SimpleProcessor<Double, Integer> {

    private final int period;
    private final List<Double> values = new ArrayList<>();
    private final double factor;
    private Double cma;
    private Double cts;

    public RangeFilter(String name, String selector, int period, double factor) {
        super(name, selector);
        this.period = period;
        this.factor = factor;
    }

    @Override
    protected Integer calculate(Double value) {
        if (values.isEmpty()) {
            for (int i = 0; i < period; i++) {
                values.add(value);
            }
            cma = value;
            cts = value;
        }

        values.add(value);
        values.remove(0);

        var sd = calculateSd(values) * factor;
        var sma = values.stream().mapToDouble(d -> d).average().orElse(value);

        var secma = Math.pow(sma - cma, 2);
        var sects = Math.pow(value - cts, 2);
        var ka = sd < secma ? 1 - sd / secma : 0;
        var kb = sd < sects ? 1 - sd / sects : 0;

        cma = ka * sma + (1 - ka) * cma;
        cts = kb * value + (1 - kb) * cts;

        return cts > cma ? 1 : -1;
    }

    public double calculateSd(List<Double> values) {
        double sum = 0.0;
        double standardDeviation = 0.0;

        for(double num : values) {
            sum += num;
        }

        double mean = sum / values.size();

        for(double num: values) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / values.size());
    }

    @Override
    public DependencyProcessor mutate() {
        return new RangeFilter(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1),
                Mutations.mutate(factor, new double[] {factor * 0.5, factor * 1.5}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RangeFilter(name, selector, period, factor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RangeFilter that = (RangeFilter) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "RangeFilter{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                ", factor=" + factor +
                '}';
    }
}
