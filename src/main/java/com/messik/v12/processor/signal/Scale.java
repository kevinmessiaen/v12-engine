package com.messik.v12.processor.signal;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

public class Scale extends SimpleProcessor<Double, Integer> {

    private final double[] thresholds;

    public Scale(String name, String selector, double... thresholds) {
        super(name, selector);
        this.thresholds = thresholds;
    }

    @Override
    protected Integer calculate(Double data) {
        for (int i = 0; i < thresholds.length; i ++) {
            if (data < thresholds[i]) {
                return i;
            }
        }
        return thresholds.length;
    }

    @Override
    public DependencyProcessor mutate() {
        return new Scale(name, selector, Mutations.mutate(thresholds, Arrays.stream(thresholds)
                .mapToObj(value -> new double[] {value * 0.5, value * 1.5})
                .toArray(double[][]::new), 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Scale scale = (Scale) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(thresholds, scale.thresholds).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(thresholds).toHashCode();
    }

    @Override
    public String toString() {
        return "Scale{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", thresholds=" + Arrays.toString(thresholds) +
                '}';
    }
}
