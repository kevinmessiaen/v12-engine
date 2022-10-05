package com.messik.v12.processor.trend;

import com.messik.v12.processor.GroupedDependencyProcessor;
import com.messik.v12.processor.average.SimpleMovingAverage;
import com.messik.v12.processor.average.TrueRange;
import com.messik.v12.processor.pivot.HighPivot;
import com.messik.v12.processor.pivot.LowPivot;

import java.util.List;

public class Trends {

    public static GroupedDependencyProcessor superTrend(int atrPeriod, int pivotPeriod, double longFactor, double shortFactor) {
        return superTrend("super_trend", atrPeriod, pivotPeriod, longFactor, shortFactor);
    }
    public static GroupedDependencyProcessor superTrend(String name, int atrPeriod, int pivotPeriod, double longFactor, double shortFactor) {
        return superTrend(name, "data", "close", atrPeriod, "high", "low", pivotPeriod, longFactor, shortFactor);
    }

    public static GroupedDependencyProcessor superTrend(String name, String candlestick, String close, int atrPeriod,
                                                        String high, String low, int pivotPeriod,
                                                        double longFactor, double shortFactor) {
        return superTrend(name, candlestick, close, atrPeriod, high, pivotPeriod, low, pivotPeriod, longFactor, shortFactor);
    }

    public static GroupedDependencyProcessor superTrend(String name, String candlestick, String close, int atrPeriod,
                                                        String high, int pivotHighPeriod,
                                                        String low, int pivotLowPeriod,
                                                        double longFactor, double shortFactor) {
        String closeTrueRange = name + "_true_range";
        String closeAverageTrueRange = name + "_average_true_range";
        String pivotHigh = name + "_pivot_high";
        String pivotLow = name + "_pivot_low";
        return new GroupedDependencyProcessor(List.of(
                new TrueRange(closeTrueRange, candlestick),
                new SimpleMovingAverage(closeAverageTrueRange, closeTrueRange, atrPeriod),
                new HighPivot(pivotHigh, high, pivotHighPeriod),
                new LowPivot(pivotLow, low, pivotLowPeriod),
                new SuperTrend(name, close, pivotHigh, pivotLow, closeAverageTrueRange, longFactor, shortFactor)
        ));
    }
}
