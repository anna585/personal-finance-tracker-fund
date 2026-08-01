package app.web.controllers.analytics;

import app.analytics.dto.SummaryResponse;
import app.analytics.service.SummaryService;
import app.config.CurrencyFormatter;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static app.web.controllers.transaction.TransactionFactoryDto.getTransactionDto;
import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static app.web.controllers.user.UserFactoryDto.getUserDto;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(SummaryController.class)
@Import(CurrencyFormatter.class)
public class SummaryControllerApiTest {

    @MockitoBean
    private SummaryService summaryService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void showDashboard_whenUserExist_thenViewDashboardAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        SummaryResponse summary = SummaryResponse.builder()
                .monthlyBalance(BigDecimal.valueOf(500.00))
                .monthlyIncome(BigDecimal.valueOf(1500.00))
                .monthlyExpense(BigDecimal.valueOf(1000.00))
                .monthlySavingRade(BigDecimal.valueOf(20))
                .build();

        TransactionDto transactionDto = getTransactionDto();
        List<TransactionDto> transactionDtoList = List.of(transactionDto, transactionDto);

        when(userService.getById(user.getId())).thenReturn(userDto);
        when(summaryService.generateSummary(user.getId())).thenReturn(summary);
        when(transactionService.getTop5Transactions(user.getId())).thenReturn(transactionDtoList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dashboard")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("summary", summary))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("transactionDtoList", transactionDtoList))
                .andExpect(model().attribute("date", LocalDate.now()))
                .andExpect(model().attribute("analyticsAvailable", true));

        verify(userService).getById(eq(user.getId()));
        verify(summaryService).generateSummary(eq(user.getId()));
        verify(transactionService).getTop5Transactions(eq(user.getId()));
    }

    @Test
    public void showDashboard_whenUserIsNull_thenRedirectLoginViewAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();

        when(userService.getById(user.getId())).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dashboard")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

    }

    @Test
    public void showDashboard_whenUserExistButSummaryIsNull_thenViewDashboardAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();

        TransactionDto transactionDto = getTransactionDto();
        List<TransactionDto> transactionDtoList = List.of(transactionDto, transactionDto);

        when(userService.getById(user.getId())).thenReturn(userDto);
        when(summaryService.generateSummary(user.getId())).thenReturn(null);
        when(transactionService.getTop5Transactions(user.getId())).thenReturn(transactionDtoList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dashboard")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("transactionDtoList", transactionDtoList))
                .andExpect(model().attribute("date", LocalDate.now()))
                .andExpect(model().attribute("analyticsMessage", "\uD83D\uDFE1 Analytics service is temporarily unavailable."))
                .andExpect(model().attribute("analyticsAvailable", false));

        verify(userService).getById(eq(user.getId()));
        verify(summaryService).generateSummary(eq(user.getId()));
        verify(transactionService).getTop5Transactions(eq(user.getId()));
    }
}
