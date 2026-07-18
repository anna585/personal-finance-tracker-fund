package app.mapper.transaction;

import app.web.dto.transaction.TransactionDto;
import app.model.entities.transaction.Transaction;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransactionMapper {

    public static TransactionDto toDto(Transaction transaction){


        if(transaction == null){
            return null;
        }

        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .date(transaction.getDate())
                .category(transaction.getCategoryType())
                .build();

    }

}
