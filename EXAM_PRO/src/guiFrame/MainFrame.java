package guiFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

import pdf.PrintPdf;
import random.CreateQuestionFront;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import data.JListModern;
import data.PaperReader;
import data.QuestionPaper;


public class MainFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	DefaultListModel lmAvailable;
	DefaultListModel lmSelected;
	JPanel jpTopics;
	int pressedIndex = 0;
	int releasedIndex = 0;
	ArrayList<String> topicNames;
	ArrayList<String> yearList;
	ArrayList<String> currentFilters;
	ArrayList<String> currentFiltersYear;
	ArrayList<QuestionPaper> allPapers;
	JPanel mainPanel,jpYear;
	PagePanel panel,panelMS,panelExternal;
	PDFFile pdffile,pdffileMS,pdffileExternal;
	int currentIndex = 1;
	int currentIndexMS = 1;
	JLabel jlcurrentPDF,jlcurrentPDFMS;
	int totalMarks = 0;
	JLabel jlTotalMarks;
	JLabel jlPageCount,jlPageCountMS;
	ArrayList<JCheckBox> jcbArrayTopics,jcbArrayYear;
	JList jListAvailable;
	JTextField jtfFileName;
	QuestionPaper qpCurrentlyShowing;
	boolean showMS;
	JTabbedPane jtpViewer;
	JPanel bottomPanel;

	public MainFrame(){
		super("Exam-Pro (Trial)");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();     
		}

		setVisible(true);
		
		initUi();
	}
	
	private boolean search(ArrayList<String> array,String input){
		for(int i = 0;i < array.size();i++){
			if(array.get(i).replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))){
				return true;
			}
			
		}
		System.out.println("ALREADY HAVE "+input);
		return false;
	}
	
	private void initUi(){
		allPapers = new ArrayList<QuestionPaper>();
		
		//JMenu to print
		JMenuBar jmb = new JMenuBar();
		JMenu jmFile = new JMenu("File");
		JMenuItem printSelectedPage = new JMenuItem("Print Exam Pack");
		printSelectedPage.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to print?");
				if(confirm == JOptionPane.YES_OPTION){
					makeQP(jtfFileName.getText(),false);
					String qpFilePath = System.getProperty("user.home")+"\\Desktop\\"+jtfFileName.getText()+"\\"+jtfFileName.getText()+"_QP.pdf";
					String msFilePath = System.getProperty("user.home")+"\\Desktop\\"+jtfFileName.getText()+"\\"+jtfFileName.getText()+"_MS.pdf";
					try {
						new PrintPdf().printPDF(qpFilePath, "QUESTION PAPER");
						new PrintPdf().printPDF(msFilePath, "MARK SCHEME PAPER");
					} catch (PrinterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					};
				}else{
					System.out.println("PRINT CANCELLED");
				}
				
				
			}
			
		});
		jmFile.add(printSelectedPage);
		jmb.add(jmFile);
		setJMenuBar(jmb);

		//Constructs bottom panels
		bottomPanel = new JPanel();
		JLabel jlcreate = new JLabel("Create:");
		jlcreate.setFont(new Font("Calibri",Font.BOLD,30));
		bottomPanel.add(jlcreate);

		jtfFileName = new JTextField();
		jtfFileName.setText("ENTER NAME OF EXAM PACK");
		jtfFileName.setPreferredSize(new Dimension(300,50));
		jtfFileName.setFont(new Font("Calibri",Font.BOLD,20));
		bottomPanel.add(jtfFileName);
		JButton jbcreateQP = new JButton("Exam Pack");
		jbcreateQP.setFont(new Font("Calibri",Font.BOLD,30));
		bottomPanel.add(jbcreateQP);
		
		jbcreateQP.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				makeQP(jtfFileName.getText(),true);
				
			}
			
		});
		
		
		try {
			mainPanel();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
//		add(bottomPanel, BorderLayout.SOUTH);
		
		//Get Size of screen
		Toolkit tk = Toolkit.getDefaultToolkit();
		int xSize = ((int) tk.getScreenSize().getWidth());
		int ySize = ((int) tk.getScreenSize().getHeight());
		
		//Default JFrame Stuff
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(xSize,ySize);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	
	public void mainPanel() throws IOException{
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new LineBorder(Color.BLACK,2));
		add(mainPanel,BorderLayout.CENTER);
		
		//Adds PDV Viewer to mainPanel Center
		addPDFViewerPanel();
		
		//Adds leftpane that shows filters and available papers
		leftPane();
		
		//Adds right pane that displays selected papers
		rightPane();
		
		pack();
	}
	
	
	
	public void leftPane(){
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.WHITE);
		sidePanel.setPreferredSize(new Dimension(500,110));
//		sidePanel.setBorder(new LineBorder(Color.BLACK,3));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(sidePanel,BorderLayout.WEST);
		
		//Adds JButton to load file
		JTextArea jtaDrag = new JTextArea();
		jtaDrag.setText("                   <<<<<<<<<<<<<<Drag Folder here>>>>>>>>>>>>");
		jtaDrag.setAlignmentY(0);
		jtaDrag.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jtaDrag.setMaximumSize(new Dimension(700, 500));
		jtaDrag.setFont(new Font("Calibri",Font.BOLD,20));
		jtaDrag.setDropTarget(new DropTarget() {
		    public synchronized void drop(DropTargetDropEvent evt) {
				ArrayList<QuestionPaper> qpArray = new ArrayList<QuestionPaper>();

		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>)
		                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File file : droppedFiles) {
		            	Files.walk(Paths.get(file.getPath()+"")).forEach(filePath -> {
						    if (Files.isRegularFile(filePath) && (filePath+"").contains(".pdf") && !(filePath+"").contains("MS")) {
						    	String[] name = (filePath+"").substring((filePath+"").lastIndexOf("\\")+1, (filePath+"").indexOf(".pdf")).split("_");
						    	QuestionPaper qp = new QuestionPaper();
						    	qp.setLocation(filePath.toString());
						    	qp.setYear(name[1].replace(" ", ""));
						    	qp.setQ(name[2]);
						    	qp.setTopicName(name[3]);
						    	qp.setTotalMarks(Integer.parseInt(name[4]));
						    	
						    	qpArray.add(qp);
						        
						    }
						    
						});
		               	System.out.println(file.getAbsolutePath());
		            }
		            
		            allPapers = qpArray;
		            addFiles();
					
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
		sidePanel.add(jtaDrag);

		
		//Label for year
		JLabel j1Year = new JLabel("Year");
		j1Year.setFont(new Font("Calibri",Font.BOLD,20));
		j1Year.setMaximumSize(new Dimension(700, 500));
		j1Year.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		j1Year.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		sidePanel.add(j1Year);
		
		//Button to select/de-select all year options
		JButton jbSelectYear = new JButton("Select All");
		jbSelectYear.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for(JCheckBox jcb:jcbArrayYear){
					jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
					if(jtpViewer.getSelectedIndex() == 0){
				        panel.requestFocusInWindow();
					}else{
				        panelMS.requestFocusInWindow();
					}
					System.out.println("Selecting all years");
					jcb.setSelected(true);
				}
				
			};
		
		});
		JButton jbDeselectYear = new JButton("Deselect All");
		jbDeselectYear.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for(JCheckBox jcb:jcbArrayYear){
					jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
					if(jtpViewer.getSelectedIndex() == 0){
				        panel.requestFocusInWindow();
					}else{
				        panelMS.requestFocusInWindow();
					}
					System.out.println("Selecting all years");
					jcb.setSelected(false);
				}
				
			};
		
		});
		JPanel jpSelectYear = new JPanel();
		jpSelectYear.add(jbSelectYear);
		jpSelectYear.add(jbDeselectYear);
		jpSelectYear.setMaximumSize(new Dimension(200,10));
		sidePanel.add(jpSelectYear);
		
		//Filter Panel
		currentFilters = new ArrayList<String>();
		currentFiltersYear = new ArrayList<String>();
		JPanel pFilter = new JPanel();
		pFilter.setBorder(new LineBorder(Color.BLACK,1));
		pFilter.setLayout(new BoxLayout(pFilter, BoxLayout.PAGE_AXIS));
		jpYear = new JPanel(new GridLayout(4,2));
		jpYear.setBorder(new LineBorder(Color.BLACK,1));
		jpYear.setPreferredSize(new Dimension(500,5));
		jpYear.setBackground(new Color(228,235,245,215));
		
		pFilter.add(new JScrollPane(jpYear));
		jpTopics = new JPanel(new GridLayout(5,0
				));
		jpTopics.setBorder(new LineBorder(Color.BLACK,1));
		jpTopics.setPreferredSize(new Dimension(500,15));
		
		JLabel jlTopic = new JLabel("Topics");
		jlTopic.setFont(new Font("Calibri",Font.BOLD,20));
		jlTopic.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlTopic.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		pFilter.add(jlTopic);
		

		
		//Button to select/de-select all TOPIC options
		JButton jbSelectTopics = new JButton("Select All");
		jbSelectTopics.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for(JCheckBox jcb:jcbArrayTopics){
					jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
					if(jtpViewer.getSelectedIndex() == 0){
				        panel.requestFocusInWindow();
					}else{
				        panelMS.requestFocusInWindow();
					}
					System.out.println("Selecting all years");
					jcb.setSelected(true);
				}
				
			};
		
		});
		JButton jbDeselectTopics = new JButton("Deselect All");
		jbDeselectTopics.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for(JCheckBox jcb:jcbArrayTopics){
					jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
					if(jtpViewer.getSelectedIndex() == 0){
				        panel.requestFocusInWindow();
					}else{
				        panelMS.requestFocusInWindow();
					}
					System.out.println("Selecting all years");
					jcb.setSelected(false);
				}
				
			};
		
		});
		JPanel jpSelectTopics = new JPanel();
		jpSelectTopics.add(jbSelectTopics);
		jpSelectTopics.add(jbDeselectTopics);
		jpSelectTopics.setMaximumSize(new Dimension(200,10));
		pFilter.add(jpSelectTopics);
		
		pFilter.add(new JScrollPane(jpTopics));
		sidePanel.add(pFilter);
		
		//Load question papers from file
		lmAvailable = new DefaultListModel();
		
		
		//List of available question papers
		jListAvailable = new JList(lmAvailable);
		jListAvailable.setFont(new Font("Calibri",Font.PLAIN,20));
		jListAvailable.setBorder(new LineBorder(Color.BLACK,1));
		jListAvailable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(jListAvailable.getSelectedValue() != null){
				QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
				if (e.getClickCount() == 2) {
					lmSelected.addElement(qp);
					checkMarks();
				}
				if(e.getClickCount() == 1){
					qpCurrentlyShowing = qp;
					jlcurrentPDF.setText(qp.toString());
					jlcurrentPDFMS.setText(qp.toString());
					setup(qp.getLocation());
					setupMS(qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf");
					currentIndex = 1;
					currentIndexMS = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					jlPageCountMS.setText("Current Page: "+currentIndexMS+"/"+pdffileMS.getNumPages());
					// TODO Auto-generated catch block
					System.out.println("ERROR SHOWING PDF");
					
				}
			}
			}
		});
		jListAvailable.setCellRenderer(new JListModern());
		jListAvailable.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
				if(e.getKeyCode() == KeyEvent.VK_LEFT){
					System.out.println("LEFT KEY");
					if(jtpViewer.getSelectedIndex() == 1){
						prevPageMS();
					}else{
						prevPage();

					}
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					System.out.println("RIGHT KEY");
					if(jtpViewer.getSelectedIndex() == 1){
						nextPageMS();
					}else{
						nextPage();
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
					int indexPaper = jListAvailable.getSelectedIndex();
					if(e.getKeyCode() == KeyEvent.VK_UP){
						if((indexPaper - 1) < lmAvailable.getSize() && (indexPaper - 1) >= 0){
						qp = (QuestionPaper) lmAvailable.getElementAt(indexPaper - 1);
						}
					}
					if(e.getKeyCode() == KeyEvent.VK_DOWN){
						if((indexPaper + 1) < lmAvailable.getSize() && (indexPaper + 1) >= 0){
						qp = (QuestionPaper) lmAvailable.getElementAt(indexPaper + 1);
						}
					}
					qpCurrentlyShowing = qp;
					System.out.println("PRESSED ARROW UP/DOWN");
					if(jListAvailable.getSelectedValue() != null){
					jlcurrentPDF.setText(qp.toString());
					jlcurrentPDFMS.setText(qp.toString());
					setup(qp.getLocation());
					setupMS(qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf");
					currentIndex = 1;
					currentIndexMS = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					jlPageCountMS.setText("Current Page: "+currentIndexMS+"/"+pdffileMS.getNumPages());
					System.out.println("ERROR SHOWING PDF");
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					lmSelected.addElement((QuestionPaper) jListAvailable.getSelectedValue());
					checkMarks();
				}

				setVisible(true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		JLabel jlAvailable = new JLabel("Available Papers");
		jlAvailable.setBorder(new LineBorder(Color.BLACK,1));
		jlAvailable.setFont(new Font("Calibri",Font.BOLD,20));
		
		jlAvailable.setMaximumSize(new Dimension(700, 500));
		jlAvailable.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlAvailable.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label

		
		
		
		
		sidePanel.add(jlAvailable);
		sidePanel.add(new JScrollPane(jListAvailable));
		
		
		
		
		
		
		
		sidePanel.setPreferredSize(new Dimension(600,200));
	}
	
	public void makeQP(String fileName,boolean check){
		new PaperReader().createFolder(fileName);
		
		PDFMergerUtility mu = new PDFMergerUtility();
		for(int i = 0;i < lmSelected.size();i++){
			QuestionPaper qp = (QuestionPaper)lmSelected.getElementAt(i);
			try {
				new CreateQuestionFront().makePaper(qp, i+1,"");
			} catch (COSVisitorException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mu.addSource(System.getProperty("user.home")+"\\Desktop\\CACHE\\"+(i+1)+".pdf");
			mu.addSource(qp.getLocation());
		}		
		mu.setDestinationFileName(System.getProperty("user.home")+"\\Desktop\\"+fileName+"\\"+fileName+"_QP.pdf");
				
		try {
			mu.mergeDocuments();
		} catch (COSVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		makeMS(fileName,System.getProperty("user.home")+"\\Desktop\\"+fileName);
		if(check){
			JOptionPane.showMessageDialog(null, "PDF Saved to "+System.getProperty("user.home")+"\\Desktop\\"+fileName);

		}else{
			JOptionPane.showMessageDialog(null, "EXAM PACK HAS BEEN SENT TO PRINT");

		}

	}
	
	public void makeMS(String fileName,String fileDirec){
		PDFMergerUtility mu = new PDFMergerUtility();
		for(int i = 0;i < lmSelected.size();i++){
			QuestionPaper qp = (QuestionPaper)lmSelected.getElementAt(i);
			System.out.println(qp.getLocation());
			String filePath = qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf";
			System.out.println("Mark Scheme : "+filePath);
			try {
				new CreateQuestionFront().makePaper(qp, i+1,"MS");
			} catch (COSVisitorException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mu.addSource(System.getProperty("user.home")+"\\Desktop\\CACHE\\"+(i+1)+"_MS.pdf");
			mu.addSource(filePath);
		}
		mu.setDestinationFileName(fileDirec+"\\"+fileName+"_MS.pdf");
				
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
	
	public void addFiles(){
		topicNames = new ArrayList<String>();
		yearList = new ArrayList<String>();
		jcbArrayTopics = new ArrayList<JCheckBox>();
		jcbArrayYear = new ArrayList<JCheckBox>();
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
				jcbArrayTopics.add(temp);
			}
			
			if(!(search(yearList,qp.getYear()))){
				yearList.add(qp.getYear());
			}
			
		}
		
		Collections.sort(yearList);
		for(String temp:yearList){
			JCheckBox tempCheck = new JCheckBox(temp);
			tempCheck.setFont(new Font("Calibri",Font.BOLD,20));
			tempCheck.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					if(tempCheck.isSelected()){
						System.out.println("Year selected");
						currentFiltersYear.add(tempCheck.getText());
						refilter();
					}else{
						System.out.println("Year unselected");
						currentFiltersYear.remove(tempCheck.getText());
						refilter();
					}
					
				}
				
			});
			tempCheck.setSelected(true);
			jcbArrayYear.add(tempCheck);
			jpYear.add(tempCheck);
		}
		
		setVisible(true);
	}
	
	public void rightPane(){
		//JPANEL FOR RIGHT SIDE
		JLabel jlSelected = new JLabel("Selected Papers",SwingConstants.LEFT);
		jlSelected.setBorder(new LineBorder(Color.black,1));
		jlSelected.setFont(new Font("Calibri",Font.BOLD,20));
		jlSelected.setMaximumSize(new Dimension(700, 500));
		jlSelected.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlSelected.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		
		//List of selected question papers
		lmSelected = new DefaultListModel();
		JList jListSelected = new JList(lmSelected);
		jListSelected.setCellRenderer(new JListModern());
		jListSelected.setFont(new Font("Calibri",Font.PLAIN,20));
		jListSelected.setBorder(new LineBorder(Color.BLACK,1));
		jListSelected.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(jListSelected.getSelectedValue() != null){
				QuestionPaper qp = (QuestionPaper) jListSelected.getSelectedValue();

				if (e.getClickCount() == 2) {
					lmSelected.removeElement(jListSelected.getSelectedValue());
					checkMarks();
				}
				
				if(e.getClickCount() == 1){
					qpCurrentlyShowing = qp;
//					if(jtpViewer.getSelectedIndex() == 0){
//				        panel.requestFocusInWindow();
//					}else{
//				        panelMS.requestFocusInWindow();
//					}
					System.out.println("ONE CLICK " +qp);
					jlcurrentPDF.setText(qp.toString());
					jlcurrentPDFMS.setText(qp.toString());
					setup(qp.getLocation());
					setupMS(qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf");
					currentIndex = 1;
					currentIndexMS = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					jlPageCountMS.setText("Current Page: "+currentIndexMS+"/"+pdffileMS.getNumPages());

				
				}
				}
			}

		});
		jListSelected.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("KEY PRESSED ON JLIST SELECTED");
				QuestionPaper qp = (QuestionPaper) jListSelected.getSelectedValue();
				if(e.getKeyCode() == KeyEvent.VK_LEFT){
					System.out.println("LEFT KEY FROM JLIST SELECTED");
					if(jtpViewer.getSelectedIndex() == 1){
						prevPageMS();
					}else{
						prevPage();

					}
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					System.out.println("RIGHT KEY FROM JLIST SELECTED");
					if(jtpViewer.getSelectedIndex() == 1){
						nextPageMS();
					}else{
						nextPage();

					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
					int indexPaper = jListSelected.getSelectedIndex();
					if(e.getKeyCode() == KeyEvent.VK_UP){
						if((indexPaper - 1) < lmSelected.getSize() && (indexPaper - 1) >= 0){
						qp = (QuestionPaper) lmSelected.getElementAt(indexPaper - 1);
						}
					}
					if(e.getKeyCode() == KeyEvent.VK_DOWN){
						if((indexPaper + 1) < lmSelected.getSize() && (indexPaper + 1) >= 0){
						qp = (QuestionPaper) lmSelected.getElementAt(indexPaper + 1);
						}
					}
					qpCurrentlyShowing = qp;
					System.out.println("PRESSED ARROW UP/DOWN");
					if(jListSelected.getSelectedValue() != null){
					jlcurrentPDF.setText(qp.toString());
					jlcurrentPDFMS.setText(qp.toString());
					setup(qp.getLocation());
					setupMS(qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf");
					currentIndex = 1;
					currentIndexMS = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					jlPageCountMS.setText("Current Page: "+currentIndexMS+"/"+pdffileMS.getNumPages());
					System.out.println("ERROR SHOWING PDF");
					}
				}
			
				if(e.getKeyCode() == KeyEvent.VK_DELETE){
					System.out.println("BACK SPACE PRESSED");
					if(jListSelected.getSelectedValue() != null){
						int currentIndex = jListSelected.getSelectedIndex();
						if(currentIndex + 1 < lmSelected.getSize()){
							lmSelected.remove(currentIndex);
							jListSelected.setSelectedIndex(currentIndex);
						}else{
							lmSelected.remove(currentIndex);
							jListSelected.setSelectedIndex(currentIndex - 1);
						}
					}
					checkMarks();
				}

				setVisible(true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane,BoxLayout.PAGE_AXIS));
		
		
		
		
		
		rightPane.add(jlSelected);
		rightPane.add(new JScrollPane(jListSelected));
		mainPanel.add(rightPane,BorderLayout.EAST);
		rightPane.setPreferredSize(new Dimension(500,200));
		
		
		//JPanel for re-arranging selected papers
		JPanel jpRearrange = new JPanel();
		jpRearrange.setBorder(new LineBorder(Color.black,1));
		jpRearrange.setAlignmentX(CENTER_ALIGNMENT);
		jpRearrange.setMaximumSize(new Dimension(500,40));
		JButton jbUp = new JButton();
		jbUp.setAlignmentX(CENTER_ALIGNMENT);
		jbUp.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbUp.setOpaque(false);
		jbUp.setContentAreaFilled(false);
		jbUp.setIcon(new ImageIcon(getImage("/paperUp.png",50,50)));

		jbUp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int currentIndex = jListSelected.getSelectedIndex();
				int newIndex = currentIndex - 1;
				if(newIndex >= 0){
				QuestionPaper qpCurrent = (QuestionPaper) lmSelected.get(currentIndex);
				QuestionPaper qpNext = (QuestionPaper) lmSelected.get(newIndex);
				
				lmSelected.remove(currentIndex);
				lmSelected.add(currentIndex, qpNext);
				lmSelected.remove(newIndex);
				lmSelected.add(newIndex, qpCurrent);
				
				jListSelected.setSelectedIndex(newIndex);
				}
				
			}
			
		});
		JButton jbDelete = new JButton();
		jbDelete.setOpaque(false);
		jbDelete.setContentAreaFilled(false);
		jbDelete.setIcon(new ImageIcon(getImage("/paperDelete.png",50,50)));

		jbDelete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(jListSelected.getSelectedValue() != null){
					int currentIndex = jListSelected.getSelectedIndex();
					if(currentIndex + 1 < lmSelected.getSize()){
						lmSelected.remove(currentIndex);
						jListSelected.setSelectedIndex(currentIndex);
					}else{
						lmSelected.remove(currentIndex);
						jListSelected.setSelectedIndex(currentIndex - 1);
					}
					checkMarks();
				}
				
			}
			
		});
		JButton jbDown = new JButton();
		jbDown.setOpaque(false);
		jbDown.setContentAreaFilled(false);
		jbDown.setIcon(new ImageIcon(getImage("/paperDown.png",50,50)));
		jbDown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int currentIndex = jListSelected.getSelectedIndex();
				int newIndex = currentIndex + 1;
				if(newIndex < lmSelected.getSize()){
				QuestionPaper qpCurrent = (QuestionPaper) lmSelected.get(currentIndex);
				QuestionPaper qpNext = (QuestionPaper) lmSelected.get(newIndex);
				
				lmSelected.remove(currentIndex);
				lmSelected.add(currentIndex, qpNext);
				lmSelected.remove(newIndex);
				lmSelected.add(newIndex, qpCurrent);
				
				jListSelected.setSelectedIndex(newIndex);
				}
				
			}
			
		});
		
		jpRearrange.add(jbUp);
		jpRearrange.add(jbDelete);
		jpRearrange.add(jbDown);
		
		rightPane.add(jpRearrange);
		

		
		JButton clearSelection = new JButton("Clear Selection");
		clearSelection.setFont(new Font("Calibri",Font.BOLD,20));
		clearSelection.setForeground(Color.red);
		clearSelection.setAlignmentX(CENTER_ALIGNMENT);
		clearSelection.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		clearSelection.setMaximumSize(new Dimension(800,50));
		clearSelection.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				lmSelected.removeAllElements();
				
			}
			
		});
		rightPane.add(clearSelection);
		
		jlTotalMarks = new JLabel("Total Marks: ");
		jlTotalMarks.setBorder(new LineBorder(Color.black,1));
		jlTotalMarks.setFont(new Font("Calibri",Font.BOLD,20));
		jlTotalMarks.setMaximumSize(new Dimension(700, 500));
		jlTotalMarks.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlTotalMarks.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		rightPane.add(jlTotalMarks);
		
		
		
		rightPane.add(bottomPanel);
	}
	
	public Image getImage(String nameOfImage,int x,int y){
		try {
			URL imgURL = getClass().getResource(nameOfImage);
		    ImageIcon ii = new ImageIcon(imgURL);
		    Image newimg = ii.getImage().getScaledInstance(x, y,
					java.awt.Image.SCALE_SMOOTH);
		    return newimg;
		} catch (Exception e) {
			System.out.println("Error reading file for paper up");
		}
		return null;
	}
	
	public void refilter(){
		jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
        panel.requestFocusInWindow();
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
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<QuestionPaper> toAddBack = new ArrayList<QuestionPaper>();
		
		for(int j = 0; j < lmAvailable.size();j++){
			QuestionPaper qp = (QuestionPaper) lmAvailable.get(j);
			for(String yFilter:currentFiltersYear){
				System.out.println(qp.getYear()+" to match with "+yFilter);
				if(qp.getYear().replace(" ", "").equalsIgnoreCase(yFilter.replace(" ", ""))){
					System.out.println("MATCHED ");
					toAddBack.add(qp);
					break;
				}
			}
		}
		lmAvailable.removeAllElements();
		for(QuestionPaper temp:toAddBack){
			lmAvailable.addElement(temp);
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
	
	public void addPDFViewerPanel(){
		//JLabel to show what is currently being shown
		jlcurrentPDF = new JLabel("No page selected");
		jlcurrentPDF.setBorder(new LineBorder(Color.black,1));
		jlcurrentPDF.setFont(new Font("Calibri",Font.PLAIN,40));
		jlcurrentPDF.setMaximumSize(new Dimension(700, 50));
		jlcurrentPDF.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlcurrentPDF.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		jlcurrentPDFMS = new JLabel("No page selected");
		jlcurrentPDFMS.setBorder(new LineBorder(Color.black,1));
		jlcurrentPDFMS.setFont(new Font("Calibri",Font.PLAIN,40));
		jlcurrentPDFMS.setMaximumSize(new Dimension(700, 50));
		jlcurrentPDFMS.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlcurrentPDFMS.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		
//		mainPanel.add(jlcurrentPDF,BorderLayout.NORTH);
		
		
		
		//To hold both the QP and MS viewer
		jtpViewer = new JTabbedPane();
		jtpViewer.setFont(new Font("Calibri",Font.BOLD,20));
		
		//JPanel for QP and MS PDF VIEWER panel
		JPanel jpQP = new JPanel(new BorderLayout());
		JPanel jpMS = new JPanel(new BorderLayout());

		jtpViewer.addTab("Question Paper", jpQP);
		jtpViewer.addTab("Mark Scheme", jpMS);
		
		
		
		//MS Paper Viewer
		JPanel jpViewerMS = new JPanel(new BorderLayout());
		
		
		
		
		panelMS = new PagePanel();
		panelMS.setBorder(new LineBorder(Color.BLUE,3));
		panelMS.setFocusable(true);
		panelMS.requestFocusInWindow();
		panelMS.grabFocus();
       
		panelMS.addKeyListener(new KeyListener(){

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				System.out.println("LEFT KEY");
				prevPageMS();
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				System.out.println("RIGHT KEY");
				nextPageMS();
			}
			setVisible(true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
			
		}
		
		});
		
		jpViewerMS.add(panelMS,BorderLayout.CENTER);
		
		///////////////////COPIED CODE
		JPanel jpButtonMS = new JPanel();
		jpButtonMS.setMaximumSize(new Dimension(2000,60));
		jpButtonMS.setBorder(new LineBorder(Color.black,1));
		jpButtonMS.setLayout(new BoxLayout(jpButtonMS,BoxLayout.X_AXIS));
		JButton jbNextMS = new JButton();
		jbNextMS.setOpaque(false);
		jbNextMS.setContentAreaFilled(false);
		jbNextMS.setIcon(new ImageIcon(getImage("/paperLeft.png",60,60)));
		
		jbNextMS.setAlignmentX(CENTER_ALIGNMENT);
		jbNextMS.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbNextMS.setMaximumSize(new Dimension(120,60));
		
		JButton jbPrevMS = new JButton();
		jbPrevMS.setOpaque(false);
		jbPrevMS.setContentAreaFilled(false);
		jbPrevMS.setIcon(new ImageIcon(getImage("/paperRight.png",60,60)));
		
		jbPrevMS.setAlignmentX(CENTER_ALIGNMENT);
		jbPrevMS.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbPrevMS.setMaximumSize(new Dimension(120,60));
		
		
		
		
		jbNextMS.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				    jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
		        	panelMS.requestFocusInWindow();
					prevPageMS();
			}
			
		});
		
		jbPrevMS.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
		        panelMS.requestFocusInWindow();
				nextPageMS();
			}
			
		});
		
		
		
		//PDF Viewer page count
		jlPageCountMS = new JLabel("Current Page: "+"0"+"/"+"0");
		jlPageCountMS.setBorder(new LineBorder(Color.black,3));
		jlPageCountMS.setFont(new Font("Calibri",Font.BOLD,20));
		jlPageCountMS.setMaximumSize(new Dimension(2000, 500));
		jlPageCountMS.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlPageCountMS.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		
		JButton printPDFMS = new JButton();
		printPDFMS.setIcon(new ImageIcon(getImage("/printIcon.png",60,60)));
		printPDFMS.setFont(new Font("Calibri",Font.BOLD,40));
		printPDFMS.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to print?");
				if(confirm == JOptionPane.YES_OPTION){
					try {
						new PrintPdf().printPDF(qpCurrentlyShowing.getLocation().substring(0, qpCurrentlyShowing.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qpCurrentlyShowing.getYear().replace(" ", "")+"_"+qpCurrentlyShowing.getQ()+".pdf", qpCurrentlyShowing.toString());
					} catch (PrinterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					};
				}else{
					System.out.println("PRINT CANCELLED");
				}
				
			}
			
		});
		
		JButton jbAdobeMS = new JButton();
		jbAdobeMS.setIcon(new ImageIcon(getImage("/adobeIcon.png",60,60)));
		jbAdobeMS.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
				    try {
				        File myFile = new File(qpCurrentlyShowing.getLocation().substring(0, qpCurrentlyShowing.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qpCurrentlyShowing.getYear().replace(" ", "")+"_"+qpCurrentlyShowing.getQ()+".pdf");
				        Desktop.getDesktop().open(myFile);
				    } catch (IOException ex) {
				        // no application registered for PDFs
				    }
				}
				
			}
			
		});
//		jpViewer.add(printPDF, BorderLayout.WEST);
		jpButtonMS.add(printPDFMS);
		jpButtonMS.add(jbAdobeMS);
		jpButtonMS.add(Box.createRigidArea(new Dimension(400,60)));
		jpButtonMS.add(jbNextMS);
		jpButtonMS.add(jbPrevMS);
		
		JPanel jpBottomMS = new JPanel();
		jpBottomMS.setLayout(new BoxLayout(jpBottomMS,BoxLayout.PAGE_AXIS));
		jpBottomMS.add(jpButtonMS);
		jpBottomMS.add(jlPageCountMS);
		
		jpViewerMS.add(jpBottomMS,BorderLayout.SOUTH);
		jpViewerMS.add(jlcurrentPDFMS,BorderLayout.NORTH);

		////////////////////////////////////COPIED CODE

		
		JPanel jpViewerQP = new JPanel(new BorderLayout());
        panel = new PagePanel();
        panel.setBorder(new LineBorder(Color.BLUE,3));
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.grabFocus();
       
        panel.addKeyListener(new KeyListener(){

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				System.out.println("LEFT KEY");
				prevPage();
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				System.out.println("RIGHT KEY");
				nextPage();
			}
			setVisible(true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
			
		}
		
	});
		jpViewerQP.add(panel,BorderLayout.CENTER);
		
		JPanel jpButton = new JPanel();
		jpButton.setMaximumSize(new Dimension(2000,60));
		jpButton.setBorder(new LineBorder(Color.black,1));
		jpButton.setLayout(new BoxLayout(jpButton,BoxLayout.X_AXIS));
		JButton jbNext = new JButton();
		jbNext.setOpaque(false);
		jbNext.setContentAreaFilled(false);
		jbNext.setIcon(new ImageIcon(getImage("/paperLeft.png",60,60)));
		
		jbNext.setAlignmentX(CENTER_ALIGNMENT);
		jbNext.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbNext.setMaximumSize(new Dimension(120,60));
		
		JButton jbPrev = new JButton();
		jbPrev.setOpaque(false);
		jbPrev.setContentAreaFilled(false);
		jbPrev.setIcon(new ImageIcon(getImage("/paperRight.png",60,60)));
		
		jbPrev.setAlignmentX(CENTER_ALIGNMENT);
		jbPrev.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbPrev.setMaximumSize(new Dimension(120,60));
		
		
		
		
		jbNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				    jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
		        	panel.requestFocusInWindow();
					prevPage();
			}
			
		});
		
		jbPrev.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				jListAvailable.setSelectedIndex(jListAvailable.getSelectedIndex());
		        panel.requestFocusInWindow();
				nextPage();
			}
			
		});
		
		
		
		//PDF Viewer page count
		jlPageCount = new JLabel("Current Page: "+"0"+"/"+"0");
		jlPageCount.setBorder(new LineBorder(Color.black,3));
		jlPageCount.setFont(new Font("Calibri",Font.BOLD,20));
		jlPageCount.setMaximumSize(new Dimension(2000, 500));
		jlPageCount.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlPageCount.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		
		JButton printPDF = new JButton();
		printPDF.setIcon(new ImageIcon(getImage("/printIcon.png",60,60)));
		printPDF.setFont(new Font("Calibri",Font.BOLD,40));
		printPDF.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to print?");
				if(confirm == JOptionPane.YES_OPTION){
					try {
						new PrintPdf().printPDF(qpCurrentlyShowing.getLocation(), qpCurrentlyShowing.toString());
					} catch (PrinterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					};
				}else{
					System.out.println("PRINT CANCELLED");
				}
				
			}
			
		});
//		jpViewer.add(printPDF, BorderLayout.WEST);
		JButton jbAdobe = new JButton();
		jbAdobe.setIcon(new ImageIcon(getImage("/adobeIcon.png",60,60)));
		jbAdobe.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
				    try {
				        File myFile = new File(qpCurrentlyShowing.getLocation());
				        Desktop.getDesktop().open(myFile);
				    } catch (IOException ex) {
				        // no application registered for PDFs
				    }
				}
				
			}
			
		});
		
		
		
		
		
		jpButton.add(printPDF);
		jpButton.add(jbAdobe);
		jpButton.add(Box.createRigidArea(new Dimension(400,60)));
		jpButton.add(jbNext);
		jpButton.add(jbPrev);
		
		JPanel jpBottom = new JPanel();
		jpBottom.setLayout(new BoxLayout(jpBottom,BoxLayout.PAGE_AXIS));
		jpBottom.add(jpButton);
		jpBottom.add(jlPageCount);
		
		jpViewerQP.add(jpBottom,BorderLayout.SOUTH);
		jpViewerQP.add(jlcurrentPDF,BorderLayout.NORTH);

		
		jpQP.add(jpViewerQP);
		jpMS.add(jpViewerMS);
		
		
//		jpViewer.add(jlPageCount,BorderLayout.NORTH);
//        mainPanel.add(jpViewerQP,BorderLayout.CENTER);
		mainPanel.add(jtpViewer,BorderLayout.CENTER);
	}
	
	public void prevPage(){

		if(currentIndex > 1){
			int maxCount = pdffile.getNumPages();
			System.out.println("Current index" + currentIndex);
			currentIndex--;
			System.out.println("New index" + currentIndex);
	        PDFPage page = pdffile.getPage(currentIndex);
	        panel.showPage(page);
			jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);
		}
		setVisible(true);

	}
	
	public void nextPage(){
		int maxCount = pdffile.getNumPages();
		System.out.println("CURRENT INDEX IS "+currentIndex+" MAX COUNT IS "+maxCount);
		if(currentIndex < maxCount ){
		currentIndex++;
		System.out.println("New index" + currentIndex);
        PDFPage page = pdffile.getPage(currentIndex);
        panel.showPage(page);
		jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);
		}else{
			System.out.println("CAN'T GO NEXT PAGE");
		}
		setVisible(true);
		

	}
	
	public void prevPageMS(){

		if(currentIndexMS > 1){
			int maxCount = pdffileMS.getNumPages();
			System.out.println("Current index" + currentIndexMS);
			currentIndexMS--;
			System.out.println("New index" + currentIndexMS);
	        PDFPage page = pdffileMS.getPage(currentIndexMS);
	        panelMS.showPage(page);
			jlPageCountMS.setText("Current Page: "+currentIndexMS+"/"+maxCount);
		}
		setVisible(true);

	}
	
	public void nextPageMS(){
		int maxCount = pdffileMS.getNumPages();
		System.out.println("CURRENT INDEX IS "+currentIndexMS+" MAX COUNT IS "+maxCount);
		if(currentIndexMS < maxCount ){
		currentIndexMS++;
		System.out.println("New index" + currentIndexMS);
        PDFPage page = pdffileMS.getPage(currentIndexMS);
        panelMS.showPage(page);
		jlPageCountMS.setText("Current Page: "+currentIndexMS+"/"+maxCount);
		}else{
			System.out.println("CAN'T GO NEXT PAGE");
		}
		setVisible(true);
		

	}
	
	public void setup(String filepath){
		try{
        //load a pdf from a byte buffer
        File file = new File(filepath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        pdffile = new PDFFile(buf);

        // show the first page
        PDFPage page = pdffile.getPage(0);
        panel.showPage(page);
		}catch(Exception e){
			System.out.println("ERROR SHOWING PDF");
		}
        
	}
	
	public void setupExternal(String filepath){
		try{
        //load a pdf from a byte buffer
        File file = new File(filepath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        pdffile = new PDFFile(buf);

        // show the first page
        PDFPage page = pdffile.getPage(0);
        panel.showPage(page);
		}catch(Exception e){
			System.out.println("ERROR SHOWING PDF");
		}
        
	}
	
	public void setupMS(String filepath){
		try{
        //load a pdf from a byte buffer
        File file = new File(filepath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        pdffileMS = new PDFFile(buf);

        // show the first page
        PDFPage page = pdffileMS.getPage(0);
        panelMS.showPage(page);
		}catch(Exception e){
			System.out.println("ERROR SHOWING PDF");
		}
        
	}
	

	public static void main(String[] args) {
//		showOnScreen(1,new MainFrame());
		new MainFrame();
//		new MainFrame();

	}
	
	public static void showOnScreen( int screen, JFrame frame ) {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gd = ge.getScreenDevices();
	    if( screen > -1 && screen < gd.length ) {
	        frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
	    } else if( gd.length > 0 ) {
	        frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY()+400);
	    } else {
	        throw new RuntimeException( "No Screens Found" );
	    }
	}
	
//	public static void showOnScreen( int screen, JFrame frame )
//	{
//	    GraphicsEnvironment ge = GraphicsEnvironment
//	        .getLocalGraphicsEnvironment();
//	    GraphicsDevice[] gs = ge.getScreenDevices();
//	    if( screen > -1 && screen < gs.length )
//	    {
//	        gs[screen].setFullScreenWindow( frame );
//	    }
//	    else if( gs.length > 0 )
//	    {
//	        gs[0].setFullScreenWindow( frame );
//	    }
//	    else
//	    {
//	        throw new RuntimeException( "No Screens Found" );
//	    }
//	}

}
