package com.michaelmatveev.wordcloud;

import java.util.ArrayList;

import com.michaelmatveev.wordcloud.Placers;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class WordCloud {

	public static final int WAS_OVER_MAX_NUMBER_OF_WORDS = 301;
	public static final int SHAPE_WAS_TOO_SMALL = 302;
	public static final int NO_SPACE = 303;

	private Word[] wWords;
//	private TextSource textSource;
//	private String stopWords = StopWords.ENGLISH;
	private boolean excludeNumbers = true;
	private enum TextCase { Lower, Upper, Keep };
	private TextCase textCase = TextCase.Keep;
	
	private WordCloudEngine wordCloudEngine;

	private WordFonter fonter;
	private WordSizer sizer;
	private WordColorer colorer;
	private WordAngler angler;
	private WordPlacer placer;
	private WordNudger nudger;
	
	Typeface typeFace;
	String[] words;
	
	private RenderOptions renderOptions = new RenderOptions();
	
	/**
	 * Make a new WordCram.
	 * <p>
	 * When constructed this way, it's the starting point of the fluent API for building WordCrams.
	 * 
	 * @param parent Your Processing sketch. You'll probably pass it as <code>this</code>.
	 */
	public WordCloud(Word[] words) {
		this.wWords = words;
		
	}
	
//	/**
//	 * Tells WordCram which words to ignore when it counts up the words in your text.
//	 * These words won't show up in the image.  {@link StopWords} provides some common sets
//	 * of stop-words.
//	 * <p>
//	 * Stop-words are always case-insensitive: if your source text contains "The plane, 
//	 * the plane!", using "the" for a stop-word is enough to block both "the" and "The".
//	 * <p>
//	 * It doesn't matter whether this is called before or after the "for{text}" methods.
//	 * <p>
//	 * <b><i>Note:</i></b> Stop-words have no effect if you're passing in your own custom
//	 * {@link Word} array, since WordCram won't do any text analysis on it (other than 
//	 * sorting the words and scaling their weights).
//	 * 
//	 * @see StopWords
//	 * 
//	 * @param stopWords a space-delimited String of words to ignore when counting the words in your text.
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram withStopWords(String stopWords) {
//		this.stopWords = stopWords;
//		return this;
//	}
//	
//	/**
//	 * Exclude numbers from the text in the WordCram.  They're excluded by default.
//	 * <p>
//	 * Words that are all numbers, like 1, 3.14159, 42, or 1492, will be excluded.
//	 * Words that have some letters and some numbers like 1A, U2, or funnyguy194 will be included.
//	 *
//	 * @see #includeNumbers()
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram excludeNumbers() {
//		this.excludeNumbers = true;
//		return this;
//	}
//	
//	/**
//	 * Include numbers from the text in the WordCram.  They're excluded by default.
//	 *
//	 * @see #excludeNumbers()
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram includeNumbers() {
//		this.excludeNumbers = false;
//		return this;
//	}
//	
//	/**
//	 * Make the WordCram change all words to lower-case.  
//	 * Stop-words are unaffected; they're always case-insensitive.
//	 * The default is to keep words as they appear in the text.
//	 * 
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram lowerCase() {
//		this.textCase = TextCase.Lower;
//		return this;
//	}
//	
//	/**
//	 * Make the WordCram change all words to upper-case.  
//	 * Stop-words are unaffected; they're always case-insensitive.
//	 * The default is to keep words as they appear in the text.
//	 * 
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram upperCase() {
//		this.textCase = TextCase.Upper;
//		return this;
//	}
//	
//	/**
//	 * Make the WordCram leave all words cased as they appear in the text.  
//	 * Stop-words are unaffected; they're always case-insensitive.
//	 * This is the default.
//	 * 
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram keepCase() {
//		this.textCase = TextCase.Keep;
//		return this;
//	}
//	
//	/**
//	 * Make a WordCram from the text on a web page.
//	 * Just before the WordCram is drawn, it'll load the web page's HTML, scrape out the text, 
//	 * and count and sort the words.
//	 * 
//	 * @param webPageAddress the URL of the web page to load 
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram fromWebPage(String webPageAddress) {
//		return fromText(new WebPage(webPageAddress, parent));
//	}
//	
//	/**
//	 * Make a WordCram from the text in a HTML file.
//	 * Just before the WordCram is drawn, it'll load the file's HTML, scrape out the text, 
//	 * and count and sort the words.
//	 * 
//	 * @param htmlFilePath the path of the html file to load 
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram fromHtmlFile(String htmlFilePath) {
//		return fromText(new WebPage(htmlFilePath, parent));
//	}
//	
//	// TODO from an inputstream!  or reader, anyway
//	
//	/**
//	 * Makes a WordCram from a String of HTML.  Just before the
//	 * WordCram is drawn, it'll scrape out the text from the HTML,
//	 * and count and sort the words. It takes one String, or any
//	 * number of Strings, or an array of Strings, so you can
//	 * easily use it with <a
//	 * href="http://processing.org/reference/loadStrings_.html"
//	 * target="blank">loadStrings()</a>.
//	 * 
//	 * @param html the String(s) of HTML
//	 * @return The WordCram, for further setup or drawing. 
//	 */
//	//example fromHtmlString(loadStrings("my.html"))
//	//example fromHtmlString("<html><p>Hello there!</p></html>")
//	public WordCram fromHtmlString(String... html) {
//		return fromText(new Html(PApplet.join(html, "")));
//	}
//	
//	/**
//	 * Makes a WordCram from a text file, either on the filesystem
//	 * or the network.  Just before the WordCram is drawn, it'll
//	 * load the file, and count and sort its words.
//	 * 
//	 * @param textFilePathOrUrl the path of the text file
//	 * @return The WordCram, for further setup or drawing. 
//	 */
//	public WordCram fromTextFile(String textFilePathOrUrl) {
//		return fromText(new TextFile(textFilePathOrUrl, parent));
//	}
//	
//	/**
//	 * Makes a WordCram from a String of text. It takes one
//	 * String, or any number of Strings, or an array of Strings,
//	 * so you can easily use it with <a
//	 * href="http://processing.org/reference/loadStrings_.html"
//	 * target="blank">loadStrings()</a>.
//	 * 
//	 * @param text the String of text to get the words from
//	 * @return The WordCram, for further setup or drawing. 
//	 */
//	//example fromTextString(loadStrings("my.txt"))
//	//example fromTextString("Hello there!")
//	public WordCram fromTextString(String... text) {
//		return fromText(new Text(PApplet.join(text, "")));
//	}
//	
//	/**
//	 * Makes a WordCram from any TextSource.
//	 * 
//         * <p> It only caches the TextSource - it won't load the text
//	 * from it until {@link #drawAll()} or {@link #drawNext()} is
//	 * called.
//	 * 
//	 * @param textSource the TextSource to get the text from.
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram fromText(TextSource textSource) {
//		this.textSource = textSource;
//		return this;
//	}

	
	/**
	 * This WordCram will render words in one of the given 
	 * <a href="http://processing.org/reference/PFont.html" target="blank">PFonts</a>.
	 * 
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withFonts(Typeface... fonts) {
		return withFonter(Fonters.pickFrom(fonts));
	}
	
	/**
	 * Make the WordCram render all words in the given
	 * <a href="http://processing.org/reference/PFont.html" target="blank">PFont</a>.
	 * 
	 * @param font the PFont to render the words in.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withFont(Typeface font) {
		return withFonter(Fonters.pickFrom(font));
	}
	
	/**
	 * Use the given WordFonter to pick fonts for each word.
	 * You can make your own, or use a pre-fab one from {@link Fonters}.
	 * 
	 * @see WordFonter
         * @see Fonters
	 * @param fonter the WordFonter to use.
	 * @return The WordCram, for further setup or drawing.
	 */
	/*=
	 * Here is a bit of a play-ground for now, to see how
	 * this might work. See docgen.rb.
	 * example withFonter({your WordFonter})
	 * example withFonter(Fonters.alwaysUse("Comic Sans"))
	 * example withFonter(new WordFonter() { ... (how to doc-gen this?)
	 =*/
	public WordCloud withFonter(WordFonter fonter) {
		this.fonter = fonter;
		return this;
	}
	
	/**
	 * Make the WordCram size words by their weight, where the
	 * "heaviest" word will be sized at <code>maxSize</code>.
	 * 
         * <p>Specifically, it makes the WordCram use {@link
         * Sizers#byWeight(int, int)}.
	 * 
	 * @param minSize the size to draw a Word of weight 0
	 * @param maxSize the size to draw a Word of weight 1
	 * @return The WordCram, for further setup or drawing.
	 */
	/*=example sizedByWeight(int minSize, int maxSize)=*/
	public WordCloud sizedByWeight(int minSize, int maxSize) {
		return withSizer(Sizers.byWeight(minSize, maxSize));
	}
	
	/**
	 * Make the WordCram size words by their rank.  The first
	 * word will be sized at <code>maxSize</code>.
	 * 
         * <p>Specifically, it makes the WordCram use {@link
         * Sizers#byRank(int, int)}.
	 * 
	 * @param minSize the size to draw the last Word
	 * @param maxSize the size to draw the first Word
	 * @return The WordCram, for further setup or drawing.
	 */
	/*=example sizedByRank(int minSize, int maxSize)=*/
	public WordCloud sizedByRank(int minSize, int maxSize) {
		return withSizer(Sizers.byRank(minSize, maxSize));
	}

	/**
	 * Use the given WordSizer to pick fonts for each word.
	 * You can make your own, or use a pre-fab one from {@link Sizers}.
	 * 
         * @see WordSizer
	 * @see Sizers
	 * @param sizer the WordSizer to use.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withSizer(WordSizer sizer) {
		this.sizer = sizer;
		return this;
	}
	
	/**
	 * Render words by randomly choosing from the given colors.
	 * Uses {@link Colorers#pickFrom(int...)}.
	 *
	 * <p> Note: if you want all your words to be, say, red,
	 * <i>don't</i> do this:
	 *
	 * <pre>
	 * ...withColors(255, 0, 0)...  // Not what you want!
	 * </pre>
         *
	 * You'll just see a blank WordCram.  Since <a
	 * href="http://processing.org/reference/color_datatype.html"
	 * target="blank">Processing stores colors as integers</a>,
	 * WordCram will see each integer as a different color, and
	 * it'll color about 1/3 of your words with the color
	 * represented by the integer 255, and the other 2/3 with the
	 * color represented by the integer 0.  The punchline is,
	 * Processing stores opacity (or alpha) in the highest bits
	 * (the ones used for storing really big numbers, from
	 * 2<sup>24</sup> to 2<sup>32</sup>), so your colors 0 and 255
	 * have, effectively, 0 opacity -- they're completely
	 * transparent.  Oops.
	 * 
	 * <p> Use this instead, and you'll get what you're after:
	 *
	 * <pre>
	 * ...withColors(color(255, 0, 0))...  // Much better!
	 * </pre>
	 * 
	 * @param colors the colors to randomly choose from.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withColors(int... colors) {
		return withColorer(Colorers.pickFrom(colors));
	}
	
	/**
	 * Renders all words in the given color.
	 * @see #withColors(int...)
	 * @param color the color for each word.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withColor(int color) {
		return withColors(color);
	}

	/**
	 * Use the given WordColorer to pick colors for each word.
	 * You can make your own, or use a pre-fab one from {@link Colorers}.
	 * 
	 * @see WordColorer
	 * @see Colorers
	 * @param colorer the WordColorer to use.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withColorer(WordColorer colorer) {
		this.colorer = colorer;
		return this;
	}

	// TODO need more overloads!
	
	/**
	 * Make the WordCram rotate each word at one of the given angles. 
	 * @param anglesInRadians The list of possible rotation angles, in radians
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud angledAt(float... anglesInRadians) {
		return withAngler(Anglers.pickFrom(anglesInRadians));
	}
	
	/**
	 * Make the WordCram rotate words randomly, between the min and max angles. 
	 * @param minAngleInRadians The minimum rotation angle, in radians
	 * @param maxAngleInRadians The maximum rotation angle, in radians
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud angledBetween(float minAngleInRadians, float maxAngleInRadians) {
		return withAngler(Anglers.randomBetween(minAngleInRadians, maxAngleInRadians));
	}

	/**
	 * Use the given WordAngler to pick angles for each word.
	 * You can make your own, or use a pre-fab one from {@link Anglers}.
	 * 
	 * @see WordAngler
	 * @see Anglers
	 * @param angler the WordAngler to use.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withAngler(WordAngler angler) {
		this.angler = angler;
		return this;
	}

	/**
	 * Use the given WordPlacer to pick locations for each word.
	 * You can make your own, or use a pre-fab one from {@link Placers}.
	 * 
	 * @see WordPlacer
	 * @see Placers
	 * @see PlottingWordPlacer
	 * @param placer the WordPlacer to use.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withPlacer(WordPlacer placer) {
		this.placer = placer;
		return this;
	}

	/**
	 * Use the given WordNudger to pick angles for each word.
	 * You can make your own, or use a pre-fab one.
	 * 
	 * @see WordNudger
	 * @see SpiralWordNudger
	 * @see RandomWordNudger
	 * @see PlottingWordNudger
	 * @param nudger the WordNudger to use.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud withNudger(WordNudger nudger) {
		this.nudger = nudger;
		return this;
	}
	
	/**
	 * How many attempts should be used to place a word.  Higher
	 * values ensure that more words get placed, but will make
	 * algorithm slower.
	 * @param maxAttempts
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud maxAttemptsToPlaceWord(int maxAttempts) {
		renderOptions.maxAttemptsToPlaceWord = maxAttempts;
		return this;
	}
	
	/**
	 * The maximum number of Words WordCram should try to draw.
	 * This might be useful if you have a whole bunch of words,
	 * and need an artificial way to cut down the list (for
	 * speed).  By default, it's unlimited.
	 * @param maxWords can be any value from 0 to Integer.MAX_VALUE. Values < 0 are treated as unlimited.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud maxNumberOfWordsToDraw(int maxWords) {
		renderOptions.maxNumberOfWordsToDraw = maxWords;
		return this;
	}
	
	/**
	 * The smallest-sized Shape the WordCram should try to draw.
	 * By default, it's 7.
	 * @param minShapeSize the size of the smallest Shape.
	 * @return The WordCram, for further setup or drawing.
	 */
	public WordCloud minPathSize(int minPathSize) {
		renderOptions.minPathSize = minPathSize;
		return this;
	}
	
//	/**
//	 * Use a custom canvas instead of the applet's default one.
//	 * This may be needed if rendering in background or in other
//	 * dimensions than the applet size is needed.
//	 * @param canvas the canvas to draw to
//	 * @return The WordCram, for further setup or drawing.
//	 */
//	public WordCram withCustomCanvas(PGraphics canvas) {
//		this.destination = canvas;
//		return this;
//	}
//	
//	
	private WordCloudEngine getWordCloudEngine() {
		if (wordCloudEngine == null) {

//			if (words == null && textSource != null) {
//				String text = textSource.getText();
//				
//				text = textCase == TextCase.Lower ? text.toLowerCase()
//				     : textCase == TextCase.Upper ? text.toUpperCase()
//				     : text;
//				
//				String[] wordStrings = new WordScanner().scanIntoWords(text);
//				words = new WordCounter(stopWords).shouldExcludeNumbers(excludeNumbers).count(wordStrings);
//			}
//			words = new WordSorterAndScaler().sortAndScale(words);
//			

			
//			if (fonter == null) fonter = Fonters.alwaysUse(this.typeFace);
			if (sizer == null) sizer = Sizers.byWeight(5, 65);
			if (colorer == null) colorer = Colorers.alwaysUse(Color.WHITE);
			if (colorer == null) colorer = Colorers.twoHuesRandomSats();
			if (angler == null) angler = Anglers.horiz();
			if (placer == null) placer = Placers.horizLine();
			if (nudger == null) nudger = new SpiralWordNudger();
			
			wordCloudEngine = new WordCloudEngine(wWords, fonter, sizer, colorer, angler, new WordShaper(), placer, nudger, renderOptions);
		}
		
		return wordCloudEngine;
	}
	

	/**
	 * If you're drawing the words one-at-a-time using {@link
	 * #drawNext()}, this will tell you whether the WordCram has
	 * any words left to draw.
	 * @return true if the WordCram has any words left to draw; false otherwise.
	 * @see #drawNext()
	 */
	public boolean hasMore() {
		return getWordCloudEngine().hasMore();
	}

	/**
	 * If the WordCram has any more words to draw, draw the next
	 * one.
	 * @see #hasMore()
	 * @see #drawAll()
	 */
	public void drawNext(Canvas canvas, int statusBarHeight) {
		getWordCloudEngine().drawNext(canvas, statusBarHeight);
	}
	
	/**
	 * Just like it sounds: draw all the words.  Once the WordCram
	 * has everything set, call this and wait just a bit.
	 * @see #drawNext()
	 */
	public void drawAll(Canvas canvas, int statusBarHeight) {
		getWordCloudEngine().drawAll(canvas, statusBarHeight);
	}
//	
//	/** 
//	 * Get the Words that WordCram is drawing. This can be useful
//	 * if you want to inspect exactly how the words were weighted,
//	 * or see how they were colored, fonted, sized, angled, or
//	 * placed, or why they were skipped.
//	 */
//	public Word[] getWords() {
//		Word[] wordsCopy = new Word[words.length];
//		System.arraycopy(words, 0, wordsCopy, 0, words.length);
//		return wordsCopy;
//	}
//	
//	/**
//	 * Get the Word at the given (x,y) coordinates.
//	 * 
//	 * <p>This can be called while the WordCram is rendering, or
//	 * after it's done.  If a Word is too small to render, or
//	 * hasn't been placed yet, it will never be returned by this
//	 * method.
//	 * 
//	 * @param x the X coordinate
//	 * @param y the Y coordinate
//	 * @return the Word that covers those coordinates, or null if there isn't one 
//	 */
//	public Word getWordAt(float x, float y) {
//		return getWordCramEngine().getWordAt(x, y);
//	}
//	
//	/**
//	 * Returns an array of words that could not be placed.
//	 * @return An array of the skipped words
//	 */
//	public Word[] getSkippedWords() {
//		return getWordCramEngine().getSkippedWords();
//	}
//	
//	/**
//	 * How far through the words are we? Useful for when drawing
//	 * to a custom PGraphics.
//	 * @return The current point of progress through the list, as a float between 0 and 1. 
//	 */
//	public float getProgress() {
//		return getWordCramEngine().getProgress();
//	}

	public Word[] getWordsAt(float x, float y, float radius) {
		return getWordCloudEngine().getWordsAt(x, y, radius);
	}
	
}
