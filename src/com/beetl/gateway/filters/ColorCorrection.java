package com.beetl.gateway.filters;

import com.beetl.gateway.filters.helpers.*;

import android.graphics.*;
import android.util.*;

public class ColorCorrection implements Filter {
	
	/**
	 * All the used red values and their frequency.
	 */
	private SparseIntArray rValues = new SparseIntArray();
	
	/**
	 * All the used green values and their frequency.
	 */
	private SparseIntArray gValues = new SparseIntArray();
	
	/**
	 * All the used blue values and their frequency.
	 */
	private SparseIntArray bValues = new SparseIntArray();
	
	@Override
	public int filterPixel(int currentImageColor, int[] otherColors, int i) {
		//For each pass
		switch(i) {
			//In the first pass
			case 0:
				{
				//Create different variables for the red, green, and blue colors
				int red = Color.red(currentImageColor);
				int green = Color.green(currentImageColor);
				int blue = Color.blue(currentImageColor);
				
				//If the value has been written in before
				if (rValues.get(red) > 0) {
					//Put values in the red values hashmap
					rValues.put(Integer.valueOf(red), Integer.valueOf(rValues.get(red) + 1));
				} else {
					//Put values in the red values hashmap
					rValues.put(Integer.valueOf(red), 2);
				}
				
				//If the value has been written in before
				if (gValues.get(green) > 0) {
					//Put values in the green values hashmap
					gValues.put(Integer.valueOf(green), Integer.valueOf(gValues.get(green) + 1));
				} else {
					//Put values in the green values hashmap
					gValues.put(Integer.valueOf(green), 2);
				}
				
				//If the value has been written in before
				if (bValues.get(blue) > 0) {
					//Put values in the blue values hashmap
					bValues.put(Integer.valueOf(blue), Integer.valueOf(bValues.get(blue) + 1));
				} else {
					//Put values in the blue values hashmap
					bValues.put(Integer.valueOf(blue), 2);
				}
				
				//Return the unchanged image
				return currentImageColor;
				}
				
			//In the second pass
			case 1:
				//Create different variables for the red, green, and blue colors
				int red = Color.red(currentImageColor);
				int green = Color.green(currentImageColor);
				int blue = Color.blue(currentImageColor);
				
				//Set the return color to its set redirects
				return Color.rgb(
						rValueRedirects[red], 
						gValueRedirects[green],
						bValueRedirects[blue]);
		}
		
		//Never seen
		return 0;
	}

	@Override
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight) {
		//This filter uses no coordinates
		return null;
	}

	@Override
	public int getNumberOfPasses() {
		//Two passes, one for measuring the concentration of color values,
		//and one for equalizing the colors
		return 2;
	}
	
	private int[]
			/**
			 * The redirects for the red values.
			 */
			rValueRedirects = null,
			/**
			* The redirects for the green values.
			*/
			gValueRedirects = null,
			/**
			* The redirects for the blue values.
			*/
			bValueRedirects = null;

	@Override
	public void betweenPasses(int i) {
		//If this is called after the first pass
		if (i == 0) {
			//Set the red value redirects
			rValueRedirects = makeRedirect(rValues);
			
			//Set the green value redirects
			gValueRedirects = makeRedirect(gValues);
			
			//Set the blue value redirects
			bValueRedirects = makeRedirect(bValues);
			
			//Set all of the HashMaps to null to conserve memory
			rValues = bValues = gValues = null;
		}
	}

	/**
	 * Creates a redirection integer array from a list of values used and a frequency in which the values are used.
	 * @param values
	 * @return Redirect array
	 */
	private int[] makeRedirect(SparseIntArray values) {
		//Create a variable to return
		int[] returnRedirect = new int[256];
		
		//And a variable to hold the values needed for concentration of pixels
		int[] totalNumberSum = new int[256];
		
		//Make a variable for the last value the method was using and the total sum of the values
		int last = 0, totalSum = 0;
		
		//For each recorded value
		for (int i = 0; i < 256; i++) {
			//If the image has this value
			if (values.get(i) > 0) {
				//Get the current half concentration
				int currentHalfConcentration = values.get(i) / 2;
				
				//If this is not the first time the redirect is being made
				if (last > 0) {
					//Set the return redirect to the current half concentration plus the other half concentrations
					returnRedirect[i] = currentHalfConcentration + totalNumberSum[last];
				//Otherwise
				} else {
					//Set the return redirect to the current half concentration 
					returnRedirect[i] = currentHalfConcentration;
				}
				
				//Set the current number sum to the total concentration, plus the current redirect, for future use
				totalNumberSum[i] = returnRedirect[i] + (values.get(i) / 2);
				
				//Set the last used number to the current number
				last = i;
			}
		}
		
		//Set the total sum
		totalSum = returnRedirect[last] + values.get(last);
		
		//Now fix the values
		for (int i = 0; i < 256; i++) {
			//If the image has this value
			if (values.get(i) > 0) {
				//Set the return redirect to the fixed redirect
				returnRedirect[i] = (int)(((float)returnRedirect[i] / (float)totalSum) * 256);
			}
		}
		
		//Return the variable
		return returnRedirect;
	}
}