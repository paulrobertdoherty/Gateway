package com.beetl.gateway.filters;

import com.beetl.gateway.filters.helpers.*;

import android.graphics.*;

public class ExaggerateColors implements Filter {
	/**
	 * A variable representing the image's highest color value.
	 */
	private int highestColorValue = 0;

	@Override
	public int filterPixel(int currentImageColor, int[] otherColors, int i) {
		//For each pass
		switch (i) {
			case 0:
				//Create different variables for the red, green, and blue colors
				int red = Color.red(currentImageColor);
				int green = Color.green(currentImageColor);
				int blue = Color.blue(currentImageColor);
				
				//Create a variable for the lowest color value
				int lowestColorValue = 0;
				
				//See if the highest color value is lower than any of these color values
				if (red > highestColorValue)
					highestColorValue = red;
				if (green > highestColorValue)
					highestColorValue = green;
				if (blue > highestColorValue)
					highestColorValue = blue;
				
				//Sets the lowest color value to the lowest color value
				if (red < green) {
					if (red < blue) {
						lowestColorValue = red;
					} else {
						lowestColorValue = blue;
					}
				} else if (green < blue) {
					lowestColorValue = green;
				} else {
					lowestColorValue = blue;
				}
				
				//Create new colors depending on grayness
				int newRed = red - lowestColorValue;
				int newGreen = green - lowestColorValue;
				int newBlue = blue - lowestColorValue;
				
				//See if the highest color value is lower than any of those color values
				if (newRed > highestColorValue)
					highestColorValue = newRed;
				if (newGreen > highestColorValue)
					highestColorValue = newGreen;
				if (newBlue > highestColorValue)
					highestColorValue = newBlue;
				
				//Return the colors as one number
				return Color.rgb(newRed, newGreen, newBlue);
			case 1:
				//Create different variables for the changed red, green, and blue colors
				int red2 = Color.red(currentImageColor);
				int green2 = Color.green(currentImageColor);
				int blue2 = Color.blue(currentImageColor);
				
				//Return the exaggerated colors based on the equation exaggeratedColor = currentColor * 255
				//                                                                       __________________
				//                                                                       highestColorValue
				return Color.rgb(
						(red2 * 255)/highestColorValue,
						(green2 * 255)/highestColorValue,
						(blue2 * 255)/highestColorValue);
		}
		
		//Never called
		return 0;
	}

	@Override
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight) {
		//This filter uses no coordinates
		return null;
	}

	@Override
	public int getNumberOfPasses() {
		//Two passes, one for lowering the brightness of grey colors,
		//and one for raising the brightness of colorful colors
		return 2;
	}

	@Override
	public void betweenPasses(int i) {
		//Do nothing
	}
}