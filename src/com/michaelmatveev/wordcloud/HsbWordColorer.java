package com.michaelmatveev.wordcloud;

abstract class HsbWordColorer implements WordColorer {
	private int range;
	
	HsbWordColorer() {
		this(255);
	}
	HsbWordColorer(int range) {
		this.range = range;
	}
	
	public int colorFor(Word word) {
		int color = getColorFor(word);
		return color;
	}
	
	abstract int getColorFor(Word word);
}
