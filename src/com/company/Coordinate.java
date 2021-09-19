package com.company;
public class Coordinate {
	private int x;
	private int y;
	private int distance = -1;
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public boolean isEqual(Coordinate c2) {
		if (this.x==c2.getX() && this.y==c2.getY()) {
			return true;
		}else {
			return false;
		}
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}
}
