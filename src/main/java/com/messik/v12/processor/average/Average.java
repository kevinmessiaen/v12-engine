package com.messik.v12.processor.average;

import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Average implements DependencyProcessor {

    private final String name;
    private final Set<String> requirements;

    public Average(String name, Set<String> requirements) {
        this.name = name;
        this.requirements = requirements;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return requirements;
    }

    @Override
    public void process(Map<String, Object> data) {
        data.put(name, requirements.stream()
                .mapToDouble(requirement -> (double) data.get(requirement))
                .average()
                .orElse(0.0));
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

        Average average = (Average) o;

        return new EqualsBuilder().append(name, average.name).append(requirements, average.requirements).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(requirements).toHashCode();
    }

    @Override
    public String toString() {
        return "Average{" +
                "name='" + name + '\'' +
                ", requirements=" + requirements +
                '}';
    }
}
