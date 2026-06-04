package karvio.utils;

import java.math.BigDecimal;
import java.util.List;

public class RangeScoringEngine {

    public static int calculate(BigDecimal value, List<RangeScore> ranges) {
        return ranges.stream()
                .filter(r -> r.matches(value))
                .findFirst()
                .map(RangeScore::score)
                .orElse(0);
    }
}