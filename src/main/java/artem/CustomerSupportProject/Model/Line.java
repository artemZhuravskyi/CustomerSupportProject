package artem.CustomerSupportProject.Model;

import lombok.Data;

import java.time.LocalDate;

@Data
public abstract class Line {
    String serviceId;
    String variationId;
    String questionTypeId;
    String categoryId;
    String subCategoryId;
    String responseType;
    LocalDate date;
}
