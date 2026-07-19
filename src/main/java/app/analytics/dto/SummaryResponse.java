package app.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryResponse {

    private long monthlyIncome;
    private long monthlyExpense;
    private long monthlyBalance;
    private long monthlySavingRade;
}
