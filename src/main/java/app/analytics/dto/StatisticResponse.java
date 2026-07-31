package app.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class StatisticResponse {

    private long totalUsers;
    private long totalTransactions;
    private long totalBudget;
    private long totalSavings;

}
