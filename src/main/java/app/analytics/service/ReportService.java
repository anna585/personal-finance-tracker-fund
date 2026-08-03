package app.analytics.service;

import app.analytics.client.TransactionClient;
import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionClient client;

    @Cacheable("report")
    @Transactional
    public ReportResponse generateReport(ReportRequest reportRequest) {

        log.info("Generating report for user {}", reportRequest.getUserId());

        try {
            return client.generateReport(reportRequest);
        } catch (FeignException e) {
            log.error("Failed to generate report for user {}. Analytics service returned {}",
                    reportRequest.getUserId(),
                    e.status(),
                    e);
            throw e;
        }

    }

    public List<ReportResponse> getReportHistory(UUID userId) {

        return client.getHistory(String.valueOf(userId)).getBody();
    }

    @CacheEvict("report")
    @Transactional
    public void deleteReport(UUID reportId) {

        client.deleteReport(reportId);

    }
}
