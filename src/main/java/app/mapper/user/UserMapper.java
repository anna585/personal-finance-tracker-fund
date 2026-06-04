package app.mapper.user;

import app.model.dto.user.UserRegisterRequest;
import app.model.entities.user.User;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class UserMapper {

    public static User toUserEntity(UserRegisterRequest userRegisterRequest){

        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
