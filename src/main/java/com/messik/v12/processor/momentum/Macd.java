package com.messik.v12.processor.momentum;

import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Macd implements DependencyProcessor {

    private final String name;
    private final String shortSelector;
    private final String longSelector;

    public Macd(String name, String shortSelector, String longSelector) {
        this.name = name;
        this.shortSelector = shortSelector;
        this.longSelector = longSelector;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(shortSelector, longSelector);
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
    public void process(Map<String, Object> data) {
        data.put(name, (double) data.get(shortSelector) - (double) data.get(longSelector));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Macd macd = (Macd) o;

        return new EqualsBuilder().append(name, macd.name).append(shortSelector, macd.shortSelector).append(longSelector, macd.longSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(shortSelector).append(longSelector).toHashCode();
    }

    @Override
    public String toString() {
        return "Macd{" +
                "name='" + name + '\'' +
                ", shortSelector='" + shortSelector + '\'' +
                ", longSelector='" + longSelector + '\'' +
                '}';
    }
}
