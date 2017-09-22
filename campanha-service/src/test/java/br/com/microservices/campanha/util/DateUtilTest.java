package br.com.microservices.campanha.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilTest {

	@Test
	public void testSoma1Dia() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dataSomada = DateUtil.somaDia(sdf.parse("2017-09-18"), 1);
		Assert.assertTrue(sdf.format(dataSomada).equals("2017-09-19"));
	}
	
	@Test
	public void testSubtrai1Dia() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dataSomada = DateUtil.somaDia(sdf.parse("2017-09-18"), -1);
		Assert.assertTrue(sdf.format(dataSomada).equals("2017-09-17"));
	}

}
