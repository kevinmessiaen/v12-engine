package com.messik.v12.processor.pivot;

import com.messik.v12.processor.GroupedDependencyProcessor;

import java.util.List;

public class Pivots {


    public static GroupedDependencyProcessor divergences(String name, String high, String low, String indicator) {
        String top = indicator + "_fractal_top";
        String bottom = indicator + "_fractal_bottom";
        String fractal = indicator + "_fractal";
        return new GroupedDependencyProcessor(List.of(
                new TopFractal(top, indicator),
                new BottomFractal(bottom, indicator),
                new Fractalize(fractal, top, bottom),
                new FractalDivergence(name, fractal, high, low, indicator)
        ));
    }
}
