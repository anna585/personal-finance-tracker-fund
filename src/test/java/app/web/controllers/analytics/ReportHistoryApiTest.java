package app.web.controllers.analytics;


import app.analytics.dto.ReportResponse;
import app.analytics.service.ReportService;
import app.config.CurrencyFormatter;
import app.model.entities.transaction.CategoryType;
import app.security.SessionInterceptor;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ReportHistoryController.class)
@Import(CurrencyFormatter.class)
public class ReportHistoryApiTest {

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private SessionInterceptor sessionInterceptor;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void reportHistory_thenViewReportHistoryAndStatus2xx() throws Exception{

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

        List<ReportResponse> responseList = List.of(response);

        when(reportService.getReportHistory(user.getId())).thenReturn(responseList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/report-history")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("report-history"))
                .andExpect(model().attribute("reportHistory", responseList));


        verify(reportService).getReportHistory(eq(user.getId()));
    }

    @Test
    public void deleteReport_thenRedirectToReportHistoryAndStatus2xx() throws Exception{
        AuthenticationUserDetails user = getUserAdminDto();
        UUID reportId = UUID.randomUUID();

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/report-history/{reportId}", reportId)
                .with(user(user))
                .with(csrf());

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/report-history?delete"));

        verify(reportService).deleteReport(eq(reportId));
    }
}
