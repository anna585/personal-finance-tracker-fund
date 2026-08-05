package app.services.user;


import app.aspect.LogAction;
import app.web.dto.user.UserRegisterRequest;
import app.model.entities.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class UserInit implements CommandLineRunner {


    @Value("${app.admin.password}")
    private String adminPassword;

    private final UserService userService;


    @LogAction("Create default user with role ADMIN. ")
    @Override
    public void run(String... args) throws Exception {

        if (userService.existsByUsername("AnnaPetrova")) {
            return;
        }

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .firstName("Anna")
                .lastName("Petrova")
                .username("AnnaPetrova")
                .password(adminPassword)
                .email("anna.angova@gmail.com")
                .userRole(UserRole.ADMIN)
                .build();

        userService.register(userRegisterRequest);
    }
}
