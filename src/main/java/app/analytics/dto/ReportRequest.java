package app.analytics.dto;

import app.web.dto.transaction.TransactionDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReportRequest {

    private UUID userId;
    private LocalDate start;
    private LocalDate end;
    private List<TransactionDto> transactions;

}
