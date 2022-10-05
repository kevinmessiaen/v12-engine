package com.messik.v12.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CandlestickWrappers {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_TIME)
            .toFormatter();

    public static CandlestickWrapper[] stable(CandlestickWrapper[] src, String fiat) {
        return Arrays.stream(src)
                .map(s -> s.toBuilder()
                        .asset(fiat)
                        .fiat(fiat)
                        .open(1.0)
                        .high(1.0)
                        .low(1.0)
                        .close(1.0)
                        .volume(1.0)
                        .build())
                .toArray(CandlestickWrapper[]::new);
    }

    public static CandlestickWrapper[][] rotate(CandlestickWrapper[][] data) {
        CandlestickWrapper[][] result = new CandlestickWrapper[data[0].length][data.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < data.length; j++) {
                result[i][j] = data[j][i];
            }
        }
        return result;
    }

    public static CandlestickWrapper[][] fromFiles(String fiat, Map<String, Path> files) {
        CandlestickWrapper[][] data = files.entrySet().stream()
                .map(stringPathEntry -> fromFile(stringPathEntry.getValue(), fiat, stringPathEntry.getKey()))
                .toArray(CandlestickWrapper[][]::new);
        LocalDateTime from = Arrays.stream(data).map(d -> d[0].getStart())
                .min(Comparator.naturalOrder())
                .orElseThrow();
        LocalDateTime to = Arrays.stream(data).map(d -> d[d.length - 1].getStart())
                .max(Comparator.naturalOrder())
                .orElseThrow();

        System.out.println("from " + from + " to " + to);

        CandlestickWrapper[] stable = stable(Arrays.stream(data[0])
                .filter(c -> c.getStart().compareTo(from) >= 0 && c.getEnd().compareTo(to) <= 0)
                .toArray(CandlestickWrapper[]::new), fiat);
        return rotate(Stream.concat(Arrays.stream(new CandlestickWrapper[][]{stable}),
                        Arrays.stream(data)
                                .map(d -> {
                                    int low = Arrays.binarySearch(stable, d[0]);
                                    int high = Arrays.binarySearch(stable, d[d.length - 1]);
                                    low = low < 0 ? ~low : low;
                                    high = high < 0 ? ~high : high;
                                    int finalLow = low;
                                    int finalHigh = high;
                                    return IntStream.range(0, stable.length)
                                            .mapToObj(i -> (i < finalLow ? d[0].toBuilder()
                                                    .start(stable[i].getStart())
                                                    .end(stable[i].getEnd())
                                                    .build()
                                                    : i > finalHigh ? d[d.length - 1].toBuilder()
                                                    .start(stable[i].getStart())
                                                    .end(stable[i].getEnd())
                                                    .build() : d[i - finalLow]))
                                            .toArray(CandlestickWrapper[]::new);
                                }))
                .toArray(CandlestickWrapper[][]::new));
    }

    public static CandlestickWrapper[] fromFile(Path path, String fiat, String asset) {
        try {
            return Files.lines(path)
                    .skip(1)
                    .map(line -> fromLine(line, fiat, asset))
                    .toArray(CandlestickWrapper[]::new);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static CandlestickWrapper fromLine(String line, String fiat, String asset) {
        String[] data = line.split(",");
        return CandlestickWrapper.builder()
                .fiat(fiat)
                .asset(asset)
                .open(Double.parseDouble(data[1]))
                .high(Double.parseDouble(data[2]))
                .low(Double.parseDouble(data[3]))
                .close(Double.parseDouble(data[4]))
                .volume(Double.parseDouble(data[5]))
                .start(LocalDateTime.parse(data[0], DATE_TIME_FORMATTER))
                .end(LocalDateTime.ofEpochSecond(Long.parseLong(data[6]) / 1000, 0, ZoneOffset.UTC))
                .build();
    }

}
