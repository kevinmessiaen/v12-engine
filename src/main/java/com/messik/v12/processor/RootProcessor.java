package com.messik.v12.processor;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.data.CandlestickWrappers;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RootProcessor implements Processor<CandlestickWrapper> {

    private final Map<String, Object> result = new HashMap<>();
    private final List<DependencyProcessor> nodes;

    public RootProcessor(List<DependencyProcessor> nodes) {
        this.nodes = nodes;
    }

    public Map<String, Object> handle(CandlestickWrapper data) {
        process(data);
        return result;
    }

    @Override
    public void process(CandlestickWrapper data) {
        result.put("data", data);
        nodes.forEach(node -> node.process(result));
    }

    @Override
    public RootProcessor mutate() {
        return new RootProcessor(nodes.stream().map(DependencyProcessor::mutate).collect(Collectors.toList()));
    }

    @Override
    public RootProcessor copy() {
        return new RootProcessor(nodes.stream().map(DependencyProcessor::copy).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RootProcessor that = (RootProcessor) o;

        return new EqualsBuilder().append(nodes, that.nodes).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(nodes).toHashCode();
    }

    @Override
    public String toString() {
        return "RootProcessor{" +
                "nodes=" + nodes +
                '}';
    }
}
