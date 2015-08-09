package com.michaelmatveev.wordcloud;

import java.util.Random;
import com.michaelmatveev.wordcloud.AMath;

public class UpperLeftWordPlacer implements WordPlacer {

	private Random r = new Random();
	
	public AVector place(Word word, int wordIndex, int wordsCount, int wordImageWidth, int wordImageHeight, int fieldWidth, int fieldHeight) {
		int x = getOneUnder(fieldWidth - wordImageWidth);
		int y = getOneUnder(fieldHeight - wordImageHeight);
		return new AVector(x, y);
	}
	
	private int getOneUnder(int limit) {
		return (int)Math.round(AMath.map(random(random(random(random(random(1.0f))))), 0, 1, 0, limit));
	}
	
	private float random(float limit) {
		return r.nextFloat() * limit;
	}
}
