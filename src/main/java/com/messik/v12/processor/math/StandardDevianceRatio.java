package com.messik.v12.processor.math;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class StandardDevianceRatio extends SimpleProcessor<Double, Double> {

    private final int period;
    private final List<Double> values = new ArrayList<>();

    public StandardDevianceRatio(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
    }

    @Override
    protected Double calculate(Double value) {
        if (values.isEmpty()) {
            for (int i = 0; i < period; i++) {
                values.add(value);
            }
        }

        values.add(value);
        values.remove(0);

        return calculateSd(values) / values.stream().mapToDouble(d -> d).average().orElse(value);
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
        return new StandardDevianceRatio(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new StandardDevianceRatio(name, selector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StandardDevianceRatio that = (StandardDevianceRatio) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "StandardDevianceRatio{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", period=" + period +
                '}';
    }
}
