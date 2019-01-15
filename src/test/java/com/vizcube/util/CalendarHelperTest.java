package com.vizcube.util;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.WEDNESDAY;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class CalendarHelperTest {

	@Test
	public void testWeeklyFreequencySettings() {
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "14/01/2019", "14/01/2019", "01/02/2019", null, 2, "14/01/2019", "30/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "14/01/2019", "10/01/2019", "01/02/2019", null, 2, "21/01/2019", "26/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "14/01/2019", "10/01/2019", null, 3, 2, "21/01/2019", "23/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "22/01/2019", "10/01/2019", null, 4, 2, "23/01/2019", "26/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "10/01/2019", "10/01/2019", null, 5, 2, "12/01/2019", "04/02/2019");
	}

	private void testWeeklyFreequencySettings(List<DayOfWeek> weekdays, String contextDateStr, String startDate, String endDate, Integer occurances, Integer every,
			String expectedNextDate, String expectedLastDate) {

		/** when */
		LocalDate actualNextOcuucranceDate = CalendarHelper.getNextOccuranceDate(getDate(contextDateStr), getDate(startDate), endDate == null ? null : getDate(endDate), weekdays, every, occurances);
		LocalDate actualLastOcuucranceDate = CalendarHelper.getLastOccuranceDate(getDate(startDate), endDate == null ? null : getDate(endDate), weekdays, every, occurances);

		/** then */
		assertDate(expectedNextDate, actualNextOcuucranceDate);
		assertDate(expectedLastDate, actualLastOcuucranceDate);
	}

	private LocalDate getDate(String ddMMYYYYWithSlash) {
		return LocalDate.parse(ddMMYYYYWithSlash, DateTimeFormatter.ofPattern(DateHelper.DATE_FORMAT_DD_MM_YYYY_WITH_SLASH));
	}

	private void assertDate(String ddMMYYYYWithSlash, LocalDate actualDate) {
		String parts[] = ddMMYYYYWithSlash.split("/");
		assertDate(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), actualDate);
	}

	private void assertDate(int expectedYear, int expectedMonth, int expectedDay, LocalDate actualDate) {
		LocalDate expectedDate = LocalDate.of(expectedYear, expectedMonth, expectedDay);
		Assert.assertTrue(expectedDate.equals(actualDate));
	}
}
