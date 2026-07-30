package app.analytics;


import app.analytics.client.TransactionClient;
import app.analytics.dto.StatisticResponse;
import app.analytics.service.StatisticService;
import app.services.user.UserService;
import app.web.dto.budget.BudgetDto;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.UserDto;
import app.web.dto.user.UsersDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceUTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionClient client;

    @InjectMocks
    StatisticService statisticService;

    @Test
    public void getAllUsersForStatistic_returnAllUsersDetails(){

        UserDto user = UserDto.builder()
                .username("Teo12345")
                .build();
        UserDto user1 = UserDto.builder()
                .username("Teo12345")
                .build();

        List<UserDto> listUser = new ArrayList<>();
        listUser.add(user);
        listUser.add(user1);

        SavingGoalsDto goals = SavingGoalsDto.builder().build();
        List<SavingGoalsDto> goalsDtoList = new ArrayList<>();
        goalsDtoList.add(goals);

        TransactionDto transaction = TransactionDto.builder().build();

        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactionDtos.add(transaction);
        transactionDtos.add(transaction);
        transactionDtos.add(transaction);
        transactionDtos.add(transaction);

        BudgetDto budgetDto = BudgetDto.builder().build();
        List<BudgetDto> budgetDtoList = new ArrayList<>();
        budgetDtoList.add(budgetDto);
        budgetDtoList.add(budgetDto);

        UsersDetails usersDetails = UsersDetails.builder()
                .users(listUser)
                .saving(goalsDtoList)
                .transactions(transactionDtos)
                .budgets(budgetDtoList)
                .build();

        StatisticResponse expectedResponse = StatisticResponse.builder()
                .totalUsers(2)
                .totalBudget(2)
                .totalSavings(1)
                .totalTransactions(4)
                .build();
        when(userService.getAllUsersDetails()).thenReturn(usersDetails);
        when(client.postStatisticForAllUsers(usersDetails))
                .thenReturn(expectedResponse);
        StatisticResponse result = statisticService.getAllUsersForStatistic();

        assertEquals(expectedResponse, result);

        verify(userService).getAllUsersDetails();
        verify(client).postStatisticForAllUsers(usersDetails);
        verifyNoMoreInteractions(userService, client);


    }
}
