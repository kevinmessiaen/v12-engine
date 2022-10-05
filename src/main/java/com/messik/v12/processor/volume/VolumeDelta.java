package com.messik.v12.processor.volume;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;

import java.util.*;

public class VolumeDelta implements DependencyProcessor {

    private final String name;
    private final String bullSelector;
    private final String bearSelector;
    private final String volumeSelector;
    private final int period;
    private double cvd;
    private List<Double> cvds = new ArrayList<>();

    public VolumeDelta(String name,
                       String bullSelector,
                       String bearSelector,
                       String volumeSelector,
                       int period) {
        this.name = name;
        this.bullSelector = bullSelector;
        this.bearSelector = bearSelector;
        this.volumeSelector = volumeSelector;
        this.period = period;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(bearSelector, bearSelector, volumeSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var bull = (double) data.get(bullSelector);
        var bear = (double) data.get(bearSelector);
        var volume = (double) data.get(volumeSelector);

        var bullVolume = (bull / (bull + bear)) * volume;
        var bearVolume = (bear / (bull + bear)) * volume;

        var delta = bullVolume - bearVolume;
        cvd += delta;

        if (cvds.isEmpty()) {
            for (int i = 0; i < period; i ++) {
                cvds.add(cvd);
            }
        }

        cvds.add(cvd);
        cvds.remove(0);

        var cvdMa = cvds.stream().mapToDouble(d -> d).average().orElse(cvd);

        data.put(name, cvd > cvdMa ? 1 : -1);
    }

    @Override
    public DependencyProcessor mutate() {
        return new VolumeDelta(name, bullSelector, bearSelector, volumeSelector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new VolumeDelta(name, bullSelector, bearSelector, volumeSelector, period);
    }

    @Override
    public String toString() {
        return "VolumeDelta{" +
                "name='" + name + '\'' +
                ", bullSelector='" + bullSelector + '\'' +
                ", bearSelector='" + bearSelector + '\'' +
                ", volumeSelector='" + volumeSelector + '\'' +
                ", period=" + period +
                '}';
    }
}
