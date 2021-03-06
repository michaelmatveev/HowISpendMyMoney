package com.michaelmatveev.wordcloud;

import android.graphics.Typeface;
import com.michaelmatveev.wordcloud.AVector;

/**
 * A WordColorer tells WordCram what color to render a word in.
 * <p>
 * <b>Note:</b> if you implement your own WordColorer, you should be familiar
 * with how <a href="http://processing.org/reference/color_datatype.html"
 * target="blank">Processing represents colors</a> -- or just make sure it uses
 * Processing's <a href="http://processing.org/reference/color_.html"
 * target="blank">color</a> method.
 * <p>
 * Some useful implementations are available in {@link Colorers}.
 */
interface WordColorer {

	/**
	 * What color should this {@link Word} be?
	 * 
	 * @param word the word to pick the color for
	 * @return the color for the word
	 */
	public int colorFor(Word word);
}

/**
 * A WordFonter tells WordCram what font to render a word in.
 * <p>
 * Some useful implementations are available in {@link Fonters}.
 * <p>
 * <i>The name "WordFonter" was picked because it matches the others: WordColorer,
 * WordPlacer, WordSizer, etc. Apologies if it sounds a bit weird to your ear; I
 * eventually got used to it.</i>
 * 
 * @author Dan Bernier
 */
interface WordFonter {
	
	/**
	 * What font should this {@link Word} be drawn in?
	 * @param word the word to pick the PFont for
	 * @return the PFont for the word
	 */
	public Typeface fontFor(Word word);
}

/**
 * Once a WordPlacer tells WordCram where a word <i>should</i> go, a WordNudger
 * tells WordCram how to nudge it around the field, until it fits in with the
 * other words around it, or the WordCram gives up on the word.
 * <p>
 * WordCram gets a PVector from the nudger, and adds it to the word's desired
 * location, to find the next spot to try fitting the word. Note that the
 * PVectors returned from a nudger <i><b>don't accumulate</b></i>: if the placer
 * puts a Word at (0, 0), and the nudger returns (1, 1), and then (2, 2),
 * WordCram will try the word at (1, 1), and then (2, 2) -- <i>not</i> (1, 1)
 * and then (3, 3).
 * <p>
 * A WordNudger should probably start nudging the word only a little, to keep it
 * near its desired location, and gradually nudge it more and more, so that,
 * even if the desired area is congested, the word can still fit in somewhere.
 * This is why the WordCram passes in <code>attemptNumber</code>: it's the
 * number of times it's attempted to place the word. This could (for example)
 * scale the PVector, since the nudges don't accumulate (see above).
 * 
 * @see RandomWordNudger
 * @see SpiralWordNudger
 * 
 */
interface WordNudger {

	/**
	 * How should this word be nudged, this time?
	 * 
	 * @param word
	 *            the word to nudge
	 * @param attemptNumber
	 *            how many times WordCram has tried to place this word; starts
	 *            at zero, and ends at
	 *            <code>(int)((1.0-word.weight) * 600) + 100</code>
	 * @return the PVector to add to the word's desired location, to get the
	 *         next spot to try fitting the word
	 */
	public AVector nudgeFor(Word word, int attemptNumber);
}

/**
 * A WordPlacer tells WordCram where to place a word (in x,y coordinates) on the field.
 * <p>
 * A WordPlacer only suggests: the WordCram will try to place the Word where the
 * WordPlacer tells it to, but if the Word overlaps other Words, a WordNudger
 * will suggest different near-by spots for the Word until it fits, or until the
 * WordCram gives up.
 * <p>
 * Some useful implementations are available in {@link Placers}.
 * 
 */
interface WordPlacer {

	/**
	 * Where should this {@link Word} be drawn on the field?
	 * 
	 * @param word
	 *            The Word to place. A typical WordPlacer might use the Word's
	 *            weight.
	 * @param wordIndex
	 *            The index (rank) of the Word to place. Since this isn't a
	 *            property of the Word, it's passed in as well.
	 * @param wordsCount
	 *            The total number of words. Gives a context to wordIndex:
	 *            "Word {wordIndex} of {wordsCount}".
	 * @param wordImageWidth
	 *            The width of the word image.
	 * @param wordImageHeight
	 *            The height of the word image.
	 * @param fieldWidth
	 *            The width of the field.
	 * @param fieldHeight
	 *            The height of the field.
	 * @return the desired location for a Word on the field, as a 2D PVector.
	 */
	public abstract AVector place(Word word, int wordIndex, int wordsCount,
			int wordImageWidth, int wordImageHeight, int fieldWidth,
			int fieldHeight);
}

/**
 * A WordSizer tells WordCram how big to render each word. 
 * You'll pass a WordSizer to WordCram via {@link WordCram#withSizer(WordSizer)}.
 * <p>
 * Some useful implementations are available in {@link Sizers}.
 * 
 */
interface WordSizer {

	/**
	 * How big should this {@link Word} be rendered?
	 * <p>
	 * Generally, a word cloud draws more important words bigger. Two typical
	 * ways to measure word's importance are its weight, and its rank (its
	 * position in the list of words, sorted by weight).
	 * <p>
	 * Given that, sizeFor is passed the Word (which knows its own weight), its
	 * rank, and the total number of words.
	 * <p>
	 * For example, given the text "I think I can I think", the words would look
	 * like this:
	 * <ul>
	 * <li>"I", weight 1.0 (3/3), rank 1</li>
	 * <li>"think", weight 0.667 (2/3), rank 2</li>
	 * <li>"can", weight 0.333 (1/3), rank 3</li>
	 * </ul>
	 * ...and the WordSizer would be called with the following values:
	 * <ul>
	 * <li>Word "I" (weight 1.0), 1, 3</li>
	 * <li>Word "think" (weight 0.667), 2, 3</li>
	 * <li>Word "can" (weight 0.333), 3, 3</li>
	 * </ul>
	 * 
	 * @param word
	 *            the Word to determine the size of
	 * @param wordRank
	 *            the rank of the Word
	 * @param wordCount
	 *            the total number of Words being rendered
	 * @return the size to render the Word
	 */
	public float sizeFor(Word word, int wordRank, int wordCount);
}

/**
 * A WordAngler tells WordCram what angle to draw a word at, in radians.
 * <p>
 * Some useful implementations are available in {@link Anglers}.
 * 
 */
interface WordAngler {

	/**
	 * What angle should this {@link Word} be rotated at?
	 * 
	 * @param word
	 *            The Word that WordCram is about to draw, and wants to rotate
	 * @return the rotation angle for the Word, in radians
	 */
	public float angleFor(Word word);
}