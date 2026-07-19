package app.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class StatisticResponse {

    private BigDecimal totalUsers;
    private BigDecimal totalTransactions;
    private BigDecimal totalBudget;
    private BigDecimal totalSavings;

}
