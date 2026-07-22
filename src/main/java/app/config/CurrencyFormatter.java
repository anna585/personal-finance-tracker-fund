package app.config;


import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Component("currencyFormatter")
public class CurrencyFormatter {

    public String format(BigDecimal amount) {

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(Locale.US);

        formatter.setCurrency(Currency.getInstance("EUR"));

        return formatter.format(amount);
    }
}
