package app.web.controllers.user;

import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import lombok.experimental.UtilityClass;


import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UserFactoryDto {

    public static UserDto getUserDto(){

        return UserDto.builder()
                .id(UUID.randomUUID())
                .firstName("Anna")
                .lastName("Angova")
                .email("ani@abv.bg")
                .userRole(UserRole.USER)
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static AuthenticationUserDetails getUserAdminDto(){

        return AuthenticationUserDetails.builder()
                .id(UUID.randomUUID())
                .username("AdminUser")
                .role(UserRole.ADMIN)
                .build();
    }

    public static User getUser() {

        return User.builder()
                .id(UUID.randomUUID())
                .firstName("Anna")
                .lastName("Angova")
                .email("ani@abv.bg")
                .userRole(UserRole.USER)
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
    }
}
