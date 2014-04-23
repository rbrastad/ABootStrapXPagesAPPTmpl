package no.rbrastad.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
	
	private Date dateOnCreate = null;
	
	
	public DateUtil() {
		dateOnCreate = getDateNow();
	}
	
	public Date getDateNow() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
		calendar.setTimeInMillis(System.currentTimeMillis());
		return calendar.getTime();
	}
	
	public Date getDateOnCreate(){	
		return dateOnCreate;
	}
	
	public Calendar getCalendarNow(){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(getDateNow());
		
		return calendar;
		
	}

}
