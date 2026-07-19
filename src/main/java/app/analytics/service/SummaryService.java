package app.analytics.service;

import app.analytics.client.TransactionClient;
import app.analytics.dto.SummaryResponse;
import app.mapper.transaction.TransactionMapper;
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final TransactionClient client;
    private final TransactionService transactionService;

    public SummaryResponse generateSummary(UUID userId) {

        YearMonth month = YearMonth.now();

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();


        List<TransactionDto> transactions = transactionService
                .getTransactionForReport(userId, start, end)
                .stream()
                .map(TransactionMapper::toDto)
                .toList();

        return client.getSummaryReport(transactions);

    }
}
