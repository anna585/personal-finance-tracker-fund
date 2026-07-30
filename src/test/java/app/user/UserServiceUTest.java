package app.user;

import app.exeption.user.AdminCannotBeDeletedException;
import app.exeption.user.EmailAlreadyExistsException;
import app.exeption.user.UserNotFoundException;
import app.exeption.user.UsernameAlreadyExistsException;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import app.services.saving.SavingService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import app.web.dto.user.UpdateUserRoleDto;
import app.web.dto.user.UserProfileDto;
import app.web.dto.user.UserRegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetService budgetService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SavingService savingService;

    @InjectMocks
    UserService userService;

    @Test
    public void getById_whenUserIsNotFound_thenThrowUserNotFoundException(){
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(id));

        verify(userRepository).findById(id);
    }

    @Test
    public void getById_whenUserIsFound_thenReturnUserDto(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("annapetrova")
                .firstName("anna")
                .lastName("petrova")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.getById(user.getId());

        assertEquals("annapetrova", user.getUsername());
        assertEquals("anna", user.getFirstName());
        assertEquals("petrova", user.getLastName());

        verify(userRepository).findById(user.getId());
    }

    @Test
    public void getEntityById_whenUserIsNotFound_thenThrowUserNotFoundException(){
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getEntityById(id));

        verify(userRepository).findById(id);
    }

    @Test
    public void getEntityById_whenUserIsFound_thenReturnUser(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("annapetrova")
                .firstName("anna")
                .lastName("petrova")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.getEntityById(user.getId());

        assertEquals("annapetrova", user.getUsername());
        assertEquals("anna", user.getFirstName());
        assertEquals("petrova", user.getLastName());

        verify(userRepository).findById(user.getId());
    }

    @Test
    public void getAllUsers_whenUserIsWithAdminRole_thenReturnListOfAllUsers(){

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.ADMIN)
                .build();

        User user1 = User.builder()
                .username("teoman15")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user));

        assertEquals(2, userService.getAllUsers().size());
        verify(userRepository).findAll();
    }

    @Test
    public void deleteUser_whenUserIsNotFound_thenThrowUserNotFoundException(){
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));

        verify(userRepository).findById(id);
    }

    @Test
    public void deleteUser_whenUserIsAdmin_thenThrowAdminCannotBeDeletedException(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .userRole(UserRole.ADMIN)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(AdminCannotBeDeletedException.class, () -> userService.deleteUser(user.getId()));

        verify(userRepository).findById(user.getId());
    }

    @Test
    public void deleteUser_whenUserIsNotAdmin_thenThrowUserWithUserRoleDelete(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    public void updateProfileInformation_whenUserIsNotFound_thenThrowUserNotFoundException(){
        UUID id = UUID.randomUUID();
        UserProfileDto userProfileDto = UserProfileDto.builder().build();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateProfileInformation(id, userProfileDto));

        verify(userRepository).findById(id);
    }

    @Test
    public void updateProfileInformation_whenEmailAlreadyExist_thenThrowEmailAlreadyExistsException(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("old@gmail.com")
                .build();

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .email("anna@gmail.com")
                .build();

        UserProfileDto userProfileDto = UserProfileDto.builder()
                .email("anna@gmail.com")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user, user1));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateProfileInformation(user.getId(), userProfileDto));

        verify(userRepository).findById(user.getId());
        verify(userRepository).findAll();
    }

    @Test
    public void updateProfileInformation_whenEmailAlreadyNotExist_thenUserUpdateSuccess(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Pamir")
                .lastName("Ivanov")
                .email("pamir@gmail.com")
                .build();

        UserProfileDto userProfileDto = UserProfileDto.builder()
                .firstName("Teoman")
                .lastName("Petrov")
                .email("teo@gmail.com")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));

        userService.updateProfileInformation(user.getId(), userProfileDto);

        assertEquals("Teoman", user.getFirstName());
        assertEquals("Petrov", user.getLastName());
        assertEquals("teo@gmail.com", user.getEmail());

        verify(userRepository).findById(user.getId());
        verify(userRepository).findAll();
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void loadUserByUsername_whenUserNameIsNotFound_thenThrowUsernameNotFoundException(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(user.getUsername()));

        verify(userRepository).findByUsername(user.getUsername());

    }

    @Test
    public void loadUserByUsername_whenUserNameIsFound_thenReturnAuthenticationUserDetails(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("annapetrova")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        userService.loadUserByUsername(user.getUsername());

        assertEquals("annapetrova", user.getUsername());
        assertEquals(UserRole.USER, user.getUserRole());

        verify(userRepository).findByUsername(user.getUsername());

    }

    @Test
    public void updateRole_whenUserIsNotFound_thenThrowUserNotFoundException(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        UpdateUserRoleDto updateUserRoleDto = UpdateUserRoleDto.builder().build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateRole(user.getId(), updateUserRoleDto));

        verify(userRepository).findById(user.getId());
    }

    @Test
    public void updateRole_whenUserIsFound_thenUserRoleUpdate(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .userRole(UserRole.USER)
                .build();

        UpdateUserRoleDto updateUserRoleDto = UpdateUserRoleDto.builder()
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.updateRole(user.getId(), updateUserRoleDto);

        assertEquals(UserRole.ADMIN, user.getUserRole());

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
    }

    @Test
    public void existsByUsername_thenReturnTrueOrFalse(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("annapetrova")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.existsByUsername(any())).thenReturn(TRUE);
        userService.existsByUsername(user.getUsername());
        verify(userRepository).existsByUsername(user.getUsername());

    }

    @Test
    public void getAllUsersDetails_thenReturnAllUsersDetails(){


        when(userRepository.findAll()).thenReturn(List.of());
        when(budgetService.getAllBudgets()).thenReturn(List.of());
        when(transactionService.getAllTransactions()).thenReturn(List.of());
        when(savingService.getAllSavingGoals()).thenReturn(List.of());

        userService.getAllUsersDetails();

       verify(userRepository).findAll();
       verify(budgetService).getAllBudgets();
       verify(transactionService).getAllTransactions();
       verify(savingService).getAllSavingGoals();

    }

    @Test
    public void register_whenUserNameAlreadyExist_thenThrowUsernameAlreadyExistsException(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("annapetrova")
                .email("old@gmail.com")
                .build();


        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .username("annapetrova")
                .email("anna@gmail.com")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(userRegisterRequest));

        verify(userRepository).findByUsername(user.getUsername());

    }

    @Test
    public void register_whenEmailAlreadyExist_thenThrowEmailAlreadyExistsException(){

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .username("annapetrova")
                .email("anna@gmail.com")
                .build();

        when(userRepository.findByUsername("annapetrova")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("anna@gmail.com")).thenReturn(TRUE);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(userRegisterRequest));

        verify(userRepository).findByUsername("annapetrova");
        verify(userRepository).existsByEmail("anna@gmail.com");
    }


}
