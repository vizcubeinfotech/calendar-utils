package com.vizcube.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

	public static final String DATE_FORMAT_DD_MM_YYYY_WITH_SLASH = "dd/MM/yyyy";
	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	public static Date getLocalDateAfterHours(int hours) {
		return getLocalDate(LocalDateTime.now().plusHours(hours));
	}

	public static LocalDateTime getDateTime(final Date date, final ZoneId zoneId) {
		return date.toInstant().atZone(zoneId).toLocalDateTime();
	}

	public static LocalDateTime getLocalDateTime(final Date date) {
		return getDateTime(date, ZoneId.systemDefault());
	}

	public static LocalDateTime getGMTDateTime(final Date date) {
		return getDateTime(date, GMT.toZoneId());
	}

	public static Date getLocalDate(final LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static boolean isDateAfterDays(String mmDASHddDASHyyyyDate, long days) {
		long diffInDays = getDifferenceInDaysFromToday(mmDASHddDASHyyyyDate);
		return diffInDays == days;
	}

	public static boolean isPastDate(String mmDASHddDASHyyyyDate) {
		long diffInDays = getDifferenceInDaysFromToday(mmDASHddDASHyyyyDate);
		return diffInDays < 0;
	}

	public static boolean isFutureDate(String mmDASHddDASHyyyyDate) {
		long diffInDays = getDifferenceInDaysFromToday(mmDASHddDASHyyyyDate);
		return diffInDays > 0;
	}

	public static boolean isFutureOrTodayDate(String mmDASHddDASHyyyyDate) {
		return !isPastDate(mmDASHddDASHyyyyDate);
	}

	public static String format(final Date date, final String format, final ZoneId zoneId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return getDateTime(date, zoneId).format(formatter);
	}

	public static String formatLocal(final Date date, final String format) {
		return format(date, format, ZoneId.systemDefault());
	}

	public static String formatGMT(final Date date, final String format) {
		return format(date, format, GMT.toZoneId());
	}

	private static long getDifferenceInDaysFromToday(String mmDASHddDASHyyyyDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

		String formatDateTime = LocalDateTime.now().format(formatter);
		LocalDate from = LocalDate.parse(formatDateTime, formatter);

		LocalDate to = LocalDate.parse(mmDASHddDASHyyyyDate, formatter);
		long diffInDays = ChronoUnit.DAYS.between(from, to);
		return diffInDays;
	}
}
