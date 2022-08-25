package artem.CustomerSupportProject.Service;

import artem.CustomerSupportProject.Model.Line;
import artem.CustomerSupportProject.Model.QueryLine;
import artem.CustomerSupportProject.Model.Response;
import artem.CustomerSupportProject.Model.WaitingTimeline;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
public class ResponsesService {

    private static final String STAR = "*";
    private static final String DASH = "-";
    private static final String EMPTY = "";

    public Response calculateAverageWaitingTime(List<Line> lines) {
        List<String> calculatedAverages = new ArrayList<>();
        List<QueryLine> queryLines = new ArrayList<>();
        lines.forEach(line -> {
            if (line instanceof QueryLine) {
                queryLines.add((QueryLine) line);
            } else {
                calculatedAverages.add(calculateAverage((WaitingTimeline) line, queryLines));
            }
        });
        return new Response(calculatedAverages);
    }

    private String calculateAverage(WaitingTimeline waitingTimeline, List<QueryLine> queryLines) {

        OptionalDouble average = queryLines.stream()
                .filter(queryLine -> isQueryDateBetweenWaitingDates(queryLine, waitingTimeline))
                .filter(queryLine -> isServiceMatches(queryLine, waitingTimeline))
                .filter(queryLine -> isQuestionsMatches(queryLine, waitingTimeline))
                .filter(queryLine -> isResponseTypesMatches(queryLine, waitingTimeline))
                .mapToInt(QueryLine::getMinutes)
                .average();
        return average.isPresent() ? EMPTY + (average.getAsDouble() % 1 == 0 ? ((Long) Math.round(average.getAsDouble())).toString() : average.getAsDouble()) : DASH;
    }

    private boolean isServiceMatches(QueryLine queryLine, WaitingTimeline waitingTimeline) {
        String queryLineServiceId = queryLine.getServiceId();
        String queryLineVariationId = queryLine.getVariationId();
        String waitingTimelineServiceId = waitingTimeline.getServiceId();
        String waitingTimelineVariationId = waitingTimeline.getVariationId();

        if (STAR.equals(waitingTimelineServiceId)) {
            return true;
        }
        if (waitingTimelineServiceId.equals(queryLineServiceId)) {
            return isCategoriesMatches(waitingTimelineVariationId, queryLineVariationId);
        }
        return false;
    }

    private boolean isQuestionsMatches(QueryLine queryLine, WaitingTimeline waitingTimeline) {
        String queryLineQuestionTypeId = queryLine.getQuestionTypeId();
        String queryLineCategoryId = queryLine.getCategoryId();
        String queryLineSubCategoryId = queryLine.getSubCategoryId();
        String waitingTimelineQuestionTypeId = waitingTimeline.getQuestionTypeId();
        String waitingTimelineCategoryId = waitingTimeline.getCategoryId();
        String waitingTimelineSubCategoryId = waitingTimeline.getSubCategoryId();

        if (STAR.equals(waitingTimelineQuestionTypeId)) {
            return true;
        }
        if (waitingTimelineQuestionTypeId.equals(queryLineQuestionTypeId)
                && isCategoriesMatches(waitingTimelineCategoryId, queryLineCategoryId)) {
            return isCategoriesMatches(waitingTimelineSubCategoryId, queryLineSubCategoryId);
        }
        return false;
    }

    private boolean isCategoriesMatches(String waitingTimelineCategory, String queryLineCategory) {
        return STAR.equals(waitingTimelineCategory)
                || waitingTimelineCategory == null
                || waitingTimelineCategory.equals(queryLineCategory);
    }

    private boolean isResponseTypesMatches(QueryLine queryLine, WaitingTimeline waitingTimeline) {
        String queryLineResponseType = queryLine.getResponseType();
        String waitingLineResponseType = waitingTimeline.getResponseType();
        return queryLineResponseType.equals(waitingLineResponseType) || queryLineResponseType.equals(STAR);
    }

    private boolean isQueryDateBetweenWaitingDates(QueryLine queryLine, WaitingTimeline waitingTimeline) {
        LocalDate queryLineDate = queryLine.getDate();
        LocalDate firstWaitingLineDate = waitingTimeline.getDate();
        LocalDate lastWaitingLineDate = Optional.ofNullable(waitingTimeline.getDateTo()).orElse(firstWaitingLineDate);
        return queryLineDate.isAfter(firstWaitingLineDate.minusDays(1))
                && queryLineDate.isBefore(lastWaitingLineDate.plusDays(1));
    }
}
