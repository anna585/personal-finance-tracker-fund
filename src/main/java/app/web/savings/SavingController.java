package app.web.savings;

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

        return populateSavingGoals(new ModelAndView("savings"),
        SavingRequest.builder().build(),
        user);

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

        savingService.createGoals(user.getId(), savingRequest);
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
}
