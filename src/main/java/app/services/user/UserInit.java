package app.services.user;


import app.web.dto.user.UserDto;
import app.web.dto.user.UserRegisterRequest;
import app.model.entities.user.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserInit implements CommandLineRunner {


    @Value("${app.admin.password}")
    private String adminPassword;

    private final UserService userService;

    public UserInit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<UserDto> users = userService.getAllUsers();
        if (!users.isEmpty()) {
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

        log.info("Default user created with username [%s] and password [%s].".formatted(
                userRegisterRequest.getUsername(), userRegisterRequest.getPassword()));
    }
}
