package mz.org.fgh.vmmc.utils;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class DateUtils {

       public static long getSecondsBetweenTwoDates(LocalDateTime startDate, LocalDateTime thruDate) {
	     return startDate.until(thruDate, ChronoUnit.SECONDS);

       }

       public static String formatDateByMonthAndDay(int day, int month) throws DateTimeException {
	     LocalDate date = LocalDate.now();
	     if (day == 31 && month == 12) {
		   date = LocalDate.now().plusMonths(1);
	     }
	     return LocalDate.of(date.getYear(), month, day).atStartOfDay(ZoneOffset.systemDefault()).format(DateTimeFormatter.ISO_INSTANT);
       }

       public static String getSimpleDateFormat(String dateInput) {
	     if (StringUtils.isNotBlank(dateInput)) {
		   LocalDateTime ldti = LocalDateTime.ofInstant(Instant.parse(dateInput), ZoneOffset.systemDefault());
		   return ldti.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	     } else
		   return "";

       }

       public static String getAppointmentsMonth() {
	     StringBuilder sb = new StringBuilder();

	     LocalDate localDateTime = LocalDate.now();
	     for (int i = 0; i <= 3; i++) {
		   Month month = localDateTime.plusMonths(i).getMonth();
		   String output = month.getDisplayName(TextStyle.FULL, new Locale("pt"));
		   sb.append(month.getValue()).append(". ").append(output).append("\n");
	     }
	     return sb.toString();

       }

       public static String getMonthByMonthId(int monthNumber) {
	     return Month.of(monthNumber).getDisplayName(TextStyle.FULL, new Locale("pt"));
       }

       public static boolean isValidDate(int day, int month) {
	     try {
		   if (month >= LocalDate.now().getMonthValue() && month <= LocalDate.now().getMonthValue() + 3) {
			 DateUtils.formatDateByMonthAndDay(day, month);
			 return true;
		   }
		   return false;
	     } catch (DateTimeException exception) {
		   return false;
	     }
       }

       public static boolean isValidDay(String dayInput) {
	     if (NumberUtils.isDigits(dayInput)) {
		   long day = Long.parseLong(dayInput);
		   return (day > 0 && day < 32) ? true : false;
	     } else {
		   return false;
	     }
       }

}
