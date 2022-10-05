package com.messik.v12.processor.signal;

import com.messik.v12.constant.Action;
import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TrendWithVolatilityTpSlSignal implements DependencyProcessor {

    private final String name;
    private final String trendSelector;
    private final String confirmationSelector;
    private final String volatilitySelector;
    private final String slSelector;
    private final String centerSelector;
    private final String atrSelector;
    private final double tpFactor;

    public TrendWithVolatilityTpSlSignal(String name, String trendSelector, String confirmationSelector, String volatilitySelector,
                                         String slSelector, String centerSelector, String atrSelector, double tpFactor) {
        this.name = name;
        this.trendSelector = trendSelector;
        this.confirmationSelector = confirmationSelector;
        this.volatilitySelector = volatilitySelector;
        this.slSelector = slSelector;
        this.centerSelector = centerSelector;
        this.atrSelector = atrSelector;
        this.tpFactor = tpFactor;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(name, trendSelector, confirmationSelector, volatilitySelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        var trend = (int) data.get(trendSelector);
        var confirmation = (int) data.get(confirmationSelector);
        var volatility = (int) data.get(volatilitySelector);
        var sl = (double) data.get(slSelector);
        var atr = (double) Objects.requireNonNullElse(data.get(atrSelector), 0.0);
        var center = (double) data.get(centerSelector);
        var close = (double) data.get("close");

        data.put("sl", null);
        data.put("tp", null);
        if (volatility > 1 && trend != 0) {
            data.put(name, trend > 0 ? Action.SHORT : Action.LONG);
        } else if (volatility > 0 && trend > 0) {
            if (confirmation > 0) {
                data.put(name, Action.LONG);
                data.put("tp", Math.max(close, center + atr * tpFactor));
            } else {
                data.put(name, Action.CLOSE_SHORT);
            }
            data.put("sl", sl);
        } else if (volatility > 0 && trend < 0) {
            if (confirmation < 0) {
                data.put(name, Action.SHORT);
                data.put("tp", Math.min(close, center - atr * tpFactor));
            } else {
                data.put(name, Action.CLOSE_LONG);
            }
            data.put("sl", sl);
        } else {
            data.put(name, Action.NONE);
            data.put("sl", sl);
        }
    }

    @Override
    public DependencyProcessor mutate() {
        return new TrendWithVolatilityTpSlSignal(name, trendSelector, confirmationSelector, volatilitySelector, slSelector, centerSelector, atrSelector,
                Mutations.mutate(tpFactor, new double[] {tpFactor * 0.5, tpFactor * 1.5}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TrendWithVolatilityTpSlSignal that = (TrendWithVolatilityTpSlSignal) o;

        return new EqualsBuilder().append(name, that.name).append(trendSelector, that.trendSelector).append(confirmationSelector, that.confirmationSelector).append(volatilitySelector, that.volatilitySelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(trendSelector).append(confirmationSelector).append(volatilitySelector).toHashCode();
    }

    @Override
    public String toString() {
        return "TrendWithVolatilityTpSlSignal{" +
                "name='" + name + '\'' +
                ", trendSelector='" + trendSelector + '\'' +
                ", confirmationSelector='" + confirmationSelector + '\'' +
                ", volatilitySelector='" + volatilitySelector + '\'' +
                ", slSelector='" + slSelector + '\'' +
                ", centerSelector='" + centerSelector + '\'' +
                ", atrSelector='" + atrSelector + '\'' +
                ", tpFactor=" + tpFactor +
                '}';
    }
}
