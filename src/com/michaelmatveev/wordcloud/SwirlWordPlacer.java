package com.michaelmatveev.wordcloud;

public class SwirlWordPlacer implements WordPlacer, Constants {

	public AVector place(Word word, int wordIndex, int wordsCount,
			int wordImageWidth, int wordImageHeight, int fieldWidth,
			int fieldHeight) {

		float normalizedIndex = (float) wordIndex / wordsCount;

		float theta = normalizedIndex * 6 * TWO_PI;
		float radius = normalizedIndex * fieldWidth / 2f;

		float centerX = fieldWidth * 0.5f;
		float centerY = fieldHeight * 0.5f;

		float x = (float)Math.cos(theta) * radius;
		float y = (float)Math.sin(theta) * radius;

		return new AVector(centerX + x, centerY + y);
	}
}
