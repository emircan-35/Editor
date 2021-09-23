import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import enigma.console.TextAttributes;
import enigma.core.Enigma;

import java.awt.Color;
import java.awt.event.KeyEvent;


public class Editor {
	private EditorLinkedList editor;
	private File log; // to make the program alive even when the program does not work
	private String openNow; // shows the name of file which is open at this moment
	private enigma.console.Console cn;
	private KeyListener klis;
	private int keypr;
	private int rkey;
	private int rkeymod;
	private int currentColor, currentAlignment;
	private boolean capsLock;
	private boolean mode;
	private boolean selection; // shows the whether selection is active or not.
	
	public Editor() {
		this.editor=new EditorLinkedList();
		this.cn = Enigma.getConsole("Editor - DEU CENG", 140, 35, 18);
		log=new File("log.txt");
		capsLock = false;
		currentColor = 7;
		currentAlignment = 1;
		selection = mode = false;
		startEditor();
	}
	
	public void startEditor() {
		//ENIGMA OPERATIONS FOR ONLY ONE TIME
		klis=new KeyListener() {
	         public void keyTyped(KeyEvent e) {}
	         public void keyPressed(KeyEvent e) {
	            if(keypr==0) {
	               keypr=1;
	               rkey=e.getKeyCode();
	             
	               rkeymod=e.getModifiersEx();
	            }
	         }
	         public void keyReleased(KeyEvent e) {}
	      };
	      //If the log file does not exist, creating it
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				System.out.println("The editor is being prevented by your operating system");
				e.printStackTrace();
			}
		}
		enigma();
	}
	public void enigma() {
      cn.getTextWindow().addKeyListener(klis);

		displayScreen();
      while(true) {
    	    if(keypr==1) {    
	        processInput((char)rkey);
	        editor.deleteScreen(cn, true); 
	        }
	        keypr=0; 
	        editor.print(cn, mode);
	    }    
    }
	
	
	//Every input is processed in this function with switch case
	public void processInput(char rckey) {
		//Switch case is preferred to be faster comparing the if else block.
		switch (rckey) {
		case '%': // left
			editor.switchCursorLeft(true);
			break; 
		case '\'': // right
			editor.switchCursorRight(true);
			break;
		case '&': // up
			editor.switchCursorUp(true);
			break;
		case '(': // down
			editor.switchCursorDown(true);
			break;
		case (char)KeyEvent.VK_CAPS_LOCK: // the system only detects a little long pressure.
			capsLock = !capsLock;
			break;
		case (char)KeyEvent.VK_DELETE:
			editor.del();
			break;
		case (char)KeyEvent.VK_ENTER:
			editor.addParagraph(currentAlignment);
			break;
		case (char)KeyEvent.VK_F1:
			editor.setActiveSelection(true);
			break;
		case (char)KeyEvent.VK_F2:
			editor.setActiveSelection(false);
			selection = true;
			break;
		case (char)KeyEvent.VK_F3:
			if ((rkeymod & KeyEvent.SHIFT_DOWN_MASK) > 0) {
				editor.upperLowerCase();
			}
			else {
				editor.dropSelections();
				selection = false;
			}
			break;
		case (char)KeyEvent.VK_X:
			if((rkeymod & KeyEvent.CTRL_DOWN_MASK) > 0) {
				editor.copyAndCut(1);
				break;
			}
			else {
				addLetter(rckey);
				break;
			}
		case (char)KeyEvent.VK_C:
			if((rkeymod & KeyEvent.CTRL_DOWN_MASK) > 0) {
				editor.copyAndCut(0);
				break;
			}
			else {
				addLetter(rckey);
				break;
			}
		case (char)KeyEvent.VK_V:
			if((rkeymod & KeyEvent.CTRL_DOWN_MASK) > 0) {
				editor.paste();
				break;
			}
			else {
				addLetter(rckey);
				break;
			}	
		case (char)KeyEvent.VK_F4:
			editor.copyAndCut(2);
			editor.find(false);
			break;
		case (char)KeyEvent.VK_F5:
			cn.getTextWindow().setCursorPosition(71, 21);
			cn.getTextWindow().output("Replace: ");
			Scanner sc=new Scanner(System.in);
			char[] word = sc.nextLine().toCharArray();
			
			sc.close();
			editor.replace(word);
			cn.getTextWindow().setCursorPosition(71, 21);
			cn.getTextWindow().output("                                                   ");
			break;
		case (char)KeyEvent.VK_F6:
			editor.next();
			break;
		case (char)KeyEvent.VK_F7:
			selectColor();
			break;
		case (char)KeyEvent.VK_F8:
			editor.leftAlignment(selection, null);
			currentAlignment = 1;
			break;
		case (char)KeyEvent.VK_F9:
			editor.rightAlignment(selection, null);
			currentAlignment = 2;
			break;
		case (char)KeyEvent.VK_F10:
			editor.justify(selection, null);
			currentAlignment = 3;
			break;
		case (char)KeyEvent.VK_F11:
			load();
			break;
		case (char)KeyEvent.VK_F12:
			save();
			break;
		case (char)KeyEvent.VK_PAGE_UP:
			if (editor.pageUp())
			for (int i = 0; i < 20; i++)
			  editor.switchCursorUp(false);
			break;
		case (char)KeyEvent.VK_PAGE_DOWN:
			if (editor.pageDown())
			for (int i = 0; i < 20; i++)
			  editor.switchCursorDown(true);
			break;
		case (char)KeyEvent.VK_HOME:
			editor.home();
			break;
		case (char)KeyEvent.VK_END:
			editor.end();
			break;
		case (char)KeyEvent.VK_BACK_SPACE:
			editor.delete(true);
			break;
		case (char)KeyEvent.VK_INSERT:
			mode = !mode;
			break;
		case (char)KeyEvent.VK_Z:
		if((rkeymod & KeyEvent.CTRL_DOWN_MASK) > 0) {
			editor.undo(currentColor);
			break;
		}
		else {
			addLetter(rckey);
			break;
		}
		default:
			addLetter(rckey);
			break;
		}
	}

	public void addLetter(char rckey) {
		
		if((rckey>='0' && rckey<='9') || rckey==' ') editor.addLetter(rckey, mode,true,currentColor);
		else if(rckey>='A' && rckey<='Z') {
          if(((rkeymod & KeyEvent.SHIFT_DOWN_MASK) > 0) || capsLock) editor.addLetter(rckey, mode,true,currentColor);
          else editor.addLetter((char)(rckey+32),mode,true,currentColor);
        }
		else if((rkeymod & KeyEvent.SHIFT_DOWN_MASK) == 0) {
          if(rckey=='.' || rckey==',' || rckey=='-') editor.addLetter(rckey,mode,true,currentColor);
        }
        else {
          if(rckey == '.')  editor.addLetter(':',mode,true,currentColor);   
          else if(rckey == ',')  editor.addLetter(';',mode,true,currentColor);
       } 
	}
	//Lets the user select a color.
	//Changes the currentColor to the selected color to change the color of text.
	public void selectColor() {
		int a = 4;
		TextAttributes color;
		while(true) {
			cn.getTextWindow().setCursorPosition(71, 20);
			cn.getTextWindow().output("Select a color: ");
			
			if(keypr==1) {    
				if ((char)rkey=='%' && a!=0) a--;
				else if((char)rkey=='\'' && a!=9) a++;
				else if((char)(rkey)==KeyEvent.VK_ENTER) break;
				cn.getTextWindow().setCursorPosition(71, 21);
				cn.getTextWindow().output("Red  Green  Blue  Yellow  Pink");
				cn.getTextWindow().setCursorPosition(71, 22);
				cn.getTextWindow().output("Orange  Gray  White  Cyan  Magenta");
			}
			if(a == 0) {color =new TextAttributes(Color.RED); cn.getTextWindow().setCursorPosition(71, 21); cn.getTextWindow().output("Red",color);}
			else if(a == 1) {color =new TextAttributes(Color.GREEN); cn.getTextWindow().setCursorPosition(76, 21); cn.getTextWindow().output("Green",color);}
			else if (a == 2){color =new TextAttributes(Color.BLUE); cn.getTextWindow().setCursorPosition(83, 21); cn.getTextWindow().output("Blue",color);}
			else if (a == 3){color =new TextAttributes(Color.YELLOW); cn.getTextWindow().setCursorPosition(89, 21); cn.getTextWindow().output("Yellow",color);}
			else if (a == 4){color =new TextAttributes(Color.PINK); cn.getTextWindow().setCursorPosition(97, 21); cn.getTextWindow().output("Pink",color);}
			else if (a == 5){color =new TextAttributes(Color.ORANGE); cn.getTextWindow().setCursorPosition(71, 22); cn.getTextWindow().output("Orange",color);}
			else if (a == 6){color =new TextAttributes(Color.GRAY); cn.getTextWindow().setCursorPosition(79, 22); cn.getTextWindow().output("Gray",color);}
			else if (a == 7){color =new TextAttributes(Color.WHITE); cn.getTextWindow().setCursorPosition(85, 22); cn.getTextWindow().output("White",color);}
			else if (a == 8){color =new TextAttributes(Color.CYAN); cn.getTextWindow().setCursorPosition(92, 22); cn.getTextWindow().output("Cyan",color);}
			else {color =new TextAttributes(Color.MAGENTA); cn.getTextWindow().setCursorPosition(98, 22); cn.getTextWindow().output("Magenta",color);}
			keypr=0;  
		}
		currentColor = a;
		cn.getTextWindow().setCursorPosition(71, 20);
		cn.getTextWindow().output("               ");
		cn.getTextWindow().setCursorPosition(71, 21);
		cn.getTextWindow().output("                              ");
		cn.getTextWindow().setCursorPosition(71, 22);
		cn.getTextWindow().output("                                  ");
	
	}

	//Saving function
	public void save() {
		//If the file which is opened in this moment has not a name,
		//The user is asked to enter a name for saving
		if (openNow==null) {
			editor.deleteScreen(cn, false);
			cn.getTextWindow().setCursorPosition(7, 7);
			cn.getTextWindow().output("WRITE THE NAME OF NEW FILE:   ");
			Scanner reader=new Scanner(System.in);
			openNow=reader.nextLine();
			reader.close();
			editor.save(openNow);
		}else {
			editor.save(openNow);
		}
	}
	
	//Loading function
	public void load() {
		editor.deleteScreen(cn, false);
		int lineNumber=0;
		//First, log file is read for deciding the length of the array
		try {
			Scanner reader=new Scanner(log);
			while (reader.hasNextLine()) {
				lineNumber++;
				reader.nextLine();
				
			}
			reader.close();
		} catch (FileNotFoundException e) {}
		
		String[] choices=new String[lineNumber+1];
		choices[0]="Create a new file";
		//Second reading for filling the array
		try {
			Scanner reader=new Scanner(log);
			for (int i = 0; i < choices.length-1; i++) {
				choices[i+1]=reader.nextLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {}
			
		
		//Taking the input for which file will open
		int y=7;
		while(true) {
			for (int i = 0; i < choices.length; i++) {
				TextAttributes attribute=new TextAttributes(Color.GREEN);
				cn.getTextWindow().setCursorPosition(7, 7+i);
				if (y-7==i) {
					cn.getTextWindow().output(choices[i],attribute);
				}else {
					cn.getTextWindow().output(choices[i]);
				}
			}
			if(keypr==1) {    
				if ((char)rkey=='&' && y!=7) y--;
				else if((char)rkey=='(' && y!=6+choices.length ) y++;
				else if((char)(rkey)==KeyEvent.VK_ENTER) break;	
			}
			keypr=0;   
		    }
		//After the selection, load operation will start
		if (y!=7) {
			editor.load(choices[y-7]);
			openNow=choices[y-7];
		}else {
			editor.deleteEditor();
			openNow=null;
		}
		
	}
	public void displayScreen() {
		for (int i = 0; i < 67; i++) {
			cn.getTextWindow().output(2+i,3, '#');
			cn.getTextWindow().output(2+i,26, '#');
		}
		for (int i = 0; i < 22; i++) {
			cn.getTextWindow().output(2,4+i, '#');
			cn.getTextWindow().output(69,4+i, '#');
		}
		cn.getTextWindow().setCursorPosition(30, 27);
		cn.getTextWindow().output("-- 1 --");
		cn.getTextWindow().setCursorPosition(71, 5);
		cn.getTextWindow().output("F1:Selection start           Ctrl + X:Cut  ");
		cn.getTextWindow().setCursorPosition(71, 6);
		cn.getTextWindow().output("F2:Selection end             Ctrl + C:Copy ");
		cn.getTextWindow().setCursorPosition(71, 7);
		cn.getTextWindow().output("F3:Drop selections           Ctrl + V:Paste ");
		cn.getTextWindow().setCursorPosition(71, 8);
		cn.getTextWindow().output("F4:Find                      Ctrl + Z:Undo changes ");
		cn.getTextWindow().setCursorPosition(71, 9);
		cn.getTextWindow().output("F5:Replace                   Shift + F3: Change lettercase");
		cn.getTextWindow().setCursorPosition(71, 10);
		cn.getTextWindow().output("F6:Next   ");
		cn.getTextWindow().setCursorPosition(71, 11);
		cn.getTextWindow().output("F7:Select Color   ");
		cn.getTextWindow().setCursorPosition(71, 12);
		cn.getTextWindow().output("F8:Left Alignment ");
		cn.getTextWindow().setCursorPosition(71, 13);
		cn.getTextWindow().output("F9:Right Alignment  ");
		cn.getTextWindow().setCursorPosition(71, 14);
		cn.getTextWindow().output("F10:Justify   ");
		cn.getTextWindow().setCursorPosition(71, 15);
		cn.getTextWindow().output("F11:Load     ");
		cn.getTextWindow().setCursorPosition(71, 16);
		cn.getTextWindow().output("F12:Save         ");

	}
}




