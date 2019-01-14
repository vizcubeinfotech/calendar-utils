package com.vizcube.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class CalendarHelper {
	
	/**
	 * TODO : enum for CalendarWeekdays
	 * MONDAY("Monday", 1), TUESDAY("Tuesday", 2), WEDNESDAY("Wednesday", 3), THURSDAY("Thursday", 4), FRIDAY("Friday", 5), SATURDAY("Saturday",
			6), SUNDAY("Sunday", 7)
		
		TODO : test cases
	 * */

	/**
	 * Method will compute and return the next occurance of weekly repeated event on or just after the contextDate.
	 * If next occurance event not found in range of startDate & endDate then return null.
	 * @param contextDate
	 * @param startDate
	 * @param endDate
	 * @param weekdays
	 * @param every
	 * @return
	 */
	public static LocalDate getNextOccuranceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, List<DayOfWeek> weekdays, Integer every) {
		LocalDate actualstartDate = null;
		int ind=0;
		for (DayOfWeek dayOfWeek : weekdays) {
			if (startDate.getDayOfWeek().compareTo(dayOfWeek) <=0 ) {
				actualstartDate = startDate.plusDays(dayOfWeek.compareTo(startDate.getDayOfWeek()));
				break;
			}
			ind++;
		}
		if (actualstartDate == null) {
			actualstartDate = startDate.minusDays(startDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every);
			ind=0;
		}

		int skipDayCount = contextDate.compareTo(actualstartDate);
		if (skipDayCount == 0) {
			return actualstartDate;
		} else if (skipDayCount < 0 || endDate.compareTo(contextDate) < 0) {
			return null;
		}
		int skipWeeks = (skipDayCount/(7*every));
		LocalDate nextDate = actualstartDate.plusWeeks(skipWeeks*every);
		if (nextDate.compareTo(contextDate) == 0) {
			return nextDate;
		}
//		int difWithFirstOfWeek = nextDate.getDayOfWeek().compareTo(weekdays.get(0));
//		if (difWithFirstOfWeek < 0) {
//			nextDate = nextDate.minusDays(difWithFirstOfWeek);
//		}
//		int ind=0;
		while (endDate.compareTo(nextDate) >= 0) {
			if (nextDate.compareTo(contextDate) >= 0) {
				return nextDate;
			}
			ind++;
			if (ind >= weekdays.size()) {
				ind=0;
				nextDate = nextDate.minusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every);
			} else {
				nextDate = nextDate.plusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(ind)));
			}
		}
		return null;
	}

	public static LocalDate getNextOccuranceDate(LocalDate contextDate, LocalDate startDate, List<DayOfWeek> weekdays, Integer every, Integer occurance) {

		LocalDate actualstartDate = startDate;
		int skipDayCount = contextDate.compareTo(actualstartDate);
		if (skipDayCount < 0) {
			return null;
		}
		//TODO: InProgress
		return null;
	}
	
//	public static LocalDate getLastOccuranceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, List<CalendarWeekdays> weekdays, Integer every, Integer occurance) {
//		return null;
//	}
}
