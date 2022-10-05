package com.messik.v12.wallet;


import com.messik.v12.constant.Interests;
import com.messik.v12.data.CandlestickWrapper;

import java.time.Duration;

public class FakeAsset {

    private static final long SECONDS_IN_ONE_DAY = Duration.ofDays(1).getSeconds();

    /**
     * The amount of borrowed asset
     */
    private double borrowed;
    /**
     * The owed interest
     */
    private double interests;
    /**
     * The amount of available asset
     */
    private double available;
    private Double cachedCandleInterest;
    private final Double dailyInterestRate;

    public FakeAsset(String name, double available) {
        this.available = available;
        this.dailyInterestRate = Interests.ASSET_DAILY_INTERESTS.getOrDefault(name, Interests.FIAT_DAILY_INTEREST);
    }

    protected double update(CandlestickWrapper candlestick) {
        if (borrowed > 0) {
            updateInterests(candlestick);
        }

        return computeEquity(candlestick);
    }

    private void updateInterests(CandlestickWrapper candlestick) {
        if (cachedCandleInterest == null) {
            cachedCandleInterest = borrowed * dailyInterestRate
                    * candlestick.getDurationSeconds() / SECONDS_IN_ONE_DAY;
        }
        interests += cachedCandleInterest;
    }

    private double computeEquity(CandlestickWrapper candlestick) {
        return (available - borrowed - interests) * candlestick.getClose();
    }

    public double sell(double amount) {
        double max = Math.min(amount / (1 - Interests.INTEREST_RATE), available);
        available -= max;
        return max * (1 - Interests.INTEREST_RATE);
    }

    public void add(double amount) {
        available += amount;
    }

    protected void liquidate() {
        available = 0;
        borrowed = 0;
        interests = 0;
        cachedCandleInterest = null;
    }

    public double getAvailable() {
        return available;
    }

    public double getBorrowed() {
        return borrowed;
    }

    public double getInterests() {
        return interests;
    }


    public void borrow(double amount) {
        borrowed += amount;
        available += amount;
        cachedCandleInterest = null;
    }

    public double repay() {
        double b = Math.min(borrowed, available);
        borrowed -= b;
        available -= b;
        double i = Math.min(interests, available);
        interests -= i;
        available -= i;
        cachedCandleInterest = null;
        return b + i;
    }
}
