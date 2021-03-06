package com.michaelmatveev.wordcloud;

public class WaveWordPlacer implements WordPlacer {

	public AVector place(Word word, int wordIndex, int wordsCount,
			int wordImageWidth, int wordImageHeight, int fieldWidth,
			int fieldHeight) {

		float normalizedIndex = (float) wordIndex / wordsCount;
		float x = normalizedIndex * fieldWidth;
		float y = normalizedIndex * fieldHeight;

		float yOffset = getYOffset(wordIndex, wordsCount, fieldHeight);
		return new AVector(x, y + yOffset);
	}

	private float getYOffset(int wordIndex, int wordsCount, int fieldHeight) {
		float theta = AMath.map(wordIndex, 0, wordsCount, Constants.PI, -Constants.PI);

		return (float) Math.sin(theta) * (fieldHeight / 3f);
	}
}
