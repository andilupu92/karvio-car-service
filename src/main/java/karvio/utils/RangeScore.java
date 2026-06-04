package karvio.utils;

import java.math.BigDecimal;

public record RangeScore(BigDecimal min, BigDecimal max, int score) {

    public boolean matches(BigDecimal value) {
        return (min == null || value.compareTo(min) >= 0) &&
                (max == null || value.compareTo(max) <= 0);
    }

}