package artem.CustomerSupportProject.RESTController;

import artem.CustomerSupportProject.Model.Response;
import artem.CustomerSupportProject.Service.LinesService;
import artem.CustomerSupportProject.Service.ResponsesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class Controller {
    private LinesService linesService;
    private ResponsesService responsesService;

    @GetMapping("/calculate-average")
    public Response response(@RequestParam String data) {
        return responsesService.calculateAverageWaitingTime(linesService.createLinesFromString(data));
    }
}
