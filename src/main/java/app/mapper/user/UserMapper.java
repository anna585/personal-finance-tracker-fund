package app.mapper.user;

import app.web.dto.user.UserDto;
import app.web.dto.user.UserRegisterRequest;
import app.model.entities.user.User;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class UserMapper {

    public static User toUserEntity(UserRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null){
            return null;
        }

        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .userRole(userRegisterRequest.getUserRole())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public static UserDto toUserDto(User user){

        if(user == null){
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }
}
