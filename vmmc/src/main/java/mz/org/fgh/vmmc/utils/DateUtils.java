package mz.org.fgh.vmmc.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private long getSecondsBetweenTwoDates(LocalDateTime startDate, LocalDateTime thruDate) {
	return startDate.until(thruDate, ChronoUnit.SECONDS);

    }

}
