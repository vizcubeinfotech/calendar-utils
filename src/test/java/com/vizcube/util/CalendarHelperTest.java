package com.vizcube.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static java.time.DayOfWeek.*;


public class CalendarHelperTest {

	@Test
	public void testWeeklyFreequencySettings() {
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "14/01/2019", "14/01/2019", "01/02/2019", null, 2, "14/01/2019", "30/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "14/01/2019", "10/01/2019", "01/02/2019", null, 2, "21/01/2019", "26/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "11/01/2019", "10/01/2019", "01/02/2019", null, 2, "12/01/2019", "26/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "14/01/2019", "10/01/2019", null, 3, 2, "21/01/2019", "23/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "22/01/2019", "10/01/2019", null, 4, 2, "23/01/2019", "26/01/2019");
		testWeeklyFreequencySettings(Arrays.asList(MONDAY, WEDNESDAY, SATURDAY), "10/01/2019", "10/01/2019", null, 5, 2, "12/01/2019", "04/02/2019");
		testWeeklyFreequencySettings(Arrays.asList(TUESDAY), "11/02/2019", "10/01/2019", null, 5, 3, "19/02/2019", "23/04/2019");
		testWeeklyFreequencySettings(Arrays.asList(TUESDAY), "11/02/2019", "10/01/2019", "28/03/2019", null, 3, "19/02/2019", "12/03/2019");
	}

	private void testWeeklyFreequencySettings(List<DayOfWeek> weekdays, String contextDateStr, String startDate, String endDate, Integer occurances, Integer every,
			String expectedNextDate, String expectedLastDate) {

		/** when */
		LocalDate actualNextOcuucranceDate = CalendarHelper.getWeeklyNextOccuranceDate(getDate(contextDateStr), getDate(startDate), endDate == null ? null : getDate(endDate), weekdays, every, occurances);
		LocalDate actualLastOcuucranceDate = CalendarHelper.getWeeklyLastOccuranceDate(getDate(startDate), endDate == null ? null : getDate(endDate), weekdays, every, occurances);

		/** then */
		assertDate(expectedNextDate, actualNextOcuucranceDate);
		assertDate(expectedLastDate, actualLastOcuucranceDate);
	}

	@Test
	public void testDailyFreequencySettings() {
		testDailyFreequencySettings("28/01/2019", "26/01/2019", null, 3, 2, "28/01/2019", "30/01/2019");
		testDailyFreequencySettings("03/02/2019", "26/01/2019", null, 6, 5, "05/02/2019", "20/02/2019");
		testDailyFreequencySettings("26/01/2019", "26/01/2019", "30/01/2019", null, 1, "26/01/2019", "30/01/2019");
		testDailyFreequencySettings("29/01/2019", "26/01/2019", "02/02/2019", null, 1, "29/01/2019", "02/02/2019");

	}

	private void testDailyFreequencySettings(String contextDateStr, String startDate, String endDate, Integer occurances, Integer every,
											  String expectedNextDate, String expectedLastDate) {

		/** when */
		LocalDate actualNextOcuucranceDate = CalendarHelper.getDailyNextOccurrenceDate(getDate(contextDateStr), getDate(startDate), endDate == null ? null : getDate(endDate), every, occurances);
		LocalDate actualLastOcuucranceDate = CalendarHelper.getDailyLastOccurrenceDate(getDate(startDate), endDate == null ? null : getDate(endDate), every, occurances);

		/** then */
		assertDate(expectedNextDate, actualNextOcuucranceDate);
		assertDate(expectedLastDate, actualLastOcuucranceDate);
	}

	@Test
	public void testYearlyFreequencySettings() {
		testYearlyFreequencySettings("05/01/2019", "05/01/2019", "05/05/2027", null, 3, "05/01/2019", "05/01/2025");
		testYearlyFreequencySettings("06/01/2019", "05/01/2019", "05/05/2027", null, 3, "05/01/2022", "05/01/2025");
		testYearlyFreequencySettings("06/03/2021", "29/02/2020", "05/05/2037", null, 3, "29/02/2032", "29/02/2032");
		testYearlyFreequencySettings("06/03/2021", "29/02/2020", null, 5, 3, "29/02/2032", "29/02/2068");
	}

	private void testYearlyFreequencySettings(String contextDateStr, String startDate, String endDate, Integer occurances, Integer every,
											 String expectedNextDate, String expectedLastDate) {

		/** when */
		LocalDate actualNextOcuucranceDate = CalendarHelper.getYearlyNextOccurrenceDate(getDate(contextDateStr), getDate(startDate), endDate == null ? null : getDate(endDate), every, occurances);
		LocalDate actualLastOcuucranceDate = CalendarHelper.getYearlyLastOccurrenceDate(getDate(startDate), endDate == null ? null : getDate(endDate), every, occurances);

		/** then */
		assertDate(expectedNextDate, actualNextOcuucranceDate);
		assertDate(expectedLastDate, actualLastOcuucranceDate);
	}

	@Test
	public void testMonthlyFreequencySettings() {
		testMonthlyFreequencySettings("05/01/2019", "05/01/2019", "05/09/2019", null, 2, 21, null, null,"21/01/2019", "21/07/2019");
		testMonthlyFreequencySettings("05/01/2019", "05/01/2019", "22/09/2019", null, 2, 21, null, null,"21/01/2019", "21/09/2019");
		testMonthlyFreequencySettings("05/04/2019", "05/01/2019", "05/10/2019", null, 1, 31, null, null,"31/05/2019", "31/08/2019");
		testMonthlyFreequencySettings("05/03/2019", "05/01/2019", null, 9, 2, 3, null, null,"03/05/2019", "03/07/2020");
		testMonthlyFreequencySettings("05/03/2019", "05/01/2019", null, 9, 2, null, 3, DayOfWeek.THURSDAY,"21/03/2019", "21/05/2020");
		testMonthlyFreequencySettings("05/03/2019", "05/01/2019", null, 9, 2, null, 5, DayOfWeek.THURSDAY,"28/03/2019", "28/05/2020");
		testMonthlyFreequencySettings("05/03/2019", "05/01/2019", "05/11/2019", null, 2, null, 5, DayOfWeek.THURSDAY,"28/03/2019", "26/09/2019");
		testMonthlyFreequencySettings("05/03/2019", "13/01/2019", "05/11/2019", null, 3, null, 2, DayOfWeek.THURSDAY,"11/04/2019", "10/10/2019");
	}

	private void testMonthlyFreequencySettings(String contextDateStr, String startDate, String endDate, Integer occurances, Integer every, Integer monthDate, Integer dayOfWeekInMonth, DayOfWeek dayOfWeek,
											  String expectedNextDate, String expectedLastDate) {

		/** when */
		LocalDate actualNextOcuucranceDate = CalendarHelper.getMonthlyNextOccuranceDate(getDate(contextDateStr), getDate(startDate), endDate == null ? null : getDate(endDate), every, occurances, monthDate,dayOfWeekInMonth, dayOfWeek);
		LocalDate actualLastOcuucranceDate = CalendarHelper.getMonthlyLastOccuranceDate(getDate(startDate), endDate == null ? null : getDate(endDate), every, occurances, monthDate, dayOfWeekInMonth, dayOfWeek);

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
