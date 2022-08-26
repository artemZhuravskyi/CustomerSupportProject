package artem.CustomerSupportProject.Service;

import artem.CustomerSupportProject.Model.Line;
import artem.CustomerSupportProject.Model.QueryLine;
import artem.CustomerSupportProject.Model.QuestionField;
import artem.CustomerSupportProject.Model.WaitingTimeline;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LinesService {

    private final static String C_TYPE = "C";
    private final static String DATE_PATTERN = "d.MM.yyyy";
    private final static String END_OF_LINE = "\\n";
    private final static String SPACE = " ";
    private final static String DOT = "\\.";
    private final static String DASH = "-";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public List<Line> createLinesFromString(String query) {
        String[] lines = query.split(END_OF_LINE);
        return Arrays.stream(lines)
                .skip(1)
                .map(this::createLineFromString)
                .collect(Collectors.toList());
    }

    private QuestionField separateFields(String stringFields) {
        String[] strings = new String[3];
        AtomicInteger index = new AtomicInteger();
        Arrays.stream(stringFields.split(DOT)).forEach(string -> strings[index.getAndIncrement()] = string);

        return QuestionField.builder()
                .category(strings[0])
                .subCategory(strings[1])
                .subSubCategory(strings[2])
                .build();
    }


    private Line createLineFromString(String lineToFill) {
        String[] queryFields = lineToFill.split(SPACE);
        QuestionField serviceFields = separateFields(queryFields[1]);
        QuestionField questionFields = separateFields(queryFields[2]);
        Line line;
        List<LocalDate> dates = separateDates(queryFields[4]);

        if (C_TYPE.equals(queryFields[0])) {
            line = new QueryLine();
            ((QueryLine) line).setMinutes(Integer.parseInt(queryFields[5]));

        } else {
            line = new WaitingTimeline();
            if (dates.size() == 2) {
                ((WaitingTimeline) line).setDateTo(dates.get(1));
            }
        }
        line.setServiceId(serviceFields.getCategory());
        line.setVariationId(serviceFields.getSubCategory());
        line.setQuestionTypeId(questionFields.getCategory());
        line.setCategoryId(questionFields.getSubCategory());
        line.setSubCategoryId(questionFields.getSubSubCategory());
        line.setResponseType(queryFields[3]);
        line.setDate(dates.get(0));
        return line;
    }

    private List<LocalDate> separateDates(String dates) {
        String[] strings = dates.split(DASH);

        return Arrays.stream(strings).map(stringDate -> LocalDate.parse(stringDate, formatter)).collect(Collectors.toList());
    }
}
