package app.web.controllers.analytics;

import app.analytics.dto.ReportRequest;
import app.analytics.dto.ReportResponse;
import app.analytics.service.ReportService;
import app.mapper.transaction.TransactionMapper;
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.AuthenticationUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/budget-reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final TransactionService transactionService;


    @GetMapping
    public ModelAndView showReportsPage() {

        ReportRequest request = ReportRequest.builder().build();

        return new ModelAndView("budget-reports")
                .addObject("request", request);
    }

    @PostMapping
    public ModelAndView generateReport(
            @Valid @ModelAttribute ReportRequest request,
            @AuthenticationPrincipal AuthenticationUserDetails principal){

        List<TransactionDto> transactions =
                transactionService.getTransactionForReport(
                        principal.getId(),
                        request.getStart(),
                        request.getEnd()).stream().map(TransactionMapper::toDto).toList();


        ReportRequest reportRequest = ReportRequest.builder()
                .userId(principal.getId().toString())
                .start(request.getStart())
                .end(request.getEnd())
                .transactions(transactions)
                .build();

        ReportResponse reports = reportService.generateReport(reportRequest);

        ModelAndView modelAndView = new ModelAndView("budget-reports");
        modelAndView.addObject("request", request);
        modelAndView.addObject("reports", reports);
        System.out.println(reports);
        return modelAndView;
    }

}
