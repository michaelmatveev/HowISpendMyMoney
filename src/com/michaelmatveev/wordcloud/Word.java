package com.michaelmatveev.wordcloud;

import java.util.HashMap;

import android.graphics.Typeface;

import com.michaelmatveev.wordcloud.AVector;

public class Word implements Comparable<Word> {
	
	public String word;
	public float weight;
	
	private Float presetSize;
	private Float presetAngle;
	private Typeface presetFont;
	private Integer presetColor;
	private AVector presetTargetPlace;
	
	// These are null until they're rendered, and can be wiped out for a re-render.
	private Float renderedSize;
	private Float renderedAngle;
	private Typeface renderedFont;
	private Integer renderedColor;
	private AVector targetPlace;
	private AVector renderedPlace;
	private Integer skippedBecause;
	
	private HashMap<String,Object> properties = new HashMap<String,Object>();
	
	public Word(String word, float weight) {
		this.word = word;
		this.weight = weight;
	}
	
	/**
	 * Set the size this Word should be rendered at - WordCram won't even call the WordSizer.
	 */
	public void setSize(float size) {
		this.presetSize = size;
	}
	
	/**
	 * Set the angle this Word should be rendered at - WordCram won't even call the WordAngler.
	 */
	public void setAngle(float angle) {
		this.presetAngle = angle;
	}
	
	/**
	 * Set the font this Word should be rendered in - WordCram won't call the WordFonter.
	 */
	public void setFont(Typeface font) {  // TODO provide a string overload? Will need the PApplet...
		this.presetFont = font;
	}
	
	/**
	 * Set the color this Word should be rendered in - WordCram won't call the WordColorer.
	 */
	public void setColor(int color) {  // TODO provide a 3-float overload? 4-float? 2-float? Will need the PApplet...
		this.presetColor = color;
	}
	
	/**
	 * Set the place this Word should be rendered at - WordCram won't call the WordPlacer.
	 */
	public void setPlace(AVector place) {
		this.presetTargetPlace.set(place);
	}
	
	/**
	 * Set the place this Word should be rendered at - WordCram won't call the WordPlacer.
	 */
	public void setPlace(float x, float y) {
		this.presetTargetPlace.set(x, y);
	}

	/*
	 * These methods are called by EngineWord: they return (for instance)
	 * either the color the user set via setColor(), or the value returned
	 * by the WordColorer. They're package-local, so they can't be called by the sketch.
	 */
	
	Float getSize(WordSizer sizer, int rank, int wordCount) {
		renderedSize = presetSize != null ? presetSize : sizer.sizeFor(this, rank, wordCount);
		return renderedSize;
	}
	
	Float getAngle(WordAngler angler) {
		renderedAngle = presetAngle != null ? presetAngle : angler.angleFor(this);
		return renderedAngle;
	}
	
	Typeface getFont(WordFonter fonter) {
		renderedFont = presetFont != null ? presetFont : fonter.fontFor(this);
		return renderedFont;
	}
	
	Integer getColor(WordColorer colorer) {
		renderedColor = presetColor != null ? presetColor : colorer.colorFor(this);
		return renderedColor;
	}
	
	AVector getTargetPlace(WordPlacer placer, int rank, int count, int wordImageWidth, int wordImageHeight, int fieldWidth, int fieldHeight) {
		targetPlace = presetTargetPlace != null ? presetTargetPlace : placer.place(this, rank, count, wordImageWidth, wordImageHeight, fieldWidth, fieldHeight);
		return targetPlace; 
	}
	
	void setRenderedPlace(AVector place) {
		renderedPlace = place.get();
	}

	/**
	 * Get the size the Word was rendered at: either the value passed to setSize(), or the value returned from the WordSizer. 
	 * @return the rendered size
	 */
	public float getRenderedSize() {
		return renderedSize;
	}

	/**
	 * Get the angle the Word was rendered at: either the value passed to setAngle(), or the value returned from the WordAngler. 
	 * @return the rendered angle
	 */
	public float getRenderedAngle() {
		return renderedAngle;
	}

	/**
	 * Get the font the Word was rendered in: either the value passed to setFont(), or the value returned from the WordFonter. 
	 * @return the rendered font
	 */
	public Typeface getRenderedFont() {
		return renderedFont;
	}

	/**
	 * Get the color the Word was rendered in: either the value passed to setColor(), or the value returned from the WordColorer. 
	 * @return the rendered color
	 */
	public int getRenderedColor() {
		return renderedColor;
	}

	/**
	 * Get the place the Word was supposed to be rendered at: either the value passed to setPlace(), 
	 * or the value returned from the WordPlacer.
	 */
	public AVector getTargetPlace() {
		return targetPlace;
	}

	/**
	 * Get the final place the Word was rendered at, or null if it couldn't be placed.
	 * It returns the original target location (which is either the value passed to setPlace(), 
	 * or the value returned from the WordPlacer), plus the nudge vector returned by the WordNudger.
	 * @return If word was placed, it's the (x,y) coordinates of the word's final location; else it's null.
	 */
	public AVector getRenderedPlace() {
		return renderedPlace;
	}
	
	/**
	 * Indicates whether the Word was placed successfully. It's the same as calling word.getRenderedPlace() != null.
	 * If this returns false, it's either because a) WordCram didn't get to this Word yet,
	 * or b) it was skipped for some reason (see {@link #wasSkipped()} and {@link #wasSkippedBecause()}).
	 * @return true only if the word was placed.
	 */
	public boolean wasPlaced() {
		return renderedPlace != null;
	}
	
	/**
	 * Indicates whether the Word was skipped.
	 * @see Word#wasSkippedBecause()
	 * @return true if the word was skipped
	 */
	public boolean wasSkipped() {
		return wasSkippedBecause() != null;
	}
	
	/**
	 * Tells you why this Word was skipped.
	 * 
	 * If the word was skipped, 
	 * then this will return an Integer, which will be one of 
	 * {@link WordCram#WAS_OVER_MAX_NUMBER_OF_WORDS}, {@link WordCram#SHAPE_WAS_TOO_SMALL}, 
	 * or {@link WordCram#NO_SPACE}.
	 * 
	 * If the word was successfully placed, or WordCram hasn't
	 * gotten to this word yet, this will return null.
	 * 
	 * @return the code for the reason the word was skipped, or null if it wasn't skipped.  
	 */
	public Integer wasSkippedBecause() {
		return skippedBecause;
	}
	
	void wasSkippedBecause(int reason) {
		skippedBecause = reason;
	}

	/**
	 * Get a property value from this Word, for a WordColorer, a WordPlacer, etc.
	 * @param propertyName
	 * @return the value of the property, or <code>null</code>, if it's not there.
	 */
	public Object getProperty(String propertyName) {
		return properties.get(propertyName);
	}
	
	/**
	 * Set a property on this Word, to be used by a WordColorer, a WordPlacer, etc, down the line.
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(String propertyName, Object propertyValue) {
		properties.put(propertyName, propertyValue);
	}
	
	/**
	 * Displays the word, and its weight (in parentheses).
	 * <code>new Word("hello", 1.3).toString()</code> will return "hello (0.3)".
	 */
	@Override
//	public String toString() {
//		String status = "";
//		if (wasPlaced()) {
//			status = renderedPlace.x + "," + renderedPlace.y;
//		}
//		else if (wasSkipped()) {
//			switch (wasSkippedBecause()) {
//			case WordCram.WAS_OVER_MAX_NUMBER_OF_WORDS:
//				status = "was over the maximum number of words";
//				break;
//			case WordCram.SHAPE_WAS_TOO_SMALL:
//				status = "shape was too small";
//				break;
//			case WordCram.NO_SPACE:
//				status = "couldn't find a spot";
//				break;
//			}
//		}
//		if (status.length() != 0) {
//			status = " [" + status + "]";
//		}
//		return word + " (" + weight + ")" + status;
//	}
	
	/**
	 * Compares Words based on weight only. Words with equal weight are arbitrarily sorted.
	 */
	public int compareTo(Word w) {
		if (w.weight < weight) {
			return -1;
		}
		else if (w.weight > weight) {
			return 1;
		}
		else return 0;
	}
}
