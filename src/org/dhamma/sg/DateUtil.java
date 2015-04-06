package org.dhamma.sg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	public static String formatDate(Date aDate) {
		DateFormat df = new SimpleDateFormat("yyMMdd hh:mm");
		String formattedDate = df.format(aDate.getTime());
		
		return formattedDate;
	}

}
