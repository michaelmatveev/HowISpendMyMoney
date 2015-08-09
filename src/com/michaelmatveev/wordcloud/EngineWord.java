package com.michaelmatveev.wordcloud;

import com.michaelmatveev.wordcloud.AVector;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

class EngineWord {
	Word word;
	int rank;	
	
	private Path path;
	private RectF bounds;
	
//	private Shape shape;
//	private BBTreeBuilder bbTreeBuilder;
//	private BBTree bbTree;

	private AVector desiredLocation;
	private AVector currentLocation;

	EngineWord(Word word, int rank, int wordCount) {
		this.word = word;
		this.rank = rank;
	}

	void setPath(Path path) {
		this.path = path;
		
		RectF bounds = new RectF();
		path.computeBounds(bounds, true);
		this.bounds = bounds;
	}

	Path getPath() {
		return path;
	}
	
	boolean intersects(EngineWord otherEWord) {
		if (rectCollide(this, otherEWord)) {
			return true;
		}		
		return false;
	}

	private boolean rectCollide(EngineWord a, EngineWord b) {
		AVector[] aPoints = a.getPoints();
		AVector[] bPoints = b.getPoints();
		AVector aTopLeft = aPoints[0];
		AVector aBottomRight = aPoints[1];
		AVector bTopLeft = bPoints[0];
		AVector bBottomRight = bPoints[1];

		return aBottomRight.y > bTopLeft.y && aTopLeft.y < bBottomRight.y
		&& aBottomRight.x > bTopLeft.x && aTopLeft.x < bBottomRight.x;
	}
	
	private AVector[] getPoints() {
		return new AVector[] { AVector.add(new AVector(bounds.left, bounds.top), currentLocation),
				AVector.add(new AVector(bounds.right, bounds.bottom), currentLocation) };
	}	

	void setDesiredLocation(WordPlacer placer, int count, int wordImageWidth, int wordImageHeight, int fieldWidth, int fieldHeight) {
		desiredLocation = word.getTargetPlace(placer, rank, count, wordImageWidth, wordImageHeight, fieldWidth, fieldHeight);
		currentLocation = desiredLocation.get();
	}

	void nudge(AVector nudge) {
		AVector desAddNudge = AVector.add(desiredLocation, nudge);
		currentLocation = desAddNudge.get();
//		bbTree.setLocation(currentLocation.get());
	}

	void finalizeLocation() {
//		AffineTransform transform = AffineTransform.getTranslateInstance(
//				currentLocation.x, currentLocation.y);
//		shape = transform.createTransformedShape(shape);
//		bbTree.setLocation(currentLocation);
//		path.offset(currentLocation.x, currentLocation.y);
		word.setRenderedPlace(currentLocation);
	}

	AVector getCurrentLocation() {
		return new AVector(currentLocation.x, currentLocation.y);
	}
	
	boolean wasPlaced() {
		return word.wasPlaced();
	}

}
