import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

import Viewer.Main;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;


public class MainFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	DefaultListModel lmAvailable;
	DefaultListModel lmSelected;
	JPanel jpTopics;
	int pressedIndex = 0;
	int releasedIndex = 0;
	ArrayList<String> topicNames;
	ArrayList<String> currentFilters;
	ArrayList<QuestionPaper> allPapers;
	JPanel mainPanel;
	PagePanel panel;
	PDFFile pdffile;
	int currentIndex = 0;
	
	public MainFrame(){
		super("Exam-Pro (Trial)");
		initUi();
	}
	
	private boolean search(ArrayList<String> array,String input){
		for(int i = 0;i < array.size();i++){
			if(array.get(i).equals(input)){
				return true;
			}
			
		}
		return false;
	}
	
	private void initUi(){
		allPapers = new ArrayList<QuestionPaper>();
		//MenuBar
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem selectDirectory = new JMenuItem("Browse Directory");
		selectDirectory.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				FileReader fr = new FileReader();
				allPapers = fr.readPapers();
				topicNames = new ArrayList<String>();
				for(QuestionPaper qp:allPapers){
					lmAvailable.addElement(qp);
					if(!(search(topicNames,qp.getTopicName()))){
						topicNames.add(qp.getTopicName());
						currentFilters.add(qp.getTopicName());
						JCheckBox temp = new JCheckBox(qp.getTopicName());
						temp.setSelected(true);
						temp.addItemListener(new ItemListener(){

							@Override
							public void itemStateChanged(ItemEvent arg0) {
								if(temp.isSelected()){
									System.out.println("Topic name added");
									currentFilters.add(temp.getText());
									refilter();
								}else{
									System.out.println("Topuc name removed");
									currentFilters.remove(temp.getText());
									refilter();
								}
								
							}
							
						});
						
						jpTopics.add(temp);
					}
				}
				
				setVisible(true);
				
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
		
		jbcreateQP.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				PDFMergerUtility mu = new PDFMergerUtility();
				for(int i = 0;i < lmSelected.size();i++){
					QuestionPaper qp = (QuestionPaper)lmSelected.getElementAt(i);
					mu.addSource(qp.getLocation());
				}
			
				mu.setDestinationFileName("C:\\Users\\Tahmidul\\Desktop\\MERGED.pdf");
				JOptionPane.showMessageDialog(null, "PDF Saved to desktop");
				
				try {
					mu.mergeDocuments();
				} catch (COSVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
		
		try {
			mainPanel();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		//Default JFrame Stuff
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1500,1000);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void mainPanel() throws IOException{
		mainPanel = new JPanel(new BorderLayout());
		add(mainPanel,BorderLayout.CENTER);
		
		//Adds PDF Viewer
		//set up the frame and panel
        panel = new PagePanel();
        mainPanel.add(panel,BorderLayout.CENTER);
        
		
		
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(500,110));
		sidePanel.setBorder(new LineBorder(Color.BLUE,3));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(sidePanel,BorderLayout.WEST);
		
		
		//PDF Page control
		JPanel jpNavigation = new JPanel();
		JButton previous = new JButton("Previous");
		JButton next = new JButton("Next");
		previous.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentIndex > 0){
				System.out.println("Current index" + currentIndex);
				currentIndex--;
				System.out.println("New index" + currentIndex);
		        PDFPage page = pdffile.getPage(currentIndex);
		        panel.showPage(page);
				}
			}
			
		});
		next.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// show the first page
				System.out.println("Current index" + currentIndex);
				currentIndex++;
				System.out.println("New index" + currentIndex);
		        PDFPage page = pdffile.getPage(currentIndex);
		        panel.showPage(page);
				
			}
			
		});
		jpNavigation.add(previous);
		jpNavigation.add(next);
		jpNavigation.setMaximumSize(new Dimension(300,10));
		sidePanel.add(jpNavigation);
		
		
		
		//Filter Panel
		currentFilters = new ArrayList<String>();
		JPanel pFilter = new JPanel();
		pFilter.setBorder(new LineBorder(Color.BLACK,3));
		pFilter.setLayout(new BoxLayout(pFilter, BoxLayout.PAGE_AXIS));
		JPanel jpYear = new JPanel(new GridLayout(4,4));
		jpYear.setBorder(new LineBorder(Color.RED,3));
		jpYear.setPreferredSize(new Dimension(500,5));
		
		for(int i = 0;i < 15;i++){
			String y = "20";
			if(i < 10){
				y = y + "0"+i;
			}else{
				y = y + i;
			}
			currentFilters.add(y);
			JCheckBox t = new JCheckBox(y);
			t.setSelected(true);

			t.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(t.isSelected()){
						System.out.println("Year has been selected");
						currentFilters.add(t.getText());
						refilter();
					}else{
						System.out.println("Year has been unselected");
						currentFilters.remove(t.getText());
						refilter();
					}
					
				}
				
			});
			jpYear.add(t);
		}
		pFilter.add(jpYear);
		jpTopics = new JPanel(new GridLayout(10,3));
		jpTopics.setBorder(new LineBorder(Color.RED,3));
		jpTopics.setPreferredSize(new Dimension(500,5));
		
		
		pFilter.add(jpTopics);
		
		
		
		
		sidePanel.add(pFilter);
		//Load question papers from file
		lmAvailable = new DefaultListModel();
		
		
		//List of available question papers
		JList jListAvailable = new JList(lmAvailable);
		jListAvailable.setFont(new Font("Calibri",Font.PLAIN,25));
		jListAvailable.setBorder(new LineBorder(Color.BLACK,3));
		
		jListAvailable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
				if (e.getClickCount() == 2) {
					lmSelected.addElement(qp);
				}
				if(e.getClickCount() == 1){
					try {
						setup(qp.getLocation());
						currentIndex = 0;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		sidePanel.add(new JScrollPane(jListAvailable));
		
		
		
		//List of selected question papers
		lmSelected = new DefaultListModel();
		JList jListSelected = new JList(lmSelected);
		jListSelected.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
					lmSelected.removeElement(jListSelected.getSelectedValue());
				}
			}
			
			public void mousePressed(MouseEvent e){
				if(e.getClickCount() == 1){
				pressedIndex = jListSelected.getSelectedIndex();
				System.out.println(jListSelected.getSelectedIndex());
				}
			}
			
			public void mouseReleased(MouseEvent e){
				if(e.getClickCount() == 1){
				releasedIndex = jListSelected.getSelectedIndex();
				QuestionPaper temp1 = (QuestionPaper) lmSelected.get(pressedIndex);
				QuestionPaper temp2 = (QuestionPaper) lmSelected.get(releasedIndex);
				lmSelected.remove(pressedIndex);
				lmSelected.add(pressedIndex, temp2);
				lmSelected.remove(releasedIndex);
				lmSelected.add(releasedIndex, temp1);
				}
				
				System.out.println("Mouse Released at index "+jListSelected.getSelectedIndex());
			}
		});
		jListSelected.setFont(new Font("Calibri",Font.PLAIN,25));
		jListSelected.setBorder(new LineBorder(Color.BLACK,3));
		
		sidePanel.add(new JScrollPane(jListSelected));
		sidePanel.setPreferredSize(new Dimension(500,200));
		
		pack();
	}
	
	public void refilter(){
		System.out.println("Refiltering");
		if(allPapers.size() != 0){
		lmAvailable.removeAllElements();
		for(int i = 0;i < currentFilters.size();i++){
			for(QuestionPaper qp:allPapers){
				if(qp.getTopicName().contains(currentFilters.get(i))){
					lmAvailable.addElement(qp);
				}
			}
		}
		}
	}
	
	public void setup(String filepath) throws IOException{
        //load a pdf from a byte buffer
        File file = new File(filepath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        pdffile = new PDFFile(buf);

        // show the first page
        PDFPage page = pdffile.getPage(0);
        panel.showPage(page);
        
	}
	

	public static void main(String[] args) {
		new MainFrame();

	}

}
