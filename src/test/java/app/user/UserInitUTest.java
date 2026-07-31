package app.user;

import app.services.user.UserInit;
import app.services.user.UserService;
import app.web.dto.user.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInitUTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserInit userInit;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userInit, "adminPassword", "123456");
    }

    @Test
    void run_whenAdminExists_shouldNotRegister() throws Exception {

        when(userService.existsByUsername("AnnaPetrova")).thenReturn(true);

        userInit.run();

        verify(userService).existsByUsername("AnnaPetrova");
        verify(userService, never()).register(any());
    }

    @Test
    void run_whenAdminDoesNotExist_shouldRegister() throws Exception {

        when(userService.existsByUsername("AnnaPetrova")).thenReturn(false);

        userInit.run();

        verify(userService).existsByUsername("AnnaPetrova");

        verify(userService).register(any(UserRegisterRequest.class));
    }
}
