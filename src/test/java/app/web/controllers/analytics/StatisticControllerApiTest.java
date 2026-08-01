package app.web.controllers.analytics;


import app.analytics.dto.StatisticResponse;
import app.analytics.service.StatisticService;
import app.config.CurrencyFormatter;
import app.services.user.UserService;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static app.web.controllers.user.UserFactoryDto.getUserDto;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(StatisticController.class)
@Import(CurrencyFormatter.class)
public class StatisticControllerApiTest {

    @MockitoBean
    private StatisticService statisticService;

    @MockitoBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;


    @Test
    public void getStatistics_thenViewStatisticsAnsStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        List<UserDto> userDtoList = List.of(userDto);
        StatisticResponse statisticResponse = StatisticResponse.builder()
                .totalBudget(1)
                .totalSavings(3)
                .totalUsers(1)
                .totalTransactions(5)
                .build();


        when(userService.getAllUsers()).thenReturn(userDtoList);
        when(statisticService.getAllUsersForStatistic()).thenReturn(statisticResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/admin/statistics")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("statistics"))
                .andExpect(model().attribute("statistics", statisticResponse))
                .andExpect(model().attribute("users", userDtoList));

        verify(userService).getAllUsers();
        verify(statisticService).getAllUsersForStatistic();
    }
}
