package com.messik.v12.processor.momentum;

import com.messik.v12.processor.GroupedDependencyProcessor;
import com.messik.v12.processor.average.ExponentialMovingAverage;

import java.util.List;

public class Momentums {

    public static GroupedDependencyProcessor macd(int shortPeriod, int longPeriod) {
        return macd("macd", "close", shortPeriod, longPeriod);
    }

    public static GroupedDependencyProcessor macd(String name, String target, int shortPeriod, int longPeriod) {
        return macd(name, target, target + "_short", shortPeriod, target + "_long",longPeriod);
    }

    public static GroupedDependencyProcessor macd(String name, String target,
                                                  String shortName, int shortPeriod,
                                                  String longName, int longPeriod) {
        return new GroupedDependencyProcessor(List.of(
                new ExponentialMovingAverage(shortName, target, shortPeriod),
                new ExponentialMovingAverage(longName, target, longPeriod),
                new Macd(name, shortName, longName)
        ));
    }

    public static GroupedDependencyProcessor adx(String name, String atr, String high, String low, int period) {
        String highChange = high + "_change";
        String lowChange = low + "_change";
        return new GroupedDependencyProcessor(List.of(
                new Change(highChange, high),
                new Change(lowChange, low),
                new AverageDirectionalIndex(name, atr, highChange, lowChange, period)
        ));
    }

}
