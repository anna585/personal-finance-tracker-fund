package app.web.controllers.savings;

import app.web.dto.saving.EditSavingRequest;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.saving.SavingRequest;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import app.services.saving.SavingService;
import app.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/savings")
@RequiredArgsConstructor
public class SavingController {

    private final UserService userService;
    private final SavingService savingService;

    private ModelAndView populateSavingGoals(ModelAndView modelAndView,
                                            SavingRequest savingRequest,
                                            UserDto user){

        return modelAndView
                .addObject("user", user)
                .addObject("savingRequest", savingRequest);

    }

    @GetMapping
    public ModelAndView getSavings(@AuthenticationPrincipal AuthenticationUserDetails principal) {

        UserDto user = userService.getById(principal.getId());

        return new ModelAndView("savings")
                .addObject("user", user)
                .addObject(
                        "savingGoals",
                        savingService.getAllSavingGoalsByUser(user.getId())
                );

    }

    @GetMapping("/add")
    public ModelAndView getAddSavingGoal(@AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        return populateSavingGoals(new ModelAndView("add-savings"),
                SavingRequest.builder().build(),
                user);
    }

    @PostMapping("/add")
    public ModelAndView addSavingGoal(@Valid @ModelAttribute SavingRequest savingRequest,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        if(bindingResult.hasErrors()){
            return populateSavingGoals(new ModelAndView("add-savings"),
                    savingRequest,
                    user);
        }

        savingService.createGoal(user.getId(), savingRequest);

        return new ModelAndView("redirect:/savings?success)")
                .addObject("savingRequest", savingRequest);
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteSavingGoal(@PathVariable UUID id){

        savingService.deleteSavingGoal(id);
        return new ModelAndView("redirect:/savings");

    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditSavingGoal(@PathVariable UUID id) {

        SavingGoalsDto goal = savingService.getSavingGoalById(id);

        EditSavingRequest editSavingRequest = EditSavingRequest.builder()
                .goalName(goal.getGoalName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .targetDate(goal.getTargetDate())
                .build();

        return new ModelAndView("edit-savings")
                .addObject("editSavingRequest", editSavingRequest)
                .addObject("goalId", id);
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateSavingGoal(
            @PathVariable UUID id,
            @Valid @ModelAttribute EditSavingRequest editSavingRequest,
            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ModelAndView("edit-savings")
                   .addObject("editSavingRequest", editSavingRequest)
                    .addObject("goalId", id);
        }

        savingService.updateSavingGoal(id, editSavingRequest);

        return new ModelAndView("redirect:/savings");
    }
}
