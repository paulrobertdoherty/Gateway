package com.beetl.gateway.filters.helpers;

public class Coordinate {
	
	/**
	 * The x coordinate value.
	 */
	private int x = 0;
	
	/**
	 * The y coordinate value.
	 */
	private int y = 0;

	public Coordinate(int x, int y) {
		//Set the coordinate values
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x coordinate from a coordinate.
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y coordinate from a coordinate.
	 * @return y
	 */
	public int getY() {
		return y;
	}
}