package app.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRegisterRequest {


    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Size(min = 8, message = "Username must be least than 8 characters!")
    private String username;
    @Size(min = 8, message = "Username must be least than 8 characters!")
    private String password;
    @NotBlank
    private String email;

}
