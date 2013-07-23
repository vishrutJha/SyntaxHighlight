import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;



public class SearcherWindow extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SearcherWindow(CustomTextArea textArea) {
		// TODO Auto-generated constructor stub
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(rootPane);
		setSize(new Dimension(400,400));
		setVisible(true);
		
//		JLabel searLabel = new JLabel("Enter search Term");
//		JTextField searchText = new JTextField("enter search Terms");
		
	}

}
