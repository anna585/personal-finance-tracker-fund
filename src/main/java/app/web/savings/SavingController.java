package app.web.savings;

import app.model.dto.saving.SavingRequest;
import app.model.dto.user.UserDto;
import app.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/savings")
public class SavingController {

    private final UserService userService;


    public SavingController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ModelAndView savingsGoals(HttpSession httpSession) {

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);
        SavingRequest savingRequest = SavingRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("savings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("savingRequest", savingRequest);

        return modelAndView;

    }

    @GetMapping("/add")
    public ModelAndView getAddingGoals(HttpSession httpSession){

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);
        SavingRequest savingRequest = SavingRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-savings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("savingRequest", savingRequest);

        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView postAddingGoals(@Valid @ModelAttribute SavingRequest savingRequest,
                                        HttpSession httpSession,
                                        BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-savings");

            return modelAndView;
        }

        UUID userId = (UUID) httpSession.getAttribute("user_id");

        userService.createGoals(userId, savingRequest);

        return new ModelAndView("redirect:/savings");

    }
}
