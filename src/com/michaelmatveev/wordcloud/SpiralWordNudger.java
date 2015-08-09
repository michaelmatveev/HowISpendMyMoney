package com.michaelmatveev.wordcloud;

import com.michaelmatveev.wordcloud.AVector;
import com.michaelmatveev.wordcloud.AMath;

public class SpiralWordNudger implements WordNudger {

	// Who knows? this seems to be good, but it seems to depend on the font --
	// bigger fonts need a bigger thetaIncrement.
	private float thetaIncrement = (float) (Math.PI * 0.03);

	public AVector nudgeFor(Word w, int attempt) {
		float rad = powerMap(0.6f, attempt, 0, 600, 1, 100);

		thetaIncrement = powerMap(1, attempt, 0, 600, 0.5f, 0.3f);
		double theta = thetaIncrement * attempt;
		float x = (float)Math.cos(theta) * rad;
		float y = (float)Math.sin(theta) * rad;
		return new AVector(x, y);
	}

	private float powerMap(float power, float v, float min1, float max1,
			float min2, float max2) {

		float val = AMath.norm(v, min1, max1);
		val = (float)Math.pow(val, power);
		return AMath.lerp(min2, max2, val);
	}
} 
