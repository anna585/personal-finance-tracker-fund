package app.web.transaction;

import app.model.dto.transaction.ExpenseTransactionRequest;
import app.model.dto.transaction.IncomeTransactionRequest;
import app.model.dto.transaction.TransactionRequest;
import app.model.entities.user.User;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
                                            User user){

        return modelAndView
                .addObject("user", user)
                .addObject("transactionRequest", transactionRequest);

    }

    @GetMapping
    public ModelAndView getTransactions(HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        return populateTransaction(new ModelAndView("transactions"),
                TransactionRequest.builder().build(),
                user);
    }

    @GetMapping("/add")
    public ModelAndView addTransaction(HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        return populateTransaction(new ModelAndView("add-transaction"),
                TransactionRequest.builder().build(),
                user);
    }

    @PostMapping("/add")
    public ModelAndView postTransaction(@Valid @ModelAttribute TransactionRequest transactionRequest,
                                        BindingResult bindingResult,
                                        HttpSession httpSession){
        User user = userService.getCurrentUser(httpSession);

        if(bindingResult.hasErrors()){

            return populateTransaction(new ModelAndView("add-transaction"),
                    TransactionRequest.builder().build(),
                    user);
        }

        transactionService.createNewTransaction(user.getId(), transactionRequest);

        return new ModelAndView("redirect:/transactions");
    }

    @GetMapping("add/income")
    public ModelAndView getIncomeTransaction(HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        IncomeTransactionRequest incomeTransactionRequest = IncomeTransactionRequest.builder().build();

        return new ModelAndView("add-income-transaction")
                .addObject("user", user)
                .addObject("incomeTransactionRequest", incomeTransactionRequest);
    }

    @PostMapping("/add/income")
    public ModelAndView postIncomeTransaction(@Valid @ModelAttribute IncomeTransactionRequest incomeTransactionRequest,
                                        BindingResult bindingResult,
                                        HttpSession httpSession){
        User user = userService.getCurrentUser(httpSession);

        if(bindingResult.hasErrors()){

            return new ModelAndView("add-income-transaction")
                    .addObject("user", user)
                    .addObject("incomeTransactionRequest", incomeTransactionRequest);
        }

        transactionService.createIncomeTransaction(user.getId(), incomeTransactionRequest);
        return new ModelAndView("redirect:/transactions");
    }

    @GetMapping("add/expense")
    public ModelAndView getExpenseTransaction(HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        ExpenseTransactionRequest expenseTransactionRequest = ExpenseTransactionRequest.builder().build();

        return new ModelAndView("add-expense-transaction")
                .addObject("user", user)
                .addObject("expenseTransactionRequest", expenseTransactionRequest);
    }

    @PostMapping("/add/expense")
    public ModelAndView postExpenseTransaction(@Valid @ModelAttribute ExpenseTransactionRequest expenseTransactionRequest,
                                              BindingResult bindingResult,
                                              HttpSession httpSession){

        User user = userService.getCurrentUser(httpSession);

        if(bindingResult.hasErrors()){

            return new ModelAndView("add-expense-transaction")
                    .addObject("user", user)
                    .addObject("expenseTransactionRequest", expenseTransactionRequest);
        }

        transactionService.createExpenseTransaction(user.getId(), expenseTransactionRequest);

        return new ModelAndView("redirect:/transactions");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteTransaction(@PathVariable UUID id){

        transactionService.deleteTransaction(id);
        return new ModelAndView("redirect:/transactions");

    }
}
