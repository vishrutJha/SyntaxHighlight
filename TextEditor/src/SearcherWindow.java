import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;



public class SearcherWindow extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTextField searchText;
	JLabel searLabel;

	public SearcherWindow(final CustomTextArea textArea) {
		// TODO Auto-generated constructor stub
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(rootPane);
		setSize(new Dimension(400,400));
		setLayout(new GridLayout(2, 4, 3, 3));
		setVisible(true);
		
		searLabel = new JLabel("Enter search Term");
		searchText = new JTextField("");
		
		add(searLabel);
		add(Box.createVerticalGlue());
		add(searchText);
		add(Box.createGlue());
		
		JButton search = new JButton("Search");
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = 0;
				searLabel.setText("Not Found");
				String stuff=textArea.getText();
				String positions = "";
				while(index < textArea.getText().length()-1){
					index+=stuff.indexOf(searchText.getText(),index);
					positions=""+index;
					textArea.positions.add(positions);
				}
				dispose();
			}
		});
		
		add(search);
		pack();
	}

}
