package com.beetl.gateway.filters;

import com.beetl.gateway.filters.helpers.*;

import android.graphics.*;

public class SmartNegative implements Filter {
	@Override
	public int filterPixel(int currentImageColor, int[] otherColors, int i) {
		//Create different variables for the red, green, and blue colors
		int red = Color.red(currentImageColor);
		int green = Color.green(currentImageColor);
		int blue = Color.blue(currentImageColor);
		
		//Finds the average of the three colors, the lighting pass
		int average = (red + green + blue) / 3;
		
		//Create new variables representing the negative colors with lighting involved
		int newRed = contain(average - (red - average));
		int newGreen = contain(average - (green - average));
		int newBlue = contain(average - (blue - average));
		
		//Return the new color values as a color integer
		return Color.rgb(newRed, newGreen, newBlue);
	}
	
	/**
	 * If a color value is greater than 255, make it 255.
	 * If it is less than 0, make it 0.
	 * @param colorValue
	 * @return Contained color
	 */
	private int contain(int colorValue) {
		//If the color value is too high
		if (colorValue > 255) {
			//Make it the maximum
			return 255;
		//If it's too low
		} else if (colorValue < 0) {
			//Make it the minimum
			return 0;
		}
		//Otherwise don't change it
		return colorValue;
	}

	@Override
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight) {
		//This filter needs no coordinates, so we may return null
		return null;
	}
	
	@Override
	public int getNumberOfPasses() {
		//This filter only needs one pass, so we return one
		return 1;
	}
	
	@Override
	public void betweenPasses(int i) {
		//Do nothing
	}
}