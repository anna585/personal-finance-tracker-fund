package app.analytics.service;

import app.analytics.client.TransactionClient;
import app.analytics.dto.SummaryResponse;
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService {

    private final TransactionClient client;
    private final TransactionService transactionService;

    public SummaryResponse generateSummary(UUID userId) {

        log.info("Calculating monthly summary for user {}", userId);

        YearMonth month = YearMonth.now();

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);


        List<TransactionDto> transactions = transactionService
                .getTransactionForReport(userId, start, end)
                .stream()
                .toList();

        try{
            return client.generateSummary(transactions);
        }catch (FeignException ex){

            log.warn("Analytics service is unavailable. Dashboard will be shown without summary.");

            return null;
        }



    }
}
