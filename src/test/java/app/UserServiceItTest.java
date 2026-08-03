package app;


import app.model.entities.user.User;
import app.model.entities.user.UserRole;
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


import static app.web.unit.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class UserServiceItTest {

    @Autowired
    private UserService underTest;

    @Autowired
    private  UserRepository userRepository;

    @Test
    public void testRegisterUser_shouldRegisterUser_withDefaultBudget(){

        UserRegisterRequest userRegisterRequest = getUserRegisterRequest();

        UserDto userDto = underTest.register(userRegisterRequest);

        User user = userRepository.findById(userDto.getId()).get();


        assertEquals(userRegisterRequest.getUsername(), user.getUsername());
        assertEquals(userRegisterRequest.getFirstName(), user.getFirstName());
        assertEquals(userRegisterRequest.getLastName(), user.getLastName());
        assertEquals(userRegisterRequest.getEmail(), user.getEmail());
        assertEquals(UserRole.USER, user.getUserRole());
        assertEquals(1, user.getBudgets().size());
        assertEquals(1, userRepository.count());
        assertNotNull(user.getPassword());
        assertNotNull(user.getCreatedOn());

    }
}
