package com.messik.v12;

import com.messik.v12.data.CandlestickWrappers;
import com.messik.v12.optimizer.Optimizer;
import com.messik.v12.processor.RootProcessor;
import com.messik.v12.processor.signal.Signals;
import com.messik.v12.simulator.Simulator;
import com.messik.v12.wallet.FakeWallet;
import com.messik.v12.wallet.FakeWalletManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class V12 {

    public static void main(String[] args) {
        optimize(List.of("BTC"));
    }

    private static void optimize(List<String> coins) {
        new Optimizer(() -> coins.stream()
                .map(o -> Signals.generate())
                .toArray(RootProcessor[]::new),
                (botConfig) -> {
                    Map<String, Double> defaultBalance = new HashMap<>();
                    defaultBalance.put("USDT", 1.0);
                    coins.forEach(c -> defaultBalance.put(c, 0.0));

                    Map<String, Double> targets = new HashMap<>();
                    coins.forEach(c -> targets.put(c, 1.0 / coins.size()));

                    var wallet = new FakeWallet(defaultBalance);
                    return new Simulator(IntStream.range(0, coins.size()).boxed()
                            .collect(Collectors.toMap(coins::get, c -> botConfig.getProcessor()[c].copy())),
                            new FakeWalletManager(wallet, targets), wallet);
                })
                .mutations(CandlestickWrappers.fromFiles("USDT",
                        coins.stream().collect(Collectors.toMap(c -> c, c -> Path.of("./src/main/resources/" + c + "USDT_1HourBars.csv")))));
    }
}
