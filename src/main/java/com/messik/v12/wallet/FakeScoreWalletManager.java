package com.messik.v12.wallet;


import com.messik.v12.data.CandlestickWrapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeScoreWalletManager {

    private final FakeWallet wallet;
    private final int maxHold;

    public FakeScoreWalletManager(FakeWallet wallet, int maxHold) {
        this.wallet = wallet;
        this.maxHold = maxHold;
    }

    public void handle(Map<CandlestickWrapper, Double> scores) {
        List<CandlestickWrapper> buy = scores.entrySet().stream()
                .filter(s -> s.getValue() > 0)
                .sorted(Comparator.comparingDouble(s -> -s.getValue()))
                .limit(maxHold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        scores.keySet().stream()
                .filter(s -> !buy.contains(s))
                .forEach(this::closeLong);

        buy.forEach(this::buy);
    }

    private void closeLong(CandlestickWrapper candlestick) {
        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());

        if (asset.getAvailable() > 0) {
            wallet.sell(fiat, asset, asset.getAvailable(), candlestick.getClose());
            wallet.repay(fiat.repay());
        }
    }

    private void buy(CandlestickWrapper candlestick) {
        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());

        double equity = wallet.getEquity();
        double wanted = equity / maxHold;
        double current = wallet.getEquity(candlestick.getAsset());
        double amount = wanted - current;
        if (equity * 0.01 > amount) {
            return;
        }

        wallet.buy(fiat, asset, amount, candlestick.getClose());
    }

}
