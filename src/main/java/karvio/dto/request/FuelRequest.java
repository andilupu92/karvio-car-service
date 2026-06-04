package karvio.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FuelRequest(@NotNull(message = "CarId is required")
                          Long carId,

                          @NotNull(message = "Kilometers is required")
                          @Min(value = 0, message = "Kilometers cannot be negative")
                          @Max(value = 999999, message = "Kilometers value is unrealistic")
                          Integer kilometers,

                          @NotNull(message = "Liters is required")
                          @Min(value = 0, message = "Liters cannot be negative")
                          BigDecimal liters,

                          @NotNull(message = "Amount is required")
                          @Digits(integer = 10, fraction = 2)
                          @Positive
                          BigDecimal amount,

                          @NotNull(message = "Date is required")
                          LocalDate date

) { }
