package karvio.dto.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponse (Long id,
                               Long carId,
                               LocalDateTime date,
                               BigDecimal amount
) { }
