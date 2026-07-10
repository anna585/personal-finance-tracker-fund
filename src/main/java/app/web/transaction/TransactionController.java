package app.web.transaction;

import app.model.dto.transaction.ExpenseTransactionRequest;
import app.model.dto.transaction.IncomeTransactionRequest;
import app.model.dto.transaction.TransactionRequest;
import app.model.dto.user.AuthenticationUserDetails;
import app.model.dto.user.UserDto;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;

    public TransactionController(UserService userService,
                                 TransactionService transactionService) {

        this.userService = userService;
        this.transactionService = transactionService;
    }

    public ModelAndView populateTransaction(ModelAndView modelAndView,
                                            TransactionRequest transactionRequest,
                                            UserDto user){

        return modelAndView
                .addObject("user", user)
                .addObject("transactionRequest", transactionRequest);

    }

    @GetMapping
    public ModelAndView getTransactions(@AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        return new ModelAndView("transactions")
                .addObject("user", user)
                .addObject("transactions",
                        transactionService.getAllTransactionsByUser(user.getId()));
    }

    @GetMapping("/add")
    public ModelAndView addTransaction(@AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        return populateTransaction(new ModelAndView("add-transaction"),
                TransactionRequest.builder().build(),
                user);
    }

    @PostMapping("/add")
    public ModelAndView postTransaction(@Valid @ModelAttribute TransactionRequest transactionRequest,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal AuthenticationUserDetails principal){
        UserDto user = userService.getById(principal.getId());

        if(bindingResult.hasErrors()){

            return populateTransaction(new ModelAndView("add-transaction"),
                   transactionRequest,
                    user);
        }

        try{
            transactionService.createNewTransaction(user.getId(), transactionRequest);
            return new ModelAndView("redirect:/transactions?success");

        }catch (RuntimeException ex){

            return new ModelAndView("transactions")
                    .addObject("user", user)
                    .addObject("transactions",
                    transactionService.getAllTransactionsByUser(user.getId()))
                    .addObject("transactionRequest", transactionRequest)
                    .addObject("error", ex.getMessage());
        }

    }

    @GetMapping("add/income")
    public ModelAndView getIncomeTransaction(@AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        IncomeTransactionRequest incomeTransactionRequest = IncomeTransactionRequest.builder().build();

        return new ModelAndView("add-income-transaction")
                .addObject("user", user)
                .addObject("incomeTransactionRequest", incomeTransactionRequest);
    }

    @PostMapping("/add/income")
    public ModelAndView postIncomeTransaction(@Valid @ModelAttribute IncomeTransactionRequest incomeTransactionRequest,
                                        BindingResult bindingResult,
                                              @AuthenticationPrincipal AuthenticationUserDetails principal){
        UserDto user = userService.getById(principal.getId());

        if(bindingResult.hasErrors()){

            return new ModelAndView("add-income-transaction")
                    .addObject("user", user)
                    .addObject("incomeTransactionRequest", incomeTransactionRequest);
        }

        transactionService.createIncomeTransaction(user.getId(), incomeTransactionRequest);
        return new ModelAndView("redirect:/transactions?success");
    }

    @GetMapping("add/expense")
    public ModelAndView getExpenseTransaction(@AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        ExpenseTransactionRequest expenseTransactionRequest = ExpenseTransactionRequest.builder().build();

        return new ModelAndView("add-expense-transaction")
                .addObject("user", user)
                .addObject("expenseTransactionRequest", expenseTransactionRequest);
    }

    @PostMapping("/add/expense")
    public ModelAndView postExpenseTransaction(@Valid @ModelAttribute ExpenseTransactionRequest expenseTransactionRequest,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());

        if(bindingResult.hasErrors()){

            return new ModelAndView("add-expense-transaction")
                    .addObject("user", user)
                    .addObject("expenseTransactionRequest", expenseTransactionRequest);
        }

        try{
            transactionService.createExpenseTransaction(user.getId(), expenseTransactionRequest);
            return new ModelAndView("redirect:/transactions?success");

        }catch (RuntimeException ex){

            return new ModelAndView("transactions")
                    .addObject("user", user)
                    .addObject("transactions",
                            transactionService.getAllTransactionsByUser(user.getId()))
                    .addObject("expenseTransactionRequest", expenseTransactionRequest)
                    .addObject("error", ex.getMessage());
        }

    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteTransaction(@PathVariable UUID id){

        transactionService.deleteTransaction(id);
        return new ModelAndView("redirect:/transactions");

    }
}
