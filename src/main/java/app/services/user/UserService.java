package app.services.user;

import app.exeption.user.*;
import app.mapper.budget.BudgetMapper;
import app.mapper.saving.SavingGoalsMapper;
import app.mapper.transaction.TransactionMapper;
import app.mapper.user.UserMapper;
import app.services.saving.SavingService;
import app.services.transaction.TransactionService;
import app.web.dto.budget.BudgetDto;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.*;
import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BudgetService budgetService;
    private final TransactionService transactionService;
    private final SavingService savingService;

    @Transactional
    public UserDto register(UserRegisterRequest userRegisterRequest){

        userRepository.findByUsername(userRegisterRequest.getUsername())
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistsException(userRegisterRequest.getUsername());
                });

        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new EmailAlreadyExistsException(userRegisterRequest.getEmail());
        }

      String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
      userRegisterRequest.setPassword(encodedPassword);

      if(userRegisterRequest.getUserRole() == null){
          userRegisterRequest.setUserRole(UserRole.USER);
      }

        User user = UserMapper.toUserEntity(userRegisterRequest);

        Budget budget =  budgetService.createDefaultBudget(user);

        user.addBudget(budget);

        userRepository.save(user);
        log.info("Registering new user with username: {}", userRegisterRequest.getUsername());

        return UserMapper.toUserDto(user);
    }

    public UserDto getById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));
        return  UserMapper.toUserDto(user);
    }

    public User getEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();

    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id));

        if(UserRole.ADMIN.equals(user.getUserRole())){
            throw new AdminCannotBeDeletedException(id);
        }
        log.info("Delete user with id {}", id);

        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto updateProfileInformation(UUID userId, @Valid UserProfileDto userProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean emailExist = userRepository.findAll()
                .stream()
                .anyMatch(u -> u.getEmail().equals(userProfileDto.getEmail())
                        && !u.getId().equals(userId));

        if(emailExist){

            throw new EmailAlreadyExistsException(userProfileDto.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(userProfileDto.getPassword());
        user.setPassword(encodedPassword);
        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setEmail(userProfileDto.getEmail());
        userRepository.save(user);

        log.info("Updated profile for username {}", userProfileDto.getUsername());

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        log.info("Loading user by username: {}", username);

        return AuthenticationUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getUserRole())
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateRole(UUID id, @Valid UpdateUserRoleDto updateUserRoleDto) {

        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id));
        
        user.setUserRole(updateUserRoleDto.getRole());
        
        userRepository.save(user);

        log.info("User role for username: {} changed", user.getUsername());
        
        return UserMapper.toUserDto(user);
    }

    public boolean existsByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    public UsersDetailLists getAllUsersDetails() {

        List<UserDto> users = userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
        List<BudgetDto> budgets = budgetService.getAllBudgets().stream().map(BudgetMapper::toDto).toList();
        List<TransactionDto> transactions = transactionService.getAllTransactions().stream().map(TransactionMapper::toDto).toList();
        List<SavingGoalsDto> savingGoals = savingService.getAllSavingGoals().stream().map(SavingGoalsMapper::toDto).toList();

        return UsersDetailLists.builder()
                .users(users)
                .budgets(budgets)
                .transactions(transactions)
                .saving(savingGoals)
                .build();

    }
}
