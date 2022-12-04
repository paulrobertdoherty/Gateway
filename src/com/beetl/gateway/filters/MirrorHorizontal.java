package com.beetl.gateway.filters;

import com.beetl.gateway.filters.helpers.*;

public class MirrorHorizontal implements Filter {

	@Override
	public int filterPixel(int currentImageColor, int[] otherColors, int i) {
		//Return the already mirrored image
		return otherColors[0];
	}

	@Override
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight) {
		//Make a coordinate array to return.  It only has one space, since that's all it needs.
		Coordinate[] neededCoordinate = new Coordinate[1];
		
		//Set that space to the mirror opposite of the current x coordinate.
		neededCoordinate[0] = new Coordinate(imageWidth - (currentX + 1), currentY);
		
		//Then return the coordinate.
		return neededCoordinate;
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