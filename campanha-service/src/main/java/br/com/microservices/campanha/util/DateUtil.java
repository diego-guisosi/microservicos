package br.com.microservices.campanha.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static Date somaDia(Date data, int quantidadeDias) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.add(Calendar.DAY_OF_MONTH, quantidadeDias);
		return calendar.getTime();
	}

}
