
public class Coordinate {
	private int xPosition;
	private int yPosition;
	
	public Coordinate(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	
	public void next() {
		if(xPosition==65) {
			xPosition=5;
			yPosition++;
		}
		else {
			xPosition++;
		}
	}
	public void previous() {
		if(xPosition==5) {
			xPosition=65;
			yPosition--;
		}
		else {
			xPosition--;
		}
	}
	

}
