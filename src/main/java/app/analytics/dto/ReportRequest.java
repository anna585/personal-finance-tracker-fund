package app.analytics.dto;

import app.web.dto.transaction.TransactionDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReportRequest {

    private String userId;
    private LocalDate start;
    private LocalDate end;
    private List<TransactionDto> transactions;

}
