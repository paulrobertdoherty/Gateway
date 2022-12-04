package com.beetl.gateway.filters;

import java.util.*;

import com.beetl.gateway.filters.helpers.*;

import android.graphics.*;

public class Blur implements Filter {
	/**
	 * The current X coordinate the filter is on.
	 */
	private int cX = 0;
	
	/**
	 * The current Y coordinate the filter is on.
	 */
	private int cY = 0;
	
	/**
	 * The square root of the total points.
	 */
	private int pointsSquareRoot = 2;
	
	/**
	 * The x diameter of the blur.
	 */
	private int diaX = 0;
	
	/**
	 * The y diameter of the blur.
	 */
	private int diaY = 0;

	@Override
	public int filterPixel(int currentImageColor, int[] otherColors, int i) {
		//The values to return
		int finalRed =
				interpolate(
						interpolate(Color.red(otherColors[0]), Color.red(otherColors[1]),
							(float)(cX % diaX) / (float)diaX),
						interpolate(Color.red(otherColors[3]), Color.red(otherColors[2]),
							(float)(cX % diaX) / (float)diaX),
						(float)(cY % diaY) / (float)diaY);
		
		int finalGreen =
				interpolate(
						interpolate(Color.green(otherColors[0]), Color.green(otherColors[1]),
							(float)(cX % diaX) / (float)diaX),
						interpolate(Color.green(otherColors[3]), Color.green(otherColors[2]),
							(float)(cX % diaX) / (float)diaX),
						(float)(cY % diaY) / (float)diaY);
		
		int finalBlue = 
				interpolate(
						interpolate(Color.blue(otherColors[0]), Color.blue(otherColors[1]),
							(float)(cX % diaX) / (float)diaX),
						interpolate(Color.blue(otherColors[3]), Color.blue(otherColors[2]),
							(float)(cX % diaX) / (float)diaX),
						(float)(cY % diaY) / (float)diaY);
		
		//Return the final values as one color
		return Color.rgb(finalRed, finalGreen, finalBlue);
	}
	
	/**
	 * Interpolates two integers using cosine and returns an integer, where x is the percentage close to n2.
	 * @param n1
	 * @param n2
	 * @param x
	 * @return An interpolated number
	 */
	private int interpolate(int n1, int n2, float x) {
		//The percentage fixed by the magic of cosine
		float x2 = (float)((1 - Math.cos(x * Math.PI)) / 2);
		
		//Linear interpolation
		return (int)(n1 * (1 - x2)) + (int)(n2 * x2);
	}

	@Override
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight) {
		//Create a list of coordinates to return
		ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
		
		//Set the diameters
		if (diaX == 0 || diaY == 0) {
			diaX = imageWidth / pointsSquareRoot;
			diaY = imageHeight / pointsSquareRoot;
		}
		
		//Add each sample
		returnList.add(new Coordinate(currentX - (currentX % diaX), currentY - (currentY % diaY)));
		
		//If in bounds
		if (currentX + diaX < imageWidth) {
			returnList.add(new Coordinate(currentX + (diaX - (currentX % diaX)), currentY - (currentY % diaY)));
			
			if (currentY + diaY < imageHeight) {
				returnList.add(new Coordinate(currentX + (diaX - (currentX % diaX)), currentY + (diaY - (currentY % diaX))));
			} else {
				returnList.add(new Coordinate(currentX + (diaX - (currentX % diaX)), imageHeight - 1));
			}
		//Otherwise
		} else {
			returnList.add(new Coordinate(imageWidth - 1, currentY - (currentY % diaY)));
			
			if (currentY + diaY < imageHeight) {
				returnList.add(new Coordinate(imageWidth - 1, currentY + (diaY - (currentY % diaX))));
			} else {
				returnList.add(new Coordinate(imageWidth - 1, imageHeight - 1));
			}
		}
		
		if (currentY + diaY < imageHeight) {
			returnList.add(new Coordinate(currentX - (currentX % diaX), currentY + (diaY - (currentY % diaX))));
		} else {
			returnList.add(new Coordinate(currentX - (currentX % diaX), imageHeight - 1));
		}
		
		//Set the current x and y
		cX = currentX;
		cY = currentY;
		
		//Then return the list as an array
		return (Coordinate[]) Arrays.copyOf(returnList.toArray(), returnList.size(), Coordinate[].class);
	}

	@Override
	public int getNumberOfPasses() {
		//This filter uses one pass
		return 1;
	}

	@Override
	public void betweenPasses(int i) {
		//Do nothing
	}
}