package com.michaelmatveev.wordcloud;

import java.util.Random;
import com.michaelmatveev.wordcloud.AMath;
import com.michaelmatveev.wordcloud.AVector;

public class Placers {

	public static WordPlacer horizLine() {
		final Random r = new Random();

		return new WordPlacer() {
			public AVector place(Word word, int wordIndex, int wordsCount,
					int wordImageWidth, int wordImageHeight, int fieldWidth, int fieldHeight) {
				int centerHorizLine = (int) ((fieldHeight - wordImageHeight) * 0.5);
				int centerVertLine = (int) ((fieldWidth - wordImageWidth) * 0.5);

				float xOff = (float) r.nextGaussian() * ((fieldWidth - wordImageWidth) * 0.2f);
				float yOff = (float) r.nextGaussian() * 50;

				return new AVector(centerVertLine + xOff, centerHorizLine + yOff);
			}
		};
	}

	public static WordPlacer centerClump() {
		final Random r = new Random();
		final float stdev = 0.4f;

		return new WordPlacer() {

			public AVector place(Word word, int wordIndex, int wordsCount,
					int wordImageWidth, int wordImageHeight, int fieldWidth, int fieldHeight) {
				return new AVector(getOneUnder(fieldWidth - wordImageWidth),
						getOneUnder(fieldHeight - wordImageHeight));
			}

			private int getOneUnder(float upperLimit) {
				return Math.round(AMath.map((float) r.nextGaussian()
						* stdev, -2, 2, 0, upperLimit));
			}
		};
	}

	public static WordPlacer horizBandAnchoredLeft() {
		final Random r = new Random();
		return new WordPlacer() {
			public AVector place(Word word, int wordIndex, int wordsCount,
					int wordImageWidth, int wordImageHeight, int fieldWidth,
					int fieldHeight) {
				float x = (float) (1 - word.weight) * fieldWidth * r.nextFloat(); // big=left, small=right
				float y = ((float) fieldHeight) * 0.5f;
				return new AVector(x, y);
			}
		};
	}

	public static WordPlacer swirl() {
		return new SwirlWordPlacer();
	}

	public static WordPlacer upperLeft() {
		return new UpperLeftWordPlacer();
	}

	public static WordPlacer wave() {
		return new WaveWordPlacer();
	}
}
