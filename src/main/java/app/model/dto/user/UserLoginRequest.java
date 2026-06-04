package app.model.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserLoginRequest {

    @Size(min = 8, message = "Username must be least than 8 characters!")
    private String username;
    @Size(min = 8, message = "Password must be least than 8 characters!")
    private String password;
}
