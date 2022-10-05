package com.messik.v12.processor.momentum;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AverageDirectionalIndex implements DependencyProcessor {

    private final String name;
    private final String atrSelector;
    private final String highChangeSelector;
    private final String lowChangeSelector;
    private final int period;
    private Double plusRma;
    private Double minusRma;
    private Double dxRma;
    private final double alpha;

    public AverageDirectionalIndex(String name, String atrSelector, String highChangeSelector, String lowChangeSelector, int period) {
        this.name = name;
        this.atrSelector = atrSelector;
        this.highChangeSelector = highChangeSelector;
        this.lowChangeSelector = lowChangeSelector;
        this.period = period;
        this.alpha = 1.0 / period;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(atrSelector, highChangeSelector, lowChangeSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var atr = (double) Objects.requireNonNullElse(data.get(atrSelector), 0.0);
        var up = (double) data.get(highChangeSelector);
        var down = -(double) data.get(lowChangeSelector);

        var plusDm = up > down && up > 0 ? up : 0;
        var minusDm = down > up && down > 0 ? down : 0;

        if (plusRma == null) {
            plusRma = plusDm;
            minusRma = minusDm;
        }
        plusRma = alpha * plusDm + (1 - alpha) * plusRma;
        minusRma = alpha * minusDm + (1 - alpha) * minusRma;

        var _plus = 100 * plusRma / atr;
        var _minus = 100 * minusRma / atr;
        var sum = _plus + _minus;

        var dx = Math.abs(_plus - _minus)/ (sum == 0 ? 1 : sum);
        if (dxRma == null) {
            dxRma = dx;
        }

        dxRma = alpha * dx + (1 - alpha) * dxRma;

        data.put(name, 100 * dxRma);
    }

    @Override
    public DependencyProcessor mutate() {
        return new AverageDirectionalIndex(name, atrSelector, highChangeSelector, lowChangeSelector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new AverageDirectionalIndex(name, atrSelector, highChangeSelector, lowChangeSelector, period);
    }
}
