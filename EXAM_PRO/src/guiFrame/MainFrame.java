package guiFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

import pdf.PrintPdf;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

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
	PagePanel panel;
	PDFFile pdffile;
	int currentIndex = 1;
	JLabel jlcurrentPDF;
	int totalMarks = 0;
	JLabel jlTotalMarks;
	JLabel jlPageCount;
	ArrayList<JCheckBox> jcbArrayTopics,jcbArrayYear;
	JList jListAvailable;
	JTextField jtfFileName;
	QuestionPaper qpCurrentlyShowing;

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
		JMenuItem printSelectedPage = new JMenuItem("Print Selected Item");
		printSelectedPage.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
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
		jmFile.add(printSelectedPage);
		jmb.add(jmFile);
		setJMenuBar(jmb);

		//Constructs bottom panels
		JPanel bottomPanel = new JPanel();
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
				makeQP(jtfFileName.getText());
				
			}
			
		});
		
		
		try {
			mainPanel();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		//Get Size of screen
		Toolkit tk = Toolkit.getDefaultToolkit();
		int xSize = ((int) tk.getScreenSize().getWidth());
		int ySize = ((int) tk.getScreenSize().getHeight());
		
		//Default JFrame Stuff
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(xSize,ySize - 100);
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
		sidePanel.setPreferredSize(new Dimension(500,110));
		sidePanel.setBorder(new LineBorder(Color.BLACK,3));
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
		j1Year.setBorder(new LineBorder(Color.BLACK,1));
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
			        panel.requestFocusInWindow();
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
			        panel.requestFocusInWindow();
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
			        panel.requestFocusInWindow();
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
			        panel.requestFocusInWindow();
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
					setup(qp.getLocation());
					currentIndex = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					// TODO Auto-generated catch block
					System.out.println("ERROR SHOWING PDF");
					
				}
			}
			}
		});
		jListAvailable.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				QuestionPaper qp = (QuestionPaper) jListAvailable.getSelectedValue();
				if(e.getKeyCode() == KeyEvent.VK_LEFT){
					System.out.println("LEFT KEY");
					prevPage();
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					System.out.println("RIGHT KEY");
					nextPage();
				}
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
					int currentIndex = jListAvailable.getSelectedIndex();
					if(e.getKeyCode() == KeyEvent.VK_UP){
						if((currentIndex - 1) < lmAvailable.getSize() && (currentIndex - 1) >= 0){
						qp = (QuestionPaper) lmAvailable.getElementAt(currentIndex - 1);
						}
					}
					if(e.getKeyCode() == KeyEvent.VK_DOWN){
						if((currentIndex + 1) < lmAvailable.getSize() && (currentIndex + 1) >= 0){
						qp = (QuestionPaper) lmAvailable.getElementAt(currentIndex + 1);
						}
					}
					qpCurrentlyShowing = qp;
					System.out.println("PRESSED ARROW UP/DOWN");
					if(jListAvailable.getSelectedValue() != null){
					jlcurrentPDF.setText(qp.toString());
					setup(qp.getLocation());
					currentIndex = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					// TODO Auto-generated catch block
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
	
	public void makeQP(String fileName){
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
		
		mu.setDestinationFileName(jfcDirectory.getSelectedFile().getPath()+"\\"+fileName+".pdf");
		}
		JOptionPane.showMessageDialog(null, "PDF Saved to "+jfcDirectory.getSelectedFile().getPath());
		makeMS(fileName,jfcDirectory.getSelectedFile().getPath());
				
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
	
	public void makeMS(String fileName,String fileDirec){
		PDFMergerUtility mu = new PDFMergerUtility();
		for(int i = 0;i < lmSelected.size();i++){
			QuestionPaper qp = (QuestionPaper)lmSelected.getElementAt(i);
			System.out.println(qp.getLocation());
			String filePath = qp.getLocation().substring(0, qp.getLocation().lastIndexOf("Questions\\")) +"Questions\\MS_"+qp.getYear().replace(" ", "")+"_"+qp.getQ()+".pdf";
			System.out.println("Mark Scheme : "+filePath);
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
//			        panel.requestFocusInWindow();
					System.out.println("ONE CLICK " +qp);
					jlcurrentPDF.setText(qp.toString());
					setup(qp.getLocation());
					currentIndex = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());

				
				}
				}
			}

		});
		jListSelected.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				QuestionPaper qp = (QuestionPaper) jListSelected.getSelectedValue();
				if(e.getKeyCode() == KeyEvent.VK_LEFT){
					System.out.println("LEFT KEY");
					prevPage();
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					System.out.println("RIGHT KEY");
					nextPage();
				}
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
					
					System.out.println("PRESSED ARROW UP/DOWN");
					if(jListSelected.getSelectedValue() != null){
					int currentIndex = jListSelected.getSelectedIndex();
						if(e.getKeyCode() == KeyEvent.VK_UP){
							if((currentIndex - 1) < lmSelected.getSize() && (currentIndex - 1) >= 0){
							qp = (QuestionPaper) lmSelected.getElementAt(currentIndex - 1);
							}
						}
						if(e.getKeyCode() == KeyEvent.VK_DOWN){
							if((currentIndex + 1) < lmSelected.getSize() && (currentIndex + 1) >= 0){
							qp = (QuestionPaper) lmSelected.getElementAt(currentIndex + 1);
							}
						}
						
						
					jlcurrentPDF.setText(qp.toString());
					setup(qp.getLocation());
					currentIndex = 1;
					jlPageCount.setText("Current Page: "+currentIndex+"/"+pdffile.getNumPages());
					// TODO Auto-generated catch block
					System.out.println("ERROR SHOWING PDF");
					qpCurrentlyShowing = qp;

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
		int tt = 0;

		for(int j = 0; j < lmAvailable.size();j++){
			QuestionPaper qp = (QuestionPaper) lmAvailable.get(j);
			boolean check = false;
			for(String yFilter:currentFiltersYear){
				System.out.println(qp.getYear()+" to match with "+yFilter);
				if(qp.getYear().replace(" ", "").equalsIgnoreCase(yFilter.replace(" ", ""))){
					System.out.println("MATCHED ");
					check = true;
					break;
				}
			}
			
			if(!check){
				System.out.println("REMOVED" + qp);
				lmAvailable.remove(j);
				j = 0;
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
	
	public void addPDFViewerPanel(){
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
		jpViewer.add(panel,BorderLayout.CENTER);
		
		JPanel jpButton = new JPanel();
		jpButton.setBorder(new LineBorder(Color.black,1));
		jpButton.setLayout(new BoxLayout(jpButton,BoxLayout.X_AXIS));
		JButton jbNext = new JButton();
		jbNext.setOpaque(false);
		jbNext.setContentAreaFilled(false);
		jbNext.setIcon(new ImageIcon(getImage("/paperLeft.png",100,100)));
		
		jbNext.setAlignmentX(CENTER_ALIGNMENT);
		jbNext.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbNext.setMaximumSize(new Dimension(120,100));
		
		JButton jbPrev = new JButton();
		jbPrev.setOpaque(false);
		jbPrev.setContentAreaFilled(false);
		jbPrev.setIcon(new ImageIcon(getImage("/paperRight.png",100,100)));
		
		jbPrev.setAlignmentX(CENTER_ALIGNMENT);
		jbPrev.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		jbPrev.setMaximumSize(new Dimension(120,100));
		
		
		
		
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
		
		
		jpViewer.add(jpButton,BorderLayout.SOUTH);
		
		//PDF Viewer page count
		jlPageCount = new JLabel("Current Page: "+"0"+"/"+"0");
		jlPageCount.setBorder(new LineBorder(Color.black,3));
		jlPageCount.setFont(new Font("Calibri",Font.BOLD,20));
		jlPageCount.setMaximumSize(new Dimension(700, 500));
		jlPageCount.setAlignmentX(CENTER_ALIGNMENT);//aligns label itself
		jlPageCount.setHorizontalAlignment(SwingConstants.CENTER);//aligns text inside the label
		
		
		JButton printPDF = new JButton();
		printPDF.setIcon(new ImageIcon(getImage("/printIcon.png",80,80)));
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
		jpButton.add(printPDF);
		jpButton.add(Box.createRigidArea(new Dimension(500,0)));
		jpButton.add(jbNext);
		jpButton.add(jbPrev);
		
		jpViewer.add(jlPageCount,BorderLayout.NORTH);
        mainPanel.add(jpViewer,BorderLayout.CENTER);
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
		if(currentIndex < maxCount ){
		currentIndex++;
		System.out.println("New index" + currentIndex);
        PDFPage page = pdffile.getPage(currentIndex);
        panel.showPage(page);
		jlPageCount.setText("Current Page: "+currentIndex+"/"+maxCount);
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
	

	public static void main(String[] args) {
		new MainFrame();

	}

}
