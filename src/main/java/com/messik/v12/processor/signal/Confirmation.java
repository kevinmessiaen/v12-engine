package com.messik.v12.processor.signal;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Confirmation implements DependencyProcessor {

    private final String name;
    private final String mainSelector;
    private final String confirmationSelector;
    private final double min;
    private final double max;

    public Confirmation(String name, String mainSelector, String confirmationSelector, double min, double max) {
        this.name = name;
        this.mainSelector = mainSelector;
        this.confirmationSelector = confirmationSelector;
        this.min = min;
        this.max = max;
    }

    @Override
    public Set<String> require() {
        return Set.of(mainSelector, confirmationSelector);
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public void process(Map<String, Object> data) {
        var main = (double) data.get(mainSelector);
        var confirmation = (double) data.get(confirmationSelector);

        if (main > confirmation && (confirmation < min || confirmation > max)) {
            data.put(name, 1);
        } else if (main < confirmation && (confirmation < min || confirmation > max)) {
            data.put(name, -1);
        } else {
            data.put(name, 0);
        }
    }

    @Override
    public DependencyProcessor mutate() {
        var delta = Math.abs(max - min);
        return new Confirmation(name, mainSelector, confirmationSelector,
                Mutations.mutate(min, new double[] {min - delta, min + delta}, 0.1),
                Mutations.mutate(max, new double[] {max - delta, max + delta}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Confirmation that = (Confirmation) o;

        return new EqualsBuilder().append(min, that.min).append(max, that.max).append(name, that.name).append(mainSelector, that.mainSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(mainSelector).append(min).append(max).toHashCode();
    }

    @Override
    public String toString() {
        return "Confirmation{" +
                "name='" + name + '\'' +
                ", mainSelector='" + mainSelector + '\'' +
                ", confirmationSelector='" + confirmationSelector + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
