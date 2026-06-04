package app.model.dto.transaction;

import app.model.entities.category.Category;
import app.model.entities.user.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class TransactionDto {

    private UUID id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private User user;
    private Category category;
}
