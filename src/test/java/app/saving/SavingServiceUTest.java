package app.saving;

import app.exeption.budget.BudgetNotEnoughException;
import app.exeption.budget.BudgetNotFoundException;
import app.exeption.savings.SavingGoalNotFoundException;
import app.exeption.user.InvalidUuidException;
import app.exeption.user.TargetDateInPastException;
import app.exeption.user.UserNotFoundException;
import app.model.entities.budget.Budget;
import app.model.entities.saving.SavingGoal;
import app.model.entities.transaction.Transaction;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.repositories.saving.SavingRepository;
import app.repositories.transaction.TransactionRepository;
import app.repositories.user.UserRepository;
import app.services.saving.SavingService;
import app.services.transaction.TransactionService;
import app.web.dto.saving.EditSavingRequest;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.saving.SavingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SavingServiceUTest {

    @Mock
    private  SavingRepository savingRepository;

    @Mock
    private  TransactionRepository transactionRepository;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  BudgetRepository budgetRepository;

    @Mock
    private  TransactionService transactionService;


    @InjectMocks
    SavingService savingService;

    @Test
    public void getSavingGoalById_whenSavingGoalNotExist_thenThrowSavingGoalNotFoundException(){

        when(savingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(SavingGoalNotFoundException.class, () -> savingService.getSavingGoalById(any()));
        verify(savingRepository).findById(any());
    }

    @Test
    public void getSavingGoalById_whenSavingGoalExist_thenReturnSavingGoal(){

        SavingGoal goal = SavingGoal.builder()
                .goalName("Vacantion")
                .id(UUID.randomUUID())
                .build();

        when(savingRepository.findById(any())).thenReturn(Optional.of(goal));

        SavingGoalsDto result = savingService.getSavingGoalById(any());

        assertNotNull(savingService.getSavingGoalById(any()));
        assertEquals(goal.getGoalName(), result.getGoalName());

    }

    @Test
    public void deleteSavingGoal_whenSavingGoalNotExist_thenThrowSavingGoalNotFoundException(){

        when(savingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(SavingGoalNotFoundException.class, () ->  savingService.deleteSavingGoal(any()));
        verify(savingRepository).findById(any());
    }

    @Test
    public void deleteSavingGoal_whenSavingGoalExist_thenSavingGoalDelete(){

        SavingGoal goal = SavingGoal.builder()
                .goalName("Vacantion")
                .id(UUID.randomUUID())
                .build();

        when(savingRepository.findById(any())).thenReturn(Optional.of(goal));

        savingService.deleteSavingGoal(goal.getId());
        verify(savingRepository).findById(goal.getId());
    }

    @Test
    public void getAllSavingGoalsByUser_thenReturnAllListSavingGoalsByUserId(){

        UUID userId = UUID.randomUUID();
        SavingGoal goal = SavingGoal.builder()
                .goalName("Vacantion1")
                .build();

        List<SavingGoal> list1 = List.of(goal, goal);

        when(savingRepository.findAllByUserId(any())).thenReturn(list1);

        assertEquals(list1.size(), savingService.getAllSavingGoalsByUser(userId).size());
        verify(savingRepository).findAllByUserId(userId);

    }

    @Test
    public void getAllSavingGoals_thenReturnAllListOfSavingGoals(){

        SavingGoal goal = SavingGoal.builder()
                .goalName("Vacantion1")
                .build();

        List<SavingGoal> list1 = List.of(goal, goal);
        List<SavingGoal> list2 = List.of(goal, goal, goal);

        List<SavingGoal> allList = new ArrayList<>();
        allList.addAll(list1);
        allList.addAll(list2);

        when(savingRepository.findAll()).thenReturn(allList);


        assertEquals(allList.size(), savingService.getAllSavingGoals().size());
        verify(savingRepository).findAll();
    }

    @Test
    public void updateSavingGoal_whenTargetDateIsPast_thenThrowTargetDateInPastException(){

        UUID id = UUID.randomUUID();

        EditSavingRequest editSavingRequest = EditSavingRequest.builder()
                .targetDate(LocalDate.now().minusDays(1))
                .build();

        assertThrows(TargetDateInPastException.class, ()-> savingService.updateSavingGoal(id, editSavingRequest));

    }

    @Test
    public void updateSavingGoal_whenTargetDateIsInFutureButSavingGoalNotFound_thenThrowInvalidUuidException(){
        LocalDate date = LocalDate.now();
        UUID id = UUID.randomUUID();

        EditSavingRequest editSavingRequest = EditSavingRequest.builder()
                .targetDate(date)
                .build();

        when(savingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(InvalidUuidException.class, () -> savingService.updateSavingGoal(id, editSavingRequest));
        verify(savingRepository).findById(id);
    }

    @Test
    public void updateSavingGoal_whenTargetDateIsInFutureButSavingGoalIsFound_thenSavingGoalIsUpdateSuccess(){

        UUID id = UUID.randomUUID();

        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(500.00))
                .build();

        SavingGoal savingGoal = SavingGoal.builder()
                .id(id)
                .goalName("Holiday")
                .currentAmount(BigDecimal.valueOf(500.00))
                .targetDate(LocalDate.of(2026,10,1))
                .targetAmount(BigDecimal.valueOf(5000.00))
                .transaction(transaction)
                .build();

        EditSavingRequest editSavingRequest = EditSavingRequest.builder()
                .goalName("Vacantion5")
                .currentAmount(BigDecimal.valueOf(800.00))
                .targetAmount(BigDecimal.valueOf(10000.00))
                .targetDate(LocalDate.of(2026,12,31))
                .build();

        when(savingRepository.findById(id)).thenReturn(Optional.of(savingGoal));

        savingService.updateSavingGoal(id, editSavingRequest);

        assertEquals("Vacantion5", savingGoal.getGoalName());
        assertEquals(BigDecimal.valueOf(800.00), savingGoal.getCurrentAmount());
        assertEquals(BigDecimal.valueOf(10000.00), savingGoal.getTargetAmount());
        assertEquals(LocalDate.of(2026,12,31), savingGoal.getTargetDate());

        assertEquals(BigDecimal.valueOf(800.00), transaction.getAmount());

        verify(savingRepository).findById(id);
        verify(savingRepository).save(any(SavingGoal.class));
    }

    @Test
    public void createGoal_whenTargetDateIsPast_thenThrowTargetDateInPastException(){

        UUID id = UUID.randomUUID();

        SavingRequest savingRequest = SavingRequest.builder()
                .targetDate(LocalDate.now().minusDays(1))
                .build();

        assertThrows(TargetDateInPastException.class, ()-> savingService.createGoal(id, savingRequest));

    }

    @Test
    public void createGoal_whenUserIdIsNotFound_thenThrowUserNotFoundException(){

        UUID userId = UUID.randomUUID();

        SavingRequest savingRequest = SavingRequest.builder()
                .goalName("Holiday")
                .targetDate(LocalDate.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()-> savingService.createGoal(userId, savingRequest));

        verify(userRepository).findById(userId);
    }

    @Test
    public void createGoal_whenBudgetIsNotFound_thenThrowBudgetNotFoundException(){
        LocalDate date = LocalDate.now();
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();

        SavingRequest savingRequest = SavingRequest.builder()
                .goalName("Holiday")
                .targetDate(LocalDate.now().plusDays(20))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, date.getMonth(), date.getYear())).thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, ()-> savingService.createGoal(userId, savingRequest));

        verify(budgetRepository).findByUserAndMonthAndYear(user, date.getMonth(), date.getYear());
        verify(userRepository).findById(userId);
    }

    @Test
    public void createGoal_whenRemainingBudgetIsNotEnough_thenThrowBudgetNotEnoughException() {
        LocalDate date = LocalDate.now();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        SavingRequest savingRequest = SavingRequest.builder()
                .goalName("Holiday")
                .currentAmount(BigDecimal.valueOf(500.00))
                .targetAmount(BigDecimal.valueOf(10000.00))
                .targetDate(LocalDate.now().plusDays(20))
                .build();

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(4200.00))
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, date.getMonth(), date.getYear())).thenReturn(Optional.of(budget));
        when(transactionService.getTotalSpentByUser(user)).thenReturn(BigDecimal.valueOf(4000.00));


        assertThrows(BudgetNotEnoughException.class, () ->  savingService.createGoal(user.getId(), savingRequest));
        verify(userRepository).findById(user.getId());
        verify(budgetRepository).findByUserAndMonthAndYear(
               user,
                date.getMonth(),
                date.getYear()
        );
        verify(transactionService).getTotalSpentByUser(user);
    }

    @Test
    public void createGoal_whenRemainingBudgetIsEnough_thenSavingGoalCreate() {
        LocalDate date = LocalDate.now();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        SavingRequest savingRequest = SavingRequest.builder()
                .goalName("Holiday")
                .currentAmount(BigDecimal.valueOf(500.00))
                .targetAmount(BigDecimal.valueOf(10000.00))
                .targetDate(LocalDate.now().plusDays(20))
                .build();

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(5000.00))
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, date.getMonth(), date.getYear())).thenReturn(Optional.of(budget));
        when(transactionService.getTotalSpentByUser(user)).thenReturn(BigDecimal.valueOf(4000.00));

        savingService.createGoal(user.getId(), savingRequest);


        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);

        verify(savingRepository).save(captor.capture());

        SavingGoal savingGoal = captor.getValue();


        assertEquals("Holiday", savingGoal.getGoalName());
        assertEquals(BigDecimal.valueOf(600.00).setScale(2), savingGoal.getCurrentAmount());
        assertEquals(BigDecimal.valueOf(10000.00), savingGoal.getTargetAmount());

        verify(userRepository).findById(user.getId());
        verify(budgetRepository).findByUserAndMonthAndYear(
                user,
                date.getMonth(),
                date.getYear()
        );
        verify(transactionService).getTotalSpentByUser(user);
        verify(savingRepository).save(any(SavingGoal.class));
    }
}
