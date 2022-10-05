package com.messik.v12.processor.momentum;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;

import java.util.*;

public class AverageDirectionalIndexMasaNakamura implements DependencyProcessor {

    private final String name;
    private final String dipName;
    private final String dimName;
    private final String trueRangeSelector;
    private final String highSelector;
    private final String lowSelector;
    private final int period;
    private Double lastHigh;
    private Double lastLow;
    private double smoothedTrueRange;
    private double smoothedDirectionalMovementPlus;
    private double smoothedDirectionalMovementMinus;
    private final List<Double> dxs = new ArrayList<>();

    public AverageDirectionalIndexMasaNakamura(String name,
                                               String trueRangeSelector,
                                               String highSelector,
                                               String lowSelector,
                                               int period) {
        this.name = name;
        this.dipName = name + "_dip";
        this.dimName = name + "_dim";
        this.trueRangeSelector = trueRangeSelector;
        this.highSelector = highSelector;
        this.lowSelector = lowSelector;
        this.period = period;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(trueRangeSelector, highSelector, lowSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var trueRange = (double) data.get(trueRangeSelector);
        var high = (double) data.get(highSelector);
        var low = (double) data.get(lowSelector);

        if (lastHigh == null) {
            lastHigh = high;
            lastLow = low;
        }

        var directionalMovementPlus = high - lastHigh > lastLow - low ? Math.max(high - lastHigh, 0) : 0;
        var directionalMovementMinus =  lastLow - low > high - lastHigh ?  Math.max(lastLow - low, 0) : 0;
        smoothedTrueRange = smoothedTrueRange - smoothedTrueRange / period + trueRange;
        smoothedDirectionalMovementPlus = smoothedDirectionalMovementPlus - smoothedDirectionalMovementPlus / period + directionalMovementPlus;
        smoothedDirectionalMovementMinus = smoothedDirectionalMovementMinus - smoothedDirectionalMovementMinus / period + directionalMovementMinus;
        var DIP = smoothedDirectionalMovementPlus  / smoothedTrueRange * 100;
        var DIM = smoothedDirectionalMovementMinus / smoothedTrueRange * 100;
        var DX = Math.abs(DIP - DIM) / (DIP + DIM) * 100;

        if (dxs.isEmpty()) {
            for (int i = 0; i < data.size(); i ++) {
                dxs.add(DX);
            }
        }
        dxs.add(DX);
        dxs.remove(0);

        lastHigh = high;
        lastLow = low;

        data.put("adx_str", smoothedTrueRange);
        data.put(name, dxs.stream().mapToDouble(d -> d).average().orElse(DX));
        data.put(dipName, DIP);
        data.put(dimName, DIM);
    }

    @Override
    public DependencyProcessor mutate() {
        return new AverageDirectionalIndexMasaNakamura(name, trueRangeSelector, highSelector, lowSelector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new AverageDirectionalIndexMasaNakamura(name, trueRangeSelector, highSelector, lowSelector, period);
    }

    @Override
    public String toString() {
        return "AverageDirectionalIndexMasaNakamura{" +
                "name='" + name + '\'' +
                ", trueRangeSelector='" + trueRangeSelector + '\'' +
                ", highSelector='" + highSelector + '\'' +
                ", lowSelector='" + lowSelector + '\'' +
                ", period=" + period +
                '}';
    }
}
