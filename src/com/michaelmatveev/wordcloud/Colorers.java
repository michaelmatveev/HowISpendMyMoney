package com.michaelmatveev.wordcloud;

import java.util.Random;
import com.michaelmatveev.wordcloud.AMath;

public class Colorers {

	public static WordColorer twoHuesRandomSats() {
		
		final float[] hues = new float[] { AMath.random(256), AMath.random(256) };

		return new HsbWordColorer() {
			public int getColorFor(Word w) {

				float hue = hues[(int)AMath.random(hues.length)];
				float sat = AMath.random(256);
				float val = AMath.random(100, 256);

				return color(hue, sat, val);
			}
		};
	}
	
	public final static int color(float x, float y, float z) {

		if (x > 255) x = 255; else if (x < 0) x = 0;
		if (y > 255) y = 255; else if (y < 0) y = 0;
		if (z > 255) z = 255; else if (z < 0) z = 0;

		return 0xff000000 | ((int)x << 16) | ((int)y << 8) | (int)z;
	}

	public static WordColorer twoHuesRandomSatsOnWhite() {
		
		final float[] hues = new float[] { AMath.random(256), AMath.random(256) };

		return new HsbWordColorer() {
			public int getColorFor(Word w) {

				float hue = hues[(int)AMath.random(hues.length)];
				float sat = AMath.random(256);
				float val = AMath.random(156);

				return color(hue, sat, val);
			}
		};
	}
	
	public static WordColorer pickFrom(final int... colors) {
		final Random r = new Random();
		return new WordColorer() {
			public int colorFor(Word w) {
				return colors[r.nextInt(colors.length)];
			}
		};
	}
	
	public static WordColorer alwaysUse(final int color) {
		return new WordColorer() {
			public int colorFor(Word w) {
				return color;
			}
		};
	}
}
