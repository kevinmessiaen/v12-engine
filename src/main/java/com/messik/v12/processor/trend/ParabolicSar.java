package com.messik.v12.processor.trend;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ParabolicSar implements DependencyProcessor {

    private final String name;
    private final String candlestickSelector;
    private int currentTrend;
    private double accelerationFactor;
    private final double accelerationStart;
    private final double accelerationStep;
    private final double accelerationStop;
    private double lastSar;
    private double currentExtremePoint;

    public ParabolicSar(String name, String candlestickSelector, double accelerationStart, double accelerationStep, double accelerationStop) {
        this.name = name;
        this.candlestickSelector = candlestickSelector;
        this.accelerationStart = accelerationStart;
        this.accelerationStep = accelerationStep;
        this.accelerationStop = accelerationStop;
    }

    @Override
    public Set<String> provide() {
        return Set.of(name);
    }

    @Override
    public Set<String> require() {
        return Collections.singleton(candlestickSelector);
    }

    @Override
    public DependencyProcessor mutate() {
        return new ParabolicSar(name, candlestickSelector,
                Mutations.mutate(accelerationStart, new double[] {0, 0.02}, 0.1),
                Mutations.mutate(accelerationStep, new double[] {0.001, 0.02}, 0.1),
                Mutations.mutate(accelerationStop, new double[] {0.005, 2}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new ParabolicSar(name, candlestickSelector, accelerationStart, accelerationStep, accelerationStop);
    }

    @Override
    public void process(Map<String, Object> data) {
        var candlestick = (CandlestickWrapper) data.get(candlestickSelector);

        if (currentTrend == 0) {
            currentTrend = candlestick.getClose() > candlestick.getOpen() ? 1 : -1;
            if (currentTrend < 0) { // down trend
                lastSar = candlestick.getHigh();
                // two first bars
                currentExtremePoint = lastSar;
            } else { // up trend
                lastSar =  candlestick.getLow();
                // first bars
                currentExtremePoint = lastSar;
                accelerationFactor = accelerationStart;
            }

            data.put(name, currentTrend);
            return;
        }

        if (currentTrend > 0) {
            var sar = lastSar + accelerationFactor * (currentExtremePoint - lastSar);
            currentTrend = candlestick.getLow() > sar ? 1 : -1;
            if (currentTrend < 0) { // check if sar touches the low price
                sar = candlestick.getHigh();
                accelerationFactor = accelerationStart;
                currentExtremePoint = sar;
            } else if (candlestick.getHigh() > currentExtremePoint) {
                    accelerationFactor = Math.min(accelerationFactor + accelerationStep, accelerationStop);
                    currentExtremePoint = candlestick.getHigh();
            }
        } else {
            var sar = lastSar - accelerationFactor * (lastSar - currentExtremePoint);
            currentTrend = candlestick.getHigh() > sar ? 1 : -1;
            if (currentTrend > 0) { // check if sar touches the low price
                sar = candlestick.getLow();
                accelerationFactor = accelerationStart;
                currentExtremePoint = sar;
            } else if (candlestick.getLow() < currentExtremePoint) {
                accelerationFactor = Math.min(accelerationFactor + accelerationStep, accelerationStop);
                currentExtremePoint = candlestick.getLow();
            }
        }

        data.put(name, currentTrend);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ParabolicSar that = (ParabolicSar) o;

        return new EqualsBuilder().append(accelerationStart, that.accelerationStart).append(accelerationStep, that.accelerationStep).append(accelerationStop, that.accelerationStop).append(name, that.name).append(candlestickSelector, that.candlestickSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(candlestickSelector).append(accelerationStart).append(accelerationStep).append(accelerationStop).toHashCode();
    }

    @Override
    public String toString() {
        return "ParaabolicSar{" +
                "name='" + name + '\'' +
                ", candlestickSelector='" + candlestickSelector + '\'' +
                ", accelerationStart=" + accelerationStart +
                ", accelerationStep=" + accelerationStep +
                ", accelerationStop=" + accelerationStop +
                '}';
    }
}
