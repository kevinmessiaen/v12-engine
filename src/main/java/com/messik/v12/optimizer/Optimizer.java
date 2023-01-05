package com.messik.v12.optimizer;

import com.messik.v12.data.CandlestickWrapper;
import com.messik.v12.processor.RootProcessor;
import com.messik.v12.simulator.Simulator;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Optimizer {

    private final HashSet<BotConfig> configs = new HashSet<>();
    private final Supplier<RootProcessor[]> botGenerator;
    private final Function<BotConfig, Simulator> simulatorBuilder;

    public Optimizer(Supplier<RootProcessor[]> botGenerator, Function<BotConfig, Simulator> simulatorBuilder) {
        this.botGenerator = botGenerator;
        this.simulatorBuilder = simulatorBuilder;
    }

    public void mutations(CandlestickWrapper[][] candlesticks) {
        CandlestickWrapper[][][] generations = new CandlestickWrapper[5][][];
        CandlestickWrapper[][][] confirmations = new CandlestickWrapper[5][][];
        int confSize = candlesticks.length / 10;
        int genSize = (candlesticks.length - confSize) / 5;
        for (int i = 0; i < 5; i++) {
            generations[i] = Arrays.stream(candlesticks, i * genSize, (i + 1) * genSize)
                    .toArray(CandlestickWrapper[][]::new);
            confirmations[i] = Arrays.stream(candlesticks, (i + 1) * genSize, (i + 1) * genSize + confSize)
                    .toArray(CandlestickWrapper[][]::new);
        }

        List<BotConfig> simulators = Arrays.stream(generations)
                .parallel()
                .flatMap(g -> init(g).stream())
                .collect(Collectors.toList());
        for (int i = 0; i < generations.length; i++) {
            simulators = training(simulators, generations[i]);
            simulators = validate(simulators, confirmations[i]);
        }

        simulators = simulators.stream()
                .filter(s -> simulate(candlesticks, s))
                .collect(Collectors.toList());

        System.out.println("best simulator:");
        simulators.stream()
                .sorted(Comparator.reverseOrder())
                .limit(10)
                .forEach(System.out::println);
    }

    private List<BotConfig> init(CandlestickWrapper[][] data) {
        List<BotConfig> best = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            System.out.println("Init " + j);
            best.addAll(IntStream.range(0, 1000)
                    .mapToObj(i -> generate())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .limit(1000)
                    .parallel()
                    .filter(s -> simulate(data, s))
                    .toList());
            best = best.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(1000)
                    .collect(Collectors.toList());
            if (!best.isEmpty()) {
                System.out.println("Best " + best.get(0));
            } else {
                System.out.println("nothing found");
            }
        }
        return best;
    }

    private boolean simulate(CandlestickWrapper[][] data, BotConfig s) {
        Simulator simulator = simulatorBuilder.apply(s);
        if (Arrays.stream(data).noneMatch(simulator::next)) {
            s.setScore(simulator.getEquity());
            s.setTrades(simulator.getTrades());
            System.out.print(simulator.getEquity() + "\r");
            return true;
        } else {
            return false;
        }
    }

    private Optional<BotConfig> generate() {
        BotConfig botConfig = BotConfig.builder()
                .processor(botGenerator.get())
                .build();

        if (configs.contains(botConfig)) {
            return Optional.empty();
        }

        configs.add(botConfig);
        return Optional.of(botConfig);
    }

    private Optional<BotConfig> mutate(BotConfig src) {
        BotConfig botConfig = BotConfig.builder()
                .processor(Arrays.stream(src.getProcessor())
                        .map(RootProcessor::mutate)
                        .toArray(RootProcessor[]::new))
                .build();

        if (configs.contains(botConfig)) {
            return Optional.empty();
        }

        configs.add(botConfig);
        return Optional.of(botConfig);
    }

    private List<BotConfig> training(List<BotConfig> simulators, CandlestickWrapper[][] data) {
        List<BotConfig> best = validate(simulators, data);
        for (int j = 0; j < 5; j++) {
            System.out.println("Training " + j);
            List<BotConfig> crossed = simulators.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(6)
                    .collect(Collectors.toList());

            Collections.shuffle(simulators);
            crossed.addAll(simulators.stream()
                    .limit(2)
                    .toList());

            best.addAll(simulators.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(25)
                    .flatMap(s -> IntStream.range(0, 10)
                            .mapToObj(i -> mutate(s))
                            .filter(Optional::isPresent)
                            .limit(2)
                            .map(Optional::get))
                    .parallel()
                    .filter(s -> simulate(data, s))
                    .toList());
            best.addAll(simulators.stream()
                    .limit(20)
                    .flatMap(s -> IntStream.range(0, 10)
                            .mapToObj(i -> mutate(s))
                            .filter(Optional::isPresent)
                            .limit(1)
                            .map(Optional::get))
                    .parallel()
                    .filter(s -> simulate(data, s))
                    .toList());

            best = best.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(2000)
                    .collect(Collectors.toList());

            if (!best.isEmpty()) {
                System.out.println("Best " + best.get(0));
            } else {
                System.out.println("nothing found");
            }
        }
        return best;
    }

    private List<BotConfig> validate(List<BotConfig> simulators, CandlestickWrapper[][] data) {
        return simulators.parallelStream()
                .filter(s -> simulate(data, s))
                .collect(Collectors.toList());
    }

}
