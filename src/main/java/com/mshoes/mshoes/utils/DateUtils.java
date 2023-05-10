package com.mshoes.mshoes.utils;

import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DateUtils {

	/**
	 * Method get current date and format output to String <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	public Date formatToDate(String strDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.parse(strDate);
	}

	public String formatToStrDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	public String formatToStrMonth(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return dateFormat.format(date);
	}
	public String formatToStrYear(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		return dateFormat.format(date);
	}

	public List<Date> getRecentDaysList(int numDays) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		List<Date> dateList = new ArrayList<>();
		LocalDate currentDate = LocalDate.now();
		for (int i = 0; i < numDays; i++) {
			LocalDate day = currentDate.minusDays(i);
			String dateStr = day.format(formatter);
			try {
				Date date = new SimpleDateFormat("yyyy/MM/dd").parse(dateStr);
				dateList.add(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(dateList);
		return dateList;
	}

}
