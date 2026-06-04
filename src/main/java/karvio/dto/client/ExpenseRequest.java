package karvio.dto.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest (Long carId,
                              Long expenseTypeId,
                              BigDecimal amount,
                              LocalDate date
) {
}