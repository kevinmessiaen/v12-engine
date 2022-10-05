package com.messik.v12.processor.signal;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.CandlestickProcessor;
import com.messik.v12.processor.GroupedDependencyProcessor;
import com.messik.v12.processor.RootProcessor;
import com.messik.v12.processor.average.*;
import com.messik.v12.processor.math.Speed;
import com.messik.v12.processor.momentum.*;
import com.messik.v12.processor.pivot.*;
import com.messik.v12.processor.trend.*;
import com.messik.v12.processor.math.StandardDevianceRatio;
import com.messik.v12.processor.volume.VolumeDelta;

import java.util.List;
import java.util.Random;

public class Signals {

    private static final Random r = new Random();

    public static RootProcessor generate() {
        return switch (r.nextInt(14)) {
            case 0 -> superTrendRsiEmaAdx();
            case 1 -> superTrendRsiEmaVolatility();
            case 2 -> superTrendRsiDivergenceVolatility();
            case 3 -> superTrendRsiEmaHannVolatility();
            case 4 -> superTrendRsiWmaVolatility();
            case 5 -> superTrendRsiHmaVolatility();
            case 6 -> testAlgo();
            case 7 -> rangeFilterRsiEmaAdx();
            case 8 -> waveTrendRsiEmaVolatility();
            case 9 -> twapTrendRsiWeightedVolatility();
            case 10 -> parabolicSarRsiWeightedVolatility();
            case 11 -> rangeFilterRsiEmaVolatility();
            case 12 -> doubleSuperTrendRsiEmaVolatilityTakeProfit();
            case 13 -> superTrendRsiEmaVolatilityTakeProfit();
            default -> throw new IllegalStateException();
        };
    }

    public static RootProcessor superTrendRsiHmaVolatility() {
        return superTrendRsiHmaVolatility(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {2.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor superTrendRsiHmaVolatility(int pivotPeriod, double stFactor, int atrPeriod,
                                                                               int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new HullMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor testAlgo() {
        return testAlgo(
                Mutations.generate(new int[] {1, 40}),
                Mutations.generate(new int[] {1, 40}),
                Mutations.generate(new int[] {1, 40}),
                Mutations.generate(new int[] {40, 70}),
                Mutations.generate(new double[] {1.0, 1.2}),
                Mutations.generate(new double[] {0.045, 0.047}),
                Mutations.generate(new double[] {0.018, 0.022}),
                Mutations.generate(new double[] {0.10, 0.12}),
                Mutations.generate(new int[] {80, 200}),
                Mutations.generate(new int[] {20, 40}),
                Mutations.generate(new int[] {60, 80}),
                Mutations.generate(new int[] {1, 20}),
                Mutations.generate(new int[] {15, 40}),
                Mutations.generate(new int[] {10, 40}),
                Mutations.generate(new double[] {0.01, 0.1}),
                Mutations.generate(new double[] {0.01, 5}),
                Mutations.generate(new int[] {1, 40}),
                Mutations.generate(new int[] {1, 20}),
                Mutations.generate(new int[] {15, 30}),
                Mutations.generate(new int[] {10, 100}),
                Mutations.generate(new int[] {1, 20}),
                Mutations.generate(new double[] {0.1, 10}));
    }

    public static RootProcessor testAlgo(int twapPeriod, int adxPeriod, int adxFilter,
                                                             int volumeWeightPeriod, double volumeWeightTrigger,
                                                             double start, double step, double stop,
                                                             int rsiPeriod, int rsiOs, int rsiOb,
                                                             int macdSlow, int macdFast, int macdSignal,
                                                             double tp, double sl,
                                                             int volumeDeltaPeriod,
                                                             int fastPeriod, int slowPeriod, int maPeriod,
                                                             int rangePeriod, double rangeMult) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.OHLC4,
                Averages.HL2,
                Averages.TRUE_RANGE,
                new SimpleMovingAverage("twap", "close", twapPeriod),
                new TrendFilter("twap_trend", "twap", "close"),
                new AverageDirectionalIndexMasaNakamura("adx", "true_range", "high", "low", adxPeriod),
                new SimpleMovingAverage("volume_ma", "volume", volumeWeightPeriod),
                new RatioSignal("volume_ratio", "volume", "volume_ma", volumeWeightTrigger),
                new ParabolicSar("sar", "data", start, step, stop),
                new RecursiveRelativeStrengthIndex("rsi", "hl2", rsiPeriod),
                Momentums.macd("macd", "close", macdSlow, macdFast),
                new SimpleMovingAverage("macd_signal", "macd", macdSignal),
                new Cross("macd_trend", "macd", "macd_signal"),
                new BullPower("bull_power", "data"),
                new BearPower("bear_power", "data"),
                new VolumeDelta("volume_delta", "bull_power", "bear_power", "volume", volumeDeltaPeriod),
                new SimpleMovingAverage("fast_ma", "volume", fastPeriod),
                new SimpleMovingAverage("slow_ma", "volume", slowPeriod),
                new TrendFilter("trend_ma", "slow_ma", "fast_ma"),
                new VolumeWeightedMovingAverage("vwma", "close", "volume", maPeriod),
                new Speed("ma_speed", "vwma"),
                new RangeFilter("range_trend", "close", rangePeriod, rangeMult),
                new PerpetualSignal("action", "twap_trend", "adx", adxFilter, "volume_ratio", "sar",
                        "rsi", rsiOs, rsiOb, "macd_trend", "volume_delta", "trend_ma", "ma_speed",
                        "range_trend", tp, sl)
                ));
    }

    public static RootProcessor superTrendDynamicRsiAdx(int pivotPeriod, double stFactor, int atrPeriod,
                                                                               int rsiPeriod, int rsiEmaPeriod, int rsiPivotPeriod,
                                                                               int adxPeriod, double adxLowThreshold, double adxHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.HLC3,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndex("rsi_close", "hlc3", rsiPeriod),
                new HighPivot("rsi_close_high", "rsi_close", rsiPivotPeriod),
                new VolumeWeightedMovingAverage("rsi_close_high_ma", "rsi_close_high", "volume", rsiEmaPeriod),
                new LowPivot("rsi_close_low", "rsi_close", rsiPivotPeriod),
                new VolumeWeightedMovingAverage("rsi_close_low_ma", "rsi_close_low", "volume", rsiEmaPeriod),
                new DynamicConfirmation("rsi_cross", "rsi_close", "rsi_close_low_ma", "rsi_close_high_ma"),
                Averages.TRUE_RANGE,
                new AverageDirectionalIndexMasaNakamura("adx", "true_range", "high", "low", adxPeriod),
                new Scale("adx_pullback", "adx", adxLowThreshold, adxHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_cross", "adx_pullback")
        ));
    }

    public static RootProcessor rangeFilterRsiEmaAdx() {
        return rangeFilterRsiEmaAdx(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {0.1, 7.5}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new int[] {10, 60}),
                Mutations.generate(new int[] {40, 100}));
    }

    public static RootProcessor rangeFilterRsiEmaAdx(int rangePeriod, double rangeFactor,
                                                                                int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                                int adxPeriod, int adxLowThreshold, int adxHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.OHLC4,
                new RangeFilter("range_filter", "ohlc4", rangePeriod, rangeFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                Averages.TRUE_RANGE,
                new AverageDirectionalIndexMasaNakamura("adx", "true_range", "high", "low", adxPeriod),
                new Scale("adx_pullback", "adx", adxLowThreshold, adxHighThreshold),
                new TrendWithVolatilitySignal("action", "range_filter", "rsi_cross", "adx_pullback")
        ));
    }

    public static RootProcessor twapTrendRsiWeightedVolatility() {
        return twapTrendRsiWeightedVolatility(
                Mutations.generate(new int[] {1, 200}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.001, 0.005}),
                Mutations.generate(new double[] {0.01, 0.05}));
    }

    public static RootProcessor twapTrendRsiWeightedVolatility(int twapPeriod,
                                                                                      int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                                      int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.OHLC4,
                Averages.HLC3,
                new ExponentialMovingAverage("twap", "ohlc4", twapPeriod),
                new TrendFilter("twap_trend", "twap", "ohlc4"),
                new RecursiveRelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new VolumeWeightedMovingAverage("rsi_ema_close", "rsi_close", "volume", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "twap_trend", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor parabolicSarRsiWeightedVolatility() {
        return parabolicSarRsiWeightedVolatility(
                Mutations.generate(new double[] {0, 0.01}),
                Mutations.generate(new double[] {0.001, 0.02}),
                Mutations.generate(new double[] {0.02, 1}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor parabolicSarRsiWeightedVolatility(double start, double step, double stop,
                                                                                      int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                                      int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.HLC3,
                new ParabolicSar("sar", "data", start, step, stop),
                new RelativeStrengthIndex("rsi_close", "hlc3", rsiPeriod),
                new VolumeWeightedMovingAverage("rsi_ema_close", "rsi_close", "volume", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "sar", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor superTrendRsiEmaVolatilityTakeProfit() {
        return superTrendRsiEmaVolatilityTakeProfit(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {2.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}),
                Mutations.generate(new double[] {0.1, 20}));
    }


    public static RootProcessor superTrendRsiEmaVolatilityTakeProfit(int pivotPeriod, double stFactor, int atrPeriod,
                                                                               int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold,
                                                                                         double tpFactor) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilityTpSlSignal("action", "super_trend", "rsi_cross", "volatility_pullback",
                        "super_trend_sl", "super_trend_center", "close_average_true_range", tpFactor)
        ));
    }

    public static RootProcessor doubleSuperTrendRsiEmaVolatilityTakeProfit() {
        return doubleSuperTrendRsiEmaVolatilityTakeProfit(
                Mutations.generate(new int[] {10, 30}),
                Mutations.generate(new double[] {1.0, 4.0}),
                Mutations.generate(new int[] {10, 30}),
                Mutations.generate(new int[] {10, 30}),
                Mutations.generate(new double[] {4.5, 10}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {7, 20}),
                Mutations.generate(new int[] {10, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 15}),
                Mutations.generate(new double[] {0.001, 0.01}),
                Mutations.generate(new double[] {0.01, 0.04}),
                Mutations.generate(new double[] {10, 20}));
    }

    public static RootProcessor doubleSuperTrendRsiEmaVolatilityTakeProfit(int shortPivotPeriod, double shortStFactor, int shortAtrPeriod,
                                                                                               int longPivotPeriod, double longStFactor, int longAtrPeriod,
                                                                                         int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                                         int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold,
                                                                                         double tpFactor) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend("short_super_trend", shortPivotPeriod, shortAtrPeriod, shortStFactor, shortStFactor),
                Trends.superTrend("long_super_trend", longPivotPeriod, longAtrPeriod, longStFactor, longStFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new DoubleTrendWithVolatilityTpSlSignal("action", "short_super_trend", "long_super_trend", "rsi_cross", "volatility_pullback",
                        "long_super_trend_sl", "long_super_trend_center", "long_super_trend_average_true_range", tpFactor)
        ));
    }

    public static RootProcessor superTrendRsiEmaVolatility() {
        return superTrendRsiEmaVolatility(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {1.0, 7.5}),
                Mutations.generate(new double[] {1.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor superTrendRsiEmaVolatility(int pivotPeriod, double stFactorLong,double stFactorShort, int atrPeriod,
                                                                        int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                        int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.HLC3,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactorLong, stFactorShort),
                new RelativeStrengthIndex("rsi_close", "hlc3", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor rangeFilterRsiEmaVolatility() {
        return rangeFilterRsiEmaVolatility(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {0.1, 7.5}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor rangeFilterRsiEmaVolatility(int rangePeriod, double rangeFactor,
                                                                               int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.OHLC4,
                new RangeFilter("range_filter", "ohlc4", rangePeriod, rangeFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "range_filter", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor waveTrendRsiEmaVolatility() {
        return waveTrendRsiEmaVolatility(
                Mutations.generate(new int[] {1, 60}),
                Mutations.generate(new int[] {1, 60}),
                Mutations.generate(new int[] {1, 10}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor waveTrendRsiEmaVolatility(int channelPeriod, int averagePeriod, int movingAveragePeriod,
                                                                               int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Averages.HLC3,
                new WaveTrend("wave_trend", "hlc3", channelPeriod, averagePeriod, movingAveragePeriod),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "wave_trend", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor superTrendRsiWmaVolatility() {
        return superTrendRsiWmaVolatility(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {2.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor superTrendRsiWmaVolatility(int pivotPeriod, double stFactor, int atrPeriod,
                                                                               int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new WeightedMovingAverage("rsi_wma_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_wma_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor superTrendRsiEmaHannVolatility() {
        return superTrendRsiEmaHannVolatility(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {2.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new double[] {-1.0, 0.0}),
                Mutations.generate(new double[] {0.0, 1.0}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor superTrendRsiEmaHannVolatility(int pivotPeriod, double stFactor, int atrPeriod,
                                                                               int rsiPeriod, int rsiEmaPeriod, double min, double max,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndexHann("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_cross", "volatility_pullback")
        ));
    }

    public static RootProcessor superTrendRsiEmaAdx() {
        return superTrendRsiEmaAdx(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {2.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {3, 40}),
                Mutations.generate(new int[] {5, 50}),
                Mutations.generate(new int[] {50, 95}),
                Mutations.generate(new int[] {1, 50}),
                Mutations.generate(new int[] {10, 60}),
                Mutations.generate(new int[] {40, 100}));
    }

    public static RootProcessor superTrendRsiEmaAdx(int pivotPeriod, double stFactor, int atrPeriod,
                                                                        int rsiPeriod, int rsiEmaPeriod, int min, int max,
                                                                        int adxPeriod, int adxLowThreshold, int adxHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                new ExponentialMovingAverage("rsi_ema_close", "rsi_close", rsiEmaPeriod),
                new Confirmation("rsi_cross", "rsi_close", "rsi_ema_close", min, max),
                Momentums.adx("adx", "close_average_true_range", "high", "low", adxPeriod),
                new Scale("adx_pullback", "adx", adxLowThreshold, adxHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_cross", "adx_pullback")
        ));
    }

    public static GroupedDependencyProcessor sdVolatility(String name, String close, int period, double low, double high) {
        String volatility = close + "_volatility";
        return new GroupedDependencyProcessor(List.of(
                new StandardDevianceRatio(volatility, close, period),
                new Scale(name, volatility, low, high)
        ));
    }

    public static RootProcessor superTrendRsiDivergenceVolatility() {
        return superTrendRsiDivergenceVolatility(
                Mutations.generate(new int[] {5, 60}),
                Mutations.generate(new double[] {2.0, 7.5}),
                Mutations.generate(new int[] {5, 30}),
                Mutations.generate(new int[] {5, 25}),
                Mutations.generate(new int[] {5, 20}),
                Mutations.generate(new double[] {0.0001, 0.01}),
                Mutations.generate(new double[] {0.001, 0.1}));
    }

    public static RootProcessor superTrendRsiDivergenceVolatility(int pivotPeriod, double stFactor, int atrPeriod,
                                                                               int rsiPeriod,
                                                                               int volatilityPeriod, double volatilityLowThreshold, double volatilityHighThreshold) {
        return new RootProcessor(List.of(
                CandlestickProcessor.CANDLESTICK_PROCESSOR,
                Trends.superTrend(pivotPeriod, atrPeriod, stFactor, stFactor),
                new RelativeStrengthIndex("rsi_close", "close", rsiPeriod),
                Pivots.divergences("rsi_divergences", "high", "low", "rsi_close"),
                sdVolatility("volatility_pullback", "close", volatilityPeriod, volatilityLowThreshold, volatilityHighThreshold),
                new TrendWithVolatilitySignal("action", "super_trend", "rsi_divergences", "volatility_pullback")
        ));
    }
}
