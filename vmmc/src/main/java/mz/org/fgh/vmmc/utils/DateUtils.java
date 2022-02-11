package mz.org.fgh.vmmc.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static long getSecondsBetweenTwoDates(LocalDateTime startDate, LocalDateTime thruDate) {
	return startDate.until(thruDate, ChronoUnit.SECONDS);

    }

    public static String formatDateByMonthAndDay(int day, int month) {
	return LocalDate.of(LocalDate.now().getYear(), month, day).atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

    }

}
