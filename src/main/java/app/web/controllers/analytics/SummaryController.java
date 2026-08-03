package app.web.controllers.analytics;

import app.analytics.dto.SummaryResponse;
import app.analytics.service.SummaryService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;
    private final UserService userService;
    private final TransactionService transactionService;


    @GetMapping
    public ModelAndView showDashboard(
            @AuthenticationPrincipal AuthenticationUserDetails principal){

       LocalDate date = LocalDate.now();

        UserDto user = userService.getById(principal.getId());

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        List<TransactionDto> transactionDtoList = transactionService.getTop5Transactions(principal.getId());

        SummaryResponse summary = summaryService
                .generateSummary(principal.getId());

        ModelAndView modelAndView = new ModelAndView("dashboard");
       modelAndView.addObject("summary", summary);
       modelAndView.addObject("user", user);
       modelAndView.addObject("transactionDtoList", transactionDtoList);
       modelAndView.addObject("date", date);

        if(summary == null){
            modelAndView.addObject("analyticsAvailable", false);
            modelAndView.addObject(    "analyticsMessage",
                   "🟡 Analytics service is temporarily unavailable.");
        }else {
            modelAndView.addObject("analyticsAvailable", true);
        }


        return modelAndView;
    }
}
