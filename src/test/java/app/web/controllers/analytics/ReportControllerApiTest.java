package app.web.controllers.analytics;

import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import app.analytics.service.ReportService;
import app.config.CurrencyFormatter;
import app.model.entities.transaction.CategoryType;
import app.security.SessionInterceptor;
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.AuthenticationUserDetails;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static app.web.controllers.transaction.TransactionFactoryDto.getTransactionDto;
import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ReportController.class)
@Import(CurrencyFormatter.class)
public class ReportControllerApiTest {

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private SessionInterceptor sessionInterceptor;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void showReportsPage_thenViewReportBudgetAndStatusIs2xx() throws Exception{
        AuthenticationUserDetails user = getUserAdminDto();

        ReportRequest request = ReportRequest.builder().build();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/budget-reports")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("budget-reports"))
                .andExpect(model().attribute("request", request));
    }

    @Test
    public void generateReport_thenViewBudgetReportsAndStatusIs2xx() throws Exception{
        AuthenticationUserDetails user = getUserAdminDto();

        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        TransactionDto transaction = getTransactionDto();
        List<TransactionDto> transactions = List.of(transaction, transaction);


        ReportResponse response = ReportResponse.builder()
                .id(user.getId())
                .balance(BigDecimal.valueOf(500.00))
                .income(BigDecimal.valueOf(1500.00))
                .expenses(BigDecimal.valueOf(1000.00))
                .generatedAt(LocalDateTime.now())
                .largestExpense(CategoryType.FOOD)
                .savingRate(BigDecimal.valueOf(10))
                .build();

        when(reportService.generateReport(any(ReportRequest.class))).thenReturn(response);
        when(transactionService.getTransactionForReport(
                eq(user.getId()),
                eq(start.atStartOfDay()),
                eq(end.atTime(LocalTime.MAX))))
                .thenReturn(transactions);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/budget-reports")
                .with(user(user))
                .with(csrf())
                .param("start", start.toString())
                .param("end", end.toString());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("budget-reports"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("report", response));

        verify(reportService).generateReport(any(ReportRequest.class));
        verify(transactionService).getTransactionForReport(eq(user.getId()),
                eq(start.atStartOfDay()),
                eq(end.atTime(LocalTime.MAX)));
    }


    @Test
    public void generateReport_whenRequestIsNotCorrect_thenViewBudgetReportsAndStatusIs3xx() throws Exception{
        AuthenticationUserDetails user = getUserAdminDto();

        ReportResponse response = ReportResponse.builder()
                .id(user.getId())
                .balance(BigDecimal.valueOf(500.00))
                .income(BigDecimal.valueOf(1500.00))
                .expenses(BigDecimal.valueOf(1000.00))
                .generatedAt(LocalDateTime.now())
                .largestExpense(CategoryType.FOOD)
                .savingRate(BigDecimal.valueOf(10))
                .build();

        when(reportService.generateReport(any(ReportRequest.class))).thenReturn(response);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/budget-reports")
                .with(user(user))
                .with(csrf())
                .param("start", "")
                .param("end", "");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("budget-reports"))
                .andExpect(model().attributeExists("request"));

        verify(reportService, never()).generateReport(any(ReportRequest.class));
    }
}
