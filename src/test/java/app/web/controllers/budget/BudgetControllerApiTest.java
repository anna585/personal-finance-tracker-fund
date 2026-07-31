package app.web.controllers.budget;


import app.config.CurrencyFormatter;
import app.model.entities.user.User;
import app.services.budget.BudgetService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import app.web.dto.budget.BudgetDto;
import app.web.dto.budget.MonthlyBudgetRequest;
import app.web.dto.user.AuthenticationUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static app.web.controllers.budget.BudgetFactoryDto.getBudgetDto;
import static app.web.controllers.user.UserFactoryDto.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(BudgetController.class)
@Import(CurrencyFormatter.class)
public class BudgetControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BudgetService budgetService;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    MockMvc mockMvc;


    @Test
    public void getBudget_thenReturnBudgetView() throws Exception{

        AuthenticationUserDetails authentication = getUserAdminDto();
        User user = getUser();
        BudgetDto budget = getBudgetDto();

        when(userService.getEntityById(authentication.getId())).thenReturn(user);
        when(budgetService.getCurrentBudget(user)).thenReturn(budget);
        when(transactionService.getTotalSpentByUser(user)).thenReturn(BigDecimal.valueOf(2000.00));
        when(budgetService.calculateRemainingBudget(user)).thenReturn(BigDecimal.valueOf(8000.00));



        MockHttpServletRequestBuilder requestMock = get("/budget")
                .with(user(authentication));

        mockMvc.perform(requestMock)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("budget"))
                .andExpect(model().attribute("user" , user))
                .andExpect(model().attribute("budget", budget))
                .andExpect(model().attributeExists("monthlyBudgetRequest"));

        verify(userService).getEntityById(authentication.getId());
        verify(budgetService).getCurrentBudget(user);
        verify(transactionService).getTotalSpentByUser(user);
        verify(budgetService).calculateRemainingBudget(user);

    }

    @Test
    public void updateBudget_whenMonthlyBudgetIsNegative_thenViewBudgetAndReturnStatus2xx() throws Exception{
        AuthenticationUserDetails authentication = getUserAdminDto();
        User user = getUser();
        BudgetDto budget = getBudgetDto();


        when(userService.getEntityById(authentication.getId())).thenReturn(user);
        when(budgetService.getCurrentBudget(user)).thenReturn(budget);
        when(transactionService.getTotalSpentByUser(user)).thenReturn(BigDecimal.valueOf(2000.00));
        when(budgetService.calculateRemainingBudget(user)).thenReturn(BigDecimal.valueOf(8000.00));

        MockHttpServletRequestBuilder requestMock = post("/budget")
                .with(user(authentication))
                .with(csrf())
                .param("monthlyBudget", "-1");

        mockMvc.perform(requestMock)
                .andExpect(status().isOk())
                .andExpect(view().name("budget"))
                .andExpect(model().attribute("user" , user))
                .andExpect(model().attribute("budget", budget))
                .andExpect(model().attributeHasErrors("monthlyBudgetRequest"));;

                verify(budgetService, never()).updateMonthlyBudget(any(User.class), any(MonthlyBudgetRequest.class));
    }

    @Test
    public void updateBudget_whenMonthlyBudgetIsPositive_thenViewBudgetAndUpdateInvokedAndStatusIsRedirect3xx() throws Exception{
        AuthenticationUserDetails authentication = getUserAdminDto();
        User user = getUser();
        BudgetDto budget = getBudgetDto();


        when(userService.getEntityById(authentication.getId())).thenReturn(user);
        when(budgetService.getCurrentBudget(user)).thenReturn(budget);
        when(transactionService.getTotalSpentByUser(user)).thenReturn(BigDecimal.valueOf(2000.00));
        when(budgetService.calculateRemainingBudget(user)).thenReturn(BigDecimal.valueOf(8000.00));

        MockHttpServletRequestBuilder requestMock = post("/budget")
                .with(user(authentication))
                .with(csrf())
                .param("monthlyBudget", "20.00");

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/budget?success"));

        verify(budgetService).updateMonthlyBudget(any(User.class), any(MonthlyBudgetRequest.class));
    }
}
