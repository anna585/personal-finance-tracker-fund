package app.analytics.client;

import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import app.analytics.dto.StatisticResponse;
import app.analytics.dto.SummaryResponse;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.UsersDetailLists;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "budget-analytics-svc",
        url = "http://localhost:8081")
public interface TransactionClient {

    @PostMapping("/api/v1/analytics/report")
    ReportResponse generateReport(
            @RequestBody ReportRequest reportRequest);

    @PostMapping("/api/v1/analytics/summary")
    SummaryResponse generateSummary(
            @RequestBody List<TransactionDto> transactions);



    @GetMapping("/api/v1/analytics/report-history")
    ResponseEntity<List<ReportResponse>> getHistory(
            @RequestParam String userId);

    @DeleteMapping("/api/v1/analytics/report-history/{reportId}")
    ResponseEntity<Void> deleteReport(@PathVariable UUID reportId);


    @PostMapping("/api/v1/analytics/statistic")
    StatisticResponse postStatisticForAllUsers(
            @RequestBody UsersDetailLists usersDetailLists);
}
