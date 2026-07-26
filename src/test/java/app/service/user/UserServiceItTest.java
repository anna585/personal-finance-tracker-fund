package app.service.user;


import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.budget.BudgetRepository;
import app.repositories.user.UserRepository;
import app.services.user.UserService;
import app.web.dto.user.UserDto;
import app.web.dto.user.UserRegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

import static app.unit.user.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class UserServiceItTest {

    @Autowired
    private UserService underTest;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Test
    public void testRegisterUser_shouldRegisterUser_withDefaultBudget(){

        UserRegisterRequest userRegisterRequest = getUserRegisterRequest();

        UserDto userDto = underTest.register(userRegisterRequest);

        UUID budgetId = userDto.getBudgets().get(0).getId();

        User user = userRepository.findById(userDto.getId()).get();
        Budget budget = budgetRepository.findById(budgetId).get();


        assertEquals(userRegisterRequest.getUsername(), user.getUsername());
        assertEquals(userRegisterRequest.getFirstName(), user.getFirstName());
        assertEquals(userRegisterRequest.getLastName(), user.getLastName());
        assertEquals(userRegisterRequest.getEmail(), user.getEmail());
        assertEquals(UserRole.USER, user.getUserRole());
        assertEquals(1, user.getBudgets().size());
        assertEquals(user.getId(), budget.getUser().getId());

    }
}
