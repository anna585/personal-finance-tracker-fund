package app.web.dto.user;

import app.model.entities.user.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateUserRoleDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    @NotNull(message = "Role is required")
    private UserRole role;
}
