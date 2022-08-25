package artem.CustomerSupportProject.Model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WaitingTimeline extends Line {

    LocalDate dateTo;
}
