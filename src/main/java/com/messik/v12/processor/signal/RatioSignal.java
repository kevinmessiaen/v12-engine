package com.messik.v12.processor.signal;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RatioSignal implements DependencyProcessor {

    private final String name;
    private final String mainSelector;
    private final String confirmationSelector;
    private final double ratio;

    public RatioSignal(String name, String mainSelector, String confirmationSelector, double ratio) {
        this.name = name;
        this.mainSelector = mainSelector;
        this.confirmationSelector = confirmationSelector;
        this.ratio = ratio;
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

        data.put(name, main > ratio * confirmation);
    }

    @Override
    public DependencyProcessor mutate() {
        return new RatioSignal(name, mainSelector, confirmationSelector,
                Mutations.mutate(ratio, new double[] {ratio * 0.5, ratio * 1.5}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RatioSignal that = (RatioSignal) o;

        return new EqualsBuilder().append(ratio, that.ratio).append(name, that.name).append(mainSelector, that.mainSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(mainSelector).append(ratio).toHashCode();
    }

    @Override
    public String toString() {
        return "RatioSignal{" +
                "name='" + name + '\'' +
                ", mainSelector='" + mainSelector + '\'' +
                ", confirmationSelector='" + confirmationSelector + '\'' +
                ", ratio=" + ratio +
                '}';
    }
}
