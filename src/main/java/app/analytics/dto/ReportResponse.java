package app.analytics.dto;

import app.model.entities.transaction.CategoryType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReportResponse {

    private UUID id;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal balance;
    private CategoryType largestExpense;
    private BigDecimal savingRate;
    private LocalDateTime generatedAt;
}

