package fr.fredos.dvdtheque.common.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	public static Date clearDate(Date dateToClear) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToClear);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}
}
