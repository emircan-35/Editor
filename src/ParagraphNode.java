
public class ParagraphNode {
	private ParagraphNode down;
	private ParagraphNode up;
	private LetterNode right; 
	private int alignment;

	public ParagraphNode() {
		this.up=null;
		this.down=null;
		this.right=null;
		alignment = 1;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	public ParagraphNode getDown() {
		return down;
	}

	public void setDown(ParagraphNode down) {
		this.down = down;
	}


	public ParagraphNode getUp() {
		return up;
	}

	public void setUp(ParagraphNode up) {
		this.up = up;
	}

	

	public LetterNode getRight() {
		return right;
	}

	public void setRight(LetterNode right) {
		this.right = right;
	}
	
	
	
}
