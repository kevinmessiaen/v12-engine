package com.messik.v12.constant;


import java.util.Map;

public interface Interests {

    /**
     * Binance rates are 0.1%
     */
    double INTEREST_RATE = 0.001;
    /**
     * Interest for borrowing USDT
     */
    double FIAT_DAILY_INTEREST = 0.0006;
    /**
     * Interest for borrowing assets
     */
    double ASSET_DAILY_INTEREST = 0.0002;

    Map<String, Double> ASSET_DAILY_INTERESTS = Map.of(
            "USD", FIAT_DAILY_INTEREST,
            "USDT", FIAT_DAILY_INTEREST,
            "BTC", ASSET_DAILY_INTEREST,
            "ETH", ASSET_DAILY_INTEREST
    );

}
