package app.services;

import app.analytics.service.StatisticService;
import app.analytics.service.SummaryService;
import app.model.entities.user.User;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final SummaryService summaryService;
    private final StatisticService statisticService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void createBudgetsForAllUsers() {

        LocalDateTime now = LocalDateTime.now();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            budgetService.createBudgetForCurrentMonth(user, now);
        }

        log.info("Budget for new mouth updated.");

    }

    @Scheduled(fixedDelay = 300000)
    public void updateDashboardSummary() {

        List<User> users = userRepository.findAll();

        for (User user : users) {
            summaryService.generateSummary(user.getId());
        }

        log.info("Dashboard summaries refreshed.");
    }

    @Scheduled(fixedRate = 300000)
    public void refreshStatistics() {

        log.info("Statistics refreshed");
        statisticService.getAllUsersForStatistic();
    }
}
