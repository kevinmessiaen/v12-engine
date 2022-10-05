package com.messik.v12.wallet;


import com.messik.v12.constant.Action;
import com.messik.v12.data.CandlestickWrapper;

import java.util.Map;

public class FakeWalletManager {

    private final FakeWallet wallet;
    private final Map<String, Double> targets;

    public FakeWalletManager(FakeWallet wallet, Map<String, Double> targets) {
        this.wallet = wallet;
        this.targets = targets;
    }

    public void handle(CandlestickWrapper candlestick,
                       Action action, Double tp, Double sl) {
        if (action.equals(Action.CLOSE)) {
            wallet.resetTpSl(candlestick.getAsset());
            closeLong(candlestick, candlestick.getClose());
            closeShort(candlestick, candlestick.getClose());
        } else if (action.equals(Action.LONG)) {
            wallet.resetTpSl(candlestick.getAsset());
            buy(candlestick);
        } else if (action.equals(Action.SHORT)) {
            wallet.resetTpSl(candlestick.getAsset());
            sell(candlestick);
        } else if (action.equals(Action.CLOSE_LONG)) {
            wallet.resetTpSl(candlestick.getAsset());
            closeLong(candlestick, candlestick.getClose());
        } else if (action.equals(Action.CLOSE_SHORT)) {
            wallet.resetTpSl(candlestick.getAsset());
            closeShort(candlestick, candlestick.getClose());
        }

        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());
        if (tp != null) {
            wallet.tp(candlestick.getAsset(), (candlestickWrapper -> {
                if (asset.getAvailable() > 0) {
                    if (candlestick.getHigh() >= tp && candlestick.getLow() <= tp) {
                        closeLong(candlestick, tp);
                        return true;
                    }
                }
                if (asset.getBorrowed() > 0 && fiat.getAvailable() > 0) {
                    if (candlestick.getHigh() >= tp && candlestick.getLow() <= tp) {
                        closeShort(candlestick, tp);
                        return true;
                    }
                }
                return false;
            }));
        }
        if (sl != null) {
            wallet.sl(candlestick.getAsset(), (candlestickWrapper -> {
                if (asset.getAvailable() > 0) {
                    if (candlestick.getHigh() >= sl && candlestick.getLow() <= sl) {
                        closeLong(candlestick, sl);
                        return true;
                    }
                }
                if (asset.getBorrowed() > 0 && fiat.getAvailable() > 0) {
                    if (candlestick.getHigh() >= sl && candlestick.getLow() <= sl) {
                        closeShort(candlestick, sl);
                        return true;
                    }
                }
                return false;
            }));
        }
    }

    private void closeLong(CandlestickWrapper candlestick, double price) {
        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());

        if (asset.getAvailable() > 0) {
            wallet.sell(fiat, asset, asset.getAvailable(), price);
            wallet.repay(fiat.repay());
        }
    }

    private void closeShort(CandlestickWrapper candlestick, double price) {
        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());

        if (asset.getBorrowed() > 0 && fiat.getAvailable() > 0) {
            wallet.buy(fiat, asset, (asset.getBorrowed() + asset.getInterests()) * price, price);
            wallet.repay(asset.repay() * price);
        }
    }

    private void buy(CandlestickWrapper candlestick) {
        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());

        if (asset.getBorrowed() > 0) {
            closeShort(candlestick, candlestick.getClose());
        }

        double equity = wallet.getEquity();
        double borrowable = equity * 2 - wallet.getBorrowed();
        double wanted = equity * targets.get(candlestick.getAsset());
        double max = wanted * 1.5;
        double current = wallet.getEquity(candlestick.getAsset()) / 2;
        double amount = Math.min(wanted, max - current);
        var borrow = Math.min(borrowable, amount * 2);
        if (equity * 0.01 > borrow) {
            return;
        }

        wallet.buy(fiat, asset, borrow, amount * 3, candlestick.getClose());
    }

    private void sell(CandlestickWrapper candlestick) {
        FakeAsset asset = wallet.get(candlestick.getAsset());
        FakeAsset fiat = wallet.get(candlestick.getFiat());

        if (fiat.getBorrowed() > 0) {
            closeLong(candlestick, candlestick.getClose());
        }

        double equity = wallet.getEquity();
        double borrowable = equity * 2 - wallet.getBorrowed();
        double wanted = equity * targets.get(candlestick.getAsset());
        double max = wanted * 1.5;
        double current = asset.getBorrowed() * candlestick.getClose() / 2;
        double amount = Math.min(wanted, max - current);
        var borrow = Math.min(borrowable, amount * 2);
        if (equity * 0.01 > borrow) {
            return;
        }

        wallet.sell(fiat, asset, borrow, amount * 3, candlestick.getClose());
    }

}
