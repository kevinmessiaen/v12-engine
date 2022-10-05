package com.messik.v12.processor.trend;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.average.ExponentialMovingAverage;
import com.messik.v12.processor.average.SimpleMovingAverage;
import com.messik.v12.processor.pivot.HighPivot;
import com.messik.v12.processor.pivot.LowPivot;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SchaffTrendCycle implements DependencyProcessor {

    private final String name;
    private final String selector;
    private final int period;
    private final int fastPeriod;
    private final int slowPeriod;
    private final String fastEmaName;
    private final ExponentialMovingAverage fastEma;
    private final String slowEmaName;
    private final ExponentialMovingAverage slowEma;
    private final String macdName;
    private final String alphaName;
    private final LowPivot alpha;
    private final String betaName;
    private final HighPivot beta;
    private final String deltaName;
    private final String epsilonName;
    private final LowPivot epsilon;
    private final String zetaName;
    private final HighPivot zeta;
    private double lastGamma;
    private Double lastDelta;
    private double lastEta;
    private Double lastStc;
    private final double factor;

    public SchaffTrendCycle(String name, String selector, int period, int fastPeriod, int slowPeriod, double factor) {
        this.name = name;
        this.selector = selector;
        this.period = period;
        this.fastPeriod = fastPeriod;
        this.slowPeriod = slowPeriod;
        this.fastEmaName = selector + "_fast_ema";
        this.fastEma = new ExponentialMovingAverage(fastEmaName, selector, fastPeriod);
        this.slowEmaName = selector + "_slow_ema";
        this.slowEma = new ExponentialMovingAverage(slowEmaName, selector, slowPeriod);
        this.macdName = selector + "_macd";
        this.alphaName = macdName + "_alpha";
        this.alpha = new LowPivot(alphaName, macdName, period);
        this.betaName = macdName + "_beta";
        this.beta = new HighPivot(betaName, macdName, period);
        this.deltaName = macdName + "_delta";
        this.epsilonName = macdName + "_epsilon";
        this.epsilon = new LowPivot(epsilonName, deltaName, period);
        this.zetaName = macdName + "_zeta";
        this.zeta = new HighPivot(zetaName, macdName, period);
        this.factor = factor;
    }

    @Override
    public Set<String> provide() {
        return Set.of();
    }

    @Override
    public Set<String> require() {
        return Collections.singleton(selector);
    }

    @Override
    public DependencyProcessor mutate() {
        return new SchaffTrendCycle(name, selector,
                Mutations.mutate(period, new int[] {1, 100}, 0.1),
                Mutations.mutate(fastPeriod, new int[] {1, 100}, 0.1),
                Mutations.mutate(slowPeriod, new int[] {1, 100}, 0.1),
                Mutations.mutate(factor, new double[] {0.1, 5.0}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new SchaffTrendCycle(name, selector, period, fastPeriod, slowPeriod, factor);
    }

    @Override
    public void process(Map<String, Object> data) {
        var src = (double) data.get(selector);

        fastEma.process(data);
        slowEma.process(data);

        var fastEmaValue = (double) data.get(fastEmaName);
        var slowEmaValue = (double) data.get(slowEmaName);

        var macdVal = fastEmaValue - slowEmaValue;
        data.put(macdName, macdVal);

        alpha.process(data);
        beta.process(data);

        var alphaVal = (double) data.get(alphaName);
        var betaVal = (double) data.get(betaName) - alphaVal;
        var gamma = betaVal > 0 ? (macdVal - alphaVal) / betaVal * 100 : lastGamma;
        lastGamma = gamma;
        var delta = lastDelta == null ? gamma : lastDelta + factor * (gamma - lastDelta);
        lastDelta = delta;

        data.put(deltaName, delta);

        epsilon.process(data);
        zeta.process(data);
        var epsilonVal = (double) data.get(epsilonName);
        var zetaVal = (double) data.get(zetaName) - epsilonVal;
        var eta = zetaVal > 0 ? (delta - epsilonVal) / zetaVal * 100 : lastEta;
        lastEta = eta;
        double stc = lastStc == null ? eta : lastStc + factor * (eta - lastStc);
        lastStc = stc;
        data.put(name, stc);
    }

}
