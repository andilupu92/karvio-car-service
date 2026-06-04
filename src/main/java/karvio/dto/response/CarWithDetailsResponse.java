package karvio.dto.response;

import java.math.BigDecimal;

public record CarWithDetailsResponse(Long carId,
                                     String name,
                                     BigDecimal consumption,
                                     Integer health,
                                     BigDecimal amount
) {}
