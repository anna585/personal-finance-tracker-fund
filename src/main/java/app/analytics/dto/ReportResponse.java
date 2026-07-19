package app.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReportResponse {

    private UUID id;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal balance;
    private String largestExpense;
    private BigDecimal savingRate;
    private LocalDate generatedAt;
}

