package com.michaelmatveev.wordcloud;

import android.graphics.Matrix;

public class ARectangle {

	private float topLeftX;
	private float topLeftY;
	private float topRightX;
	private float topRightY;
	private float bottomLeftX;
	private float bottomLeftY;
	private float bottomRightX;
	private float bottomRightY;
	
	public ARectangle(float topLeftX, float topLeftY, float topRightX, float topRightY,
			float bottomLeftX, float bottomLeftY, float bottomRightX, float bottomRightY) {
		this.topLeftX     = topLeftX;
		this.topLeftY     = topLeftY;
		this.topRightX    = topRightX;
		this.topRightY    = topRightY;
		this.bottomLeftX  = bottomLeftX;
		this.bottomLeftY  = bottomLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
		
	}
	
	public ARectangle(float[] points) {
		this.topLeftX     = points[0];
		this.topLeftY     = points[1];
		this.topRightX    = points[2];
		this.topRightY    = points[3];
		this.bottomLeftX  = points[4];
		this.bottomLeftY  = points[5];
		this.bottomRightX = points[6];
		this.bottomRightY = points[7];		
	}
	
	public ARectangle RotateAroundCenter(float angle) {
		
		float[] srcRect = {this.topLeftX, this.topLeftY, this.topRightX, this.topRightY,
			this.bottomLeftX, this.bottomLeftY, this.bottomRightX, this.bottomRightY};
			
		float[] rotatedRect = new float[8];
		
		float centerX = this.topRightX - this.bottomLeftX;
		float centerY = this.topRightY - this.bottomLeftY;
		
		Matrix matrix = new Matrix();		
		matrix.reset();
		matrix.setRotate(angle,centerX,centerY);	
		matrix.mapPoints(rotatedRect, srcRect);
		
		ARectangle x = new ARectangle(rotatedRect);		
		return x;
	}
	
	

}