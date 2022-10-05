package com.messik.v12.processor.pivot;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.processor.DependencyProcessor;
import com.messik.v12.processor.SimpleProcessor;

public class BearPower extends SimpleProcessor<CandlestickWrapper, Double> {

    private Double lastClose;

    public BearPower(String name, String selector) {
        super(name, selector);
    }

    @Override
    protected Double calculate(CandlestickWrapper data) {
        if (lastClose == null) {
            lastClose = data.getOpen();
        }

       var result = (data.getClose() < data.getOpen()
        ? (lastClose > data.getOpen()
        ? Math.max(lastClose - data.getOpen(), data.getHigh() - data.getLow())
               : data.getHigh() - data.getLow())
        : (data.getClose() > data.getOpen()
        ? (lastClose > data.getOpen()
        ? Math.max(lastClose - data.getLow(), data.getHigh() - data.getClose())
        : Math.max(data.getOpen() - data.getLow(), data.getHigh() - data.getClose()))
        : (data.getHigh() - data.getClose() > data.getClose() - data.getLow()
        ? (lastClose > data.getOpen()
        ? Math.max(lastClose - data.getOpen(), data.getHigh() - data.getLow())
        : data.getHigh() - data.getLow())
        : (data.getHigh() - data.getClose() < data.getClose() - data.getLow()
        ? (lastClose > data.getOpen()
        ? Math.max(lastClose - data.getLow(), data.getHigh() - data.getClose())
        : data.getOpen() - data.getLow())
        : (lastClose > data.getOpen()
        ? Math.max(lastClose - data.getOpen(), data.getHigh() - data.getLow())
        : (lastClose < data.getOpen()
        ? Math.max(data.getOpen() - data.getLow(), data.getHigh() - data.getClose())
        : data.getHigh() - data.getLow()))))));

        lastClose = data.getClose();
        return result;
    }

    @Override
    public DependencyProcessor mutate() {
        return this;
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

    @Override
    public String toString() {
        return "BullPower{" +
                "name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", lastClose=" + lastClose +
                '}';
    }
}
