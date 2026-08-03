package app.analytics.dto;

import app.web.dto.transaction.TransactionDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReportRequest {

    private UUID userId;
    @NotNull(message = "Start date is required")
    private LocalDate start;
    @NotNull(message = "End date is required")
    private LocalDate end;
    private List<TransactionDto> transactions;

}
