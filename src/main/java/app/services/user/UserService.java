package app.services.user;

import app.mapper.user.UserMapper;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entities.budget.Budget;
import app.model.entities.saving.SavingGoal;
import app.model.entities.transaction.Transaction;
import app.model.entities.user.User;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import app.services.saving.SavingService;
import app.services.transaction.TransactionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BudgetService budgetService;
    private final SavingService savingService;
    private final TransactionService transactionService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BudgetService budgetService, SavingService savingService, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.budgetService = budgetService;
        this.savingService = savingService;
        this.transactionService = transactionService;
    }

    public UserDto register(UserRegisterRequest userRegisterRequest){

        userRepository.findByUsername(userRegisterRequest.getUsername())
                .ifPresent(user -> {
                    throw new RuntimeException("User with username already exists!");
                });

      String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
      userRegisterRequest.setPassword(encodedPassword);

        User userEntity = UserMapper.toUserEntity(userRegisterRequest);

        userEntity = userRepository.save(userEntity);

        Budget defaultBudget =  budgetService.createDefaultBudget(userEntity);
        userEntity.setBudgets(List.of(defaultBudget));

        SavingGoal defaultSavingGoal =  savingService.createDefaultSaving(userEntity);
        userEntity.setSavingGoals(List.of(defaultSavingGoal));

        Transaction defaultTransaction= transactionService.createDefaultTransaction(userEntity);
        userEntity.setTransactions(List.of(defaultTransaction));



        userRepository.save(userEntity);

        return UserMapper.toUserDto(userEntity);
    }

    public UserDto login(UserLoginRequest userLoginRequest){
       Optional<User> userLog = userRepository.findByUsername(userLoginRequest.getUsername());

       if(userLog.isEmpty() || !passwordEncoder.matches(userLoginRequest.getPassword(), userLog.get().getPassword())){
           throw new RuntimeException("Username or password mismatch!");
       }

       return UserMapper.toUserDto(userLog.get());
    }
}
