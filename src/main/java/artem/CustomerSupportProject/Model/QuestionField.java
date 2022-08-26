package artem.CustomerSupportProject.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionField {
    String category;
    String subCategory;
    String subSubCategory;



}
