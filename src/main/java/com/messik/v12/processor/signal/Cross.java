package com.messik.v12.processor.signal;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Cross implements DependencyProcessor {

    private final String name;
    private final String mainSelector;
    private final String confirmationSelector;

    public Cross(String name, String mainSelector, String confirmationSelector) {
        this.name = name;
        this.mainSelector = mainSelector;
        this.confirmationSelector = confirmationSelector;
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

        data.put(name, main > confirmation ? 1 : -1);
    }

    @Override
    public DependencyProcessor mutate() {
       return new Cross(name, mainSelector, confirmationSelector);
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Cross that = (Cross) o;

        return new EqualsBuilder().append(name, that.name).append(mainSelector, that.mainSelector).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(mainSelector).toHashCode();
    }

    @Override
    public String toString() {
        return "Cross{" +
                "name='" + name + '\'' +
                ", mainSelector='" + mainSelector + '\'' +
                ", confirmationSelector='" + confirmationSelector + '\'' +
                '}';
    }
}
