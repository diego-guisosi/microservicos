package br.com.exercico.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamReader implements Stream{
	
	private char[] charArray;
	private char next;
	private boolean hasNext;
	private int vowelIndex=-1;
	
	private List<Character> electedVowels;
	private Map<Integer,Character> characterMap = new HashMap<Integer, Character>();
	
	public StreamReader(String input) {
		charArray = input.toCharArray();
		electedVowels = electVowels(charArray);
	}

	public char getNext() {
		
		if(!hasNext) {
			throw new IllegalStateException("Stream does not have more characteres to read");
		}
		
		return next;
	}

	public boolean hasNext() {
		
		hasNext = false;
		
		for(vowelIndex++; vowelIndex < electedVowels.size(); vowelIndex++) {
			Character vowel = electedVowels.get(vowelIndex);
			if (electedVowels.stream().filter(ev -> ev.equals(vowel)).count() == 1) {
				next = vowel;
				hasNext = true;
				break;
			}
		}
		
		return hasNext;
	}
	
	private List<Character> electVowels(char[] charArray) {
		List<Character> electedVowels = new ArrayList<>();
		for(int currentIndex = 0; currentIndex < charArray.length; currentIndex++) {
			characterMap.put(currentIndex, charArray[currentIndex]);
			
			int firstCharIndex = currentIndex - 3;
			int secondCharIndex = firstCharIndex + 1;
			int thirdCharIndex = firstCharIndex + 2;
			
			if(firstCharIndex < 0) {
				continue;
			}
			
			Character fourthChar = characterMap.get(currentIndex);
			
			if(isVowel(fourthChar)) {
				if(!isLastChar(currentIndex)) {
					continue;
				} else {
					firstCharIndex++;
					secondCharIndex++;
					thirdCharIndex++;
				}
			}
			
			Character firstChar = characterMap.get(firstCharIndex);
			Character secondChar = characterMap.get(secondCharIndex);
			Character thirdChar = characterMap.get(thirdCharIndex);
			
			if(isVowel(firstChar) && !isVowel(secondChar) && isVowel(thirdChar)) {
				electedVowels.add(thirdChar);
			}
		}
		return electedVowels;
	}
	
	private boolean isLastChar(int currentIndex) {
		return currentIndex + 1 >= charArray.length;
	}
	
	private boolean isVowel(char character) {
		return "aeiouAEIOU".indexOf(character) >= 0 ;
	}

}
