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

    public BudgetController(UserService userService, BudgetService budgetService, TransactionService transactionService) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public ModelAndView getMonthlyBudgetRequest(HttpSession httpSession){

        MonthlyBudgetRequest monthlyBudgetRequest = MonthlyBudgetRequest.builder().build();

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        User user = userService.getEntityById(userId);
        BudgetDto budget = budgetService.getCurrentBudget(user);

        BigDecimal spent = transactionService.getTotalSpentByUser(userId);

        BigDecimal remaining =budget.getMonthlyLimit().subtract(spent);

        return new ModelAndView("budget")
                .addObject("monthlyBudgetRequest", monthlyBudgetRequest)
                .addObject("budget", budget)
                .addObject("user", user)
                .addObject("spent", spent)
                .addObject("remaining", remaining);
    }

    @PostMapping
    public ModelAndView postMonthlyBudgetRequest(@Valid @ModelAttribute MonthlyBudgetRequest monthlyBudgetRequest,
                                                 BindingResult bindingResult,
                                                 HttpSession httpSession){

        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("budget");
            return modelAndView;
        }

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        User user = userService.getEntityById(userId);

        budgetService.updateMonthlyBudget(user,monthlyBudgetRequest);

        return new ModelAndView("redirect:/budget");
    }

}
