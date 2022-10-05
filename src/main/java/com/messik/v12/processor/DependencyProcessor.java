package com.messik.v12.processor;

import java.util.Map;
import java.util.Set;

public interface DependencyProcessor extends Processor<Map<String, Object>> {

    Set<String> provide();
    Set<String> require();

    @Override
    DependencyProcessor mutate();

    @Override
    DependencyProcessor copy();
}
