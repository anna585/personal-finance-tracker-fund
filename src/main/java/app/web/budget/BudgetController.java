package app.web.budget;

import app.model.dto.budget.BudgetDto;
import app.model.dto.budget.MonthlyBudgetRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    private final UserService userService;
    private final BudgetService budgetService;
    private final TransactionService transactionService;

    public BudgetController(UserService userService,
                            BudgetService budgetService,
                            TransactionService transactionService) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.transactionService = transactionService;
    }

    public ModelAndView populateBudgetPage(ModelAndView modelAndView,
                                           MonthlyBudgetRequest monthlyBudgetRequest,
                                           User user,
                                           UUID userId){

        BudgetDto budget = budgetService.getCurrentBudget(user);

        if(budget == null){
            throw new RuntimeException("Please enter monthly budget!");
        }

        BigDecimal spent = transactionService.getTotalSpentByUser(userId);
        BigDecimal remaining = budgetService.calculateRemainingBudget(user);

        return modelAndView
                .addObject("monthlyBudgetRequest", monthlyBudgetRequest)
                .addObject("budget", budget)
                .addObject("user", user)
                .addObject("spent", spent)
                .addObject("remaining", remaining);

    }

    @GetMapping
    public ModelAndView getMonthlyBudgetRequest(HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

       return populateBudgetPage(new ModelAndView("budget"),
               MonthlyBudgetRequest.builder().build(),
               user,
               user.getId());
    }

    @PostMapping
    public ModelAndView postMonthlyBudgetRequest(@Valid @ModelAttribute MonthlyBudgetRequest monthlyBudgetRequest,
                                                 BindingResult bindingResult,
                                                 HttpSession httpSession){
    User user = userService.getCurrentUser(httpSession);


        if(bindingResult.hasErrors()){

            return populateBudgetPage(new ModelAndView("budget"),
                    monthlyBudgetRequest,
                    user,
                    user.getId());
        }

        budgetService.updateMonthlyBudget(user,monthlyBudgetRequest);
        return new ModelAndView("redirect:/budget?success");
    }

}
