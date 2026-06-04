package app.model.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRegisterRequest {


    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

}
