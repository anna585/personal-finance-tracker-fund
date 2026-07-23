package app.web.controllers.transaction;

import app.web.dto.transaction.TransactionDto;
import app.web.dto.transaction.TransactionRequest;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;

    private ModelAndView populateTransaction(ModelAndView modelAndView,
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

            transactionService.createNewTransaction(user.getId(), transactionRequest);
            return new ModelAndView("redirect:/transactions?success");

    }


    @PostMapping("/{id}/delete")
    public ModelAndView deleteTransaction(@PathVariable UUID id){

        transactionService.deleteTransaction(id);
        return new ModelAndView("redirect:/transactions");

    }

    @GetMapping("/{id}/edit")
    public ModelAndView updateTransaction(@PathVariable UUID id) {

        TransactionDto transaction = transactionService.getTransactionById(id);

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .type(transaction.getType())
                .category(transaction.getCategory())
                .amount(transaction.getAmount())
                .build();

        return new ModelAndView("edit-transaction")
                .addObject("transactionRequest", transactionRequest)
                .addObject("transactionId", id);
    }

    @PostMapping("/{id}/edit")
    public ModelAndView getEditTransaction(
            @PathVariable UUID id,
            @Valid @ModelAttribute TransactionRequest transactionRequest,
            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ModelAndView("edit-transaction")
                    .addObject("transactionRequest", transactionRequest)
                    .addObject("transactionId", id);
        }

        transactionService.updateTransaction(id, transactionRequest);

        return new ModelAndView("redirect:/transactions");
    }
}
