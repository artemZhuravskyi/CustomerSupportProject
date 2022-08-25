package artem.CustomerSupportProject.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Response {
    List<String> averageWaitingTime;
}
