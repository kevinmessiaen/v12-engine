package com.messik.v12.processor.momentum;

import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

import java.util.ArrayList;
import java.util.List;

public class RateOfChange extends SimpleProcessor<Double, Double> {

    private final int period;
    private List<Double> latest = new ArrayList<>();

    public RateOfChange(String name, String selector, int period) {
        super(name, selector);
        this.period = period;
    }

    @Override
    protected Double calculate(Double data) {
        if (latest.isEmpty()) {
            for(int i = 0; i < period; i ++) {
                latest.add(data);
            }
        }

        latest.add(data);
        var last = latest.remove(0);

        return 100 * (data - last) / last;
    }

    @Override
    public DependencyProcessor mutate() {
        return new RateOfChange(name, selector, Mutations.mutate(period, new int[] {1, 100}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return new RateOfChange(name, selector, period);
    }


}
