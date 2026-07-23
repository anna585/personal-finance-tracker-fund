package app.web.controllers.analytics;

import app.analytics.dto.ReportResponse;
import app.analytics.service.ReportService;
import app.web.dto.user.AuthenticationUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/report-history")
@RequiredArgsConstructor
public class ReportHistoryController {

    private final ReportService reportService;

    @GetMapping
    public ModelAndView getReportHistory(@AuthenticationPrincipal AuthenticationUserDetails principal){

        List<ReportResponse> reportHistory = reportService.getReportHistory(principal.getId());

        return new ModelAndView("report-history")
                .addObject("reportHistory", reportHistory);
    }

    @PostMapping("/{reportId}")
    public ModelAndView deleteReport(@PathVariable UUID reportId){

        reportService.deleteReport(reportId);
        return new ModelAndView("redirect:/report-history");

    }
}


