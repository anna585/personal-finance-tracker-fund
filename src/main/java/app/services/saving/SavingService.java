package app.services.saving;

import app.mapper.saving.SavingGoalsMapper;
import app.mapper.user.UserMapper;
import app.web.dto.saving.EditSavingRequest;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.saving.SavingRequest;
import app.web.dto.user.UserDto;
import app.model.entities.budget.Budget;
import app.model.entities.saving.SavingGoal;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.repositories.saving.SavingRepository;
import app.repositories.transaction.TransactionRepository;
import app.repositories.user.UserRepository;
import app.services.transaction.TransactionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SavingService {

    private final SavingRepository savingRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    public SavingGoalsDto getSavingGoalById(UUID id) {

        return savingRepository.findById(id)
                .map(SavingGoalsMapper::toDto)
                .orElse(null);
    }

    public void deleteSavingGoal(UUID id) {

        savingRepository.deleteById(id);
    }

    public UserDto createGoals(UUID userId, @Valid SavingRequest savingRequest) {
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));

        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear())
                .orElseThrow(() ->
                        new RuntimeException("No budget found. Please create a budget first."));

        BigDecimal spent = transactionService.getTotalSpentByUser(userId);
        BigDecimal remaining =budget.getMonthlyLimit().subtract(spent);

        BigDecimal autoSave = remaining.multiply(BigDecimal.valueOf(0.10));
        BigDecimal currentAmountAndAutoSave = savingRequest.getCurrentAmount().add(autoSave);

        if(remaining.compareTo(autoSave) < 0){
            throw new IllegalArgumentException("The monthly budget is not sufficient to create this savings goal.");
        } else if (remaining.compareTo(currentAmountAndAutoSave) < 0) {
            throw new IllegalArgumentException("The entered amount exceeds the remaining monthly budget.");
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(currentAmountAndAutoSave)
                .type(TransactionType.EXPENSE)
                .categoryType(CategoryType.SAVING)
                .date(LocalDate.now())
                .build();
        transactionRepository.save(transaction);


        SavingGoal savingGoal = SavingGoal.builder()
                .user(user)
                .goalName(savingRequest.getGoalName())
                .targetAmount(savingRequest.getTargetAmount())
                .currentAmount(currentAmountAndAutoSave)
                .targetDate(savingRequest.getTargetDate())
                .transaction(transaction)
                .build();

        savingRepository.save(savingGoal);

        user.getSavingGoals().add(savingGoal);
        userRepository.save(user);

        return UserMapper.toUserDto(user);

    }

    public Long getCountOfSavingGoals() {

        return savingRepository.count();
    }

    public List<SavingGoal> getAllSavingGoalsByUser(UUID userId) {

        return savingRepository.findAllByUserId(userId);
    }

    public SavingGoalsDto updateSavingGoals(UUID id, EditSavingRequest editSavingRequest) {

        SavingGoal savingEntity = savingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SavingGoal with id [%s] does not exist".formatted(id)));

        Transaction transaction = savingEntity.getTransaction();

        transaction.setAmount(editSavingRequest.getCurrentAmount());


        savingEntity.setGoalName(editSavingRequest.getGoalName());
        savingEntity.setTargetAmount(editSavingRequest.getTargetAmount());
        savingEntity.setCurrentAmount(editSavingRequest.getCurrentAmount());
        savingEntity.setTargetDate(editSavingRequest.getTargetDate());

        SavingGoal updateSavingGoal = savingRepository.save(savingEntity);

        return SavingGoalsMapper.toDto(updateSavingGoal);
    }
}
