package com.company;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.awt.Color;
import enigma.core.Enigma;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import enigma.console.TextAttributes;
import java.awt.Color;
import enigma.core.Enigma;

public class NumberMaze {
	// The top class of the game
	private boolean gameOver;
	private int score;
	private int playerNumber;
    int px; 
    int py;
	private Stack leftPack;
	private Stack rightPack;
	private Queue inputs;
	private int time;
	private enigma.console.Console cn;
	private KeyListener klis;
	private int keypr;
	private int rkey;
	private int[][] walls;
	private Number[] numbers;
	private int numberCounter;
	private int counterMakingTwo;

	public NumberMaze() {
		this.gameOver=false;
		this.time=0;
		numberCounter=0;
		this.px=5;
		this.py=5;
		this.playerNumber = 5;
		this.cn = Enigma.getConsole("Number Maze", 110, 30, 18);
		//.cn = Enigma.getConsole("Number Maze");
		this.leftPack = new Stack(8);
		this.rightPack = new Stack(8);
		this.walls = new int[23][55];
		this.numbers = new Number[500]; 							/////////
		this.inputs = new Queue(10000);								/////////
		counterMakingTwo = -1;
		startGame();
	}
	public void startGame() {
		int count = 0;
		cn.getTextWindow().output(64,20,'0');
		File path = new File("Maze.txt");
		try {
			Scanner reading = new Scanner(path);
			while (reading.hasNextLine()) {
				String line = reading.nextLine();
				cn.getTextWindow().output(line + '\n');
				settingGameArea(line, count);
				count++;
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		// At this point, walls are initialized
		settingRandomNumber();
		// Enigma
		showNumbers();
    	setPathsRed();

		// generate next number
		createInputs();

		enigma();
		// Starting by reading the input file
	}
	
	
	public void enigma() {
			klis=new KeyListener() {
	         public void keyTyped(KeyEvent e) {}
	         public void keyPressed(KeyEvent e) {
	            if(keypr==0) {
	               keypr=1;
	               rkey=e.getKeyCode();
	            }
	         }
	         public void keyReleased(KeyEvent e) {}
	      };
	      cn.getTextWindow().addKeyListener(klis);
	      cn.getTextWindow().setCursorPosition(px, py);
	      TextAttributes attrs=new TextAttributes(Color.BLUE);
	      cn.getTextWindow().output(String.valueOf(playerNumber),attrs);
	      attrs=new TextAttributes(Color.WHITE);
	      
	      while(!(gameOver)) {
		         if(keypr==1) {    // if keyboard button pressed
		            if(rkey==KeyEvent.VK_LEFT) px--;   
		            if(rkey==KeyEvent.VK_RIGHT) px++;
		            if(rkey==KeyEvent.VK_UP) py--;
		            if(rkey==KeyEvent.VK_DOWN) py++;
		            if(rkey==KeyEvent.VK_Q)packQOperation();
		            if(rkey==KeyEvent.VK_W)packWOperation();
		            char rckey=(char)rkey;
		            //        left          right          up            down
		            if(rckey=='%' || rckey=='\'' || rckey=='&' || rckey=='(') { 
		            	int x = px;
		            	int y = py;
		            	if(rckey=='%') {
		            		x = px + 1;
		            	}
		            	else if(rckey=='\'') {
		            		x = px - 1;
		            	}
		            	else if(rckey=='&') {
		            		y = py + 1;
		            	}
		            	else if(rckey=='(') {
		            		y = py - 1;
		            	}
		            	if (walls[py][px]!=1) {
			            	cn.getTextWindow().setCursorPosition(x, y);
			            	cn.getTextWindow().output(" ");
			            	//cn.getTextWindow().output(x,y,' ');
			            	setPathsRed();
			            	cn.getTextWindow().setCursorPosition(px, py);
						}else {
							px=x;
							py=y;
						}
		            }
		            keypr=0;    // last action  
		         }
		         try {
		        	standardTimeOperations();
					gameOver=checkCollision(px,py);
					if (gameOver) break;
					cn.getTextWindow().setCursorPosition(px, py);
					attrs=new TextAttributes(Color.BLUE);
					cn.getTextWindow().output(String.valueOf(playerNumber),attrs);
					gameOver=doRedMovement();
	            	Thread.sleep(500);
				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}
		      }
	   }

	public void standardTimeOperations() {
    	showTime();
		time++;
		if (time == counterMakingTwo + 4 && this.playerNumber == 1) {
			this.playerNumber++;
		}
		if(time%5 == 0) {
			nextNumber();
		}
		movements();
	}

	public boolean doRedMovement() {
		for (int i = 0; i < numberCounter; i++) {
			if (numbers[i]!=null && numbers[i].getColor()=='R' && numbers[i].getPath().isEmpty()==false) {
				Coordinate positionWanted=(Coordinate) numbers[i].getPath().peek();
				if (isValidPosition(positionWanted.getX(), positionWanted.getY())) {
					cn.getTextWindow().output(numbers[i].getPosition().getX(), numbers[i].getPosition().getY(),' ');
					numbers[i].changePosition();
					TextAttributes attrs = arrangeColor(i);
					cn.getTextWindow().output(numbers[i].getPosition().getX(), numbers[i].getPosition().getY(),String.valueOf(numbers[i].getValue()).charAt(0), attrs);
					if (checkCollision(px, py)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public void removeDots(Number number) {
		int length=number.getPath().size();
		for (int i = 0; i < length; i++) {
			Coordinate cord=(Coordinate)number.getPath().pop();
			cn.getTextWindow().output(cord.getX(), cord.getY(), ' ');
		}
	}
	private boolean isValidQ(Queue q, Coordinate dot){
		boolean valid = true;
		int x = dot.getX();
		int y = dot.getY();
		for(int i = 0; i < q.size(); i++){
			Coordinate element = (Coordinate) q.dequeue();
			q.enqueue(element);
			if(element.getX() == x && element.getY() == y){
				valid = false;
				break;
			}
		}
		return valid;
	}
	//TODO pathfinder
	private void findPath(Number numberCoordinate) {
		Stack path = new Stack(23*55);
		Coordinate end = new Coordinate(px, py);
		end.setDistance(0);
		Queue rawPath = new Queue(23*55);
		rawPath.enqueue(end);
		boolean found = false;
		while(!rawPath.isEmpty()){
			Coordinate dot = (Coordinate) rawPath.dequeue();
			path.push(dot);
			if(dot.getY() != 22 && walls[dot.getY() + 1][dot.getX()] == 0){
				Coordinate dotToAdd = new Coordinate( dot.getX(), dot.getY() + 1);
				if(isValidCoordinateForPath(path, dotToAdd) && isValidQ(rawPath, dotToAdd)){
					dotToAdd.setDistance(dot.getDistance() + 1);
					if(!found){
						rawPath.enqueue(dotToAdd);
					}
					if(numberCoordinate.getPosition().getX() == dotToAdd.getX() && numberCoordinate.getPosition().getY() == dotToAdd.getY()){
						found = true;
					}
				}
			}
			if(dot.getY() != 0 && walls[dot.getY() - 1][dot.getX()] == 0){
				Coordinate dotToAdd = new Coordinate( dot.getX(), dot.getY() - 1);
				if(isValidCoordinateForPath(path, dotToAdd) && isValidQ(rawPath, dotToAdd)){
					dotToAdd.setDistance(dot.getDistance() + 1);
					if(!found){
						rawPath.enqueue(dotToAdd);
					}
					if(numberCoordinate.getPosition().getX() == dotToAdd.getX() && numberCoordinate.getPosition().getY() == dotToAdd.getY()){
						found = true;
					}
				}
			}
			if(dot.getX() != 54 && walls[dot.getY()][dot.getX() + 1] == 0){
				Coordinate dotToAdd = new Coordinate( dot.getX() + 1, dot.getY());
				if(isValidCoordinateForPath(path, dotToAdd) && isValidQ(rawPath, dotToAdd)){
					dotToAdd.setDistance(dot.getDistance() + 1);
					if(!found){
						rawPath.enqueue(dotToAdd);
					}
					if(numberCoordinate.getPosition().getX() == dotToAdd.getX() && numberCoordinate.getPosition().getY() == dotToAdd.getY()){
						found = true;
					}
				}
			}
			if(dot.getX() != 0 && walls[dot.getY()][dot.getX() - 1] == 0){
				Coordinate dotToAdd = new Coordinate( dot.getX() - 1, dot.getY());
				if(isValidCoordinateForPath(path, dotToAdd) && isValidQ(rawPath, dotToAdd)){
					dotToAdd.setDistance(dot.getDistance() + 1);
					if(!found){
						rawPath.enqueue(dotToAdd);
					}
					if(numberCoordinate.getPosition().getX() == dotToAdd.getX() && numberCoordinate.getPosition().getY() == dotToAdd.getY()){
						found = true;
					}
				}
			}
		}
		while(!rawPath.isEmpty()){
			path.push(rawPath.dequeue());
		}
		int[][] dots = new int[23][55];
		for(int i = 0; i < dots.length; i++){
			for(int j = 0; j < dots[0].length; j++){
				dots[i][j] = - 1;
			}
		}
		while(!path.isEmpty()){
			Coordinate dot = (Coordinate) path.pop();
			dots[dot.getY()][dot.getX()] = dot.getDistance();
		}

		int xOfSelection = numberCoordinate.getPosition().getX();
		int yOfSelection = numberCoordinate.getPosition().getY();

		while(true){
			int minDistance = Integer.MAX_VALUE;
			int newX = -1;
			int newY = -1;
			if(xOfSelection != 0 && dots[yOfSelection][xOfSelection - 1] != -1 && dots[yOfSelection][xOfSelection - 1] < minDistance){
				minDistance = dots[yOfSelection][xOfSelection - 1];
				newX = xOfSelection - 1;
				newY = yOfSelection;
			}
			if(xOfSelection != 54 && dots[yOfSelection][xOfSelection + 1] != -1 && dots[yOfSelection][xOfSelection + 1] < minDistance){
				minDistance = dots[yOfSelection][xOfSelection + 1];
				newX = xOfSelection + 1;
				newY = yOfSelection;
			}
			if(yOfSelection != 0 && dots[yOfSelection - 1][xOfSelection] != -1 && dots[yOfSelection - 1][xOfSelection] < minDistance){
				minDistance = dots[yOfSelection - 1][xOfSelection];
				newX = xOfSelection;
				newY = yOfSelection - 1;
			}
			if(yOfSelection != 22 && dots[yOfSelection + 1][xOfSelection] != -1 && dots[yOfSelection][xOfSelection] < minDistance){
				minDistance = dots[yOfSelection + 1][xOfSelection];
				newX = xOfSelection;
				newY = yOfSelection + 1;
			}
			path.push( new Coordinate(newX, newY));
			xOfSelection = newX;
			yOfSelection = newY;
			if (minDistance == 0){
				break;
			}
		}

		numberCoordinate.setPath(path);
	}

	public void showPath(Stack path) {
		Stack tempPath=new Stack(1000);
		while (!(path.isEmpty())) {
			Coordinate coordinate=(Coordinate) path.pop();
			cn.getTextWindow().output(coordinate.getX(), coordinate.getY(), '.');
			tempPath.push(coordinate);
		}
		while (!(tempPath.isEmpty())) {
			path.push(tempPath.pop());
		}
	}
	
	public boolean isValidCoordinateForPath(Stack path, Coordinate dot) {
		int x = dot.getX();
		int y = dot.getY();
		boolean isValid=true;
		Stack tempQueue=new Stack(23*55);
		while (!(path.isEmpty())) {
			Coordinate element=(Coordinate) path.pop();
			tempQueue.push(element);
			if (element.getX()==x && element.getY()==y) {
				isValid=false;
				break;
			}
		}

		while (!(tempQueue.isEmpty())) {
			path.push(tempQueue.pop());
		}
		return isValid;
	}

	public void setPathsRed() {
		for (int i = 0; i < numberCounter; i++) {
			if (numbers[i]!=null && numbers[i].getColor()=='R') {
				if (numbers[i].getPath()!=null) {
					removeDots(numbers[i]);
					numbers[i].removePath();
					findPath(numbers[i]);
					showPath(numbers[i].getPath());
				    }
			}
		}
	}
	
	public void movements() {
		for (int i = 0; i < numberCounter; i++) {
			if (numbers[i] != null && numbers[i].getColor() == 'Y') {
				Coordinate newPosition=randomMovement(numbers[i]);
				numbers[i].setPosition(newPosition);
			}
		}

		showNumbers();
	}
	
	public Coordinate randomMovement(Number n) {
		Random rnd = new Random();
		int xx=n.getPosition().getX();
		int yy=n.getPosition().getY();
		while(true) {
			int x = xx;
			int y = yy;

	    	cn.getTextWindow().output(x,y,' ');
			int rand = rnd.nextInt(4);
			switch (rand) {
				case 0: 
					y += 1;
					break;
				case 1: 
					y -= 1;
					break;
				case 2: 
					x += 1;
					break;
				case 3: 
					x -= 1;
					break;
			}
			if (isValidPosition(x,y)) {
				return (new Coordinate(x,y));
			}
		}
	}
	
	public void nextNumber() {

		addInput();
		showNextNumber();
	}

	public void showNextNumber() {
		for (int i = 0; i < numberCounter; i++) {
			if (numbers[i] == null) {
				numbers[i] = (Number) inputs.dequeue();
				TextAttributes attrs = arrangeColor(i);
				cn.getTextWindow().output(numbers[i].getPosition().getX(), numbers[i].getPosition().getY(),
						String.valueOf(numbers[i].getValue()).charAt(0), attrs);
				break;
			}
		}
		cn.getTextWindow().setCursorPosition(57, 2);
		for (int i = 0; i < 10; i++) {
			System.out.print(((Number) inputs.peek()).getValue());
			inputs.enqueue(inputs.dequeue());
		}
	}

	public void addInput() {
		Number nextInput = generateRandomNumber();
		this.inputs.enqueue(nextInput);
	}

	public void createInputs() {
		for (int i = 0; i < 10; i++) {
			Number nextInput = generateRandomNumber();
			this.inputs.enqueue(nextInput);
		}
		cn.getTextWindow().setCursorPosition(57, 2);
		for (int i = 0; i < 10; i++) {
			Number a = (Number) inputs.peek();
			System.out.print(a.getValue());
			inputs.enqueue(inputs.dequeue());
		}
	}

	public void checkMatchForPacks() {
		// For comparison, creating temp stacks
		Stack tempLeftPack = new Stack(this.leftPack.size());
		Stack tempRightPack = new Stack(this.rightPack.size());

		// Assignments for temp stacks
		while (!(this.leftPack.isEmpty())) {
			tempLeftPack.push(this.leftPack.pop());
		}
		while (!(this.rightPack.isEmpty())) {
			tempRightPack.push(this.rightPack.pop());
		}

		// Now, searching for matched elements
		while ((!(tempLeftPack.isEmpty())) && (!(tempRightPack.isEmpty()))) {
			int elementRightPack = (int) tempRightPack.pop();
			int elementLeftPack = (int) tempLeftPack.pop();
			if (elementLeftPack == elementRightPack) {
				this.playerNumber++;
				if (playerNumber==9) {
					this.playerNumber=1;
					this.counterMakingTwo=this.time;
				}
				this.score += elementLeftPack * findScoreFactor(elementLeftPack);
				cn.getTextWindow().setCursorPosition(64, 20);
				cn.getTextWindow().output(String.valueOf(this.score));				
				showPacks();
			} else {
				this.leftPack.push(elementLeftPack);
				this.rightPack.push(elementRightPack);
			}
		}

		// If any of packs is not empty,
		if (!(tempLeftPack.isEmpty())) {
			while (!(tempLeftPack.isEmpty())) {
				this.leftPack.push(tempLeftPack.pop());
			}
		}

		if (!(tempRightPack.isEmpty())) {
			while (!(tempRightPack.isEmpty())) {
				this.rightPack.push(tempRightPack.pop());
			}
		}
	}

	public int findScoreFactor(int value) {
		if (value < 4) {
			return 1;
		} else if (value < 7) {
			return 5;
		} else {
			return 25;
		}
	}

	public void packQOperation() {
		// Checking whether the right pack has one element at least
		if (!(rightPack.isEmpty())) {
			if (leftPack.isFull()) {
				leftPack.pop();
			}
			leftPack.push(rightPack.pop());
		} else {
			// Impossible, show a warning to the player
		}
		// Also, calling the function which check score by investigating packs
		checkMatchForPacks();
		showPacks();
	}

	public void packWOperation() {
		// Checking whether the left pack has one element at least
		if (!(leftPack.isEmpty())) {
			if (rightPack.isFull()) {
				rightPack.pop();
			}
			rightPack.push(leftPack.pop());
		} else {
			// Impossible, show a warning to the player
		}
		// Also, calling the function which check score by investigating packs
		checkMatchForPacks();
		showPacks();
	}

	public boolean checkCollision(int xGone, int yGone) {
		// Searching with the function for check whether there is a number or not
		int searchForNumber = searchForNumber(xGone, yGone);
		if (searchForNumber == -1)
			return false;

		// If there is a number,
		if (numbers[searchForNumber].getValue() <= this.playerNumber) { // --/----------//----------/--//
			// Number is taken by the player
			// First checking whether the leftpack is already filled
			if (leftPack.isFull())
				leftPack.pop();

			leftPack.push(numbers[searchForNumber].getValue());
			showPacks();
			// cn.getTextWindow().setCursorPosition(numbers[searchForNumber].getxPosition(),
			// numbers[searchForNumber].getyPosition());
			cn.getTextWindow().output(numbers[searchForNumber].getPosition().getX(), numbers[searchForNumber].getPosition().getY(),
					' ');
			numbers[searchForNumber] = null;
			// Also, calling the function which check score by investigating packs
			checkMatchForPacks();
		} else {
			// The player dies
			cn.getTextWindow().setCursorPosition(5, 25);
			cn.getTextWindow().output("Game is over." + "      Score: " + this.score);
			return true;
		}
		return false;
	}

	public void removePackArea() {
		int x = 59;
		int y = 14;

		for (int i = 0; i < 8; i++) {
			cn.getTextWindow().output(x, y - i, ' ');
		}
		x = 65;
		y = 14;
		for (int i = 0; i < 8; i++) {
			cn.getTextWindow().output(x, y - i, ' ');
		}
	}

	public void showPacks() {
		removePackArea();
		Stack leftTempStack = new Stack(this.leftPack.size());
		Stack rightTempStack = new Stack(this.rightPack.size());

		int x = 59;
		int y = 15 - leftPack.size();

		while (!(this.leftPack.isEmpty())) {
			int element = (int) leftPack.pop();
			leftTempStack.push(element);
			cn.getTextWindow().output(x, y, String.valueOf(element).charAt(0));
			y++;
		}
		x = 65;
		y = 15 - rightPack.size();
		while (!(this.rightPack.isEmpty())) {
			int element = (int) rightPack.pop();
			rightTempStack.push(element);
			cn.getTextWindow().output(x, y, String.valueOf(element).charAt(0));
			y++;
		}

		while (!(leftTempStack.isEmpty())) {
			this.leftPack.push(leftTempStack.pop());
		}
		while (!(rightTempStack.isEmpty())) {
			this.rightPack.push(rightTempStack.pop());
		}

	}

	public int searchForNumber(int xGone, int yGone) {
		for (int i = 0; i < numberCounter; i++) {
			if (numbers[i] != null && numbers[i].getPosition().getX() == xGone && numbers[i].getPosition().getY() == yGone) {
				return i;
			}
		}
		return -1;
	}


	public void showTime() {
		cn.getTextWindow().setCursorPosition(64, 22);;
		cn.getTextWindow().output(String.valueOf(this.time));
	}

	public void showNumbers() {
		for (int i = 0; i < numberCounter; i++) {
			if (numbers[i] != null) {
				TextAttributes attrs = arrangeColor(i);
				cn.getTextWindow().output(numbers[i].getPosition().getX(), numbers[i].getPosition().getY(),String.valueOf(numbers[i].getValue()).charAt(0), attrs);
			}
			
		}
	}

	
	public TextAttributes arrangeColor(int i) {
		TextAttributes attrs = new TextAttributes(Color.white);
		if (numbers[i].getColor() == 'R') {
			attrs = new TextAttributes(Color.RED);
		} else if (numbers[i].getColor() == 'Y') {
			attrs = new TextAttributes(Color.YELLOW);
		} else {
			attrs = new TextAttributes(Color.GREEN);
		}
		return attrs;
	}

	public void settingGameArea(String line, int row) {
		char[] characters = line.toCharArray();
		for (int i = 0; i < 55; i++) {
			if (characters[i] == '#') {
				this.walls[row][i] = 1;
			}
		}
	}

	public void settingRandomNumber() {
		for (int i = 0; i < 25; i++) {
			numbers[i] = generateRandomNumber();
		}
	}

	public Number generateRandomNumber() {
		this.numberCounter++;
		Random random = new Random();
		int probabilityRandom = random.nextInt(100) + 1;
		int xPosition = 0;
		int yPosition = 0;
		while (true) {
			xPosition = random.nextInt(53) + 1;
			yPosition = random.nextInt(21) + 1;
			if (isValidPosition(xPosition, yPosition) && xPosition != this.px && yPosition != this.py) {
				break;
			}
		}

		if (probabilityRandom < 6) {
			int numberValue = random.nextInt(3) + 7;
			return (new Number('R', xPosition, yPosition, numberValue,10000));
		} else if (probabilityRandom < 26) {
			int numberValue = random.nextInt(3) + 4;
			return (new Number('Y', xPosition, yPosition, numberValue));
		} else {
			int numberValue = random.nextInt(3) + 1;
			return (new Number('G', xPosition, yPosition, numberValue));
		}
	}

	public boolean isValidPosition(int x, int y) {
		if (x>54 || y>22) return false;
		if (walls[y][x] == 1) return false;
		for(int i = 0; i < numbers.length; i++) {
			if(numbers[i] != null && x == numbers[i].getPosition().getX() && numbers[i].getPosition().getY() == y) {
				return false;
			}
		}
		
		return true;
	}
}