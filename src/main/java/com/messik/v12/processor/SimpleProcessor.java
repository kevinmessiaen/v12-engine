package com.messik.v12.processor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class SimpleProcessor<R, L> implements DependencyProcessor {

    protected final String name;
    protected final String selector;

    public SimpleProcessor(String name, String selector) {
        this.name = name;
        this.selector = selector;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Collections.singleton(selector);
    }

    @Override
    public void process(Map<String, Object> data) {
        data.put(name, calculate((R) data.get(selector)));
    }

    protected abstract L calculate(R data);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SimpleProcessor<?, ?> that = (SimpleProcessor<?, ?>) o;

        return new EqualsBuilder().append(name, that.name).append(selector, that.selector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(selector).toHashCode();
    }
}
