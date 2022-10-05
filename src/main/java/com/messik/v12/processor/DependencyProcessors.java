package com.messik.v12.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyProcessors {

    public static Set<String> requirements(List<DependencyProcessor> processors) {
        Set<String> missing = new HashSet<>();
        processors.forEach(processor -> {
            missing.addAll(processor.require());
            missing.removeAll(processor.provide());
        });
        return missing;
    }

}
