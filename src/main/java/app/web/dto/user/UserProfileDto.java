package app.web.dto.user;

import app.model.entities.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileDto {

    private UUID id;
    private String username;
    @NotBlank(message = "Firstname must not be blank!")
    private String firstName;
    @NotBlank(message = "Lastname must not be blank!")
    private String lastName;
    @NotBlank(message = "Email must not be blank!")
    private String email;
    @Size(min = 8, message = "Password must be least than 8 characters!")
    private String password;
    private UserRole userRole;

}
