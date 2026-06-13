package app.mapper.category;

import app.model.dto.category.CategoryDto;
import app.model.entities.category.Category;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryMapper {

    public CategoryDto toDto(Category category){

        if(category == null){
            return null;
        }

        return CategoryDto.builder()
                .id(category.getId())
                .categoryType(category.getCategoryType())
                .build();
    }
}
