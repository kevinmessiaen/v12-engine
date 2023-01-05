package com.messik.v12.optimizer;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.processor.RootProcessor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class BotConfig implements Comparable<BotConfig> {

    private RootProcessor[] processor;
    @EqualsAndHashCode.Exclude
    private double score;
    @EqualsAndHashCode.Exclude
    private int trades;

    @Override
    public int compareTo(BotConfig o) {
        return Double.compare(score, o.score);
    }


}
