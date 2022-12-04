package com.beetl.gateway.filters;

import com.beetl.gateway.filters.helpers.*;

public class Bulge implements Filter {

	@Override
	public int filterPixel(int currentImageColor, int[] otherColors, int i) {
		//Return the color at that coordinate
		return otherColors[0];
	}

	@Override
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight) {
		//Create two variables representing the distance to the center of the image
		int xDistanceToCenter = currentX - (imageWidth / 2);
		int yDistanceToCenter = currentY - (imageHeight / 2);
		
		//And another one representing the distance in space
		float distanceToCenter = (float)Math.sqrt(Math.pow(xDistanceToCenter, 2) + Math.pow(yDistanceToCenter, 2));
		
		//A variable for the maximum radius for the bulge
		int maxRadius = 0;
		
		//If the image is in portrait perspective
		if (imageHeight > imageWidth) {
			//Set the max radius to half the width
			maxRadius = imageWidth / 2;
		//Otherwise
		} else {
			//Set it to half the height
			maxRadius = imageHeight / 2;
		}
		
		//Two variables used in the coordinate
		int x = 0;
		int y = 0;
		
		//If the current coordinate is in the maximum radius
		if (distanceToCenter <= maxRadius) {
			//The ratio of the distance to the center and the maximum radius
			float rOfDtoMR = distanceToCenter / maxRadius;
			
			//Fix x to go more towards the center as it moves to the center
			x = (imageWidth / 2) + (int)((currentX - (imageWidth / 2)) * Math.pow(rOfDtoMR, 2));
			
			//Fix y to go more towards the center as it moves to the center
			y = (imageHeight / 2) + (int)((currentY - (imageHeight / 2)) * Math.pow(rOfDtoMR, 2));
		//Otherwise
		} else {
			//Just return the normal stuff
			x = currentX;
			y = currentY;
		}
		
		//Create a coordinate array to hold the coordinate
		return new Coordinate[] {
				//Create a coordinate to direct the pixel to the new pixel
				new Coordinate(x, y)
		};
	}

	@Override
	public int getNumberOfPasses() {
		//This filter only uses one pass
		return 1;
	}

	@Override
	public void betweenPasses(int i) {
		//Do nothing
	}
}