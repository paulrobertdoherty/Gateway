package com.beetl.gateway.filters.helpers;

public interface Filter {
	/**
	 * Takes one pixel and changes its color, using other passes and colors if needed.
	 * @param currentImageColor
	 * @param otherPasses
	 * @param i
	 * @return Filtered color
	 */
	public int filterPixel(int currentImageColor, int[] otherColors, int i);
	
	/**
	 * Returns all coordinates needed for the filter.
	 * @param currentX
	 * @param currentY
	 * @param imageWidth
	 * @param imageHeight
	 * @return All needed coordinates for the filter
	 */
	public Coordinate[] getNeededCoordinates(int currentX, int currentY, int imageWidth, int imageHeight);

	/**
	 * Gets the number of passes a filter needs.
	 * @return Number of passes a filter needs
	 */
	public int getNumberOfPasses();
	
	/**
	 * A method called whenever a filter is between passes, including at the start.
	 * @param i
	 */
	public void betweenPasses(int i);
}