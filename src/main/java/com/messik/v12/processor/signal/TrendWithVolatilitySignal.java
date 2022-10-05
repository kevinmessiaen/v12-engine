package com.messik.v12.processor.signal;

import com.messik.v12.constant.Action;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TrendWithVolatilitySignal implements DependencyProcessor {

    private final String name;
    private final String trendSelector;
    private final String confirmationSelector;
    private final String volatilitySelector;

    public TrendWithVolatilitySignal(String name, String trendSelector, String confirmationSelector, String volatilitySelector) {
        this.name = name;
        this.trendSelector = trendSelector;
        this.confirmationSelector = confirmationSelector;
        this.volatilitySelector = volatilitySelector;
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

        if (volatility > 1 && trend != 0) {
            data.put(name, trend > 0 ? Action.SHORT : Action.LONG);
        } else if (volatility > 0 && trend > 0) {
            data.put(name, confirmation > 0 ? Action.LONG : Action.CLOSE_SHORT);
        } else if (volatility > 0 && trend < 0) {
            data.put(name, confirmation < 0 ? Action.SHORT : Action.CLOSE_LONG);
        } else {
            data.put(name, Action.NONE);
        }
    }

    @Override
    public DependencyProcessor mutate() {
        return this;
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TrendWithVolatilitySignal that = (TrendWithVolatilitySignal) o;

        return new EqualsBuilder().append(name, that.name).append(trendSelector, that.trendSelector).append(confirmationSelector, that.confirmationSelector).append(volatilitySelector, that.volatilitySelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(trendSelector).append(confirmationSelector).append(volatilitySelector).toHashCode();
    }

    @Override
    public String toString() {
        return "TrendWithVolatilitySignal{" +
                "name='" + name + '\'' +
                ", trendSelector='" + trendSelector + '\'' +
                ", confirmationSelector='" + confirmationSelector + '\'' +
                ", volatilitySelector='" + volatilitySelector + '\'' +
                '}';
    }
}
