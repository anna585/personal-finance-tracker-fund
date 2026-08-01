package app.web.controllers.transaction;

import app.config.CurrencyFormatter;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.services.transaction.TransactionService;
import app.services.user.UserService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.transaction.TransactionRequest;
import app.web.dto.user.AuthenticationUserDetails;
import app.web.dto.user.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.math.BigDecimal;
import java.util.List;

import static app.web.controllers.transaction.TransactionFactoryDto.getTransaction;
import static app.web.controllers.transaction.TransactionFactoryDto.getTransactionDto;
import static app.web.controllers.user.UserFactoryDto.getUserAdminDto;
import static app.web.controllers.user.UserFactoryDto.getUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(TransactionController.class)
@Import(CurrencyFormatter.class)
public class TransactionControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    MockMvc mockMvc;


    @Test
    public void getTransactions_thenViewTransactionAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        Transaction transaction = getTransaction();
        List<Transaction> transactions = List.of(transaction, transaction);

        when(userService.getById(user.getId())).thenReturn(userDto);
        when(transactionService.getAllTransactionsByUser(user.getId())).thenReturn(transactions);

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.get("/transactions")
                .with(user(user));

        mockMvc.perform(requestMock)
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attributeExists("transactions"));

        verify(userService).getById(user.getId());
        verify(transactionService).getAllTransactionsByUser(userDto.getId());
    }

    @Test
    public void addTransaction_thenViewAddTransactionAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        TransactionRequest transactionRequest = TransactionRequest.builder().build();

        when(userService.getById(user.getId())).thenReturn(userDto);

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.get("/transactions/add")
                .with(user(user));

        mockMvc.perform(requestMock)
                .andExpect(status().isOk())
                .andExpect(view().name("add-transaction"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("transactionRequest", transactionRequest));

        verify(userService).getById(user.getId());
    }

    @Test
    public void postTransaction_thenRedirectViewTransactionAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        TransactionDto transactionDto = getTransactionDto();
        TransactionRequest transactionRequest = TransactionRequest.builder().build();

        when(userService.getById(user.getId())).thenReturn(userDto);
        when(transactionService.createNewTransaction(user.getId(), transactionRequest)).thenReturn(transactionDto);

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/transactions/add")
                .with(user(user))
                .with(csrf())
                .formField("amount", String.valueOf(BigDecimal.valueOf(50.00)))
                .formField("type", String.valueOf(TransactionType.INCOME))
                .formField("category", String.valueOf(CategoryType.RETURN_AMOUNT));

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions?success"));

        verify(userService).getById(user.getId());
        verify(transactionService).createNewTransaction(eq(userDto.getId()), any(TransactionRequest.class));
    }

    @Test
    public void postTransaction_whenWrongCompleteRequest_thenViewTransactionAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        UserDto userDto = getUserDto();
        TransactionDto transactionDto = getTransactionDto();
        TransactionRequest transactionRequest = TransactionRequest.builder().build();

        when(userService.getById(user.getId())).thenReturn(userDto);
        when(transactionService.createNewTransaction(user.getId(), transactionRequest)).thenReturn(transactionDto);

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/transactions/add")
                .with(user(user))
                .with(csrf())
                .formField("amount", "")
                .formField("type", "")
                .formField("category", "");

        mockMvc.perform(requestMock)
                .andExpect(status().isOk())
                .andExpect(view().name("add-transaction"));

        verify(userService).getById(user.getId());
        verify(transactionService, never()).createNewTransaction(eq(userDto.getId()), any(TransactionRequest.class));
    }

    @Test
    public void updateTransaction_thenViewEditTransactionAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();

        TransactionDto transactionDto = getTransactionDto();
        TransactionRequest expected = TransactionRequest.builder()
                .type(transactionDto.getType())
                .category(transactionDto.getCategory())
                .amount(transactionDto.getAmount())
                .build();

        when(transactionService.getTransactionById(transactionDto.getId())).thenReturn(transactionDto);

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.get("/transactions/{id}/edit", transactionDto.getId())
                .with(user(user));

        mockMvc.perform(requestMock)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-transaction"))
                .andExpect(model().attribute("transactionId", transactionDto.getId()))
                .andExpect(model().attribute("transactionRequest", expected));

        verify(transactionService).getTransactionById(transactionDto.getId());
    }

    @Test
    public void postEditTransaction_thenRedirectViewTransactionsAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        TransactionDto transactionDto = getTransactionDto();

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/transactions/{id}/edit", transactionDto.getId())
                .with(user(user))
                .with(csrf())
                .formField("amount", String.valueOf(BigDecimal.valueOf(100.00)))
                .formField("type", String.valueOf(TransactionType.EXPENSE))
                .formField("category", String.valueOf(CategoryType.HEALTH));

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));

        verify(transactionService).updateTransaction(eq(transactionDto.getId()) , any(TransactionRequest.class));
    }

    @Test
    public void postEditTransaction_thenViewEditTransactionsAndStatusIs2xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        TransactionDto transactionDto = getTransactionDto();

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/transactions/{id}/edit", transactionDto.getId())
                .with(user(user))
                .with(csrf())
                .formField("amount", "")
                .formField("type", String.valueOf(TransactionType.EXPENSE))
                .formField("category", String.valueOf(CategoryType.HEALTH));

        mockMvc.perform(requestMock)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-transaction"))
                .andExpect(model().attribute("transactionId", transactionDto.getId()));

        verify(transactionService, never()).updateTransaction(eq(transactionDto.getId()) , any(TransactionRequest.class));
    }

    @Test
    public void deleteTransaction_thenRedirectViewTransactionsAndStatusIs3xx() throws Exception{

        AuthenticationUserDetails user = getUserAdminDto();
        TransactionDto transactionDto = getTransactionDto();

        MockHttpServletRequestBuilder requestMock = MockMvcRequestBuilders.post("/transactions/{id}/delete", transactionDto.getId())
                .with(user(user))
                .with(csrf());

        mockMvc.perform(requestMock)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));

        verify(transactionService).deleteTransaction(eq(transactionDto.getId()));
    }
}
