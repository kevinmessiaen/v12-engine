package com.messik.v12.processor.pivot;

import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Fractalize implements DependencyProcessor {

    private final String name;
    private final String topSelector;
    private final String bottomSelector;

    public Fractalize(String name, String topSelector, String bottomSelector) {
        this.name = name;
        this.topSelector = topSelector;
        this.bottomSelector = bottomSelector;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(topSelector, bottomSelector);
    }

    @Override
    public void process(Map<String, Object> data) {
        boolean top = (boolean) data.get(topSelector);
        boolean bottom = (boolean) data.get(bottomSelector);

        data.put(name, top ? 1 : bottom ? -1 : 0);
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

        Fractalize that = (Fractalize) o;

        return new EqualsBuilder().append(name, that.name).append(topSelector, that.topSelector).append(bottomSelector, that.bottomSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(topSelector).append(bottomSelector).toHashCode();
    }
}
