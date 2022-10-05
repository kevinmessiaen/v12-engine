package com.messik.v12.processor.trend;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.average.ExponentialMovingAverage;
import com.messik.v12.processor.average.SimpleMovingAverage;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class WaveTrend implements DependencyProcessor {

    private final String name;
    private final String selector;
    private final int channelPeriod;
    private final int averagePeriod;
    private final int movingAveragePeriod;
    private final String emaName;
    private final ExponentialMovingAverage ema;
    private final String emaDeltaName;
    private final String emaDeltaCurrentName;
    private final ExponentialMovingAverage emaDelta;
    private final String ciName;
    private final String ciFastName;
    private final ExponentialMovingAverage ciFast;
    private final String ciSlowName;
    private final SimpleMovingAverage ciSlow;

    public WaveTrend(String name, String selector, int channelPeriod, int averagePeriod, int movingAveragePeriod) {
        this.name = name;
        this.selector = selector;
        this.channelPeriod = channelPeriod;
        this.averagePeriod = averagePeriod;
        this.movingAveragePeriod = movingAveragePeriod;
        this.emaName = selector + "_ema";
        this.ema = new ExponentialMovingAverage(emaName, selector, channelPeriod);
        this.emaDeltaName = emaName + "_delta";
        this.emaDeltaCurrentName = emaDeltaName + "_current";
        this.emaDelta = new ExponentialMovingAverage(emaDeltaName, emaDeltaCurrentName, channelPeriod);
        this.ciName = selector + "_ci";
        this.ciSlowName = ciName + "_slow";
        this.ciFastName = ciName + "_fast";
        this.ciFast = new ExponentialMovingAverage(ciFastName, ciName, averagePeriod);
        this.ciSlow = new SimpleMovingAverage(ciSlowName, ciFastName, movingAveragePeriod);
    }

    @Override
    public Set<String> provide() {
        return Set.of(emaName, emaDeltaName, emaDeltaCurrentName, ciName, ciFastName, ciSlowName);
    }

    @Override
    public Set<String> require() {
        return Collections.singleton(selector);
    }

    @Override
    public DependencyProcessor mutate() {
        return new WaveTrend(name, selector,
                Mutations.mutate(channelPeriod, new int[] {1, 100}, 0.1),
                Mutations.mutate(averagePeriod, new int[] {1, 100}, 0.1),
                Mutations.mutate(movingAveragePeriod, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new WaveTrend(name, selector, channelPeriod, averagePeriod, movingAveragePeriod);
    }

    @Override
    public void process(Map<String, Object> data) {
        var src = (double) data.get(selector);

        ema.process(data);
        var emaValue = (double) data.get(emaName);

        data.put(emaDeltaCurrentName, Math.abs(src - emaValue));
        emaDelta.process(data);
        var emaDeltaValue = (double) data.get(emaName);

        var ci = (src - emaValue) / (0.015 * emaDeltaValue);
        data.put(ciName, ci);

        ciFast.process(data);
        ciSlow.process(data);

        data.put(name, (double) data.get(ciSlowName) < (double) data.get(ciFastName) ? 1 : - 1 );
    }

    @Override
    public String toString() {
        return "WaveTrend{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", channelPeriod=" + channelPeriod +
                ", averagePeriod=" + averagePeriod +
                ", movingAveragePeriod=" + movingAveragePeriod +
                '}';
    }
}
