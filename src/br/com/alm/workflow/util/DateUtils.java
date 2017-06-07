package br.com.alm.workflow.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
	
	/**
	 * Converte LocalDate java object em campo de data do ALM
	 * 
	 * @param javaDate
	 * @return
	 */
	public static String convertLocalDateToAlmDate(LocalDate javaDate) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
		
		return javaDate.format(formatter);
	}

}
