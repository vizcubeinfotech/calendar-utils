package com.vizcube.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CalendarHelper {

	/**
	 * Method will compute and return the next occurance of weekly repeated event on
	 * or just after the contextDate. If next occurance event not found in range of
	 * startDate & endDate then return null.
	 * 
	 * @param contextDate
	 * @param startDate
	 * @param endDate
	 * @param weekdays
	 * @param every
	 * @return
	 */
	public static LocalDate getNextOccuranceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, List<DayOfWeek> weekdays, Integer every,
			Integer occurance) {
		validateInput(startDate, weekdays, every);

		boolean withEndDate = false;
		boolean isNeverEnds = false;
		boolean endsByOccurrence = false;
		if (null == endDate) {
			if (null == occurance) {
				throw new IllegalArgumentException("Select valid event ending scenario.");
			}
			isNeverEnds = occurance == -1;
			endsByOccurrence = occurance != -1;
		} else if (null != occurance) {
			throw new IllegalArgumentException("Select valid event ending scenario.");
		} else {
			withEndDate = true;
		}

		LocalDate actualstartDate = null;
		int ind = 0;
		for (DayOfWeek dayOfWeek : weekdays) {
			if (startDate.getDayOfWeek().compareTo(dayOfWeek) <= 0) {
				actualstartDate = startDate.plusDays(dayOfWeek.compareTo(startDate.getDayOfWeek()));
				break;
			}
			ind++;
		}
		if (actualstartDate == null) {
			actualstartDate = startDate.minusDays(startDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every);
			ind = 0;
		}

		if (startDate.until(contextDate, ChronoUnit.DAYS) == 0) {
			return actualstartDate;
		}

		int skipDayCount = (int) actualstartDate.until(contextDate, ChronoUnit.DAYS);
		int skipWeeks = (skipDayCount / (7 * every.intValue()));
		int skipOccurrences = skipWeeks * weekdays.size();
		int remainingOccurrences = 0;
		if (endsByOccurrence) {
			remainingOccurrences = occurance - skipOccurrences;
		}
		if (skipDayCount == 0) {
			return actualstartDate;
		}
		if (skipDayCount < 0) {
			return null;
		}
		if (withEndDate && contextDate.until(endDate, ChronoUnit.DAYS) < 0) {
			return null;
		}
		if (endsByOccurrence && remainingOccurrences <= 0) {
			return null;
		}
		LocalDate nextDate = actualstartDate.plusWeeks(skipWeeks * every.intValue());
		if (nextDate.until(contextDate, ChronoUnit.DAYS) == 0) {
			return nextDate;
		}

		while (isNeverEnds || (withEndDate && nextDate.until(endDate, ChronoUnit.DAYS) >= 0) || (endsByOccurrence && remainingOccurrences > 0)) {
			if (contextDate.until(nextDate, ChronoUnit.DAYS) >= 0) {
				return nextDate;
			} else if (endsByOccurrence) {
				remainingOccurrences--;
			}
			ind++;
			if (ind >= weekdays.size()) {
				ind = 0;
				nextDate = nextDate.minusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every.intValue());
			} else {
				nextDate = nextDate.plusDays(weekdays.get(ind).compareTo(nextDate.getDayOfWeek()));
			}
		}
		return null;
	}

	public static LocalDate getLastOccuranceDate(LocalDate startDate, LocalDate endDate, List<DayOfWeek> weekdays, Integer every, Integer occurance) {
		validateInput(startDate, weekdays, every);

		boolean withEndDate = false;
		boolean isNeverEnds = false;
		boolean endsByOccurrence = false;
		if (null == endDate) {
			if (null == occurance) {
				throw new IllegalArgumentException("Select valid event ending scenario.");
			}
			isNeverEnds = occurance == -1;
			endsByOccurrence = occurance != -1;
			if (isNeverEnds) {
				return null;
			}
		} else if (null != occurance) {
			throw new IllegalArgumentException("Select valid event ending scenario.");
		} else {
			withEndDate = true;
		}

		LocalDate actualstartDate = null;
		int ind = 0;
		for (DayOfWeek dayOfWeek : weekdays) {
			if (startDate.getDayOfWeek().compareTo(dayOfWeek) <= 0) {
				actualstartDate = startDate.plusDays(dayOfWeek.compareTo(startDate.getDayOfWeek()));
				break;
			}
			ind++;
		}
		if (actualstartDate == null) {
			actualstartDate = startDate.minusDays(startDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every);
			ind = 0;
		}
		if (endsByOccurrence) {
			int occurrenceFactor = occurance / weekdays.size();
			int occurrenceRemain = occurance % weekdays.size();
			LocalDate nextDate = actualstartDate.plusWeeks(every * occurrenceFactor);
			if (occurrenceRemain == 0) {
				if (ind == 0) {
					nextDate = nextDate.minusDays(weekdays.get(weekdays.size() - 1).compareTo(nextDate.getDayOfWeek()));
				} else {
					nextDate = nextDate.minusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(ind - 1)));
				}
				return nextDate;
			}
			while (occurrenceRemain > 1) {
				// end by occurrence logic
				ind++;
				if (ind >= weekdays.size()) {
					ind = 0;
					nextDate = nextDate.minusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every);
				} else {
					nextDate = nextDate.plusDays(weekdays.get(ind).compareTo(nextDate.getDayOfWeek()));
				}
				occurrenceRemain--;
			}
			return nextDate;
			
		} else if (withEndDate) {
			
			// WithEndDate logic
			int skipDayCount = (int) actualstartDate.until(endDate, ChronoUnit.DAYS);
			int skipWeeks = (skipDayCount / (7 * every));
			if (skipDayCount == 0) {
				return actualstartDate;
			}
			LocalDate nextDate = actualstartDate.plusWeeks(skipWeeks * every);
			if (endDate.until(nextDate, ChronoUnit.DAYS) == 0) {
				return nextDate;
			}
			LocalDate prevDate = nextDate;
			while (nextDate.until(endDate, ChronoUnit.DAYS) > 0) {
				prevDate = nextDate;
				ind++;
				if (ind >= weekdays.size()) {
					ind = 0;
					nextDate = nextDate.minusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(0))).plusWeeks(every);
				} else {
					nextDate = nextDate.plusDays(weekdays.get(ind).compareTo(nextDate.getDayOfWeek()));
				}
			}
			return prevDate;
		}

		return null;
	}

	private static void validateInput(LocalDate startDate, List<DayOfWeek> weekdays, Integer every) {
		if (null == startDate) {
			throw new IllegalArgumentException("Provide startDate.");
		}
		if (null == weekdays || 0 == weekdays.size()) {
			throw new IllegalArgumentException("Provide days of week.");
		}
		if (null == every || 0 == every.intValue()) {
			throw new IllegalArgumentException("Provide no of weeks to repeat event.");
		}
	}
}
