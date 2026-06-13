package app.web.transaction;

import app.model.dto.transaction.ExpenseTransaction;
import app.model.dto.transaction.IncomeTransactionRequest;
import app.model.dto.transaction.TransactionRequest;
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
@RequestMapping("/transactions")
public class TransactionController {

    private final UserService userService;

    public TransactionController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getTransactions(HttpSession httpSession){

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transactions");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView addTransaction(HttpSession httpSession){

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);
        TransactionRequest transactionRequest = TransactionRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-transaction");
        modelAndView.addObject("user", user);
        modelAndView.addObject("transactionRequest", transactionRequest);

        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView postTransaction(@Valid @ModelAttribute TransactionRequest transactionRequest,
                                        BindingResult bindingResult,
                                        HttpSession httpSession){
        UUID id = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(id);
        if(bindingResult.hasErrors()){

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-transaction");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        userService.createNewTransaction(id, transactionRequest);

        return new ModelAndView("redirect:/transactions");
    }

    @GetMapping("add/income")
    public ModelAndView getIncomeTransaction(HttpSession httpSession){

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);
        IncomeTransactionRequest incomeTransactionRequest = IncomeTransactionRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-income-transaction");
        modelAndView.addObject("user", user);
        modelAndView.addObject("incomeTransactionRequest", incomeTransactionRequest);


        return modelAndView;
    }

    @PostMapping("/add/income")
    public ModelAndView postIncomeTransaction(@Valid @ModelAttribute IncomeTransactionRequest incomeTransactionRequest,
                                        BindingResult bindingResult,
                                        HttpSession httpSession){
        UUID id = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(id);

        if(bindingResult.hasErrors()){

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-income-transaction");
            modelAndView.addObject("user", user);

            return modelAndView;
        }

        userService.createIncomeTransaction(id, incomeTransactionRequest);

        return new ModelAndView("redirect:/transactions");
    }

    @GetMapping("add/expense")
    public ModelAndView getExpenseTransaction(HttpSession httpSession){

        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(userId);
        ExpenseTransaction expenseTransaction = ExpenseTransaction.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-expense-transaction");
        modelAndView.addObject("user", user);
        modelAndView.addObject("expenseTransaction", expenseTransaction);


        return modelAndView;
    }

    @PostMapping("/add/expense")
    public ModelAndView postExpenseTransaction(@Valid @ModelAttribute ExpenseTransaction expenseTransaction,
                                              BindingResult bindingResult,
                                              HttpSession httpSession){

        UUID id = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.getById(id);

        if(bindingResult.hasErrors()){

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-expense-transaction");
            modelAndView.addObject("user", user);

            return modelAndView;
        }

        userService.createExpenseTransaction(id, expenseTransaction);

        return new ModelAndView("redirect:/transactions");
    }

}
