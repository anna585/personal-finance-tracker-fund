package app.web.user;

import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entities.user.User;
import app.services.budget.BudgetService;
import app.services.saving.SavingService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final SavingService savingService;

    public UserController(UserService userService,
                          TransactionService transactionService,
                          BudgetService budgetService,
                          SavingService savingService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.savingService = savingService;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession httpSession) {

        User user = userService.getCurrentUser(httpSession);

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        BigDecimal income = transactionService.getTotalIncomeByUser(user.getId());
        BigDecimal expenses = transactionService.getTotalSpentByUser(user.getId());

        BigDecimal currentBalance =
                transactionService.calculateCurrentBalance(user.getId());

        return new ModelAndView("dashboard")
                .addObject("user", user)
                .addObject("income", income)
                .addObject("expenses", expenses)
                .addObject("currentBalance", currentBalance);
    }

    @GetMapping("/login")
    public ModelAndView getLogin() {

        UserLoginRequest userLoginRequest = UserLoginRequest.builder().build();

        return new ModelAndView("login")
                .addObject("userLoginRequest", userLoginRequest);
    }

    @PostMapping("/login")
    public ModelAndView postLogin(@Valid @ModelAttribute UserLoginRequest userLoginRequest,
                                  BindingResult bindingResult,
                                  HttpSession httpSession){

        if(bindingResult.hasErrors()){
            return new ModelAndView("login");
        }

        try {
            UserDto user = userService.login(userLoginRequest);
            httpSession.setAttribute("user_id", user.getId());

            return new ModelAndView("redirect:/dashboard");

        } catch (RuntimeException e) {

            return new ModelAndView("login")
                    .addObject("userLoginRequest", userLoginRequest)
                    .addObject("error", e.getMessage());
        }

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

    @GetMapping("/logout")
    public ModelAndView getLogoutPage(HttpSession httpSession){

        httpSession.invalidate();
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/admin/users")
    public ModelAndView getUsers(){

        return new ModelAndView("users")
                .addObject("users", userService.getAllUsers());
    }

    @GetMapping("/admin/reports")
    public ModelAndView getReports(){

        Long countOfUsers = userService.getCountOfUsers();
        Long countOfTransactions = transactionService.getCountOfTransaction();
        Long countOfBudgets = budgetService.getCountOfBudgets();
        Long countOfSavingGoals = savingService.getCountOfSavingGoals();

        return new ModelAndView("reports")
                .addObject("users", userService.getAllUsers())
                .addObject("countOfUsers", countOfUsers)
                .addObject("countOfTransactions", countOfTransactions)
                .addObject("countOfBudgets", countOfBudgets)
                .addObject("countOfSavingGoals", countOfSavingGoals);
    }

    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable UUID id){

        UserDto user = userService.getById(id);

        if(user == null){
            return new ModelAndView("redirect:/admin/users");
        }

        userService.deleteUser(id);
        return new ModelAndView("redirect:/admin/users");
    }
}
