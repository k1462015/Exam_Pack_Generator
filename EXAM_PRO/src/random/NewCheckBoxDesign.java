package random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class NewCheckBoxDesign {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Testing");
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel panel1 = new JPanel();
		JLabel label1 = new JLabel("TAB PAGE 1");
		panel1.add(label1);
		tabbedPane.addTab("Question Paper", panel1);
		
		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel("TAB PAGE 2");
		panel2.add(label2);
		tabbedPane.addTab("Mark Scheme", panel2);
		
		frame.add(tabbedPane);
		
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
