import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import enigma.console.TextAttributes;

public class EditorLinkedList {
	private ParagraphNode headParagraph;
	private LetterNode cursor;
	private Stack operations;
	private boolean isActiveSelection;
	private long start;
	private String copied;
	private int[] copiedColor;
	private String find;
	private int next;
	private int nextLimit, addLetter;
	private LetterNode printHead, printEnd;
	private ParagraphNode printHeadP, printEndP;
	private int numberOfPage, cursorx, cursory;
	
	public EditorLinkedList() {
		start = System.currentTimeMillis(); // sleeping slows down getting input so I used a running time for flashing
		cursor=new LetterNode('|'); //Cursor node, thanks to this node, any coordinate information was not used.
		operations=new Stack(); // to hold the operations done by the user
		addLetter = 0;
		isActiveSelection = false;
		operations.push("STOP"); //Meaning just stop :)
		copied= find = null;
		next = nextLimit = numberOfPage = 1;
		cursorx = cursory = 5; //For screen operation, two integer showing the x and y value is used
		this.headParagraph=new ParagraphNode();
		this.headParagraph.setRight(cursor);
		this.cursor.setPrevLeftP(headParagraph);
		printHead = cursor;
	}
	
	public void addParagraph(int aligment) {
		LetterNode tempLetter=this.cursor;
		if (tempLetter.getLeft()!=null) while (tempLetter.getLeft()!=null) tempLetter=tempLetter.getLeft();
		ParagraphNode tempParag=tempLetter.getPrevLeftP();
		ParagraphNode newParag=new ParagraphNode();
		newParag.setRight(cursor);
		newParag.setAlignment(aligment);
		if(cursor.getLeft() != null) cursor.getLeft().setRight(null);
		else cursor.getPrevLeftP().setRight(null);
		cursor.setPrevLeftP(newParag);
		ParagraphNode downParag=tempParag.getDown();
		tempParag.setDown(newParag);
		newParag.setUp(tempParag);
		if (downParag!=null) {
			newParag.setDown(downParag);
			downParag.setUp(newParag);
		}
		if (cursory == 24) pageDown();
		addLetter = 2;
	}
	public void addLetter(char letter, boolean mode,boolean undo,int currentColor) {
		LetterNode newLetter=new LetterNode(letter);
		newLetter.setColor(currentColor);
		if (mode) {
			overwrite(newLetter);
			if (undo) {
				operations.push("STOP");
				operations.push("delete");
				operations.push("switchCursorRight");
			}
		}
		else {
			insert(newLetter);
			if (undo) {
				operations.push("STOP");
				operations.push("delete");
			}
		}
	}
	private void insert(LetterNode newLetter) {

		if (newLetter.getLetter() != ' ') addLetter = 1;
		else addLetter = 2;
		if (cursor.getLeft()!=null) {
			cursor.getLeft().setRight(newLetter);
			newLetter.setLeft(cursor.getLeft());
		}else {
			cursor.getPrevLeftP().setRight(newLetter);
			newLetter.setPrevLeftP(cursor.getPrevLeftP());
		}
		newLetter.setRight(cursor); 
		cursor.setLeft(newLetter);
	}
	//Sets the writing mode to overwrite
	//Overwrite mode deletes the character right to the cursor when typing
	//Deletes the right of the newly added letter and adds the letter there.
	//Finally moves the cursor right to change the cursor's placement.
	private void overwrite(LetterNode newLetter) {
		if (newLetter.getLetter() != ' ') addLetter = 1;
		else addLetter = 2;
		if (cursor.getLeft()!=null) {
			cursor.getLeft().setRight(newLetter);
			newLetter.setLeft(cursor.getLeft());
		}else {
			cursor.getPrevLeftP().setRight(newLetter);
			newLetter.setPrevLeftP(cursor.getPrevLeftP());
		}
		newLetter.setRight(cursor); 
		cursor.setLeft(newLetter);
		if(cursor.getRight() != null) {
			if(cursor.getRight().getRight() != null) {
				cursor.getRight().getRight().setLeft(cursor);
				cursor.setRight(cursor.getRight().getRight());
			}
			else {
				cursor.setRight(null);
			}
		}
	}

	public void upperLowerCase() {
		boolean uppercase = false;
		boolean lowercase = false;
		ParagraphNode tempParagraph=this.headParagraph;
		LetterNode temp = headParagraph.getRight();
		while (tempParagraph!=null) {
			while(temp != null) {
				if (temp.isSelected()) {
					if (64 < temp.getLetter() && temp.getLetter() < 91) uppercase = true;
					else if (96 < temp.getLetter() && temp.getLetter() < 123) lowercase = true;
				}
				temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
		}
		if (!uppercase && !lowercase) return;
		else if (uppercase && lowercase) {
			tempParagraph=this.headParagraph;
			temp = headParagraph.getRight();
			while (tempParagraph!=null) {
				while(temp != null) {
					if (temp.isSelected()) {
						 if (96 < temp.getLetter() && temp.getLetter() < 123) temp.setLetter((char)(temp.getLetter() -32));
					}
					temp = temp.getRight();
				}
				tempParagraph=tempParagraph.getDown();
				if (tempParagraph!=null) 
					temp= tempParagraph.getRight();
			}
		}
		else if (lowercase) {
			tempParagraph=this.headParagraph;
			temp = headParagraph.getRight();
			while (tempParagraph!=null) {
				while(temp != null) {
					if (temp.isSelected() && 96 < temp.getLetter() && temp.getLetter() < 123) {
						 if (temp.getLeft() == null || temp.getLeft().getLetter() == ' ') temp.setLetter((char)(temp.getLetter() -32));
					}
					temp = temp.getRight();
				}
				tempParagraph=tempParagraph.getDown();
				if (tempParagraph!=null) 
					temp= tempParagraph.getRight();
			}
		}
		else {
			tempParagraph=this.headParagraph;
			temp = headParagraph.getRight();
			while (tempParagraph!=null) {
				while(temp != null) {
					if (temp.isSelected()) {
						if (64 < temp.getLetter() && temp.getLetter() < 91) temp.setLetter((char)(temp.getLetter() +32));;
					}
					temp = temp.getRight();
				}
				tempParagraph=tempParagraph.getDown();
				if (tempParagraph!=null) 
					temp= tempParagraph.getRight();
			}
		}
	}

	//Undo function
	public void undo(int currentColor) {
		/*Some of other functions in this class has an argument named "undo.
		 * If the undo is active, meaning user pressed ctrl + z combination, 
		 * The program should take the last operation,
		 * BUT to prevent infinite loops, a boolean, named undo is used.
		 * If the undo is active, the function DOES NOT add this operation to the stack.
		 * ALSO,
		 * It can bee seen below that, many function do some operation related the operation stack,
		 * In this way, all the operation done by the user is hold, and STOP letter is used for
		 * stopping purposes, because it is needed to stop right after MATTER operations such as deleting or adding
		 * BUT in some operations such as cursor movements, STOP string is not added to the stack
		 * because it does not matter and undo function should continue to work.
		 */
		
		
		//First, popping the stack until coming a "STOP" string
		//Stop means just stop :)
		while (!operations.peek().equals("STOP")) {
			String undo=(String) operations.pop();
			//According to string popping, with an if-else block, last operation is taken back
			if (undo.equals("switchCursorLeft")) switchCursorLeft(false);
			else if (undo.equals("switchCursorRight"))switchCursorRight(false);
			else if (undo.equals("switchCursorUp"))switchCursorUp(false);
			else if (undo.equals("switchCursorDown"))switchCursorDown(false);
			else if (undo.equals("delete"))delete(false);
			else {
				String[] addLetter=undo.split(";");
				addLetter(addLetter[1].charAt(0),false,false,Integer.parseInt(addLetter[2]));         
			}
		}
		if (operations.peek().equals("STOP")) operations.pop();
		if (operations.isEmpty()) operations.push("STOP");
	}
	
	public void switchCursorRight(boolean undo) {
		addLetter = 2;
		if (cursor.getRight() == null){
			LetterNode temp = cursor;
			if (temp.getLeft()!=null) while (temp.getLeft()!=null) temp=temp.getLeft();
			if (temp.getPrevLeftP().getDown() != null) {
				if (undo)operations.push("switchCursorLeft");
				if (cursor.getLeft() != null) cursor.getLeft().setRight(null);
				else cursor.getPrevLeftP().setRight(null);
				cursor.setPrevLeftP(temp.getPrevLeftP().getDown());
				cursor.setRight(cursor.getPrevLeftP().getRight());
				cursor.getPrevLeftP().setRight(cursor);
				if (cursory == 24) pageDown();
				else cursory++;
			}
			return;
		}
		if ((cursor.isLineBreak())) {
			if (cursory == 24 )pageDown();
			else cursory++;
		}

		if (cursor.getLeft() == null) { // head | a b  or  head | a
			if (undo)operations.push("switchCursorLeft");
			ParagraphNode temp = cursor.getPrevLeftP();
			cursor.getRight().setPrevLeftP(cursor.getPrevLeftP()); // a left
			cursor.getPrevLeftP().setRight(cursor.getRight()); // head right 
			cursor.setLeft(cursor.getPrevLeftP().getRight());  // cursor left
			if (cursor.getRight().getRight() != null) cursor.getRight().getRight().setLeft(cursor); // b left 
			cursor.setRight(cursor.getRight().getRight()); // cursor right
			temp.getRight().setRight(cursor);  // a right
			LetterNode temp1=this.cursor.getLeft();
			if (isActiveSelection) {
				if (temp1.isSelected()) {
					temp1.setSelected(false);
				}else {
					temp1.setSelected(true);	
				}
			}
			}
		else { // c | a b
			if (undo)operations.push("switchCursorLeft");
			LetterNode temp = cursor.getLeft();
			cursor.getRight().setLeft(cursor.getLeft()); // a left
			cursor.getLeft().setRight(cursor.getRight()); // c right 
			cursor.setLeft(cursor.getLeft().getRight());  // cursor left
			if (cursor.getRight().getRight() != null) cursor.getRight().getRight().setLeft(cursor); // b left 
			cursor.setRight(cursor.getRight().getRight()); // cursor right
			temp.getRight().setRight(cursor);  // a right
			if (isActiveSelection) {
				if (cursor.getLeft().isSelected()) {
					cursor.getLeft().setSelected(false);
				}else {
					cursor.getLeft().setSelected(true);
				}
			}
		}
	}
	public void switchCursorLeft(boolean undo) {
		if(cursor != headParagraph.getRight() && cursor == printHead) pageUp();
		addLetter = 2;
		if (cursor.getLeft() == null) {
			if (cursor.getPrevLeftP().getUp() != null) {
				if (undo)operations.push("switchCursorRight");
				cursor.getPrevLeftP().setRight(cursor.getRight());
				if (cursor.getRight() != null) cursor.getRight().setPrevLeftP(cursor.getPrevLeftP());
				LetterNode temp = cursor.getPrevLeftP().getUp().getRight();
				if(temp == null) { 
					cursor.getPrevLeftP().getUp().setRight(cursor);
					cursor.setPrevLeftP(cursor.getPrevLeftP().getUp());
				}else {
					while(temp.getRight() != null) temp = temp.getRight();
					temp.setRight(cursor);
					cursor.setLeft(temp);
				}
				cursor.setRight(null);
				cursory--;
			} 
			return;
		}
		if (cursor.getLeft().isLineBreak()) cursory--;
		if (cursor.getLeft().getLeft() == null) {  // head a | b   or   head a |
			if (undo)operations.push("switchCursorRight");
			cursor.getLeft().setRight(cursor.getRight());
			cursor.getLeft().getPrevLeftP().setRight(cursor);
			if (cursor.getRight() != null) cursor.getRight().setLeft(cursor.getLeft()); // head a | b
			cursor.setRight(cursor.getLeft());
			cursor.setPrevLeftP(cursor.getLeft().getPrevLeftP());
			cursor.getRight().setLeft(cursor); // şunu ekledim
			LetterNode temp=this.cursor.getRight();
			if (isActiveSelection) {
				if (temp.isSelected()) {
					temp.setSelected(false);
				}else {
					temp.setSelected(true);	
				}
			}
		}else { // c a | b
			if (undo)operations.push("switchCursorRight");
			cursor.getLeft().setRight(cursor.getRight());
			cursor.getLeft().getLeft().setRight(cursor); 
			if (cursor.getRight() != null) cursor.getRight().setLeft(cursor.getLeft());
			cursor.setRight(cursor.getLeft());
			cursor.setLeft(cursor.getLeft().getLeft());
			cursor.getRight().setLeft(cursor); // şunu ekledim
			if (isActiveSelection) {
				if (cursor.getRight().isSelected()) {
					cursor.getRight().setSelected(false);
				}else {
					cursor.getRight().setSelected(true);
				}
			}
		}
	}
	public void switchCursorUp(boolean undo) {
		LetterNode temp = this.cursor,  first = null;
		ParagraphNode up = null;
		int distance = 0;
		boolean emptyP = false;

		if (temp.getLeft() != null) first = temp.getLeft();
		while(temp.getLeft() != null && !temp.getLeft().isLineBreak()) {
			distance += temp.getEmptySize();
			temp = temp.getLeft();
			distance++;
		}
		distance += temp.getEmptySize();
		if (distance > 61) {
			end();
			distance = 61;
		}
		if (temp.getLeft() != null) {
			temp = temp.getLeft();
			while(temp.getLeft() != null && !temp.getLeft().isLineBreak()) { 
				temp = temp.getLeft();
			}
			
			for(int i = 0; i < distance; i++) {
				if(temp.getRight() == null) break;
				distance -= temp.getEmptySize();
				temp = temp.getRight();
			}
		}
		else { 
			if (temp.getPrevLeftP().getUp() != null) {
				if (undo) {
					operations.push("STOP");
					operations.push("switchCursorDown");
				}
				up= temp.getPrevLeftP().getUp();
			}
			else return;
			temp = up.getRight();

			if (temp == null) emptyP = true;
			else {
				while(temp.getRight() != null) {
					temp = temp.getRight();
				}
				while(temp.getLeft() != null && !temp.getLeft().isLineBreak()) {
					temp = temp.getLeft();
				}
				for(int i = 0; i < distance; i++) {
					if(temp.getRight() == null) break;
					distance -= temp.getEmptySize();
					temp = temp.getRight();
				}
			}	
		} 
		if(cursor.getRight() != null) {
			if(cursor.getLeft() != null) cursor.getRight().setLeft(cursor.getLeft());
			else cursor.getRight().setPrevLeftP(cursor.getPrevLeftP());
		}
		if (cursor.getLeft() != null) cursor.getLeft().setRight(cursor.getRight());
		else cursor.getPrevLeftP().setRight(cursor.getRight());
		
		if (emptyP) {
			cursor.setRight(temp);
			up.setRight(cursor);
			cursor.setPrevLeftP(up);
		}
		else {
			if (temp.getRight() == null) {
				temp.setRight(cursor);
				cursor.setRight(null);
				cursor.setLeft(temp);
			}
			else {
				if (temp.getLeft() == null) temp.getPrevLeftP().setRight(cursor);
				else temp.getLeft().setRight(cursor);
				if (temp.getLeft() == null) cursor.setPrevLeftP(temp.getPrevLeftP());
				else cursor.setLeft(temp.getLeft());
				cursor.setRight(temp);
				temp.setLeft(cursor);
			}
		}
		if (isActiveSelection) {
			temp = cursor.getRight();
			if (temp == null && first!= null) temp = up.getDown().getRight();
			while (temp != null) {
				if (temp.isSelected()) temp.setSelected(false);
				else temp.setSelected(true);
				if (temp.equals(first)) break;
				temp = temp.getRight();
				if (first!= null && temp == null && cursor.getRight() != null) temp = up.getDown().getRight();
			}
		}
		if (distance == 61) end();
		if (cursory == 5) {
			cursory = 24;
			addLetter = 0;
		}
		else {
			cursory--;
			addLetter = 2;
		}
	}
	public void switchCursorDown(boolean undo) {
		LetterNode temp = this.cursor,first = null;
		ParagraphNode down = null;
		int distance = 0;
		if (temp.getRight() != null ) first = temp.getRight();
		while(temp.getLeft() != null && !temp.getLeft().isLineBreak()) {
			distance += temp.getEmptySize();
			temp = temp.getLeft();
			distance++;
		}
		distance += temp.getEmptySize();
		if (distance > 61) {
			end();
			distance = 61;
		}
		temp = this.cursor;
		while(temp.getRight() != null && !temp.isLineBreak()) {
			temp = temp.getRight();
		}
		if (temp!= null && temp.isLineBreak()) {
			if(cursor.getLeft() != null && !cursor.getLeft().isLineBreak()) {
				for(int i = 0; i < distance; i++) {
					if(temp.getRight() == null || temp.getRight().isLineBreak()) break;
					distance -= temp.getEmptySize();
					temp = temp.getRight();
				}
			}
			else if (temp.getLetter() != ' ')  temp = temp.getRight();
		}
		else {
			while(temp.getLeft() != null) {
				temp = temp.getLeft();
			}
			if (temp.getPrevLeftP().getDown() != null) {
				if (undo) {
					operations.push("STOP");
					operations.push("switchCursorUp");
				}
				down= temp.getPrevLeftP().getDown();
			}
			else return;
			temp = down.getRight();
			if(temp != null) {
				for(int i = 0; i < distance-1; i++) {
					if(temp.getRight() == null || temp.getRight().isLineBreak()) break;
					distance -= temp.getEmptySize();
					temp = temp.getRight();
				}
			}
		}
		if(cursor.getRight() != null) {
			if(cursor.getLeft() != null) cursor.getRight().setLeft(cursor.getLeft());
			else cursor.getRight().setPrevLeftP(cursor.getPrevLeftP());
		}
		if (cursor.getLeft() != null) cursor.getLeft().setRight(cursor.getRight());
		else cursor.getPrevLeftP().setRight(cursor.getRight());
		
		if (temp == null) { 
			cursor.setRight(null);
			down.setRight(cursor);
			cursor.setPrevLeftP(down);
		}
		else {
			if(temp.getLeft() == null) {
				cursor.setPrevLeftP(down);
				down.setRight(cursor);
				temp.setLeft(cursor);
				cursor.setRight(temp);
			}
			else {
				cursor.setRight(temp.getRight());
				if (temp.getRight() != null) temp.getRight().setLeft(cursor);
				temp.setRight(cursor);
				cursor.setLeft(temp);
			}
		}
		if (isActiveSelection) {
			if (first == null) first = down.getRight();
			while (first != null && !first.equals(cursor)) {
				if (first.isSelected()) first.setSelected(false);
				else first.setSelected(true);
				first = first.getRight();
				if (first == null && down!= null) first = down.getRight();
			}
		}
		if (distance == 61) end();
		if (cursory == 24) {
			cursory = 5;
			addLetter = 1;
		}
		else {
			cursory++;
			addLetter = 2;
		}
	}
	
	public void home() {
		while (cursor.getLeft() != null && !cursor.getLeft().isLineBreak()) {
			switchCursorLeft(false);
		}
	}
	public void end() {
		while (true) {
			if (cursor.getRight() != null )switchCursorRight(false);
			if (cursor.getRight() == null ) break;
			else if (cursor.getLeft() != null && cursor.getLeft().isLineBreak()) break;
		}
		while (cursor.getLeft() != null && cursor.getLeft().getLetter() == ' ') switchCursorLeft(false);
	}
	
	private void page() {
		if(numberOfPage == 1 || (printEndP == null && printEnd == null)) {
			if (headParagraph.getRight() != null) {
				printHead= headParagraph.getRight();
				printHeadP = null;
			}
			else {
				printHeadP = headParagraph;
				printHead = null;
			}
			printEndP = null;
			printEnd = null;
			numberOfPage = 1;
		}
		else {
			if (printEnd!= null && printEnd.getRight() != null) printHead = printEnd.getRight();
			else if (printEnd != null && printEnd.getRight() == null) {
				LetterNode temp = printEnd;
				while (temp.getLeft() != null) temp = temp.getLeft();
				if ( temp.getPrevLeftP().getDown().getRight() != null) {
					printHead = temp.getPrevLeftP().getDown().getRight();
					printHeadP = null;
				}
				else {
					printHeadP = temp.getPrevLeftP().getDown();
					printHead = null;
				}
			}
			else {
				if ( printEndP.getDown().getRight() != null) {
					printHead = printEndP.getDown().getRight();
					printHeadP = null;
				}
				else {
					printHeadP = printEndP.getDown();
					printHead = null;
				}
			}
		}
	}
	public boolean pageDown() {
		LetterNode temp = null;
		ParagraphNode tempParagraph = null;
		if(printHead != null) {
			temp = printHead;
			while(temp.getLeft() != null) temp = temp.getLeft();
			tempParagraph = temp.getPrevLeftP();
		}
		else tempParagraph = printHeadP;
		
		if(printHead != null) temp = printHead;
		else temp = printHeadP.getRight();
		
		int lineCount = 0;
		while (tempParagraph!=null ) {
			lineCount++;
			if(lineCount == 21) break;
			while (temp !=null ) {
				temp = temp.getRight();
				if(temp != null && temp.getLeft().isLineBreak()) lineCount++;
				if(lineCount == 21) break;
			}
			if(lineCount == 21) break;
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp=tempParagraph.getRight();
		}
		if (lineCount != 21) return false;
		calculateNewPrintHead(temp,tempParagraph);
		numberOfPage++;
		return true;
	}
	public boolean pageUp() {
		LetterNode temp = null;
		ParagraphNode tempParagraph = null;
		if (numberOfPage == 1) return false;
		if(printEnd != null) {
			temp = printEnd;
			while(temp.getLeft() != null) temp = temp.getLeft();
			tempParagraph = temp.getPrevLeftP();
		}
		else tempParagraph = printEndP;
		
		if(printEnd != null) temp = printEnd;
		else temp = printEndP.getRight();
		
		int lineCount = 0;
		while (tempParagraph!=null) {
			while (temp !=null) {
				temp = temp.getLeft();
				if(temp !=  null && temp.getLeft()!= null && temp.getLeft().isLineBreak()) lineCount++;
				if(lineCount == 20) break;
			}
			if(lineCount == 20) break;
			lineCount++;
			if(lineCount == 20) break;
			tempParagraph=tempParagraph.getUp();
			if (tempParagraph!=null) {
				temp=tempParagraph.getRight();
				if (temp != null) {
					while(temp.getRight() != null) temp = temp.getRight();
				}
			}	
		}
		if (lineCount != 20) return false;
		calculateNewPrintHead(temp,tempParagraph);
		numberOfPage--;
		return true;
	}
	private void calculateNewPrintHead(LetterNode temp, ParagraphNode tempParagraph) {
		if (temp != null) {
			printHead = temp;
			printHeadP = null;
		}
		else if (tempParagraph.getRight() != null) {
			printHead = tempParagraph.getRight();
			printHeadP = null;
		}
		else {
			printHeadP = tempParagraph;
			printHead = null;
		}
		
		if (printHead != null && printHead.getLeft() != null) {
			printEnd = printHead.getLeft();
			printEndP = null;
		}
		else {
			if (printHead != null && printHead.getLeft() == null) {
				tempParagraph = printHead.getPrevLeftP().getUp();
			}
			else tempParagraph = printHeadP.getUp();
			
			if (tempParagraph == null) return;
			if (tempParagraph.getRight() == null ) {
				printEndP = tempParagraph;
				printEnd= null;
			}
			else {
				temp = tempParagraph.getRight();
				while (temp.getRight() != null) temp = temp.getRight();
				printEnd = temp;
				printEndP = null;
			}
		}
	}
	
	public void del() {

		if (cursor.getRight()!=null) {
			switchCursorRight(false);
			delete(false);
		}
	}
	//Deletion method
	//Deletes the char to the left of the cursor
	//Checks various situations to delete correctly according to what is next to cursor.
	//Deletes the paragraphs if pressed next to the starting of a paragraph.
	public void delete(boolean undo) {
		if(cursor != headParagraph.getRight() && cursor == printHead) pageUp();
		addLetter = 2;
		if(cursor.getLeft() != null) {
			if (cursor.getLeft() == printEnd) printEnd = null; //////
			if (undo) {
				operations.push("STOP");
				operations.push("addLetter;"+this.cursor.getLeft().getLetter() + ";" + cursor.getLeft().getColor());
			}
			if(cursor.getLeft().getLeft()!=null) {
				cursor.getLeft().getLeft().setRight(cursor);
				cursor.setLeft(cursor.getLeft().getLeft());
			}
			else {
				cursor.getLeft().getPrevLeftP().setRight(cursor);
				cursor.setPrevLeftP(cursor.getLeft().getPrevLeftP());
			}
		}
		else {
			if (cursor.getPrevLeftP() == printEndP) printEndP = null; //////
			if(cursor.getPrevLeftP().getUp() != null) {
				LetterNode temp;
				ParagraphNode temp2 = cursor.getPrevLeftP();
				if(cursor.getPrevLeftP().getUp().getRight() != null) {
					temp = cursor.getPrevLeftP().getUp().getRight();
					while(temp.getRight() != null) {
						temp = temp.getRight();
					}
					cursor.setLeft(temp);
					temp.setRight(cursor);
				}
				else {
					cursor.getPrevLeftP().getUp().setRight(cursor);
					cursor.setPrevLeftP(cursor.getPrevLeftP().getUp());
				}
				if(temp2.getDown() == null) temp2.getUp().setDown(null);
				else {
					temp2.getUp().setDown(temp2.getDown());
					temp2.getDown().setUp(temp2.getUp());
				}	
			}
		}
	}
		
	public void save(String nameFile)  {
		//The function takes a string, which is the name of the file will be open
		
	    BufferedWriter writer;
		File file=new File(nameFile+".txt");
		//Creating the file if it does not exist
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter("log.txt", true);
				fw.write(nameFile+"\n");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Writing the file
		try {
			//Every letter is written to the file
			writer = new BufferedWriter(new FileWriter(nameFile+".txt"));
			ParagraphNode tempParagraph=this.headParagraph;
			while (tempParagraph!=null) {
				LetterNode tempLetter=tempParagraph.getRight();
				while (tempLetter!=null) {
					if (tempLetter.getLetter()!='|') writer.write(tempLetter.getLetter());
					tempLetter=tempLetter.getRight();
				}
				writer.write("\n");
				tempParagraph=tempParagraph.getDown();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void load(String nameFile) {
		Scanner reader;
		try {
			reader = new Scanner(new File(nameFile+".txt"));
			deleteEditor(); //Which means as calling the constructor function in another way...
			//Processing data coming from text to the editor
			while (reader.hasNextLine()) {
				char[] line=reader.nextLine().toCharArray();
				for (int i = 0; i < line.length; i++) {
					insert(new LetterNode(line[i]));
				}
				addParagraph(1);
				addLetter = 1;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void deleteEditor() {
		//Resetting the attributes of editor
		cursor=new LetterNode('|');
		headParagraph=new ParagraphNode();
		headParagraph.setRight(cursor);
		cursor.setPrevLeftP(headParagraph);
		operations=new Stack();
		operations.push("STOP");
	}
	
	//Finds all instances of the selected word using searching algorithm.
	//If in next mode just finds the next word.
	//Checks the find variable to compare if the selected word exists while moving in the linkedlist.
	//If it exists marks it by selecting it.
	//If in find mode only marks the first next word.
	//To determine this stores an integer to determine which word to highlight. 
	public void find(boolean next) {
		char[] charFind = find.toCharArray();
		int nextCount = 0;
		int nextLimitCount = 0;
		if(charFind.length == 0) return;
		LetterNode temp = headParagraph.getRight();
		ParagraphNode head = headParagraph;
		LetterNode compare;
		boolean check2 = false;
		while(true) {
			if(temp.getLetter() == charFind[0]) {
				compare = temp;
				boolean check = true;
				for (int i = 0; i < charFind.length; i++) {
					if(compare.getLetter() != charFind[i]) {
						check = false;
						break;
					}
					if(compare.getRight() != null) compare = compare.getRight();			
					else check2 = true;
				}
				if(check) {
					if(!next) {
						nextLimitCount++; 
						nextLimit = nextLimitCount;
					}
					if(!check2) compare = compare.getLeft();
					else check2 = false;
					LetterNode compare2 = compare;
					for (int i = 0; i < charFind.length; i++) {
						if(!next) compare2.setSelected(true);
						else {
							nextCount++;
							if(nextCount == this.next) {
								for (int j = 0; j < charFind.length; j++) {
									compare2.setSelected(true);
									compare2 = compare2.getLeft();
								}
								return;
							}
							break;
							
						}
						compare2 = compare2.getLeft();
					}
					if(temp.getRight()!= null) temp = temp.getRight();
					else {
						if(head.getDown() != null) {		
							head = head.getDown();
							temp = head.getRight();
							if(temp == null) {
								while(temp == null) {
									if(head.getDown() != null) {
										head = head.getDown();
										temp = head.getRight();
									}
									else break;
								}
								if(temp == null) break;
							}
						}
						else  break;				
					}
				}
				else {
					if(temp.getRight()!= null) temp = temp.getRight();
					else {
						if(head.getDown() != null) {		
							head = head.getDown();
							temp = head.getRight();
							if(temp == null) {
								while(temp == null) {
									if(head.getDown() != null) {
										head = head.getDown();
										temp = head.getRight();
									}
									else break;
								}
								if(temp == null) break;
							}
						}
						else  break;				
					}
				}		
			}
			else {
				if(temp.getRight()!= null) temp = temp.getRight();
				else {
					if(head.getDown() != null) {		
						head = head.getDown();
						temp = head.getRight();
						if(temp == null) {
							while(temp == null) {
								if(head.getDown() != null) {
									head = head.getDown();
									temp = head.getRight();
								}
								else break;
							}
							if(temp == null) break;
						}
							
					}
					else  break;
					
				}
			}
		}
		
	}
	//Changes the integer that determines which word to highlight in find.
	//Also deletes the highlighting of the previous selection.
	public void next() {

		ParagraphNode paragraph = headParagraph;
		LetterNode temp = paragraph.getRight();
		while(true) {
			if(temp.isSelected()) temp.setSelected(false);
			if(temp.getRight() != null) temp = temp.getRight();
			else {
				if(paragraph.getDown() != null) {
					paragraph = paragraph.getDown();
					temp = paragraph.getRight();
					if(temp == null) {
						while(temp == null) {
							if(paragraph.getDown() != null) {
								paragraph = paragraph.getDown();
								temp = paragraph.getRight();
							}
							else break;
						}
						if(temp == null) break;
					}
				}
				else break;			
			}
		}
		find(true);
		if(next <= nextLimit) 	next++;
		else next = 1;
	
	}
	//Replaces the selected word or words with a new one.
	//Checks for highlighted word or words and replaces them using char[] word.
	public void replace(char[] word) {
		ParagraphNode paragraph = headParagraph;
		LetterNode temp = paragraph.getRight();
		LetterNode node1 = null;
		boolean operation = false;
		while(true) {
			boolean condition = false;
			if(temp.getRight() != null)
			condition = temp.isSelected() && !temp.getRight().isSelected();
			if(temp.isSelected() && temp.getRight() == null) {
				operation = true;
			}
			else if(condition) {
				operation = true;
				node1 = temp.getRight();
			}
			if(operation) {
				boolean end = false;
				while(temp.isSelected()) {
					if(temp.getLeft() != null) temp = temp.getLeft();
					else {end = true; break;}
				}
				if(end) {
					LetterNode letter=new LetterNode(word[0]);
					temp.getPrevLeftP().setRight(letter);
					letter.setPrevLeftP(temp.getPrevLeftP());
					for (int i = 1; i < word.length; i++) {
						LetterNode letter2 =new LetterNode(word[i]);
						letter.setRight(letter2);
						letter2.setLeft(letter);
						letter = letter.getRight();
					}
					temp = letter;
					end = false;
				}
				else {
					for (int i = 0; i < word.length; i++) {
						LetterNode letter=new LetterNode(word[i]);
						temp.setRight(letter);
						letter.setLeft(temp);
				
						temp = temp.getRight();
					}
				}
				operation = false;
			}
			if(condition) {
				node1.setLeft(temp);
				temp.setRight(node1);
			}		
			
			if(temp.getRight() != null) temp = temp.getRight();
			else {
				if(paragraph.getDown() != null) {
					paragraph = paragraph.getDown();
					temp = paragraph.getRight();
					if(temp == null) {
						while(temp == null) {
							if(paragraph.getDown() != null) {
								paragraph = paragraph.getDown();
								temp = paragraph.getRight();
							}
							else break;
						}
						if(temp == null) break;
					}
				}
				else break;			
			}
		}	
	}
	
	public void paste() {
		//The string, named copied, must be already filled with copy or cut operations 
		if (this.copied == null) return;
		char[] charCopied= copied.toCharArray();
		
		//All the char elements of string is added with insert function
		for (int i = 0; i < charCopied.length; i++) {
			LetterNode letter = new LetterNode(charCopied[i]);
			letter.setColor(copiedColor[i]);
			insert(letter);
		}
	}
    public void copyAndCut(int mode) {
		String copiedString="";
		int[] copiedColor = new int[500];
		int count = 0;
		ParagraphNode tempParagraph = headParagraph;
		LetterNode tempLetter;
		LetterNode selectedLetter = null;
		//First, adding the selected letter to the string named copiedString
		while (tempParagraph!=null) {
			tempLetter = tempParagraph.getRight();
			while (tempLetter!=null) {
				if (tempLetter.isSelected()) {
					copiedString+=tempLetter.getLetter();
					copiedColor[count] = tempLetter.getColor();
					count++;
					selectedLetter = tempLetter;
				}
				tempLetter=tempLetter.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if(tempParagraph == null) {
				
			}
		}
		boolean changePage = false;
		if(mode != 2) {copied=copiedString; this.copiedColor = copiedColor;}
		else find = copiedString;
		//Cut mode
		//Moves the cursor to the position of the word or words that will be cut.
		//Cuts them and stores them.
		if(mode == 1 && selectedLetter != null) {
			while(cursor.getPrevLeftP() != headParagraph) switchCursorLeft(false);
			ParagraphNode tempParagraph2 = headParagraph;
			while(tempParagraph2 != null) {
				if(cursor.getLeft() != null) {
					if(cursor.getLeft().isSelected()) {
						delete(false);
						if(printEnd == cursor.getLeft() || printEndP == cursor.getPrevLeftP()) {
							addLetter = 1;
							numberOfPage = 1;
							changePage = true;
						}
						if(cursor.getLeft()== null && cursor.getRight() == null) delete(false);
					}
				}
				if(cursor.getRight() == null) tempParagraph2 = tempParagraph2.getDown();
				switchCursorRight(false);
			}		
		}
		if (changePage) addLetter = 1;
	}
	
    private void calculateLineBreaks() {
		int countLetter = 0;
		ParagraphNode tempParagraph=this.headParagraph;
		LetterNode temp = headParagraph.getRight();
		while (tempParagraph!=null) {
			while(temp != null) {
				temp.setLineBreak(false);
				temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
		}
		tempParagraph=this.headParagraph;
		temp = headParagraph.getRight();
		
		while (tempParagraph!=null) {
			while(temp != null) {
				countLetter = 0;
				while(true) {
					if(temp != null && temp.getLetter() != '|') countLetter++;
					temp = temp.getRight();
					if (temp == null || (countLetter != 0 && countLetter %61 == 0)) break;
				}
				if (temp == null) break;
				if (temp.getLetter() == '|' && temp.getRight() == null) break;
				if (temp == cursor && temp.getLeft().getLetter() != ' '  && (temp.getRight() != null)
						&& temp.getRight().getLetter() == ' ' && temp.getRight().getRight() == null) temp.setLineBreak(true);
				else {
					if (temp != cursor || (temp == cursor && temp.getRight() != null))
					while(temp.getLetter() != ' ') {
						if (temp.getLeft() == null || (temp.getLeft().isLineBreak())) { 
							for(int i = 0; i < 60; i++) {
								if (temp.getRight() == null) break;
								if (temp.getLetter() == '|') i--;
								temp = temp.getRight();	
							}
							break;
						}
						temp = temp.getLeft();
					}
					
					if (temp.getRight() == null) break;
					if (temp.getLetter() ==  ' ' || temp == cursor) {
						while (temp.getRight() != null) {
							if (temp.getRight() == cursor && temp.getRight().getRight() == null) break;
							if (temp.getRight().getLetter() != ' ' && temp.getRight().getLetter() != '|') break;
							if (temp.getRight() == cursor && temp.getRight().getRight() != null && temp.getRight().getRight().getLetter() != ' ') break;
							temp = temp.getRight();
						}
						if (temp.getRight() == cursor && temp.getRight().getRight() == null)break;
						if (temp.getRight() == null) break;
					}
					temp.setLineBreak(true);
				}
				temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
		}
		
	}
	public void leftAlignment(boolean selection, ParagraphNode tempParagraph) {
		if (tempParagraph == null) {
			 tempParagraph=this.headParagraph;
			LetterNode temp = headParagraph.getRight();
		if (selection) {
			while (tempParagraph!=null) {
				while(temp != null) {
					if (temp.isSelected()) {
						tempParagraph.setAlignment(1);
						break;
					}
					temp = temp.getRight();
				}
				tempParagraph=tempParagraph.getDown();
				if (tempParagraph!=null) 
					temp= tempParagraph.getRight();
			}
		}
		else {
			temp = cursor;
			while(temp.getLeft() != null) {
				temp= temp.getLeft();
			}
			temp.getPrevLeftP().setAlignment(1);
		}
		tempParagraph=this.headParagraph;
		}

		LetterNode temp = tempParagraph.getRight();
		while (tempParagraph!=null) {
			while(temp != null && tempParagraph.getAlignment() == 1) {
				temp.setEmptySize(0); 
				temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
		}
	}
	public void rightAlignment(boolean selection, ParagraphNode tempParagraph) {
		if (tempParagraph == null) {
			 tempParagraph=this.headParagraph;
			LetterNode temp = headParagraph.getRight();
		if (selection) {
			while (tempParagraph!=null) {
				while(temp != null) {
					if (temp.isSelected()) {
						tempParagraph.setAlignment(2);
						break;
					}
					temp = temp.getRight();
				}
				tempParagraph=tempParagraph.getDown();
				if (tempParagraph!=null) 
					temp= tempParagraph.getRight();
			}
		}
		else {
			temp = cursor;
			while(temp.getLeft() != null) {
				temp= temp.getLeft();
			}
			temp.getPrevLeftP().setAlignment(2);
		}
		tempParagraph=this.headParagraph;
		}
		LetterNode temp = tempParagraph.getRight();
		while (tempParagraph != null) {
			while(temp != null && tempParagraph.getAlignment() == 2) {
				int countLetter = 0;
				while( temp.getRight() != null && !temp.isLineBreak()) {
					temp.setEmptySize(0);
					temp = temp.getRight(); 
					countLetter++;
				}
				while (temp.getLeft()!= null && (temp.getLetter() == ' ' || temp.getLetter() == '|' )) {
					temp = temp.getLeft();
					countLetter--;
				}
				while(temp.getLeft() != null && !temp.getLeft().isLineBreak()) {
					temp = temp.getLeft();
				}
				if (countLetter < 60) 
					temp.setEmptySize(60-countLetter);
					
				while(temp!= null && !temp.isLineBreak()) {
					temp = temp.getRight();
				}
				if (temp!= null && temp.isLineBreak()) temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
					
		}
	}
	public void justify(boolean selection, ParagraphNode tempParagraph) {
		if (tempParagraph == null) {
			 tempParagraph=this.headParagraph;
			LetterNode temp = headParagraph.getRight();
		if (selection) {
			while (tempParagraph!=null) {
				while(temp != null) {
					if (temp.isSelected()) {
						tempParagraph.setAlignment(3);
						break;
					}
					temp = temp.getRight();
				}
				tempParagraph=tempParagraph.getDown();
				if (tempParagraph!=null) 
					temp= tempParagraph.getRight();
			}
		}
		else {
			temp = cursor;
			while(temp.getLeft() != null) {
				temp= temp.getLeft();
			}
			temp.getPrevLeftP().setAlignment(3);
		}
		tempParagraph=this.headParagraph;
		}
		LetterNode temp = tempParagraph.getRight();
		while (tempParagraph!=null) {
			while(temp != null && tempParagraph.getAlignment() == 3) {
				temp.setEmptySize(0); 
				temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
		}
		tempParagraph=this.headParagraph;
		temp = headParagraph.getRight();
		int countEmpty = 0, countLetter = 0;
		while (tempParagraph!=null) {
			while(temp != null && tempParagraph.getAlignment() == 3) {
				countEmpty = countLetter = 0;
				while( temp.getRight() != null && !temp.getRight().isLineBreak()) {
					temp = temp.getRight();
					if (temp.getLetter() == ' ') countEmpty++;
					countLetter++;
				}
				if (temp.getRight() == null) break;
				while (temp.getLetter() == ' ' || temp.getLetter() == '|' ) {
					temp = temp.getLeft();
					countLetter--;
					countEmpty--;
				}
				if (countLetter != 60 && countEmpty != 0) {
					int addingSize = 60 - countLetter;
					while(countEmpty > 0) {
						if (temp.getLetter() == ' ') {
							temp.setEmptySize((int)(addingSize/countEmpty));
							addingSize = (int)(addingSize - (addingSize / countEmpty));
							countEmpty--;
						}
						temp = temp.getLeft();
					}
				}
				while(!temp.isLineBreak() && temp != null) {
					temp = temp.getRight();
				}
				if(temp != null) temp = temp.getRight();
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) 
				temp= tempParagraph.getRight();
		}
	}
	
	public boolean print(enigma.console.Console cn, boolean mode)  {
		calculateLineBreaks();
		page();
		boolean cursorVisible = false;
		int coordx = 5, coordy =5;
		LetterNode tempLetter = cursor;
		ParagraphNode tempParagraph = null;
		while(tempLetter.getLeft() != null) tempLetter = tempLetter.getLeft();
		tempParagraph = tempLetter.getPrevLeftP();
		cn.getTextWindow().setCursorPosition(71, 18);
		if (tempParagraph.getAlignment() == 3) {
			cn.getTextWindow().output("Alignment: Justified ");
		}
		else if (tempParagraph.getAlignment() == 2) {
			cn.getTextWindow().output("Alignment: Right ");
		}
		else {
			cn.getTextWindow().output("Alignment: Left ");
		}
		cn.getTextWindow().setCursorPosition(71, 20);
		if (mode) cn.getTextWindow().output("Mode: Overwrite");
		else cn.getTextWindow().output("Mode: Insert");
		
		cn.getTextWindow().setCursorPosition(30, 27);
		cn.getTextWindow().output("-- " + numberOfPage + " --");
		
		if(printHead != null) {
			tempLetter = printHead;
			while(tempLetter.getLeft() != null) tempLetter = tempLetter.getLeft();
			tempParagraph = tempLetter.getPrevLeftP();
		}
		else tempParagraph = printHeadP;
		
		if(printHead != null) tempLetter = printHead;
		else tempLetter = printHeadP.getRight();
		
		while (tempParagraph!=null && coordy < 25) {
			if (tempParagraph.getAlignment() == 3) {
				justify(false, tempParagraph);
			}
			else if (tempParagraph.getAlignment() == 2) {
				rightAlignment(false, tempParagraph);
			}
			else {
				leftAlignment(false, tempParagraph);
			}
			while (tempLetter!=null && coordy < 25) {
				if(coordx < 68) {
				if(tempParagraph.getAlignment() == 2) {
					for (int i = 0; i < tempLetter.getEmptySize(); i++) {
						cn.getTextWindow().output(coordx, coordy, ' ');
						coordx++;
					}
				}
				if(tempLetter.getLetter() == '|') { 
					 cursory = coordy;
				     cursorx = coordx; 
				     cursorVisible = true;
				     if(System.currentTimeMillis() - start <= 1000L && System.currentTimeMillis() - start > 700L) { // flashing
							TextAttributes txt=new TextAttributes(Color.white, Color.white);
							if(!mode) cn.getTextWindow().output(cursorx, cursory, ' ');
							else cn.getTextWindow().output(cursorx, cursory, ' ',txt);
					    }
					else cn.getTextWindow().output(cursorx, cursory,'|');
					if(System.currentTimeMillis() - start >= 1000L) start = System.currentTimeMillis();
				}
				else {
					if (tempLetter.isSelected()) {
						TextAttributes txt=new TextAttributes(Color.white, Color.gray);
						cn.getTextWindow().output(coordx, coordy, tempLetter.getLetter(), txt);
					}else {
						TextAttributes txt;
						if(tempLetter.getColor() == 0) txt =new TextAttributes(Color.RED);
						else if(tempLetter.getColor() == 1) txt =new TextAttributes(Color.GREEN);
						else if(tempLetter.getColor() == 2) txt =new TextAttributes(Color.BLUE);
						else if(tempLetter.getColor() == 3) txt =new TextAttributes(Color.YELLOW);
						else if(tempLetter.getColor() == 4) txt =new TextAttributes(Color.PINK);
						else if(tempLetter.getColor() == 5) txt =new TextAttributes(Color.ORANGE);
						else if(tempLetter.getColor() == 6) txt =new TextAttributes(Color.GRAY);
						else if(tempLetter.getColor() == 7) txt =new TextAttributes(Color.WHITE);
						else if(tempLetter.getColor() == 8) txt =new TextAttributes(Color.CYAN);			
						else txt = new TextAttributes(Color.MAGENTA);							
						cn.getTextWindow().output(coordx, coordy, tempLetter.getLetter(), txt);
					}
					if(tempParagraph.getAlignment() == 3 && tempLetter.getLetter() == ' ') {
						for (int i = 0; i < tempLetter.getEmptySize(); i++) {
							coordx++;
							if (tempLetter.isSelected()) cn.getTextWindow().output(coordx, coordy, ' ', new TextAttributes(Color.white, Color.gray));
							else cn.getTextWindow().output(coordx, coordy, ' ');
						}
					}
				}
				}
				if(tempLetter.isLineBreak()) {
					coordx=5;
					coordy++;
				}
				else {
					coordx++;
				}
				tempLetter=tempLetter.getRight(); 
			}
			tempParagraph=tempParagraph.getDown();
			if (tempParagraph!=null) {
				tempLetter=tempParagraph.getRight();
				coordy++;
				coordx=5;
			}
		}
		if (addLetter != 2) {
			while (!cursorVisible) {
				deleteScreen(cn,true);
				if (addLetter == 1) {
					pageDown();
				}
				else pageUp();
				cursorVisible = print(cn, mode);
			}
		}
		if (cursorVisible) addLetter = 0;
		return cursorVisible;
	}
	public void deleteScreen(enigma.console.Console cn, boolean guide) {
		for (int j = 5; j < 68; j++) {
			for (int i = 5; i < 25; i++) {
				cn.getTextWindow().output(j,i, ' ');
			}
		}
		if (guide) {
			cn.getTextWindow().setCursorPosition(75, 20);
			cn.getTextWindow().output("                ");
			cn.getTextWindow().setCursorPosition(75, 18);
			cn.getTextWindow().output("                 ");
		}
		
	}

	public boolean isActiveSelection() {
		return isActiveSelection;
	}
	public void setActiveSelection(boolean isActiveSelection) {
		this.isActiveSelection = isActiveSelection;
	}
	
	public void dropSelections() {
		ParagraphNode tempParagraph=this.headParagraph;
		LetterNode temp = headParagraph.getRight();
		isActiveSelection = false;
		while (tempParagraph!=null) {
			if (temp!= null && temp.isSelected()) {
				temp.setSelected(false);
			}
			if(temp == null || temp.getRight() == null) {
				tempParagraph = tempParagraph.getDown();
				if(tempParagraph != null && tempParagraph.getRight() != null) temp = tempParagraph.getRight();
			}
			else {
				temp = temp.getRight();
			}
		}	
	}
}
