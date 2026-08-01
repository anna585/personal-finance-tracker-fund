package app.web.controllers.saving;


import app.config.CurrencyFormatter;
import app.model.entities.saving.SavingGoal;
import app.services.saving.SavingService;
import app.services.user.UserService;
import app.web.controllers.savings.SavingController;
import app.web.dto.saving.EditSavingRequest;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.saving.SavingRequest;
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


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static app.web.controllers.saving.SavingGoalFactoryDto.getSavingGoals;
import static app.web.controllers.saving.SavingGoalFactoryDto.getSavingGoalsDto;
import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static app.web.controllers.user.UserFactoryDto.getUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(SavingController.class)
@Import(CurrencyFormatter.class)
public class SavingControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SavingService savingService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getSavings_thenViewSavingAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        SavingGoal goal = getSavingGoals();
        List<SavingGoal> list = List.of(goal, goal, goal);

        when(userService.getById(user.getId())).thenReturn(userDto);
        when(savingService.getAllSavingGoalsByUser(user.getId())).thenReturn(list);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/savings")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("savings"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attributeExists("savingGoals"));

        verify(userService).getById(eq(user.getId()));
        verify(savingService).getAllSavingGoalsByUser(eq(userDto.getId()));
    }

    @Test
    public void addSavings_thenViewSavingAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        SavingRequest savingRequest = SavingRequest.builder().build();


        when(userService.getById(user.getId())).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/savings/add")
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("add-savings"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("savingRequest" , savingRequest));

        verify(userService).getById(eq(user.getId()));
    }

    @Test
    public void addSavingGoal_thenRedirectViewSavingSuccessAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        SavingRequest savingRequest = SavingRequest.builder().build();


        when(userService.getById(user.getId())).thenReturn(userDto);
        when(savingService.createGoal(user.getId(), savingRequest)).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/savings/add")
                .with(user(user))
                .with(csrf())
                .formField("goalName", "Vacantion")
                .formField("targetAmount", String.valueOf(BigDecimal.valueOf(5000.00)))
                .formField("currentAmount", String.valueOf(BigDecimal.valueOf(50.00)))
                .formField("targetDate", String.valueOf(LocalDate.now()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/savings?success"));

        verify(userService).getById(eq(user.getId()));
        verify(savingService).createGoal(eq(userDto.getId()), any(SavingRequest.class));
    }

    @Test
    public void addSavingGoal_whenWrongCompleteRequest_thenViewAddSavingsAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        SavingRequest savingRequest = SavingRequest.builder().build();


        when(userService.getById(user.getId())).thenReturn(userDto);
        when(savingService.createGoal(user.getId(), savingRequest)).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/savings/add")
                .with(user(user))
                .with(csrf())
                .formField("goalName", "")
                .formField("targetAmount", "")
                .formField("currentAmount", "")
                .formField("targetDate", String.valueOf(LocalDate.now()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("add-savings"));

        verify(userService).getById(eq(user.getId()));
        verify(savingService, never()).createGoal(eq(userDto.getId()), any(SavingRequest.class));
    }

    @Test
    public void getEditSavingGoal_thenViewEditSavingAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        SavingGoalsDto goalsDto = getSavingGoalsDto();
        EditSavingRequest editSavingRequest = EditSavingRequest.builder()
                .goalName(goalsDto.getGoalName())
                .targetDate(goalsDto.getTargetDate())
                .currentAmount(goalsDto.getCurrentAmount())
                .targetAmount(goalsDto.getTargetAmount())
                .build();

        when(savingService.getSavingGoalById(goalsDto.getId())).thenReturn(goalsDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/savings/{id}/edit", goalsDto.getId())
                .with(user(user));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-savings"))
                .andExpect(model().attribute("goalId", goalsDto.getId()))
                .andExpect(model().attribute("editSavingRequest" , editSavingRequest));

        verify(savingService).getSavingGoalById(eq(goalsDto.getId()));
    }

    @Test
    public void updateSavingGoal_thenRedirectSavingViewAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        SavingGoalsDto goalsDto = getSavingGoalsDto();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/savings/{id}/edit", goalsDto.getId())
                .with(user(user))
                .with(csrf())
                .formField("goalName", "Winter Holiday")
                .formField("targetAmount", String.valueOf(BigDecimal.valueOf(6500.00)))
                .formField("currentAmount", String.valueOf(BigDecimal.valueOf(200.00)))
                .formField("targetDate", String.valueOf(LocalDate.now().plusDays(60)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/savings"));

        verify(savingService).updateSavingGoal(eq(goalsDto.getId()), any(EditSavingRequest.class));
    }

    @Test
    public void updateSavingGoal_thenViewEditSavingAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        SavingGoalsDto goalsDto = getSavingGoalsDto();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/savings/{id}/edit", goalsDto.getId())
                .with(user(user))
                .with(csrf())
                .formField("goalName", "")
                .formField("targetAmount", "")
                .formField("currentAmount","")
                .formField("targetDate", String.valueOf(LocalDate.now().minusDays(1)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-savings"))
                .andExpect(model().attribute("goalId", goalsDto.getId()));

        verify(savingService, never()).updateSavingGoal(eq(goalsDto.getId()), any(EditSavingRequest.class));
    }

    @Test
    public void deleteSavingGoal_thenRedirectViewSavingsAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        SavingGoalsDto goalsDto = getSavingGoalsDto();

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/savings/{id}/delete", goalsDto.getId())
                .with(user(user))
                .with(csrf());

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/savings"));

        verify(savingService).deleteSavingGoal(eq(goalsDto.getId()));
    }
}
