package app.web.controllers.analytics;

import app.analytics.dto.StatisticResponse;
import app.analytics.service.StatisticService;
import app.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;
    private final UserService userService;


    @GetMapping
    public ModelAndView getStatistics(){

        StatisticResponse statistics = statisticService.getAllUsersForStatistic();


        return new ModelAndView("statistics")
                .addObject("statistics", statistics)
                .addObject("users", userService.getAllUsers());
    }

}
