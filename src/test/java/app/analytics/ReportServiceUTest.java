package app.analytics;


import app.analytics.client.TransactionClient;
import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import app.analytics.service.ReportService;
import app.model.entities.user.User;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceUTest {

    @Mock
    private TransactionClient client;

    @InjectMocks
    ReportService reportService;

    @Test
    public void generateReport_thenReturnReport(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        ReportRequest reportRequest = ReportRequest.builder()
                .userId(user.getId())
                .build();

        ReportResponse reportResponse = ReportResponse.builder()
                .id(user.getId())
                .build();

        when(client.generateReport(any())).thenReturn(reportResponse);

        reportService.generateReport(reportRequest);
        verify(client).generateReport(reportRequest);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void generateReport_whenFailed_thenThrowFeignException(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        ReportRequest reportRequest = ReportRequest.builder()
                .userId(user.getId())
                .build();

        when(client.generateReport(reportRequest)).thenThrow(Mockito.mock(FeignException.class));

        assertThrows(
                FeignException.class,
                () -> reportService.generateReport(reportRequest)
        );

        verify(client).generateReport(reportRequest);
    }

}
