package app.analytics.service;

import app.analytics.client.TransactionClient;
import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionClient client;

    @Transactional
    public ReportResponse generateReport(ReportRequest reportRequest) {

        log.info("Generating report for user {}", reportRequest.getUserId());

        ReportRequest dto = ReportRequest.builder()
                .userId(reportRequest.getUserId())
                .start(reportRequest.getStart())
                .end(reportRequest.getEnd())
                .transactions(reportRequest.getTransactions())
                .build();

        try {

           return client.generateReport(dto);

        } catch (FeignException e) {
            log.error("Analytics service returned {}", e.status(), e);
            throw e;
        }

    }

    public List<ReportResponse> getReportHistory(UUID userId) {

        return client.getHistory(String.valueOf(userId)).getBody();
    }

    public void deleteReport(UUID reportId) {

        client.deleteReport(reportId);

    }
}
