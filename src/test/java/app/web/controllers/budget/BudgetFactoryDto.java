package app.web.controllers.budget;

import app.web.dto.budget.BudgetDto;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class BudgetFactoryDto {

    public static BudgetDto getBudgetDto(){

        return BudgetDto.builder()
                .id(UUID.randomUUID())
                .monthlyLimit(BigDecimal.valueOf(10000.00))
                .month(LocalDate.now().getMonth())
                .year(LocalDate.now().getYear())
                .build();
    }
}
