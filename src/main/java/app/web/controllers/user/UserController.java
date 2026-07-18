package app.web.controllers.user;

import app.web.dto.user.UserDto;
import app.web.dto.user.UserLoginRequest;
import app.web.dto.user.UserRegisterRequest;
import app.services.budget.BudgetService;
import app.services.saving.SavingService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final SavingService savingService;


    @GetMapping("/login")
    public ModelAndView getLogin() {

        UserLoginRequest userLoginRequest = UserLoginRequest.builder().build();

        return new ModelAndView("login")
                .addObject("userLoginRequest", userLoginRequest);
    }

    @GetMapping("/register")
    public ModelAndView getRegister() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder().build();

        return new ModelAndView("register")
                .addObject("userRegisterRequest", userRegisterRequest);
    }

    @PostMapping("/register")
    public ModelAndView postRegister(@Valid @ModelAttribute UserRegisterRequest userRegisterRequest,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ModelAndView("register");
        }

            userService.register(userRegisterRequest);
            return new ModelAndView("redirect:/login");
    }

    @GetMapping("/admin/users")
    public ModelAndView getUsers(){

        return new ModelAndView("users")
                .addObject("users", userService.getAllUsers());
    }


    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable UUID id){

        UserDto user = userService.getById(id);

        if(user == null){
            return new ModelAndView("redirect:/admin/users");
        }

        try {
            userService.deleteUser(id);
            return new ModelAndView("redirect:/admin/users");

        } catch (RuntimeException e) {

            return new ModelAndView("users")
                    .addObject("error", e.getMessage());
        }


    }
}
