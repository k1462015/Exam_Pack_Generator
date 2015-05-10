import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class MainFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	DefaultListModel lmAvailable;
	
	public MainFrame(){
		super("Exam-Pro (Trial)");
		initUi();
	}
	
	private void initUi(){
		//MenuBar
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem selectDirectory = new JMenuItem("Browse Directory");
		selectDirectory.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				FileReader fr = new FileReader();
				ArrayList<QuestionPaper> qpArray = fr.readPapers();
				for(QuestionPaper qp:qpArray){
					lmAvailable.addElement(qp);
				}
				
			}
			
		});
		menuBar.add(file);
		file.add(selectDirectory);
		setJMenuBar(menuBar);
		
		//Constructs bottom panels
		JPanel bottomPanel = new JPanel();
		JLabel jlcreate = new JLabel("Create:");
		JButton jbcreateQP = new JButton("Question-Paper");
		JButton jbcreateMS = new JButton("Mark-Scheme");
		bottomPanel.add(jlcreate);
		bottomPanel.add(jbcreateQP);
		bottomPanel.add(jbcreateMS);
		
		mainPanel();
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		//Default JFrame Stuff
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1500,1000);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void mainPanel(){
		JPanel mainPanel = new JPanel(new BorderLayout());
		add(mainPanel,BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(500,110));
		sidePanel.setBorder(new LineBorder(Color.BLUE,3));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(sidePanel,BorderLayout.WEST);

		//Filter Panel
		JPanel pFilter = new JPanel();
		pFilter.setBorder(new LineBorder(Color.BLACK,3));
		pFilter.setLayout(new BoxLayout(pFilter, BoxLayout.PAGE_AXIS));
		JPanel jpYear = new JPanel();
		jpYear.setBorder(new LineBorder(Color.RED,3));
		jpYear.setPreferredSize(new Dimension(500,5));
		
		for(int i = 0;i < 15;i++){
			String y = "20";
			if(i < 10){
				y = y + "0"+i;
			}else{
				y = y + i;
			}
			JCheckBox t = new JCheckBox(y);
			jpYear.add(t);
		}
		pFilter.add(jpYear);
		JComboBox jcbTopic = new JComboBox(new String[]{"Transition Metals","Periodicity"});
		jcbTopic.setFont(new Font("Calibri",Font.BOLD,25));
		pFilter.add(jcbTopic);
		
		sidePanel.add(pFilter);
		//Load question papers from file
		lmAvailable = new DefaultListModel();
		
		
		//List of available question papers
		JList jListAvailable = new JList(lmAvailable);
		jListAvailable.setFont(new Font("Calibri",Font.PLAIN,25));
		jListAvailable.setBorder(new LineBorder(Color.BLACK,3));
		jListAvailable.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
				System.out.println(qp.getLocation());
				
			}
			
		});
		
		
		JList jListSelected = new JList(new String[]{"Question 3","Question 4"});
		jListSelected.setBorder(new LineBorder(Color.BLACK,3));

		sidePanel.add(new JScrollPane(jListAvailable));
		sidePanel.add(new JScrollPane(jListSelected));
		
		
		sidePanel.setPreferredSize(new Dimension(500,200));
		pack();
		//List of selected question papers
	}

	public static void main(String[] args) {
		new MainFrame();

	}

}
