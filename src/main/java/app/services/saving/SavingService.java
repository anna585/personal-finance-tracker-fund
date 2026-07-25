package app.services.saving;

import app.exeption.user.InvalidUuidException;
import app.exeption.budget.BudgetNotEnoughException;
import app.exeption.budget.BudgetNotFoundException;
import app.exeption.savings.SavingGoalNotFoundException;
import app.exeption.user.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingService {

    private final SavingRepository savingRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    public SavingGoalsDto getSavingGoalById(UUID id) {

        SavingGoal savingGoal = savingRepository.findById(id)
                .orElseThrow(() -> new SavingGoalNotFoundException(id));

        return SavingGoalsMapper.toDto(savingGoal);
    }

    @Transactional
    public void deleteSavingGoal(UUID id) {

        SavingGoal savingGoal = savingRepository.findById(id)
                .orElseThrow(() -> new SavingGoalNotFoundException(id));

        savingRepository.delete(savingGoal);

        log.info("Delete saving goal with id {}", id);
    }

    @Transactional
    public UserDto createGoal(UUID userId, @Valid SavingRequest savingRequest) {

        if(savingRequest.getTargetDate().isBefore(LocalDate.now())){
            throw new TargetDateInPastException();
        }

        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(userId));

        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear())
                .orElseThrow(()->
                        new BudgetNotFoundException(userId));

        BigDecimal spent = transactionService.getTotalSpentByUser(userId);
        BigDecimal remaining =budget.getMonthlyLimit().subtract(spent);

        BigDecimal autoSave = remaining.multiply(BigDecimal.valueOf(0.10));
        BigDecimal currentAmountAndAutoSave = savingRequest.getCurrentAmount().add(autoSave);

       if (remaining.compareTo(currentAmountAndAutoSave) < 0) {
            throw new BudgetNotEnoughException();
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(currentAmountAndAutoSave)
                .type(TransactionType.EXPENSE)
                .categoryType(CategoryType.SAVING)
                .createdAt(LocalDateTime.now())
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

        log.info("Creating saving goal with id {} and target amount {} EURO.", savingGoal.getId(), savingGoal.getTargetAmount());

        return UserMapper.toUserDto(user);

    }

    public List<SavingGoal> getAllSavingGoalsByUser(UUID userId) {

        return savingRepository.findAllByUserId(userId);
    }

    @Transactional
    public SavingGoalsDto updateSavingGoal(UUID id, EditSavingRequest editSavingRequest) {

        if (editSavingRequest.getTargetDate().isBefore(LocalDate.now())) {
            throw new TargetDateInPastException();
        }

        SavingGoal savingEntity = savingRepository.findById(id)
                .orElseThrow(() -> new InvalidUuidException());

        Transaction transaction = savingEntity.getTransaction();

        transaction.setAmount(editSavingRequest.getCurrentAmount());


        savingEntity.setGoalName(editSavingRequest.getGoalName());
        savingEntity.setTargetAmount(editSavingRequest.getTargetAmount());
        savingEntity.setCurrentAmount(editSavingRequest.getCurrentAmount());
        savingEntity.setTargetDate(editSavingRequest.getTargetDate());
        savingRepository.save(savingEntity);

        log.info("Updating saving goal with name {}", editSavingRequest.getGoalName());

        return SavingGoalsMapper.toDto(savingEntity);
    }
}
