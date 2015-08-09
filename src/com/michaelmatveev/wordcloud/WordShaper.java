package com.michaelmatveev.wordcloud;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;

class WordShaper {
	
	float[] srcRect = new float[8];
	float[] dstRect = new float[8];
	
	Path getPathFor(EngineWord eWord, Typeface font, float fontSize, float angle, int minPathSize) {

		Path path = makePath(eWord, font, fontSize);
		
		if (isTooSmall(path, minPathSize)) {
			return null;		
		}
		
//		rotate(path, angle);
		
		return path;
		
	//	return moveToOrigin(rotate(path, angle));
	}

	private Path makePath(EngineWord eWord, Typeface font, float fontSize) {		
		
		Paint paint = new Paint();
		paint.setTypeface(font);		
		paint.setTextSize(fontSize);
		
		Path path = new Path();
		paint.getTextPath(eWord.word.word, 0, eWord.word.word.length(), 0, 0, path);	
		
//		RectF rectBounds = new RectF();
//		path.computeBounds(rectBounds, true);
//		
//		srcRect[0] = rectBounds.right;
//		srcRect[1] = rectBounds.top;
//		srcRect[2] = rectBounds.right;
//		srcRect[3] = rectBounds.bottom;
//		srcRect[4] = rectBounds.left;
//		srcRect[5] = rectBounds.bottom;
//		srcRect[6] = rectBounds.left;
//		srcRect[7] = rectBounds.top;
		
		return path;
	}
	
	private boolean isTooSmall(Path path, int minPathSize) {
		RectF rectBounds = new RectF();
		path.computeBounds(rectBounds, true);
		
		// Most words will be wider than tall, so this basically boils down to height.
		// For the odd word like "I", we check width, too.
		return rectBounds.height() < minPathSize || rectBounds.width() < minPathSize;
	}
	
//	private Path rotate(Path path, float rotation) {
//		if (rotation == 0) {
//			return path;
//		}
//		
//		RectF rectBounds = new RectF();
//		path.computeBounds(rectBounds, true);
//		
////		float[] src = {rectBounds.};
////		
//		Matrix matrix = new Matrix();
//		
//		float x1 = rectBounds.centerX();
//		float y1 = rectBounds.centerY();
//		
//		
//	//	path.transform(matrix);
//		
//		PathMeasure pm = new PathMeasure();
//		pm.setPath(path, false);
//		
//		float[] mtx = new float[9];
//		Matrix matrix2 = new Matrix();
//		
//		matrix2.reset();
//		
//		matrix2.getValues(mtx);
//		
////		matrix2.setTranslate(100, 100);
//		matrix2.getValues(mtx);
//		
//		matrix2.setRotate(rotation,x1,y1);		
//		matrix2.getValues(mtx);
//		
////		pm.getMatrix(0, matrix2, 0x01 | 0x02);
////		matrix2.getValues(mtx);
//		
////		matrix.reset();
//		
////		matrix.preRotate(rotation, rectBounds.centerX(), rectBounds.centerY());
//		
////		matrix.setTranslate(100, 100);
////		matrix.setRotate(rotation, 100, 100);
//		
//		RectF rectBounds2 = new RectF();
//		path.computeBounds(rectBounds2, true);
//		path.transform(matrix2);
//		path.computeBounds(rectBounds2, true);		
//		matrix2.mapPoints(dstRect, srcRect);
//		
//		if (rectBounds2.isEmpty())
//			path.computeBounds(rectBounds2, true);
//		
//		RectF newR = new RectF(dstRect[4], dstRect[1], dstRect[0], dstRect[3]);
//		
//
//		
//		
////		
////		
////		
////		float[] src = {10,5,10,-5,-10,-5,-10,5};
////		float[] dist = new float[8];
////		Matrix matrix = new Matrix();
//////		matrix.setTranslate(10, 10);
////		
////		matrix.setRotate(30, 0, 0);
////		matrix.mapVectors(dist, src);
//
//		
////		ShapeDrawable shape = new ShapeDrawable();
//
//		
//		return path;
//	}

//	private Shape moveToOrigin(Shape shape) {
//		Rectangle2D rect = shape.getBounds2D();
//		
//		if (rect.getX() == 0 && rect.getY() == 0) {
//			return shape;
//		}
//		
//		return AffineTransform.getTranslateInstance(-rect.getX(), -rect.getY()).createTransformedShape(shape);
//	}
}