package app.model.dto.user;

import app.model.entities.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRegisterRequest {


    @NotBlank(message = "Firstname must not be blank!")
    private String firstName;
    @NotBlank(message = "Lastname must not be blank!")
    private String lastName;
    @Size(min = 8, message = "Username must be least than 8 characters!")
    private String username;
    @Size(min = 8, message = "Password must be least than 8 characters!")
    private String password;
    @NotBlank(message = "Email must not be blank!")
    private String email;
    private UserRole userRole;

}
