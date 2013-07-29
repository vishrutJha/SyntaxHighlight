import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;


public class TextFrame extends JFrame implements ActionListener,CaretListener, DropTargetListener, WindowListener{

	private static final long serialVersionUID = 1L;
	public CustomTextArea textArea;
	int pos;
	JTabbedPane frame;
	boolean fileOpened = false;
	private File openFile;
	private Vector<CustomTextArea> tabs;
	private String fileName = "Untitled.txt";
	private File saveFile;
	JMenu[] menuItems;
	JMenuItem wCount;
	JMenuItem fStatus;
	JMenuItem fType;
	JMenuItem[] fileItems;
	JButton newTab;
	JButton closeTab;
	JMenuBar statusBar;
	JTextField searcher;
	JFileChooser fileChooser;
	private JMenuItem[] optItems;
	private JMenuItem[] editItems;
	boolean color = false;
	
	@SuppressWarnings("serial")
	public TextFrame(String defFile) throws URISyntaxException, IOException{
		setTitle("Better Notepad");
		addWindowListener(this);
		setSize(800, 600);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setMinimumSize(new Dimension(640, 480));
		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMaximumSize(new Dimension(1024, 768));
		setLayout(new BorderLayout(5,5));
		
		JMenuBar menuBar = new JMenuBar();
		statusBar = new JMenuBar();
		statusBar.setLayout(new GridLayout(1, 4, 2, 2));
		wCount = new JMenuItem("Line:");
		fStatus = new JMenuItem("Writable");
		fType = new JMenuItem("");
		searcher = new JTextField("search..");
		
		statusBar.add(searcher);
		statusBar.add(wCount);
		statusBar.add(fStatus);
		statusBar.add(fType);
		
		tabs = new Vector<CustomTextArea>();
		
		searcher = new JTextField("Search");
				
		menuItems = new JMenu[]{new JMenu("File"),new JMenu("Options"),
									new JMenu("Edit"), new JMenu("Tools")};
		
		fileItems = new JMenuItem[]{
										new JMenuItem("New"),
										new JMenuItem("Open"),
										new JMenuItem("Save"),
										new JMenuItem("Save As"),
										new JMenuItem("Close")};
		
		/* 
		 * Now that Items are initialized, I will set
		 * Mnemonics and Accelerators on them using the 
		 * created functions 
		 */
		setMnemonics();
		
		setAccelerators();
		
		optItems = new JMenuItem[]{
				new JMenuItem("Font"),
				new JMenuItem("Settings"),
				new JMenuItem("Preferences")};
		
		editItems = new JMenuItem[]{
										new JMenuItem("Cut"),
										new JMenuItem("Copy"),
										new JMenuItem("Paste"),
										new JMenuItem("Select All")};
		
		//Creating the menu Bar
		for(int i=0;i<menuItems.length;i++){
			menuBar.add(menuItems[i]);
		}
		
		Action remTav = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeTab();
				
			}
		};
		
		Action addTav = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addTab(new CustomTextArea());				
			}
		};

		Font butFont = new Font("butfont", Font.BOLD, 18);
		
		menuBar.add(Box.createGlue());
		
		newTab = new JButton("+");
		newTab.setForeground(Color.BLUE);
		newTab.setFont(butFont);
		newTab.setFocusPainted(false);
		
		closeTab = new JButton("X");
		closeTab.setForeground(Color.RED);
		closeTab.setFocusPainted(false);
		closeTab.setFont(butFont);
		
		newTab.addActionListener(addTav);
		closeTab.addActionListener(remTav);
		
		//menuBar.add(searchBar);
		//menuBar.add(search);
		menuBar.add(newTab);
		menuBar.add(closeTab);
		
		addMenuItems();
		
		add(menuBar,BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);
				
		frame = new JTabbedPane(JTabbedPane.TOP);
				
		textArea = new CustomTextArea();
		
		addTab(textArea);
				
		if(defFile!=null){
			openFilesToView(defFile);
		}
				
		add(frame);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}
	

	/*
	 * initiates each indivisial tab to its properties
	 * adds listeners
	 * sets filepath, filename
	 */
	
	private void addTab(CustomTextArea textPlace) {
		
		//Setting tab's credentials
		textPlace.title = fileName;
		if(fileOpened){
			textPlace.firstSave = false;
		}
		
		//Add a Scrolling component to each added tab
		JScrollPane pane = new JScrollPane(textPlace);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		frame.addTab(fileName, pane);
		
		//Part to implement Drag and Drop for each tab
		setDropTarget(new DropTarget(textPlace, this));
		textPlace.addCaretListener(this);
		textPlace.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				updateFileType();
			}
		});
		
		//add this new tab to the Vector of tabs
		tabs.add(textPlace);
		
	}
	
	private void removeTab(){
		
		int i=frame.getSelectedIndex();
		if(i>=0){
			JOptionPane.showConfirmDialog(rootPane, "Are you sure You want to close "
								+tabs.elementAt(i).title, "Confirm ", JOptionPane.YES_NO_OPTION);
			if(tabs.elementAt(i).firstSave){
				saveTabAt(i);
			}
			frame.remove(i);
			tabs.remove(i);
		}
		else{
			JOptionPane.showMessageDialog(rootPane, "NO more tabs to remove", 
					"Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void openFilesToView(String defFile){
		/*
		 * a part of the handling sequence for
		 * fitting in multiple tabs into the 
		 * user space
		 */
		if(defFile!=null){
			try{
				File file = new File(defFile);
				if(file.exists()){
					openToDisplay(file);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void setMnemonics() {
		//Setting Mnemonic Listeners on MenuItems
		menuItems[0].setMnemonic(KeyEvent.VK_F);
		menuItems[1].setMnemonic(KeyEvent.VK_P);
		menuItems[2].setMnemonic(KeyEvent.VK_E);
		menuItems[3].setMnemonic(KeyEvent.VK_T);
		
		//Setting Mnemonic Listeners on sub-Menu Items
		fileItems[0].setMnemonic(KeyEvent.VK_N);
		fileItems[0].setMnemonic(KeyEvent.VK_O);
		fileItems[2].setMnemonic(KeyEvent.VK_S);
		fileItems[3].setMnemonic(KeyEvent.VK_A);
		fileItems[4].setMnemonic(KeyEvent.VK_X);
		
	}

	private void setAccelerators() {
		//Setting Accelerators and Mnemonics for Shortcut keys here
				fileItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
						ActionEvent.CTRL_MASK));
				fileItems[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
						ActionEvent.CTRL_MASK));
				fileItems[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
						ActionEvent.CTRL_MASK));				
				fileItems[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
						ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));				
				fileItems[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 
						ActionEvent.ALT_MASK));
				
//				menuItems[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 
//						ActionEvent.CTRL_MASK));
//				menuItems[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 
//						ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
		
	}

	private void addMenuItems(){
		int i;
		//for the File Menu
		for(i=0;i<fileItems.length;i++){
			menuItems[0].add(fileItems[i]);
			
			fileItems[i].setActionCommand(fileItems[i].getText());
			fileItems[i].addActionListener(this);
		}
		
		//for the Options Menu
		for(i=0;i<optItems.length;i++){
			menuItems[1].add(optItems[i]);
			
			optItems[i].setActionCommand(optItems[i].getText());
			optItems[i].addActionListener(this);
		}
		
		//for Edit Menu
		for(i=0;i<editItems.length;i++){
			menuItems[2].add(editItems[i]);
			
			editItems[i].setActionCommand(editItems[i].getText());
			editItems[i].addActionListener(this);
		}
		
		//for Tools
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		switch(action){
		
		case "New": CustomTextArea newText = new CustomTextArea();
					addTab(newText);
					break;
		
		case "Open": fileChooser = new JFileChooser();
					fileChooser.updateUI();
					openFile = getSelected(fileChooser,true);
					if(openFile!=null){
						if(tabs.lastElement().getText().length()==0){
							openToDisplay(openFile);
						}
						else{
							CustomTextArea openText = new CustomTextArea();
							addTab(openText);
							openToDisplay(openFile);
						}
					}
					break;
		
		case "Save":
					saveTabAt(frame.getSelectedIndex());
					break;
		
		case "Save As": fileChooser = new JFileChooser();
						saveFile = getSelected(fileChooser,false);
						if(saveFile!=null){
							saveFileFunct(saveFile);
							frame.setTitleAt(frame.getSelectedIndex(), saveFile.getName());
							tabs.elementAt(frame.getSelectedIndex()).firstSave = false;
							try {
								tabs.elementAt(frame.getSelectedIndex()).filepath = 
																	saveFile.getCanonicalPath();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					break;
		
		case "Close": this.windowClosing(null);
					break;
		
		case "Cut": textArea.cut();
					break;
		
		case "Compile": System.out.println("compilation selected!");
					break;
					
		case "Font":break;
					
		case "Paste": textArea.paste();
					break;
				
		case "Select All": textArea.selectAll();
					break;
		
		default : break;
		}
		
	}
	
	private void saveTabAt(int pos){
		
		textArea = tabs.elementAt(pos);
		if(textArea.filepath!=null){
		try{
			saveFile = new File(textArea.filepath);
			saveFileFunct(saveFile);
			tabs.elementAt(pos).firstSave = false;
		}catch(Exception w){
			w.printStackTrace();
		}
		}
		else{
			fileChooser = new JFileChooser();
			saveFile = getSelected(fileChooser,false);
			if(saveFile!=null){
				saveFileFunct(saveFile);
				frame.setTitleAt(pos, saveFile.getName());
				tabs.elementAt(pos).firstSave = false;
			}
		}
		
	}

	private void saveFileFunct(File sFile) {
		try {
			FileWriter filer = new FileWriter(sFile);
			BufferedWriter writer = new BufferedWriter(filer);
			CustomTextArea toSave = textArea;
			writer.write(toSave.getText());
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void openToDisplay(File toOpen) {
		/*
		 * Performs the opening of file stream
		 * and its appending operation to a  
		 * newly added tabulet and sets the 
		 * tab title
		 */
		try{
		FileReader filer = new FileReader(toOpen);
		BufferedReader reader = new BufferedReader(filer);
		
		String allContent="";
		
		textArea = tabs.lastElement();
		textArea.title = toOpen.getName();
		textArea.filepath = toOpen.getCanonicalPath();
		frame.setTitleAt(tabs.size()-1, toOpen.getName());
		
		String temp;
				
		while((temp=reader.readLine())!=null){
			allContent+=(temp+"\n");
		}
		
		textArea.setText(allContent);
		
		frame.setSelectedIndex(tabs.size()-1);
		reader.close();
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void updateFileType(){
		String extensions = tabs.elementAt(frame.getSelectedIndex()).title;
		String type = extensions.substring(extensions.indexOf(".")+1,extensions.length());
		String showText;
		System.out.println("type is "+extensions+type);
		switch(type){
		case "c":showText="C File";
				break;
		case "txt":showText="text File";
				break;
		case "py":showText="python File";
				break;
		case "java":showText="java File";
				break;
		case "cpp":showText="C++ file";
		default:showText="unKnown";		
		}
		
		fType.setText(showText);
	}
	
	private File getSelected(JFileChooser fileChooser, boolean mode) {
		
		/*
		 * mode it to choose if opening or saving a file
		 * mode true => Opening
		 * mode false => saving
		 */
		
		if(mode){
			if(fileChooser.showOpenDialog(rootPane) == JFileChooser.APPROVE_OPTION)
				return fileChooser.getSelectedFile();
		}
		else{
			if(fileChooser.showSaveDialog(rootPane) == JFileChooser.APPROVE_OPTION)
				return fileChooser.getSelectedFile();
		}
		
		return null;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		
	}

	/*
	 * this amazing piece of code incorporates
	 * drag and drop of multiple files to 
	 * open into multiple tabs each with an
	 * individual identity and path storage
	 */
	
	@Override
	public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY);
		
		if(dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
			try {
				Object files = dtde.getTransferable().
						getTransferData(DataFlavor.javaFileListFlavor);
				
					System.out.println(files.toString());
					String path = files.toString();
					path = path.subSequence(path.indexOf("[")+1, path.indexOf("]")).toString();
										
					System.out.println("path is:"+path);
					
					String[] paths = path.split(", ");
					
					int i=0;
					
					if(textArea.getText().length()==0){
						fileOpened = true;
						openFilesToView(paths[i]);
						i++;
					}
					while(i<paths.length){
						CustomTextArea area = new CustomTextArea();
						addTab(area);
						openFilesToView(paths[i]);
						i++;
					}
					
					fileOpened = false;
					
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("initialized close");
		
		if(tabs.size()==0){
			System.exit(0);
		}
		
		for(int i=0;i<tabs.size();i++){
			CustomTextArea a = tabs.elementAt(i);
			if(a.filepath==null || a.firstSave == true){
				int option = JOptionPane.showConfirmDialog(this.rootPane, 
												"File "+a.title+" not saved!\nsave??",
												"Unsaved Document",
												JOptionPane.YES_NO_CANCEL_OPTION);
				switch(option){
				case JOptionPane.YES_OPTION:
						saveTabAt(i);
						tabs.remove(i);
						frame.remove(i);
						break;
				
				case JOptionPane.CANCEL_OPTION:
						break;
						
				case JOptionPane.NO_OPTION:
						if(tabs.size()==1){
							dispose();
							System.exit(0);
						}
						tabs.remove(i);
						frame.remove(i);
						break;
				}
			}
			else{
				if(tabs.size()==1){
					dispose();
					System.exit(0);
				}
				tabs.remove(i);
				frame.remove(i);
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caretUpdate(CaretEvent car) {
		// TODO Auto-generated method stub
		try {
			int line=textArea.getLineOffset(textArea, car.getMark());
			int col=car.getDot()-textArea.getLineStartOffset(textArea, line);
			wCount.setText("Line: "+line+",Col: "+col);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
