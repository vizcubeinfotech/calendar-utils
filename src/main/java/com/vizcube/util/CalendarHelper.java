package com.vizcube.util;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class CalendarHelper {

	/**
	 * Method will compute and return the next occurrence of weekly repeated event
	 * on or just after the contextDate. If next occurrence event not found in range
	 * of startDate and endDate then return null.
	 * 
	 * @param contextDate
	 *            the date for which next date needs to lookup
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date (may be null)
	 * @param weekdays
	 *            the week days selected to repeat
	 * @param every
	 *            after weeks for repeat
	 * @param occurance
	 *            number of occurrence of event if endDate is not set
	 * @return the next occurrence date
	 */
	public static LocalDate getWeeklyNextOccuranceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, List<DayOfWeek> weekdays, Integer every,
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
		if (withEndDate && contextDate.until(endDate, ChronoUnit.DAYS) < 0) {
			return null;
		}
		if (endsByOccurrence && remainingOccurrences <= 0) {
			return null;
		}
		if (skipDayCount <= 0) {
			return actualstartDate;
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

	/**
	 * This Method will compute and return the last occurrence of weekly repeated
	 * event for given settings
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date (may be null)
	 * @param weekdays
	 *            the week days selected to repeat
	 * @param every
	 *            after weeks for repeat
	 * @return the last occurrence date
	 */
	public static LocalDate getWeeklyLastOccuranceDate(LocalDate startDate, LocalDate endDate, List<DayOfWeek> weekdays, Integer every, Integer occurance) {
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
			// end by occurrence logic
			int occurrenceFactor = occurance / weekdays.size();
			int occurrenceRemain = occurance % weekdays.size();
			LocalDate nextDate = actualstartDate.plusWeeks(every * occurrenceFactor);
			if (occurrenceRemain == 0) {
				if (ind == 0) {
					nextDate = nextDate.plusDays(weekdays.get(weekdays.size() - 1).compareTo(nextDate.getDayOfWeek())).minusWeeks(every);
				} else {
					nextDate = nextDate.minusDays(nextDate.getDayOfWeek().compareTo(weekdays.get(ind - 1)));
				}
				return nextDate;
			}
			while (occurrenceRemain > 1) {
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

	/**
	 * Method will compute and return the next occurrence of daily repeated event
	 * on or just after the contextDate. If next occurrence event not found in range
	 * of startDate and endDate then return null.
	 *
	 * @param contextDate
	 *            the date for which next date needs to lookup
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date (may be null)
	 * @param every
	 *            after days for repeat
	 * @param occurance
	 *            number of occurrence of event if endDate is not set
	 * @return the next occurrence date
	 */
	public static LocalDate getDailyNextOccurrenceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, Integer every, Integer occurance) {
		validateInput(startDate, every);

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


		int skipDayCount = (int)startDate.until(contextDate, ChronoUnit.DAYS);
		int skipDaysOccurrences = (skipDayCount/every);
		int remainingOccurrences=0;
		if (endsByOccurrence) {
			remainingOccurrences = occurance - skipDaysOccurrences;
		}
		if (withEndDate && contextDate.until(endDate, ChronoUnit.DAYS) < 0) {
			return null;
		}
		if (endsByOccurrence && remainingOccurrences <= 0) {
			return null;
		}
		if (skipDayCount <= 0) {
			return startDate;
		}
		LocalDate nextDate = startDate.plusDays(every);
		if (nextDate.until(contextDate, ChronoUnit.DAYS) == 0) {
			return nextDate;
		}

		while (isNeverEnds || (withEndDate && nextDate.until(endDate, ChronoUnit.DAYS) >= 0) || (endsByOccurrence && remainingOccurrences>0)) {
			if (contextDate.until(nextDate, ChronoUnit.DAYS) >= 0) {
				return nextDate;
			} else if (endsByOccurrence) {
				remainingOccurrences--;
			}
			nextDate = nextDate.plusDays(every);
		}
		return null;
	}

	/**
	 * This Method will compute and return the last occurrence of daily repeated
	 * event for given settings
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date (may be null)
	 * @param every
	 *            after days for repeat
	 * @param occurance
	 *            number of occurrence of event if endDate is not set
	 * @return the last occurrence date
	 */
	public static LocalDate getDailyLastOccurrenceDate(LocalDate startDate, LocalDate endDate, Integer every, Integer occurance) {
		validateInput(startDate, every);

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


		if (endsByOccurrence) {
			LocalDate nextDate = startDate.plusDays(every * (occurance-1));

			return nextDate;
		} else if (withEndDate) {
			//WithEndDate logic
			int skipDayCount = (int)startDate.until(endDate, ChronoUnit.DAYS);
			int skipDayFactor = (skipDayCount/every);
			if (skipDayCount == 0) {
				return startDate;
			}
			LocalDate nextDate = startDate.plusDays(skipDayFactor*every);
			if (endDate.until(nextDate, ChronoUnit.DAYS) == 0) {
				return nextDate;
			}
			LocalDate prevDate = startDate;
			while (nextDate.until(endDate, ChronoUnit.DAYS) > 0) {
				prevDate = nextDate;
				nextDate = nextDate.plusDays(every);
			}
			return prevDate;
		}

		return null;
	}

	/**
	 * Method will compute and return the next occurrence of yearly repeated event
	 * on or just after the contextDate. If next occurrence event not found in range
	 * of startDate and endDate then return null.
	 *
	 * @param contextDate
	 *            the date for which next date needs to lookup
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date (may be null)
	 * @param every
	 *            after years for repeat
	 * @param occurance
	 *            number of occurrence of event if endDate is not set
	 * @return the next occurrence date
	 */
	public static LocalDate getYearlyNextOccurrenceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, Integer every, Integer occurance) {
		validateInput(startDate, every);

		boolean withEndDate = false;
		boolean isNeverEnds = false;
		boolean endsByOccurrence = false;
		boolean isLeapDayOfFeb = startDate.get(ChronoField.MONTH_OF_YEAR) == 2 && startDate.get(ChronoField.DAY_OF_MONTH) == 29;
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

		if (isLeapDayOfFeb) {
			int nextYear = startDate.getYear();
			LocalDate nextDate = startDate;

			while((endsByOccurrence && occurance > 0) || (withEndDate && nextDate.until(endDate, ChronoUnit.DAYS) >= 0)) {
				if (contextDate.isBefore(nextDate) || contextDate.isEqual(nextDate)) {
					return nextDate;
				} else if (endsByOccurrence) {
					occurance--;
				}
				do {
					nextYear += every;
				} while (!Year.isLeap(nextYear));
				nextDate = nextDate.withYear(nextYear);
			}
			return null;
		} else {
			int skipDayCount = (int) startDate.until(contextDate, ChronoUnit.YEARS);
			int skipDaysOccurrences = (skipDayCount / every);
			int remainingOccurrences = 0;
			if (endsByOccurrence) {
				remainingOccurrences = occurance - skipDaysOccurrences;
			}
			if (withEndDate && contextDate.until(endDate, ChronoUnit.DAYS) < 0) {
				return null;
			}
			if (endsByOccurrence && remainingOccurrences <= 0) {
				return null;
			}
			if (startDate.until(contextDate, ChronoUnit.DAYS) <= 0) {
				return startDate;
			}
			LocalDate nextDate = startDate.plusYears(every);
			if (nextDate.until(contextDate, ChronoUnit.DAYS) <= 0) {
				return nextDate;
			}

			while (isNeverEnds || (withEndDate && nextDate.until(endDate, ChronoUnit.DAYS) >= 0) || (endsByOccurrence && remainingOccurrences > 0)) {
				if (contextDate.until(nextDate, ChronoUnit.DAYS) >= 0) {
					return nextDate;
				} else if (endsByOccurrence) {
					remainingOccurrences--;
				}
				nextDate = nextDate.plusYears(every);
			}
		}
		return null;
	}

	/**
	 * This Method will compute and return the last occurrence of daily repeated
	 * event for given settings
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date (may be null)
	 * @param every
	 *            after years for repeat
	 * @param occurance
	 *            number of occurrence of event if endDate is not set
	 * @return the last occurrence date
	 */
	public static LocalDate getYearlyLastOccurrenceDate(LocalDate startDate, LocalDate endDate, Integer every, Integer occurance) {
		validateInput(startDate, every);

		boolean withEndDate = false;
		boolean isNeverEnds = false;
		boolean endsByOccurrence = false;
		boolean isLeapDayOfFeb = startDate.get(ChronoField.MONTH_OF_YEAR) == 2 && startDate.get(ChronoField.DAY_OF_MONTH) == 29;
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

		int lcm = every > 4 ? every : 4;
		int add = lcm;
		int n = every > 4 ? 4 : every;
		while (lcm % n != 0) {
			lcm+=add;
		}
		if (endsByOccurrence) {
			LocalDate nextDate = null;
			nextDate = startDate.plusYears((occurance-1) * (isLeapDayOfFeb ? lcm : every));

			return nextDate;
		} else if (withEndDate) {
			//WithEndDate logic
			int skipYearCount = (int)startDate.until(endDate, ChronoUnit.YEARS);
			int skipYearFactor = (skipYearCount/(isLeapDayOfFeb ? lcm : every));
			if (startDate.until(endDate, ChronoUnit.DAYS) == 0) {
				return startDate;
			}
			LocalDate nextDate = startDate.plusYears(skipYearFactor*(isLeapDayOfFeb ? lcm : every));
			if (endDate.until(nextDate, ChronoUnit.DAYS) == 0) {
				return nextDate;
			}
			LocalDate prevDate = startDate;
			while (nextDate.until(endDate, ChronoUnit.DAYS) > 0) {
				prevDate = nextDate;
				nextDate = nextDate.plusYears(isLeapDayOfFeb ? lcm : every);
			}
			return prevDate;
		}

		return null;
	}

	public static LocalDate getMonthlyNextOccuranceDate(LocalDate contextDate, LocalDate startDate, LocalDate endDate, Integer every, Integer occurance, Integer monthDate, Integer dayOfWeekInMonth, DayOfWeek dayOfWeek) {

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

		boolean repeatMonthDate = false;
		boolean repeatDayOfWeekInMonth = false;
		if (monthDate != null) {
			if (dayOfWeekInMonth != null) {
				throw new IllegalArgumentException("");
			} else {
				repeatMonthDate = true;
			}
		} else if(dayOfWeekInMonth != null) {
			repeatDayOfWeekInMonth = true;
		} else {
			throw new IllegalArgumentException("");
		}

		LocalDate actualStartDate = null;
		if (repeatMonthDate) {
			if (monthDate > 31 && monthDate < 1) {
				throw new IllegalArgumentException("Provide valid date of month!");
			}
			if (startDate.getDayOfMonth() > monthDate) {
				actualStartDate = getNextDateOfNextEventMonth(startDate, every, monthDate);
			} else {
				try {
					actualStartDate = startDate.withDayOfMonth(monthDate);
				} catch (DateTimeException ex) {
					actualStartDate = getNextDateOfNextEventMonth(startDate, every, monthDate);
				}
			}
		} else if (repeatDayOfWeekInMonth) {//last week day logic remain
			actualStartDate = startDate.with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
			if (actualStartDate.isBefore(startDate)) {
				actualStartDate = actualStartDate.withDayOfMonth(1).plusMonths(every).with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
			}
		}

		if (contextDate.isBefore(actualStartDate) || contextDate.isEqual(actualStartDate)) {
			return actualStartDate;
		}

		int skipMonth = (int)actualStartDate.until(contextDate, ChronoUnit.MONTHS);
		int skipMonthFactor = skipMonth / every;
		int remaningOccurrence = occurance - every * skipMonthFactor;
		LocalDate nextLocalDate = actualStartDate.withDayOfMonth(1).plusMonths(every*skipMonthFactor);
		nextLocalDate = repeatMonthDate ? getNextDateOfNextEventMonth(nextLocalDate.minusMonths(every), every, monthDate) : nextLocalDate.with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
		while(isNeverEnds || (endsByOccurrence && occurance>0) || (withEndDate && (nextLocalDate.isBefore(endDate) || nextLocalDate.isEqual(endDate)))) {
			if (contextDate.isBefore(nextLocalDate) || contextDate.isEqual(nextLocalDate)) {
				return nextLocalDate;
			} else if (endsByOccurrence) {
				occurance--;
			}
			nextLocalDate = repeatMonthDate ? getNextDateOfNextEventMonth(nextLocalDate, every, monthDate): nextLocalDate.withDayOfMonth(1).plusMonths(every).with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth,dayOfWeek));
		}
		return null;
	}

	public static LocalDate getMonthlyLastOccuranceDate(LocalDate startDate, LocalDate endDate, Integer every, Integer occurance, Integer monthDate, Integer dayOfWeekInMonth, DayOfWeek dayOfWeek) {
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

		boolean repeatMonthDate = false;
		boolean repeatDayOfWeekInMonth = false;
		if (monthDate != null) {
			if (dayOfWeekInMonth != null) {
				throw new IllegalArgumentException("");
			} else {
				repeatMonthDate = true;
			}
		} else if(dayOfWeekInMonth != null) {
			repeatDayOfWeekInMonth = true;
		} else {
			throw new IllegalArgumentException("");
		}

		LocalDate actualStartDate = null;
		if (repeatMonthDate) {
			if (monthDate > 31 && monthDate < 1) {
				throw new IllegalArgumentException("Provide valid date of month!");
			}
			if (startDate.getDayOfMonth() > monthDate) {
				actualStartDate = getNextDateOfNextEventMonth(startDate, every, monthDate);
			} else {
				try {
					actualStartDate = startDate.withDayOfMonth(monthDate);
				} catch (DateTimeException ex) {
					actualStartDate = getNextDateOfNextEventMonth(startDate, every, monthDate);
				}
			}
		} else if (repeatDayOfWeekInMonth) {//last week day logic remain
			actualStartDate = startDate.with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
			if (actualStartDate.isBefore(startDate)) {
				actualStartDate = actualStartDate.withDayOfMonth(1).plusMonths(every).with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
			}
		}

		LocalDate nextDate = null;
		if (withEndDate) {
			int skipMonth = (int)actualStartDate.until(endDate, ChronoUnit.MONTHS);
			int skipMonthFactor = skipMonth / every;
			nextDate = actualStartDate.plusMonths(skipMonthFactor * every);

			if (repeatDayOfWeekInMonth) {
				nextDate = nextDate.withDayOfMonth(1).with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
				if (nextDate.until(endDate, ChronoUnit.DAYS) < 0) {
					return nextDate.minusMonths(every).withDayOfMonth(1).with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
				} else
					return nextDate;
			} else if (repeatMonthDate) {
				nextDate = getNextDateOfNextEventMonth(nextDate.minusMonths(every), every, monthDate);
				if (nextDate.until(endDate,ChronoUnit.DAYS) < 0) {
					while (actualStartDate.isBefore(nextDate) || actualStartDate.isEqual(nextDate)) {
						try {
							nextDate = nextDate.minusMonths(every);
							nextDate = nextDate.withDayOfMonth(monthDate);
							if (nextDate.until(endDate,ChronoUnit.DAYS) < 0) {
								continue;
							}
							return nextDate;
						} catch (DateTimeException ex) {
						}
					}
				} else  {
					return nextDate;
				}
			}

		} else if (endsByOccurrence) {
			if (repeatDayOfWeekInMonth) {
				nextDate = actualStartDate.withDayOfMonth(1).plusMonths((occurance - 1) * every).with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth, dayOfWeek));
			} else if (repeatMonthDate) {
				nextDate = actualStartDate;
				occurance--;
				while (occurance > 0) {
					nextDate = getNextDateOfNextEventMonth(nextDate,every,monthDate);
					occurance--;
				}
			}
			return nextDate;
		}
		return null;
	}

	private static LocalDate getNextDateOfNextEventMonth(LocalDate currentDate, int every, int monthDate) {
		LocalDate nextDate = currentDate;
		try {
			nextDate = currentDate.plusMonths(every);
			nextDate = nextDate.withDayOfMonth(monthDate);
			return nextDate;
		} catch (DateTimeException ex) {
			return getNextDateOfNextEventMonth(nextDate, every, monthDate);
		}
	}

	private static void validateInput(LocalDate startDate, List<DayOfWeek> weekdays, Integer every) {
		validateInput(startDate, every);
		if (null == weekdays || 0 == weekdays.size()) {
			throw new IllegalArgumentException("Provide days of week.");
		}
	}

	private static void validateInput(LocalDate startDate, Integer every) {
		if (null == startDate) {
			throw new IllegalArgumentException("Provide startDate.");
		}
		if (null == every || 0 == every.intValue()) {
			throw new IllegalArgumentException("Provide no of weeks to repeat event.");
		}
	}
}
