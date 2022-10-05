package com.messik.v12.processor.momentum;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RelativeStrengthIndexMoneyFlowIndex implements DependencyProcessor {

    private final String name;
    private final String candlestickSelector;
    private final int period;
    private final double multiplier;
    private final double rsiMfiPosY;

    private final List<Double> history = new ArrayList<>();

    public RelativeStrengthIndexMoneyFlowIndex(String name, String candlestickSelector, int period, double multiplier, double rsiMfiPosY) {
        this.name = name;
        this.candlestickSelector = candlestickSelector;
        this.period = period;
        this.multiplier = multiplier;
        this.rsiMfiPosY = rsiMfiPosY;
    }


    @Override
    public Set<String> provide() {
        return null;
    }

    @Override
    public Set<String> require() {
        return null;
    }

    @Override
    public void process(Map<String, Object> data) {
        var candlestick = (CandlestickWrapper) data.get(candlestickSelector);

        var value = ((candlestick.getClose() - candlestick.getOpen()) / (candlestick.getHigh() - candlestick.getLow())) * multiplier;
        if (history.isEmpty()) {
            for (int i = 0; i < period; i++) {
                history.add(value);
            }
        }

        history.add(value);
        history.remove(0);

        data.put(name, history.stream().mapToDouble(d -> d).average().orElse(value) - rsiMfiPosY);
    }

    @Override
    public DependencyProcessor mutate() {
        return new RelativeStrengthIndexMoneyFlowIndex(name, candlestickSelector,
                Mutations.mutate(period, new int[] {1, 100}, 0.1),
                Mutations.mutate(multiplier, new double[] {multiplier * 0.5, multiplier * 1.5}, 0.1),
                Mutations.mutate(rsiMfiPosY, new double[] {rsiMfiPosY * 0.5, rsiMfiPosY * 1.5}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RelativeStrengthIndexMoneyFlowIndex(name, candlestickSelector, period, multiplier, rsiMfiPosY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RelativeStrengthIndexMoneyFlowIndex that = (RelativeStrengthIndexMoneyFlowIndex) o;

        return new EqualsBuilder().append(period, that.period).append(multiplier, that.multiplier).append(rsiMfiPosY, that.rsiMfiPosY).append(name, that.name).append(candlestickSelector, that.candlestickSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(candlestickSelector).append(period).append(multiplier).append(rsiMfiPosY).toHashCode();
    }

    @Override
    public String toString() {
        return "RelativeStrengthIndexMoneyFlowIndex{" +
                "name='" + name + '\'' +
                ", candlestick='" + candlestickSelector + '\'' +
                ", period=" + period +
                ", multiplier=" + multiplier +
                ", rsiMfiPosY=" + rsiMfiPosY +
                '}';
    }
}
