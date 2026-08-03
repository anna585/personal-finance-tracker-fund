package app.web.controllers.user;

import app.services.user.UserService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import app.web.dto.user.UserProfileDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;

import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static app.web.controllers.user.UserFactoryDto.getUserDto;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAllUsers_whenUserIsAdmin_thenReturnStatus200andViewUsers() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of(getUserDto(), getUserDto(), getUserDto()));
        List<UserDto> users = userService.getAllUsers();

        MockHttpServletRequestBuilder request = get("/admin/users")
                .with(user(getUserAdminDto()));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("users", users));

    }

    @Test
    public void postRegister_shouldReturn302RedirectAndRedirectToLogin_thenRedirectToLogin() throws Exception{

        MockHttpServletRequestBuilder request = post("/register")
                .formField("firstName", "Anna")
                .formField("lastName", "Petrova")
                .formField("username", "annapetrova")
                .formField("password", "12345678")
                .formField("email", "anna@gmail.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService).register(any());

    }

    @Test
    public void postRegister_shouldReturn200AndShowRegisterViewAndRegisterServiceMethodNeverInvoked() throws Exception{

        MockHttpServletRequestBuilder request = post("/register")
                .formField("firstName", "Anna")
                .formField("lastName", "Petrova")
                .formField("username", "anna")
                .formField("password", "1234")
                .formField("email", "anna@gmail.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
        verifyNoInteractions(userService);
    }

    @Test
    public void postRequestToChangeUserStatus_fromUserAdmin_shouldRedirectToUserPageWithSuccessMessage() throws Exception{

        UUID id = UUID.randomUUID();

        AuthenticationUserDetails authentication = getUserAdminDto();
        MockHttpServletRequestBuilder request = post("/admin/users/{id}/update-role", id)
                .with(user(authentication))
                .with(csrf())
                .param("id", id.toString())
                .param("role", "ADMIN");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/" + id + "/update-role?success"));

        verify(userService).updateRole(eq(id), any());
    }

    @Test
    public void postRequestToChangeUserStatus_fromUserNormal_shouldReturn403StatusCodeAndViewIsForbidden() throws Exception{

        UUID id = UUID.randomUUID();

        UserDto user = getUserDto();
        MockHttpServletRequestBuilder request = post("/admin/users/{id}/update-role", id)
                .with(user(String.valueOf(user)))
                .with(csrf());

         mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verify(userService, never()).updateRole(eq(id), any());
    }

    @Test
    public void postRequestToChangeUserStatus_shouldReturn200AndShowUpdateRoleViewAndUpdateRoleMethodNeverInvoked() throws Exception{
        UUID id = UUID.randomUUID();

        AuthenticationUserDetails authentication = getUserAdminDto();
        MockHttpServletRequestBuilder request = post("/admin/users/{id}/update-role", id)
                .with(user(authentication))
                .with(csrf())
                .param("id", String.valueOf(id))
                .param("role", "");


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("update-role"))
                .andExpect(model().attributeHasFieldErrors("updateUserRoleDto", "role"));
        verifyNoInteractions(userService);
    }

    @Test
    public void getRegister_shouldRegisterUser() throws Exception{

        MockHttpServletRequestBuilder request = get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegisterRequest"));
    }

    @Test
    public void getProfile_shouldViewProfileAndReturnStatus200() throws Exception{

        AuthenticationUserDetails authentication = getUserAdminDto();

        when(userService.getById(authentication.getId())).thenReturn(getUserDto());

        MockHttpServletRequestBuilder request = get("/profile")
                .with(user(authentication));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userProfileDto"));

        verify(userService).getById(authentication.getId());
    }

    @Test
    public void menageRole_whenUserIsAdmin_thenReturnToViewUpdateRole() throws Exception{

        AuthenticationUserDetails authentication = getUserAdminDto();

        when(userService.getById(authentication.getId())).thenReturn(getUserDto());

        MockHttpServletRequestBuilder request = get("/admin/users/{id}/update-role", authentication.getId())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("update-role"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("updateUserRoleDto"));

        verify(userService).getById(authentication.getId());

    }

    @Test
    public void menageRole_whenUserIsNotAdmin_shouldReturn403StatusCodeAndViewIsForbidden() throws Exception{

        UserDto user = getUserDto();

        when(userService.getById(user.getId())).thenReturn(getUserDto());

        MockHttpServletRequestBuilder request = get("/admin/users/{id}/update-role", user.getId())
                .with(user(String.valueOf(user)))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verify(userService, never()).getById(eq(user.getId()));

    }

    @Test
    public void updateProfile_whenUserProfileDtoIsCorrect_thenUserProfileUpdateSuccessAndRedirectToViewProfileSuccess() throws Exception{
        AuthenticationUserDetails authentication = getUserAdminDto();

        when(userService.updateProfileInformation(eq(authentication.getId()), any(UserProfileDto.class))).thenReturn(getUserDto());

        MockHttpServletRequestBuilder request = post("/profile")
                .with(user(authentication))
                .with(csrf())
                .param("firstName", "Anna")
                .param("lastName", "Petrova")
                .param("email", "anna@gmail.com")
                .param("password", "12345678");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));

        verify(userService).updateProfileInformation(eq(authentication.getId()), any(UserProfileDto.class));

    }

    @Test
    public void updateProfile_whenUserProfileDtoIsNotCorrect_thenUserProfileCanNotUpdateAndViewProfile() throws Exception{
        AuthenticationUserDetails authentication = getUserAdminDto();

        when(userService.getById(authentication.getId()))
                .thenReturn(getUserDto());

        MockHttpServletRequestBuilder request = post("/profile")
                .with(user(authentication))
                .with(csrf())
                .param("firstName", "")
                .param("lastName", "Petrova")
                .param("email", "anna@gmail.com")
                .param("password", "123456");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeHasErrors("userProfileDto"));

    }

    @Test
    public void deleteUser_thenRedirectViewAdminUsersAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/users/{id}/delete", user.getId())
                .with(user(user))
                .with(csrf());

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?delete"));

        verify(userService).deleteUser(eq(user.getId()));
    }

    @Test
    public void getLogin_thenViewLoginAndStatusIs2xx() throws Exception{
        AuthenticationUserDetails user = getUserAdminDto();

        MockHttpServletRequestBuilder requestBuilder = get("/login")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userLoginRequest"));
    }

    @Test
    public void getLogin_whenRequestIsWrong_thenViewLoginWithErrorMessageAndStatusIs2xx() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = get("/login")
                .formField("error", "true");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "Invalid username or password."))
                .andExpect(model().attributeExists("userLoginRequest"));
    }
}
