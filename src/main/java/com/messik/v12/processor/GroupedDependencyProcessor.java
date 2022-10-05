package com.messik.v12.processor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupedDependencyProcessor implements DependencyProcessor {

    private final List<DependencyProcessor> processors;
    private final Set<String> requirements;
    private final Set<String> provided;

    public GroupedDependencyProcessor(List<DependencyProcessor> processors) {
        this.processors = processors;
        requirements = DependencyProcessors.requirements(processors);
        provided = processors.stream().flatMap(processor -> processor.provide().stream()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> provide() {
        return provided;
    }

    @Override
    public Set<String> require() {
        return requirements;
    }

    @Override
    public void process(Map<String, Object> data) {
        processors.forEach(processor -> processor.process(data));
    }

    @Override
    public GroupedDependencyProcessor mutate() {
        return new GroupedDependencyProcessor(processors.stream().map(DependencyProcessor::mutate).collect(Collectors.toList()));
    }

    @Override
    public GroupedDependencyProcessor copy() {
        return new GroupedDependencyProcessor(processors.stream().map(DependencyProcessor::copy).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GroupedDependencyProcessor that = (GroupedDependencyProcessor) o;

        return new EqualsBuilder().append(processors, that.processors).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(processors).toHashCode();
    }

    @Override
    public String toString() {
        return "GroupedDependencyProcessor{" +
                "processors=" + processors +
                '}';
    }
}
