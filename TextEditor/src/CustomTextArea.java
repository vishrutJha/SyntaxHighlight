import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


public class CustomTextArea extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String [] keywords={"abstract", "do", "import", "public", "throws", "boolean",
			"double", "instanceof", "return", "transient", "break", "else", "int", "short",
			"try", "byte", "extends", "interface", "static", "void", "case", "final", "long",
			"strictfp", "volatile", "catch", "finally", "native", "super", "while", "char", "float",
			"new", "switch", "class", "for", "package", "synchronized", "continue", "if", "private",
			"this", "default", "implements", "protected", "const", "goto", "null", "true", "false"};
	
	
	SimpleAttributeSet blue;
	SimpleAttributeSet normal;
	SimpleAttributeSet mod;
	DefaultStyledDocument doc;
	String prev;
	UndoManager undo;
	Vector<String> positions;
	public boolean firstSave=true;
	public String title = "Untitled";
	public String filepath = null;
	private String keywordString = "(\\W)*(";
	
	public CustomTextArea() {

		firstSave = false;
		positions = new Vector<>();		
		undo = new UndoManager();
		
		setKeywords();
		
		setDocumentProperties();
		
        setDocument(doc);
        
        addListenerComponents();
	}
	
	private void addListenerComponents() {
		
		doc.addUndoableEditListener(new UndoableEditListener() {
			
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});		
		
		InputMap inp = getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap aMap = getActionMap();
		
		inp.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		inp.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");
		
		aMap.put("Undo", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					if(undo.canUndo())
						undo.undo();
				}
				catch(CannotUndoException e1){
					e1.printStackTrace();
				}
			}
		});
		
		aMap.put("Redo", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					if(undo.canRedo())
						undo.redo();
				}
				catch(CannotRedoException e2){
					e2.printStackTrace();
				}
			}
		});
		
		inp.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Find");

		aMap.put("Find", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSearch();
			}
		});

		
	}
	
	int getLineOffset(JTextComponent comp, int offset) throws BadLocationException{
		
		Document doc = comp.getDocument();
		if(offset<0){
				throw new BadLocationException("Bad arguement offset", -1);
		}else if(offset > doc.getLength()){
				throw new BadLocationException("Bad Arguement beyond doc", doc.getLength()+1);
		}else{
			javax.swing.text.Element map = doc.getDefaultRootElement();
			return map.getElementIndex(offset);
		}
	}

	int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException{
		javax.swing.text.Element map = comp.getDocument().getDefaultRootElement();
		if(line<0){
				throw new BadLocationException("invalid line count", -1);
		}else if(line > map.getElementCount()){
				throw new BadLocationException("count out of bound", comp.getDocument().getLength()+1);
		}else{
			javax.swing.text.Element lineElem = map.getElement(line);
			return lineElem.getStartOffset();
		}
		
	}
		
	private void setDocumentProperties() {
		
        blue = new SimpleAttributeSet();
        StyleConstants.setBold(blue, true);
        StyleConstants.setForeground(blue, Color.BLUE);
        
        normal = new SimpleAttributeSet();
        StyleConstants.setBold(normal, false);
        StyleConstants.setForeground(normal, Color.black);

        doc = new DefaultStyledDocument() {
            /**
			 * this is to add colouring functionality while content is inserted to Document
			 * contains an override to default document properties
			 */
			private static final long serialVersionUID = 1L;

			public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches(keywordString))
                            setCharacterAttributes(wordL, wordR - wordL, blue, false);
                        else
                            setCharacterAttributes(wordL, wordR - wordL, normal, false);
                        wordL = wordR;
                    }
                    wordR++;
                }
            }

            public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches(keywordString)) {
                    setCharacterAttributes(before, after - before, blue, false);
                } else {
                    setCharacterAttributes(before, after - before, normal, false);
                }
            }
        };
		
		
	}

	private void setKeywords() {
	
		for(String s:keywords){
			keywordString+=s;
			keywordString+="|";
		}
		keywordString+=")";
	}

	private void doSearch(){
		SearcherWindow search = new SearcherWindow(this);
		search.requestFocus();
		
		for(String pos : positions)
			System.out.println("found at  "+pos);
		
	}
		
	private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }
	
}
