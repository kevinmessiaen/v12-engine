package com.messik.v12.processor.pivot;

import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

public class FractalDivergence implements DependencyProcessor {

    private final String name;
    private final String fractalSelector;
    private final String highSelector;
    private final String lowSelector;
    private final String indicatorSelector;
    private final List<Double> highHistory = new ArrayList<>();
    private final List<Double> lowHistory = new ArrayList<>();
    private final List<Double> indicatorHistory = new ArrayList<>();
    private final List<Double> highIndicator = new ArrayList<>();
    private final List<Double> lowIndicator = new ArrayList<>();
    private final List<Double> highPrices = new ArrayList<>();
    private final List<Double> lowPrices = new ArrayList<>();

    public FractalDivergence(String name, String fractalSelector, String highSelector, String lowSelector, String indicatorSelector) {
        this.name = name;
        this.fractalSelector = fractalSelector;
        this.highSelector = highSelector;
        this.lowSelector = lowSelector;
        this.indicatorSelector = indicatorSelector;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(fractalSelector, highSelector, lowSelector, indicatorSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var fractal = (int) data.get(fractalSelector);
        var high = (double) data.get(highSelector);
        var low = (double) data.get(lowSelector);
        var indicator = (double) data.get(indicatorSelector);

        if (highHistory.isEmpty()) {
            for (int i = 0; i < 3; i ++) {
                highHistory.add(high);
                lowHistory.add(low);
                indicatorHistory.add(indicator);
            }
        }
        highHistory.add(high);
        lowHistory.add(low);
        indicatorHistory.add(indicator);
        highHistory.remove(0);
        lowHistory.remove(0);
        indicatorHistory.remove(0);

        if (fractal > 0) {
            if (highPrices.isEmpty()) {
                for (int i = 0; i < 3; i ++) {
                    highPrices.add(highHistory.get(0));
                    highIndicator.add(indicatorHistory.get(0));
                }
            }
            highPrices.add(highHistory.get(0));
            highIndicator.add(indicator);
            highPrices.remove(0);
            highIndicator.remove(0);

            data.put(name, (highHistory.get(0) > highPrices.get(2) && indicatorHistory.get(0) < highIndicator.get(0))
                    || (highHistory.get(0) < highPrices.get(2) && indicatorHistory.get(0) > highIndicator.get(0)) ? -1 : 0);
        } else if (fractal < 0) {
            if (lowPrices.isEmpty()) {
                for (int i = 0; i < 3; i ++) {
                    lowPrices.add(lowHistory.get(0));
                    lowIndicator.add(indicatorHistory.get(0));
                }
            }
            lowPrices.add(lowHistory.get(0));
            lowIndicator.add(indicatorHistory.get(0));
            lowPrices.remove(0);
            lowIndicator.remove(0);

            data.put(name, (lowHistory.get(0) < lowPrices.get(0) && indicatorHistory.get(0) > lowIndicator.get(0))
                    || (lowHistory.get(0)> lowPrices.get(0) && indicatorHistory.get(0) < lowIndicator.get(0)) ? 1 : 0);
        } else {
            data.put(name, 0);
        }
    }

    @Override
    public DependencyProcessor mutate() {
        return new FractalDivergence(name, fractalSelector, highSelector, lowSelector, indicatorSelector);
    }

    @Override
    public DependencyProcessor copy() {
        return new FractalDivergence(name, fractalSelector, highSelector, lowSelector, indicatorSelector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FractalDivergence that = (FractalDivergence) o;

        return new EqualsBuilder().append(name, that.name).append(fractalSelector, that.fractalSelector).append(highSelector, that.highSelector).append(lowSelector, that.lowSelector).append(indicatorSelector, that.indicatorSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(fractalSelector).append(highSelector).append(lowSelector).append(indicatorSelector).toHashCode();
    }
}
