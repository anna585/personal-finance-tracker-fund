package app.web.unit;

import app.model.entities.user.UserRole;
import app.web.dto.user.UserRegisterRequest;


public class UserFactory {

    public static UserRegisterRequest getUserRegisterRequest(){
        return UserRegisterRequest.builder()
                .username("annapetrova")
                .password("12345678")
                .firstName("anna")
                .lastName("petrova")
                .email("anna.petrova@gmail.com")
                .userRole(UserRole.USER)
                .build( );
    }

}
