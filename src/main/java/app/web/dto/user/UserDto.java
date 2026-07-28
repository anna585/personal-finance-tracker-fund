package app.web.dto.user;

import app.model.entities.user.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class UserDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole userRole;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

}
