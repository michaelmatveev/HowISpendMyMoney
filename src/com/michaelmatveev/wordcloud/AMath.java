package com.michaelmatveev.wordcloud;

import java.util.Random;

public class AMath {
	
	/**
	 * Return a random number in the range [howsmall, howbig).
	 * <P>
	 * The number returned will range from 'howsmall' up to
	 * (but not including 'howbig'.
	 * <P>
	 * If howsmall is >= howbig, howsmall will be returned,
	 * meaning that random(5, 5) will return 5 (useful)
	 * and random(7, 4) will return 7 (not useful.. better idea?)
	 */
	public final static float random(float howsmall, float howbig) {
		if (howsmall >= howbig) return howsmall;
		float diff = howbig - howsmall;
		return random(diff) + howsmall;
	}
	
	/**
	 * Return a random number in the range [0, howbig).
	 * <P>
	 * The number returned will range from zero up to
	 * (but not including) 'howbig'.
	 */
	public final static float random(float howbig) {
		// for some reason (rounding error?) Math.random() * 3
		// can sometimes return '3' (once in ~30 million tries)
		// so a check was added to avoid the inclusion of 'howbig'

		// avoid an infinite loop
		if (howbig == 0) return 0;

		// internal random number object
		Random internalRandom = new Random();

		float value = 0;
		do {
			value = internalRandom.nextFloat() * howbig;
		} while (value == howbig);
		return value;
	}
	
	static public final float lerp(float start, float stop, float amt) {
		return start + (stop-start) * amt;
	}

	/**
	 * Normalize a value to exist between 0 and 1 (inclusive).
	 * Mathematically the opposite of lerp(), figures out what proportion
	 * a particular value is relative to start and stop coordinates.
	 */
	static public final float norm(float value, float start, float stop) {
		return (value - start) / (stop - start);
	}	
	
	/**
	 * Convenience function to map a variable from one coordinate space
	 * to another. Equivalent to unlerp() followed by lerp().
	 */
	static public final float map(float value,
			float istart, float istop,
			float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

	static public final double map(double value,
			double istart, double istop,
			double ostart, double ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
}