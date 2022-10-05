package com.messik.v12.processor.signal;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DynamicConfirmation implements DependencyProcessor {

    private final String name;
    private final String mainSelector;
    private final String minSelector;
    private final String maxSelector;

    public DynamicConfirmation(String name, String mainSelector,
                               String minSelector,
                               String maxSelector) {
        this.name = name;
        this.mainSelector = mainSelector;
        this.minSelector = minSelector;
        this.maxSelector = maxSelector;
    }

    @Override
    public Set<String> require() {
        return Set.of(mainSelector, minSelector, maxSelector);
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public void process(Map<String, Object> data) {
        var main = (double) data.get(mainSelector);
        var min = (double) data.get(minSelector);
        var max = (double) data.get(maxSelector);

        if (main > max) {
            data.put(name, -1);
        } else if (main < min) {
            data.put(name, 1);
        } else {
            data.put(name, 0);
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

        DynamicConfirmation that = (DynamicConfirmation) o;

        return new EqualsBuilder().append(name, that.name).append(mainSelector, that.mainSelector).append(minSelector, that.minSelector).append(maxSelector, that.maxSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(mainSelector).append(minSelector).append(maxSelector).toHashCode();
    }

    @Override
    public String toString() {
        return "DynamicConfirmation{" +
                "name='" + name + '\'' +
                ", mainSelector='" + mainSelector + '\'' +
                ", minSelector='" + minSelector + '\'' +
                ", maxSelector='" + maxSelector + '\'' +
                '}';
    }
}
