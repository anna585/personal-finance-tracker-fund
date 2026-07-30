package app.analytics;

import app.analytics.client.TransactionClient;
import app.analytics.dto.ReportRequest;
import app.analytics.dto.SummaryResponse;
import app.analytics.service.SummaryService;
import app.model.entities.user.User;
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SummaryServiceUTest {

    @Mock
    private TransactionClient client;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    SummaryService summaryService;

    @Test
    public void generateSummary_thenReturnListOfAllTransactionReports(){
        UUID userId = UUID.randomUUID();

        YearMonth month = YearMonth.now();

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        List<TransactionDto> transactions = transactionService
                .getTransactionForReport(userId, start, end)
                .stream()
                .toList();

        SummaryResponse response = SummaryResponse.builder()
                .build();

        when(transactionService.getTransactionForReport(any(), any(), any()))
                .thenReturn(transactions);
        when(client.generateSummary(transactions)).thenReturn(response);

        summaryService.generateSummary(userId);
        verify(client).generateSummary(transactions);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void generateSummary_whenFailed_thenThrowFeignException(){

        UUID userId = UUID.randomUUID();

        List<TransactionDto> transactions = List.of(
                TransactionDto.builder().build());

        when(transactionService.getTransactionForReport(any(), any(), any()))
                .thenReturn(transactions);

        when(client.generateSummary(transactions))
                .thenThrow(Mockito.mock(FeignException.class));

        assertNull(summaryService.generateSummary(userId));

        verify(transactionService).getTransactionForReport(any(), any(), any());
        verify(client).generateSummary(transactions);
    }
}
