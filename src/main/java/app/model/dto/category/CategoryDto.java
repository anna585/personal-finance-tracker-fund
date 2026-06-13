package app.model.dto.category;

import app.model.entities.category.CategoryType;
import app.model.entities.transaction.Transaction;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CategoryDto {

    private UUID id;
    private CategoryType categoryType;
    private Transaction transaction;
}
