package com.company;
public class Number {
	private char color;
	private Coordinate position;
	private int value;
	private Stack path;
	
	public Number(char color, int xPosition, int yPosition, int value) {
		this.color = color;
		this.position=new Coordinate(xPosition,yPosition);
		this.value=value;
	}
	
	public Number(char color, int xPosition, int yPosition, int value,int capacity) {
		this.color = color;
		this.position=new Coordinate(xPosition,yPosition);
		this.value=value;
		this.path=new Stack(capacity);
	}
	
	public void changePosition() {
		Stack tempPath=new Stack(1000);
		while (!(path.isEmpty())) {
			tempPath.push(path.pop());
		}
		this.position=(Coordinate) tempPath.pop();
		while (!(tempPath.isEmpty())) {
			path.push(tempPath.pop());
		}
	}
	public boolean addPath(Coordinate coordinate) {
		if (path.isFull()) {
			return false;
		}else {
			path.push(coordinate);
			return true;
		}
	}
	
	public void removePath() {
		this.path=null;
		this.path=new Stack(1000);
	}
	
	public Stack getPath() {
		return path;
	}

	public void setPath(Stack path) {
		this.path = path;
	}

	public char getColor() {
		return color;
	}
	public void setColor(char color) {
		this.color = color;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	public Coordinate getPosition() {
		return position;
	}
	public void setPosition(Coordinate position) {
		this.position = position;
	}


}
