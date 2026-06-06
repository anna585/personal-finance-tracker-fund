package app.mapper.transaction;

import app.model.dto.transaction.TransactionDto;
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
                .user(transaction.getUser())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .date(transaction.getDate())
                .category(transaction.getCategory())
                .build();

    }
}
