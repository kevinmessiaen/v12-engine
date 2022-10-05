package com.messik.v12.processor.signal;

import com.messik.v12.constant.Action;
import com.messik.v12.optimizer.Mutations;
import com.messik.v12.processor.DependencyProcessor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PerpetualSignal implements DependencyProcessor {

    private final String name;
    private final String twapTrendName;
    private final String adxName;
    private final String adxDimName;
    private final String adxDipName;
    private final int adxThreshold;
    private final String volumeName;
    private final String sarName;
    private final String rsiName;
    private final int rsiOs;
    private final int rsiOb;
    private final String macdTrendName;
    private final String volumeDeltaName;
    private final String trendMaName;
    private final String maSpeedName;
    private final String rangeName;
    private final double tp;
    private final double sl;

    public PerpetualSignal(String name, String twapTrendName, String adxName, int adxThreshold, String volumeName, String sarName,
                           String rsiName, int rsiOs, int rsiOb, String macdTrendName, String volumeDeltaName,
                           String trendMaName, String maSpeedName, String rangeName, double tp, double sl) {
        this.name = name;
        this.twapTrendName = twapTrendName;
        this.adxName = adxName;
        this.adxDimName = adxName + "_dim";
        this.adxDipName = adxName + "_dip";
        this.adxThreshold = adxThreshold;
        this.volumeName = volumeName;
        this.sarName = sarName;
        this.rsiName = rsiName;
        this.rsiOs = rsiOs;
        this.rsiOb = rsiOb;
        this.macdTrendName = macdTrendName;
        this.volumeDeltaName = volumeDeltaName;
        this.trendMaName = trendMaName;
        this.maSpeedName = maSpeedName;
        this.rangeName = rangeName;
        this.tp = tp;
        this.sl = sl;
    }

    @Override
    public Set<String> provide() {
        return Collections.singleton(name);
    }

    @Override
    public Set<String> require() {
        return Set.of(name, adxName);
    }

    @Override
    public void process(Map<String, Object> data) {
        var twapTrend = (int) data.get(twapTrendName);
        var adx = (double) data.get(adxName);
        var dip = (double) data.get(adxDipName);
        var dim = (double) data.get(adxDimName);
        var volumeGood = (boolean) data.get(volumeName);
        var sar = (int) data.get(sarName);
        var rsi = (double) data.get(rsiName);
        var macdTrend = (int) data.get(macdTrendName);
        var volumeDelta = (int) data.get(volumeDeltaName);
        var trendMa = (int) data.get(trendMaName);
        var maSpeed = (double) data.get(maSpeedName);
        var range = (int) data.get(rangeName);

        data.put("tp", null);
        data.put("sl", null);
        if (twapTrend > 0 && (dip > dim && adx > adxThreshold) && volumeGood && sar == 1 && rsi < rsiOb && macdTrend == 1
        && volumeDelta == 1 && trendMa == 1 && maSpeed > 0 && range == 1) {
            data.put(name, Action.LONG);
            data.put("tp", (double) data.get("close") + tp * (double) data.get("adx_str"));
            data.put("sl", (double) data.get("close") - sl * (double) data.get("adx_str"));
        } else if ((dip < dim && adx > adxThreshold) && volumeGood && sar == -1 && rsi > rsiOs && macdTrend == -1
        && volumeDelta == -1 && trendMa == -1 && maSpeed < 0 && range == -1) {
            data.put(name, Action.SHORT);
            data.put("tp", (double) data.get("close") - tp * (double) data.get("adx_str"));
            data.put("sl", (double) data.get("close") + sl * (double) data.get("adx_str"));
        } else {
            data.put(name, Action.NONE);
        }
    }

    @Override
    public DependencyProcessor mutate() {
        return new PerpetualSignal(name, twapTrendName, adxName, Mutations.mutate(adxThreshold, new int[] {1, 100}, 0.1), volumeName, sarName, rsiName,
                Mutations.mutate(rsiOs, new int[] {5, 50}, 0.1),
                Mutations.mutate(rsiOb, new int[] {50, 95}, 0.1), macdTrendName, volumeDeltaName, trendMaName,
                maSpeedName, rangeName,
                Mutations.mutate(tp, new double[] {tp * 0.5, tp * 1.5}, 0.1),
                Mutations.mutate(sl, new double[] {sl * 0.5, sl * 1.5}, 0.1));
    }

    @Override
    public DependencyProcessor copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PerpetualSignal that = (PerpetualSignal) o;

        return new EqualsBuilder().append(adxThreshold, that.adxThreshold).append(name, that.name).append(adxName, that.adxName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(adxName).append(adxThreshold).toHashCode();
    }

    @Override
    public String toString() {
        return "PerpetualSignal{" +
                "name='" + name + '\'' +
                ", twapTrendName='" + twapTrendName + '\'' +
                ", adxName='" + adxName + '\'' +
                ", adxDimName='" + adxDimName + '\'' +
                ", adxDipName='" + adxDipName + '\'' +
                ", adxThreshold=" + adxThreshold +
                ", volumeName='" + volumeName + '\'' +
                ", sarName='" + sarName + '\'' +
                ", rsiName='" + rsiName + '\'' +
                ", rsiOs=" + rsiOs +
                ", rsiOb=" + rsiOb +
                ", macdTrendName='" + macdTrendName + '\'' +
                ", tp=" + tp +
                ", sl=" + sl +
                '}';
    }
}
