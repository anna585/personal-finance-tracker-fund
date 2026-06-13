package app.web.user;

import app.model.dto.budget.BudgetDto;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.services.budget.BudgetService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    public UserController(UserService userService, TransactionService transactionService, BudgetService budgetService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession httpSession) {

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);


        BigDecimal income = transactionService.getTotalIncomeByUser(userId);
        BigDecimal expenses = transactionService.getTotalSpentByUser(userId);


        BigDecimal currentBudget = income.subtract(expenses);


        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("dashboard");
        modelAndView.addObject("user", user);
        modelAndView.addObject("income", income);
        modelAndView.addObject("expenses", expenses);
        modelAndView.addObject("currentBudget", currentBudget);


        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView getLogin() {

        UserLoginRequest userLoginRequest = UserLoginRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("userLoginRequest", userLoginRequest);

        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView postLogin(@Valid @ModelAttribute UserLoginRequest userLoginRequest,
                                  BindingResult bindingResult,
                                  HttpSession httpSession){

        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("login");

            return modelAndView;
        }

        UserDto user = userService.login(userLoginRequest);
        httpSession.setAttribute("user_id", user.getId());

        return new ModelAndView("redirect:/dashboard");

    }

    @GetMapping("/register")
    public ModelAndView getRegister() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("userRegisterRequest", userRegisterRequest);

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView postRegister(@Valid @ModelAttribute UserRegisterRequest userRegisterRequest,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("register");

            return modelAndView;
        }

        userService.register(userRegisterRequest);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/logout")
    public ModelAndView getLogoutPage(HttpSession httpSession){

        httpSession.invalidate();

        return new ModelAndView("redirect:/");
    }
}
