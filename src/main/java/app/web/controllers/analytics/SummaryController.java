package app.web.controllers.analytics;

import app.analytics.dto.SummaryResponse;
import app.analytics.service.SummaryService;
import app.services.user.UserService;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;
    private final UserService userService;


    @GetMapping
    public ModelAndView getSummaryResponse(
            @AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        SummaryResponse summaryResponses = summaryService
                .generateSummary(principal.getId());

        return new ModelAndView("/dashboard")
                .addObject("summaryResponses", summaryResponses)
                .addObject("user", user);

    }
}
