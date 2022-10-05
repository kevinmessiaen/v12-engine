package com.messik.v12.processor.pivot;

import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

import java.util.ArrayList;
import java.util.List;

public class TopFractal extends SimpleProcessor<Double, Boolean> {

    private final List<Double> history = new ArrayList<>();

    public TopFractal(String name, String selector) {
        super(name, selector);
    }

    @Override
    public DependencyProcessor mutate() {
        return new TopFractal(name, selector);
    }

    @Override
    public DependencyProcessor copy() {
        return new TopFractal(name, selector);
    }

    @Override
    protected Boolean calculate(Double data) {
        if (history.isEmpty()) {
            for (int i = 0; i < 5; i ++) {
                history.add(data);
            }
        }

        history.add(data);
        history.remove(0);

        return history.get(0) < history.get(2) && history.get(1) < history.get(2)
                && history.get(2) > history.get(3) && history.get(2) > history.get(4);
    }


}
