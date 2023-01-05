package com.messik.v12.wallet;

import com.messik.v12.data.CandlestickWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FakeWallet implements Comparable<FakeWallet> {

    public final Map<String, FakeAsset> assets = new HashMap<>();
    public final Map<String, Double> equities = new HashMap<>();
    public final Map<String,  Predicate<CandlestickWrapper>> tp = new HashMap<>();
    public final Map<String,  Predicate<CandlestickWrapper>> sl = new HashMap<>();
    private double equity;
    private int trades;
    private double borrowed;
    private Double peak = null;
    private Double trough = null;
    private double mdd = 0;

    public FakeWallet(Map<String, Double> amounts) {
        amounts.forEach((asset, amount) -> assets.put(asset, new FakeAsset(asset, amount)));
    }

    public double update(CandlestickWrapper[] candlesticks) {
        equity = 0;
        borrowed = 0;
        for (CandlestickWrapper candlestick : candlesticks) {
            var slHandler = sl.get(candlestick.getAsset());
            var tpHandler = tp.get(candlestick.getAsset());
            if ((slHandler != null && slHandler.test(candlestick)) || (tpHandler != null && tpHandler.test(candlestick))) {
                sl.remove(candlestick.getAsset());
                tp.remove(candlestick.getAsset());
            }
        }
        for (CandlestickWrapper candlestick : candlesticks) {
            double asset = assets.get(candlestick.getAsset()).update(candlestick);
            borrowed += assets.get(candlestick.getAsset()).getBorrowed() * candlestick.getClose();
            equity += asset;
            equities.put(candlestick.getAsset(), asset);
        }

        if (equity <= 0) {
            assets.values().forEach(FakeAsset::liquidate);
            mdd = -1;
            return 0.0;
        }

        if (peak == null) {
            peak = equity;
            trough = equity;
        }

        if (equity > peak) {
            mdd = Math.min(mdd, (trough - peak) / peak);
            peak = equity;
            trough = equity;
        }
        trough = Math.min(trough, equity);

        return equity;
    }


    public boolean filter() {
        return Math.min(mdd, (trough - peak) / peak) < -0.6;
    }

    public FakeAsset get(String asset) {
        return assets.get(asset);
    }

    public void buy(FakeAsset fiat, FakeAsset asset, double amount, double price) {
        double bought = fiat.sell(amount);
        asset.add(bought / price);
    }

    public void buy(FakeAsset fiat, FakeAsset asset, double borrow, double amount, double price) {
        trades ++;
        fiat.borrow(borrow);
        borrowed += borrow;
        double sold = fiat.sell(fiat.getAvailable());
        asset.add(sold / price);
    }

    public void sell(FakeAsset fiat, FakeAsset asset, double amount, double price) {
        trades ++;
        double sold = asset.sell(amount) * price;
        fiat.add(sold);
    }

    public void sell(FakeAsset fiat, FakeAsset asset, double borrow, double amount, double price) {
        trades ++;
        asset.borrow(borrow / price);
        borrowed += borrow;
        double sold = asset.sell(asset.getAvailable());
        fiat.add(sold * price);
    }

    public void repay(double fiat) {
        borrowed -= fiat;
    }

    @Override
    public int compareTo(FakeWallet o) {
        return Double.compare(equity, o.equity);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "equity=" + equity +
                ", mdd=" + mdd +
                ", borrowed=" + borrowed +
                '}';
    }

    public double getEquity() {
        return equity;
    }

    public double getEquity(String asset) {
        return equities.get(asset);
    }

    public double getBorrowed() {
        return borrowed;
    }

    public void tp(String asset, Predicate<CandlestickWrapper> handler) {
        tp.put(asset, handler);
    }
    public void sl(String asset, Predicate<CandlestickWrapper> handler) {
        sl.put(asset, handler);
    }

    public void resetTpSl(String asset) {
        sl.remove(asset);
        tp.remove(asset);
    }

    public int getTrades() {
        return trades;
    }
}
