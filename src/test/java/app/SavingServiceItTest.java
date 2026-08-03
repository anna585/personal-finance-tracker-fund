package app;

import app.model.entities.budget.Budget;
import app.model.entities.saving.SavingGoal;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.budget.BudgetRepository;
import app.repositories.saving.SavingRepository;
import app.repositories.transaction.TransactionRepository;
import app.repositories.user.UserRepository;
import app.services.saving.SavingService;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.saving.SavingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class SavingServiceItTest {

    @Autowired
    private SavingService underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void createGoal_whenBudgetIsEnough_thenGoalIsCreateSuccess(){

        LocalDate now = LocalDate.now();

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);

        SavingRequest savingRequest = SavingRequest.builder()
                .targetAmount(BigDecimal.valueOf(9000))
                .currentAmount(BigDecimal.valueOf(200))
                .goalName("Summer Holiday")
                .targetDate(now.plusDays(90))
                .build();

        Budget currectBudget = Budget.builder()
                .user(user)
                .month(now.getMonth())
                .year(now.getYear())
                .monthlyLimit(BigDecimal.valueOf(5000))
                .build();

        budgetRepository.save(currectBudget);

        SavingGoalsDto savingGoalsDto = underTest.createGoal(user.getId(), savingRequest);

        List<SavingGoal> goal = savingRepository.findAllByUserId(user.getId());

        Transaction transaction = transactionRepository.findAll().get(0);

        assertEquals(1, savingRepository.count());
        assertEquals(1, transactionRepository.count());
        assertEquals("Summer Holiday", savingGoalsDto.getGoalName());
        assertEquals(BigDecimal.valueOf(9000).setScale(2), savingGoalsDto.getTargetAmount().setScale(2));
        assertEquals(BigDecimal.valueOf(700).setScale(2), savingGoalsDto.getCurrentAmount().setScale(2));

        assertEquals("Summer Holiday", goal.get(0).getGoalName());
        assertEquals(BigDecimal.valueOf(9000).setScale(2), goal.get(0).getTargetAmount().setScale(2));
        assertEquals(BigDecimal.valueOf(700).setScale(2), goal.get(0).getCurrentAmount().setScale(2));

        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals(CategoryType.SAVING, transaction.getCategoryType());
        assertEquals(BigDecimal.valueOf(700).setScale(2), transaction.getAmount().setScale(2));
    }
}
