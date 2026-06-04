package karvio.dto.request;

import karvio.annotation.MaxCurrentYear;
import jakarta.validation.constraints.*;

public record CarRequest(@NotNull(message = "Name is required")
                     String name,

                     @NotNull(message = "EnergyType is required")
                     String energyType,

                     @NotNull(message = "Year is required")
                     @Min(value = 1886, message = "Year must be after 1886")
                     @MaxCurrentYear
                     Integer year,

                     @NotNull(message = "Kilometers is required")
                     @Min(value = 0, message = "Kilometers cannot be negative")
                     @Max(value = 999999, message = "Kilometers value is unrealistic")
                     Integer kilometers
) { }
