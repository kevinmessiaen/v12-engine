package com.messik.v12.processor.average;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VolumeWeightedMovingAverage implements DependencyProcessor {

    private final String name;
    private final String valueSelector;
    private final String volumeSelector;
    private final int period;
    private final List<Double> values = new ArrayList<>();
    private final List<Double> volumes = new ArrayList<>();

    public VolumeWeightedMovingAverage(String name, String valueSelector, String volumeSelector, int period) {
        this.name = name;
        this.valueSelector = valueSelector;
        this.volumeSelector = volumeSelector;
        this.period = period;
    }

    @Override
    public void process(Map<String, Object> data) {
        var value = (double) data.get(valueSelector);
        var volume = (double) data.get(volumeSelector);

        if (values.isEmpty()) {
            for (int i = 0; i < period; i++) {
                values.add(value);
                volumes.add(volume);
            }
        }

        values.add(value);
        volumes.add(volume);
        values.remove(0);
        volumes.remove(0);

        data.put(name, volumeWightedMovingAverage(values, volumes));
    }

    private double volumeWightedMovingAverage(List<Double> values, List<Double> volumes) {
        int weight = 0;
        double sum = 0;

        for (int i = 0; i < values.size(); i ++) {
            weight += volumes.get(i);
            sum += values.get(i) * volumes.get(i);
        }

        return sum / weight;
    }

    @Override
    public Set<String> provide() {
        return Set.of();
    }

    @Override
    public Set<String> require() {
        return Set.of();
    }

    @Override
    public DependencyProcessor mutate() {
        return new VolumeWeightedMovingAverage(name, valueSelector, volumeSelector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new VolumeWeightedMovingAverage(name, valueSelector, volumeSelector, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        VolumeWeightedMovingAverage that = (VolumeWeightedMovingAverage) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(period, that.period).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(period).toHashCode();
    }

    @Override
    public String toString() {
        return "VolumeWeightedMovingAverage{" +
                "name='" + name + '\'' +
                ", valueSelector='" + valueSelector + '\'' +
                ", volumeSelector='" + volumeSelector + '\'' +
                ", period=" + period +
                '}';
    }
}
