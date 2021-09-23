
public class LetterNode {
	private LetterNode right;
	private LetterNode left;
	private ParagraphNode prevLeftP;
	private int color, emptySize;
	private boolean isSelected, lineBreak = false;
	private char letter;
	
	public LetterNode(char letter) {
		this.isSelected=false;
		this.letter=letter;
		this.left=null;
		this.right=null;
		this.prevLeftP=null;
		color = 7;
		emptySize = 0;
	}

	public boolean isLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(boolean lineBreak) {
		this.lineBreak = lineBreak;
	}
	public LetterNode getLeft() {
		return left;
	}

	public void setLeft(LetterNode left) {
		this.left = left;
		prevLeftP = null;
	}
	public int getEmptySize() {
		return emptySize;
	}

	public void setEmptySize(int emptySize) {
		this.emptySize = emptySize;
	}
	public ParagraphNode getPrevLeftP() {
		return prevLeftP;
	}

	public void setPrevLeftP(ParagraphNode prevLeftP) {
		this.prevLeftP = prevLeftP;
		left = null;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getColor() {
		return color;
	}
	public LetterNode getRight() {
		return right;
	}
	public void setRight(LetterNode right) {
		this.right = right;
	}
	public char getLetter() {
		return letter;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	
	
}
