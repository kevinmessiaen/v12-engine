package com.messik.v12.processor.trend;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SuperTrend implements DependencyProcessor {

    private final String name;
    private final String closeSelector;
    private final String phSelector;
    private final String plSelector;
    private final String atrSelector;
    private final double longFactor;
    private final double shortFactor;
    private double center;
    private Double tup;
    private Double tdown;
    private int trend;
    private Double lastClose;

    public SuperTrend(String name, String closeSelector, String phSelector, String plSelector, String atrSelector, double longFactor, double shortFactor) {
        this.name = name;
        this.closeSelector = closeSelector;
        this.phSelector = phSelector;
        this.plSelector = plSelector;
        this.atrSelector = atrSelector;
        this.longFactor = longFactor;
        this.shortFactor = shortFactor;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(closeSelector, phSelector, plSelector, atrSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var close = (double) data.get(closeSelector);
        var ph =  (double) data.get(phSelector);
        var pl =  (double) data.get(plSelector);
        var atr =  (double) data.get(atrSelector);

        if (lastClose == null) {
            lastClose = close;
            center = (ph + pl) / 2;
        }

        center = (center * 2 + (ph + pl) / 2) / 3;

        double up = center - (longFactor * atr);
        double down = center + (shortFactor * atr);
        trend = tup == null ? 0
                : close > tdown ? 1
                : close < tup ? -1 : trend;
        tup = tup != null && lastClose > tup ? Math.max(up, tup) : up;
        tdown = tdown != null && lastClose < tdown ? Math.min(down, tdown) : down;
        lastClose = close;

        data.put(name, trend);
        data.put(name + "_tup", tup);
        data.put(name + "_tdown", tdown);
        data.put(name + "_sl", trend < 0 ? tdown : tup);
        data.put(name + "_center", center);
    }

    @Override
    public DependencyProcessor mutate() {
        return new SuperTrend(name, closeSelector, phSelector, plSelector, atrSelector,
                Mutations.mutate(longFactor, new double[] {0.1, 12}, 0.1),
                Mutations.mutate(shortFactor, new double[] {0.1, 12}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new SuperTrend(name, closeSelector, phSelector, plSelector, atrSelector, longFactor, shortFactor);
    }

    @Override
    public String toString() {
        return "SuperTrend{" +
                "name='" + name + '\'' +
                ", closeSelector='" + closeSelector + '\'' +
                ", phSelector='" + phSelector + '\'' +
                ", plSelector='" + plSelector + '\'' +
                ", atrSelector='" + atrSelector + '\'' +
                ", longFactor=" + longFactor +
                ", shortFactor=" + shortFactor +
                '}';
    }
}
