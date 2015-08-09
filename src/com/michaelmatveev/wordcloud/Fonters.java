package com.michaelmatveev.wordcloud;

import java.util.Random;

import android.graphics.Typeface;

public class Fonters {
	
	public static WordFonter alwaysUse(final Typeface font) {
		return new WordFonter() {
			public Typeface fontFor(Word word) {
				return font;
			}
		};
	}
	
	public static WordFonter pickFrom(final Typeface... fonts) {
		final Random r = new Random();
		return new WordFonter() {
			public Typeface fontFor(Word w) {
				return fonts[r.nextInt(fonts.length)];
			}
		};
	}
}
