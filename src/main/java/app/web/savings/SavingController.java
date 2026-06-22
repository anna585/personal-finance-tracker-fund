package app.web.savings;

import app.model.dto.saving.EditSavingRequest;
import app.model.dto.saving.SavingGoalsDto;
import app.model.dto.saving.SavingRequest;
import app.model.entities.user.User;
import app.services.saving.SavingService;
import app.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/savings")
public class SavingController {

    private final UserService userService;
    private final SavingService savingService;


    public SavingController(UserService userService,
                            SavingService savingService) {
        this.userService = userService;
        this.savingService = savingService;
    }

    public ModelAndView populateSavingGoals(ModelAndView modelAndView,
                                            SavingRequest savingRequest,
                                            User user){

        return modelAndView
                .addObject("user", user)
                .addObject("savingRequest", savingRequest);

    }

    @GetMapping
    public ModelAndView savingsGoals(HttpSession httpSession) {

        User user = userService.getCurrentUser(httpSession);

        return new ModelAndView("savings")
                .addObject("user", user)
                .addObject(
                        "savingGoals",
                        savingService.getAllSavingGoalsByUser(user.getId())
                );

    }

    @GetMapping("/add")
    public ModelAndView getAddingGoals(HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        return populateSavingGoals(new ModelAndView("add-savings"),
                SavingRequest.builder().build(),
                user);
    }

    @PostMapping("/add")
    public ModelAndView postAddingGoals(@Valid @ModelAttribute SavingRequest savingRequest,
                                        BindingResult bindingResult,
                                        HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        if(bindingResult.hasErrors()){
            return populateSavingGoals(new ModelAndView("add-savings"),
                    savingRequest,
                    user);
        }

        try {
            savingService.createGoals(user.getId(), savingRequest);

        } catch (IllegalArgumentException ex) {

            return new ModelAndView("savings")
                    .addObject("user", user)
                    .addObject("savingGoals",
                    savingService.getAllSavingGoalsByUser(user.getId()))
                    .addObject("errorMessage", ex.getMessage());
        }


        return new ModelAndView("redirect:/savings");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteSavingGoal(@PathVariable UUID id, HttpSession httpSession){

        SavingGoalsDto savingGoal = savingService.getSavingGoalById(id);

        if(savingGoal == null){
            throw new RuntimeException("The Saving goal is not found!");
        }

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
    public ModelAndView editSavingGoals(
            @PathVariable UUID id,
            @Valid @ModelAttribute EditSavingRequest editSavingRequest,
            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ModelAndView("edit-savings")
                   .addObject("editSavingRequest", editSavingRequest)
                    .addObject("goalId", id);
        }

        savingService.updateSavingGoals(id, editSavingRequest);

        return new ModelAndView("redirect:/savings");
    }
}
