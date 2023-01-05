package com.messik.v12.simulator;

import com.messik.v12.constant.Action;
import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.processor.RootProcessor;
import com.messik.v12.wallet.FakeWallet;
import com.messik.v12.wallet.FakeWalletManager;

import java.util.Map;

public class Simulator {

    private final Map<String, RootProcessor> calculators;
    private final FakeWalletManager walletManager;
    private final FakeWallet wallet;

    public Simulator(Map<String, RootProcessor> calculators, FakeWalletManager walletManager, FakeWallet wallet) {
        this.calculators = calculators;
        this.walletManager = walletManager;
        this.wallet = wallet;
    }

    public boolean next(CandlestickWrapper[] candlesticks) {
        wallet.update(candlesticks);
        for (CandlestickWrapper candlestick : candlesticks) {
            if (calculators.containsKey(candlestick.getAsset())) {
                var result = calculators.get(candlestick.getAsset()).handle(candlestick);
                var action = (Action) result.get("action");
                var sl = (Double) result.get("sl");
                var tp = (Double) result.get("tp");
                walletManager.handle(candlestick, action, tp, sl);
            }
        }

        return wallet.filter();
    }

    public double getEquity() {
        return wallet.getEquity();
    }

    public int getTrades() {
        return wallet.getTrades();
    }
}
