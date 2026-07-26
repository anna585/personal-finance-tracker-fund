package app.controller.user;

import app.services.user.UserService;
import app.web.controllers.user.UserController;
import app.web.dto.user.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static app.unit.user.UserFactoryDto.getUserAdminDto;
import static app.unit.user.UserFactoryDto.getUserDto;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

        MockHttpServletRequestBuilder request = get("/admin/users").with(user(getUserAdminDto()));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("users", users));

    }
}
