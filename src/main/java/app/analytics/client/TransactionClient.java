package app.analytics.client;

import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import app.analytics.dto.StatisticResponse;
import app.analytics.dto.SummaryResponse;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "budget-analytics-svc",
        url = "http://localhost:8081")
public interface TransactionClient {

    @PostMapping("/api/v1/analytics/report")
    ReportResponse postReport(
            @RequestBody ReportRequest reportRequest);

    @PostMapping("/api/v1/analytics/summary")
    SummaryResponse getSummaryReport(
            @RequestBody List<TransactionDto> transactions);



    @GetMapping("/api/v1/analystics/report-history")
    List<ReportResponse> getHistory(
            @RequestParam String userId);

    @DeleteMapping("/api/v1/analytics/report-history/{reportId}")
    ResponseEntity<Void> deleteReport(@PathVariable String reportId);


    @PostMapping("/api/v1/analytics/statistic")
    StatisticResponse getStatisticForAllUsers(
            @RequestBody List<UserDto> userList);
}
