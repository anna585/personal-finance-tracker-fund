package app.web.controllers.budget;

import app.web.dto.budget.BudgetDto;
import app.web.dto.budget.MonthlyBudgetRequest;
import app.web.dto.user.AuthenticationUserDetails;
import app.model.entities.user.User;
import app.services.budget.BudgetService;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final UserService userService;
    private final BudgetService budgetService;
    private final TransactionService transactionService;

     private ModelAndView createBudgetView(ModelAndView modelAndView,
                                             MonthlyBudgetRequest request,
                                             User user){

        BudgetDto budget = budgetService.getCurrentBudget(user);

        return modelAndView
                .addObject("monthlyBudgetRequest", request)
                .addObject("budget", budget)
                .addObject("user", user)
                .addObject("spent", transactionService.getTotalSpentByUser(user))
                .addObject("remaining", budgetService.calculateRemainingBudget(user));

    }

    @GetMapping
    public ModelAndView getBudget(@AuthenticationPrincipal AuthenticationUserDetails principal){

        User user = userService.getEntityById(principal.getId());

       return createBudgetView(new ModelAndView("budget"),
               MonthlyBudgetRequest.builder().build(),
               user);
    }

    @PostMapping
    public ModelAndView updateBudget(@Valid @ModelAttribute MonthlyBudgetRequest monthlyBudgetRequest,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal AuthenticationUserDetails principal){
        User user = userService.getEntityById(principal.getId());


        if(bindingResult.hasErrors()){

            return createBudgetView(new ModelAndView("budget"),
                    monthlyBudgetRequest,
                    user);
        }

        budgetService.updateMonthlyBudget(user,monthlyBudgetRequest);
        return new ModelAndView("redirect:/budget?success");
    }

}
