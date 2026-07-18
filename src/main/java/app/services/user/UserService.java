package app.services.user;

import app.exeption.AdminUserCanNotBeDelete;
import app.exeption.EmailAlreadyExistsException;
import app.exeption.UserNotFoundException;
import app.exeption.UsernameAlreadyExistException;
import app.mapper.user.UserMapper;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import app.web.dto.user.UserRegisterRequest;
import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BudgetService budgetService;


    public UserDto register(UserRegisterRequest userRegisterRequest){

        userRepository.findByUsername(userRegisterRequest.getUsername())
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistException("User with username [%s] already exists!".formatted(userRegisterRequest.getUsername()));
                });

        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "User with email [%s] already exists!"
                            .formatted(userRegisterRequest.getEmail()));
        }

      String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
      userRegisterRequest.setPassword(encodedPassword);

      if(userRegisterRequest.getUserRole() == null){
          userRegisterRequest.setUserRole(UserRole.USER);
      }

        User userEntity = UserMapper.toUserEntity(userRegisterRequest);

        userEntity = userRepository.save(userEntity);

        Budget defaultBudget =  budgetService.createDefaultBudget(userEntity);
        userEntity.setBudgets(new ArrayList<>(List.of(defaultBudget)));

        userEntity.setSavingGoals(new ArrayList<>());

        userEntity.setTransactions(new ArrayList<>());

        userRepository.save(userEntity);

        return UserMapper.toUserDto(userEntity);
    }

    public UserDto getById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id [%s] does not exist.".formatted(id)));
        return  UserMapper.toUserDto(user);
    }

    public User getEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    public List<UserDto> getAllUsers() {

        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID id) {

        User user = userRepository.findUserById(id);

        if(user.getUserRole().equals(UserRole.ADMIN)){
            throw new AdminUserCanNotBeDelete("ADMIN users cannot be deleted!");
        }

         userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found!".formatted(username)));
        return AuthenticationUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getUserRole())
                .build();
    }
}
