import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

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
	ArrayList<String> currentFiltersYear;
	ArrayList<QuestionPaper> allPapers;
	JPanel mainPanel;
	PagePanel panel;
	PDFFile pdffile;
	int currentIndex = 1;
	JLabel jlcurrentPDF;
	int totalMarks = 0;
	JLabel jlTotalMarks;
	JLabel jlPageCount;

	public MainFrame(){
		super("Exam-Pro (Trial)");
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {e.printStackTrace();     }
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
						temp.setFont(new Font("Calibri",Font.BOLD,15));
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
		jlcreate.setFont(new Font("Calibri",Font.BOLD,30));
		JButton jbcreateQP = new JButton("Question-Paper");
		jbcreateQP.setFont(new Font("Calibri",Font.BOLD,30));
		JButton jbcreateMS = new JButton("Mark-Scheme");
		jbcreateMS.setFont(new Font("Calibri",Font.BOLD,30));
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
				JFileChooser jfcDirectory = new JFileChooser();
				jfcDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfcDirectory.setCurrentDirectory(new java.io.File("."));
				
				jfcDirectory.setAcceptAllFileFilterUsed(false);

				if(jfcDirectory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					System.out.println("CURRENT DIREC "+jfcDirectory.getSelectedFile().getPath());
				
				mu.setDestinationFileName(jfcDirectory.getSelectedFile().getPath()+"\\MERGED.pdf");
				}
				JOptionPane.showMessageDialog(null, "PDF Saved to "+jfcDirectory.getSelectedFile().getPath());
				
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
		//:\Users\Tahmidul\Documents\exam_pro\CHEM5 PDF - Copy\Questions\MS_QP.GETYEAR()_QP.GETQ()
		jbcreateMS.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				PDFMergerUtility mu = new PDFMergerUtility();
				for(int i = 0;i < lmSelected.size();i++){
					QuestionPaper qp = (QuestionPaper)lmSelected.getElementAt(i);
					System.out.println(qp.getLocation());
					String filePath = qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf";
					System.out.println("Mark Scheme : "+filePath);
					mu.addSource(filePath);
				}
				JFileChooser jfcDirectory = new JFileChooser();
				jfcDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfcDirectory.setCurrentDirectory(new java.io.File("."));
				
				jfcDirectory.setAcceptAllFileFilterUsed(false);

				if(jfcDirectory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					System.out.println("CURRENT DIREC "+jfcDirectory.getSelectedFile().getPath());
				
				mu.setDestinationFileName(jfcDirectory.getSelectedFile().getPath()+"\\MARKSCHEME.pdf");
				}
				JOptionPane.showMessageDialog(null, "PDF Saved to "+jfcDirectory.getSelectedFile().getPath());
				
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
		mainPanel.setBorder(new LineBorder(Color.BLACK,2));
		add(mainPanel,BorderLayout.CENTER);
		
		//JLabel to show what is currently being shown
		jlcurrentPDF = new JLabel("No page selected");
		jlcurrentPDF.setBorder(new LineBorder(Color.black,3));
		jlcurrentPDF.setFont(new Font("Calibri",Font.BOLD,70));
		jlcurrentPDF.setMaximumSize(new Dimension(700, 500));
		jlcurrentPDF.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlcurrentPDF.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		mainPanel.add(jlcurrentPDF,BorderLayout.NORTH);
		
		//Adds PDF Viewer
		//set up the frame and panel
		JPanel jpViewer = new JPanel(new BorderLayout());
        panel = new PagePanel();
		jpViewer.add(panel,BorderLayout.CENTER);
		
		JPanel jpButton = new JPanel(new BorderLayout());
		JButton jbNext = new JButton();
		jbNext.setOpaque(false);
		jbNext.setContentAreaFilled(false);
		try {
			URL imgURL = getClass().getResource("/upButton.png");
		    ImageIcon ii = new ImageIcon(imgURL);
		    Image newimg = ii.getImage().getScaledInstance(200, 200,
					java.awt.Image.SCALE_SMOOTH);
		    jbNext.setIcon(new ImageIcon(newimg));
		} catch (Exception e) {
			System.out.println("Error reading file");
		}
		JButton jbPrev = new JButton();
		jbPrev.setOpaque(false);
		jbPrev.setContentAreaFilled(false);
		try {
			URL imgURL = getClass().getResource("/downButton.png");
		    ImageIcon ii = new ImageIcon(imgURL);
		    Image newimg = ii.getImage().getScaledInstance(200, 200,
					java.awt.Image.SCALE_SMOOTH);
		    jbPrev.setIcon(new ImageIcon(newimg));
		} catch (Exception e) {
			System.out.println("Error reading file");
		}
		jpButton.add(jbNext,BorderLayout.NORTH);
//		jpButton.add(Box.createRigidArea(new Dimension(5,500)));
		jpButton.add(jbPrev,BorderLayout.SOUTH);
		
		jbNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
					if(currentIndex > 1){
					int maxCount = pdffile.getNumPages();
					System.out.println("Current index" + currentIndex);
					currentIndex--;
					System.out.println("New index" + currentIndex);
			        PDFPage page = pdffile.getPage(currentIndex);
			        panel.showPage(page);
					jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);

					}
				
			}
			
		});
		
		jbPrev.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// show the first page
				System.out.println("Current index" + currentIndex);
				int maxCount = pdffile.getNumPages();
				if(currentIndex < maxCount )
				currentIndex++;
				System.out.println("New index" + currentIndex);
		        PDFPage page = pdffile.getPage(currentIndex);
		        panel.showPage(page);
				jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);
				
			}
			
		});
		
		
		jpViewer.add(jpButton,BorderLayout.EAST);
		
		//PDF Viewer page count
		jlPageCount = new JLabel("Current Page: "+"0"+"/"+"0");
		jlPageCount.setBorder(new LineBorder(Color.black,3));
		jlPageCount.setFont(new Font("Calibri",Font.BOLD,20));
		jlPageCount.setMaximumSize(new Dimension(700, 500));
		jlPageCount.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlPageCount.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		jpViewer.add(jlPageCount,BorderLayout.SOUTH);
        mainPanel.add(jpViewer,BorderLayout.CENTER);
        
		
		
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(500,110));
		sidePanel.setBorder(new LineBorder(Color.BLACK,3));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(sidePanel,BorderLayout.WEST);
		
		
		//PDF Page control
		JPanel jpNavigation = new JPanel();
		JButton previous = new JButton("Previous");
		JButton next = new JButton("Next");
		previous.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentIndex > 1){
				int maxCount = pdffile.getNumPages();
				System.out.println("Current index" + currentIndex);
				currentIndex--;
				System.out.println("New index" + currentIndex);
		        PDFPage page = pdffile.getPage(currentIndex);
		        panel.showPage(page);
				jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);

				}
			}
			
		});
		next.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// show the first page
				System.out.println("Current index" + currentIndex);
				int maxCount = pdffile.getNumPages();
				if(currentIndex < maxCount )
				currentIndex++;
				System.out.println("New index" + currentIndex);
		        PDFPage page = pdffile.getPage(currentIndex);
		        panel.showPage(page);
				jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);
				
			}
			
		});
		jpNavigation.add(previous);
		jpNavigation.add(next);
		jpNavigation.setMaximumSize(new Dimension(300,10));
		sidePanel.add(jpNavigation);
		
		//Label for year
		JLabel j1Year = new JLabel("Year");
		j1Year.setBorder(new LineBorder(Color.BLACK,1));
		j1Year.setFont(new Font("Calibri",Font.BOLD,20));
		j1Year.setMaximumSize(new Dimension(700, 500));
		j1Year.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		j1Year.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		sidePanel.add(j1Year);
		
		//Filter Panel
		currentFilters = new ArrayList<String>();
		currentFiltersYear = new ArrayList<String>();
		JPanel pFilter = new JPanel();
		pFilter.setBorder(new LineBorder(Color.BLACK,1));
		pFilter.setLayout(new BoxLayout(pFilter, BoxLayout.PAGE_AXIS));
		JPanel jpYear = new JPanel(new GridLayout(4,4));
		jpYear.setBorder(new LineBorder(Color.BLACK,1));
		jpYear.setPreferredSize(new Dimension(500,5));
		
		for(int i = 0;i < 15;i++){
			String y = "20";
			if(i < 10){
				y = y + "0"+i;
			}else{
				y = y + i;
			}
			currentFiltersYear.add(y);
			JCheckBox t = new JCheckBox(y);
			t.setSelected(true);

			t.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					System.out.println("Number of years is "+currentFiltersYear.size());
					if(t.isSelected()){
						System.out.println("Year has been selected");
						currentFiltersYear.add(t.getText());
						refilter();
						System.out.println("Number of years after removel "+currentFiltersYear.size());

					}else{
						System.out.println("Year has been unselected");
						currentFiltersYear.remove(t.getText());
						refilter();
						System.out.println("Number of years after removal "+currentFiltersYear.size());

					}
					
				}
				
			});
			jpYear.add(t);
		}
		pFilter.add(new JScrollPane(jpYear));
		jpTopics = new JPanel(new GridLayout(5,0
				));
		jpTopics.setBorder(new LineBorder(Color.BLACK,1));
		jpTopics.setPreferredSize(new Dimension(500,15));
		
		JLabel jlTopic = new JLabel("Topics");
		jlTopic.setFont(new Font("Calibri",Font.BOLD,20));
//		jlTopic.setMaximumSize(new Dimension(700, 500));
		jlTopic.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlTopic.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		pFilter.add(jlTopic);
		pFilter.add(new JScrollPane(jpTopics));
		
		
		
		
		sidePanel.add(pFilter);
		//Load question papers from file
		lmAvailable = new DefaultListModel();
		
		
		//List of available question papers
		JList jListAvailable = new JList(lmAvailable);
		jListAvailable.setFont(new Font("Calibri",Font.PLAIN,20));
		jListAvailable.setBorder(new LineBorder(Color.BLACK,1));
		
		jListAvailable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
				if (e.getClickCount() == 2) {
					lmSelected.addElement(qp);
					checkMarks();
				}
				if(e.getClickCount() == 1){
					jlcurrentPDF.setText(qp.toString());
					try {
						setup(qp.getLocation());
						currentIndex = 1;
						jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		JLabel jlAvailable = new JLabel("Available Papers");
		jlAvailable.setBorder(new LineBorder(Color.BLACK,1));
		jlAvailable.setFont(new Font("Calibri",Font.BOLD,20));
		
		jlAvailable.setMaximumSize(new Dimension(700, 500));
		jlAvailable.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlAvailable.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		
		
//		jlAvailable.setVerticalAlignment(SwingConstants.CENTER);
		
		sidePanel.add(jlAvailable);
		sidePanel.add(new JScrollPane(jListAvailable));
		
		
		
		//List of selected question papers
		lmSelected = new DefaultListModel();
		JList jListSelected = new JList(lmSelected);
		jListSelected.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
					System.out.println("GENERATING PAGE");
					new PDFCreatePage().generatePage(qp);
					lmSelected.removeElement(jListSelected.getSelectedValue());
					checkMarks();
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
		jListSelected.setFont(new Font("Calibri",Font.PLAIN,20));
		jListSelected.setBorder(new LineBorder(Color.BLACK,1));
		
		JLabel jlSelected = new JLabel("Selected Papers",SwingConstants.LEFT);
		jlSelected.setBorder(new LineBorder(Color.black,1));
		jlSelected.setFont(new Font("Calibri",Font.BOLD,20));
		jlSelected.setMaximumSize(new Dimension(700, 500));
		jlSelected.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlSelected.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
//		sidePanel.add(jlSelected);
		
//		sidePanel.add(new JScrollPane(jListSelected));
		//JPANEL FOR RIGHT SIDE
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane,BoxLayout.PAGE_AXIS));
		rightPane.add(jlSelected);
		rightPane.add(new JScrollPane(jListSelected));
//		mainPanel.add(new JScrollPane(jListSelected),BorderLayout.EAST);
		mainPanel.add(rightPane,BorderLayout.EAST);
		rightPane.setPreferredSize(new Dimension(400,200));
		
		
		
		sidePanel.setPreferredSize(new Dimension(500,200));
		
		jlTotalMarks = new JLabel("Total Marks: ");
		jlTotalMarks.setFont(new Font("Calibri",Font.BOLD,20));
		jlTotalMarks.setMaximumSize(new Dimension(700, 500));
		jlTotalMarks.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlTotalMarks.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		sidePanel.add(jlTotalMarks);
		
		pack();
	}
	
	public void refilter(){
		System.out.println("Refiltering");
		if(allPapers.size() != 0){
		lmAvailable.removeAllElements();
		for(int i = 0;i < currentFilters.size();i++){
			for(QuestionPaper qp:allPapers){

				if(qp.getTopicName().replace(" ", "").equals(currentFilters.get(i).replace(" ", ""))){
					lmAvailable.addElement(qp);
				}
			}
		}
		int tt = 0;
		while(tt == 0){
			tt = 1;
		for(int j = 0; j < lmAvailable.size();j++){
			QuestionPaper qp = (QuestionPaper) lmAvailable.get(j);
			System.out.println(qp + " at index "+j);
			int sYear = Integer.parseInt(qp.getYear().substring(3, 5));
			String stringYear = "";
			if(sYear < 10){
				stringYear = "200"+sYear;
			}else{
				stringYear = "20"+sYear;
			}
			boolean check = false;
			for(String yFilter:currentFiltersYear){
				System.out.println(stringYear+" to match with "+yFilter);
				if(stringYear.replace(" ", "").equals(yFilter.replace(" ", ""))){
					System.out.println("MATCHED ");
					check = true;
					break;
				}
			}
			
			if(!check){
//				System.out.println("Removed "+qp+" AS YEAR IS NOT INCLUDED "+qp.getYear());
				System.out.println("REMOVED" + qp);
				lmAvailable.remove(j);
				tt = 0;
			}
		}
		}
		
		
		}
	}
	
	public void checkMarks(){
		totalMarks = 0;
		for(int i = 0; i < lmSelected.getSize();i++){
			int mark = ((QuestionPaper) lmSelected.getElementAt(i)).getTotalMarks();
			totalMarks+= mark;
		}
		
		jlTotalMarks.setText("Total Marks: "+totalMarks);
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
