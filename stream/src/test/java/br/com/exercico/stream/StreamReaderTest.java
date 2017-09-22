package br.com.exercico.stream;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StreamReaderTest {

	@Test
	public void testExemploExercicio() {
		Stream stream = new StreamReader("aAbBABacafe");
		List<Character> characters = new ArrayList<>();
		
		while(stream.hasNext()) {
			characters.add(stream.getNext());
		}
		
		Assert.assertTrue(characters.size() == 1);
		Assert.assertTrue(characters.get(0) == 'e');
	}
	
	@Test
	public void testExemploExercicioComVogalERepitida() {
		Stream stream = new StreamReader("aAbBABacafeE");
		List<Character> characters = new ArrayList<>();
		
		while(stream.hasNext()) {
			characters.add(stream.getNext());
		}
		
		Assert.assertTrue(characters.size() == 0);
	}
	
	@Test
	public void testExemploComConsoanteNoFinalDaStream() {
		Stream stream = new StreamReader("aAbBABacafecafiB");
		List<Character> characters = new ArrayList<>();
		
		while(stream.hasNext()) {
			characters.add(stream.getNext());
		}
		
		Assert.assertTrue(characters.size() == 2);
		Assert.assertTrue(characters.get(0) == 'e');
		Assert.assertTrue(characters.get(1) == 'i');
	}

}
