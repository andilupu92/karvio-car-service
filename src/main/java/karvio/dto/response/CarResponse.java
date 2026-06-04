package karvio.dto.response;

import java.math.BigDecimal;

public record CarResponse(Long id,
                          String name,
                          String energyType,
                          Integer kilometers,
                          Integer year,
                          BigDecimal consumption,
                          Integer healthScore
) { }