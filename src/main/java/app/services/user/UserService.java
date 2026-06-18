package app.services.user;

import app.mapper.user.UserMapper;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BudgetService budgetService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       BudgetService budgetService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.budgetService = budgetService;

    }

    public UserDto register(UserRegisterRequest userRegisterRequest){

        userRepository.findByUsername(userRegisterRequest.getUsername())
                .ifPresent(user -> {
                    throw new RuntimeException("User with username already exists!");
                });

      String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
      userRegisterRequest.setPassword(encodedPassword);

      if(userRegisterRequest.getUserRole() == null){
          userRegisterRequest.setUserRole(UserRole.USER);
      }

        User userEntity = UserMapper.toUserEntity(userRegisterRequest);

        userEntity = userRepository.save(userEntity);

//        Budget defaultBudget =  budgetService.createDefaultBudget(userEntity);
//        userEntity.setBudgets(new ArrayList<>(List.of(defaultBudget)));

        budgetService.createDefaultBudget(userEntity);

        userEntity.setSavingGoals(new ArrayList<>());

        userEntity.setTransactions(new ArrayList<>());

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

    public UserDto getById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User with id [%s] does not exist.".formatted(id)));
        return  UserMapper.toUserDto(user);
    }

    public User getEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public List<UserDto> getAllUsers() {

        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    public void deleteUser(UUID id) {

         userRepository.deleteById(id);
    }

    public User getCurrentUser(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");

        if(userId == null){
            throw new RuntimeException("User not logged in");
        }

        return getEntityById(userId);
    }

    public Long getCountOfUsers() {

        return userRepository.count();
    }
}
