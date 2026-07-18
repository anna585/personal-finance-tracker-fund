package app.mapper.budget;

import app.web.dto.budget.BudgetDto;
import app.model.entities.budget.Budget;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BudgetMapper {

    public static BudgetDto toDto(Budget budget){

        if(budget == null){
            return null;
        }

        return BudgetDto.builder()
                .id(budget.getId())
                .monthlyLimit(budget.getMonthlyLimit())
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }
}
