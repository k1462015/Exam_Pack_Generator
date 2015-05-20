package pdf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

public class ExternalPDFViewer {
	static PDFPage page;
	static PagePanel panel;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JButton button = new JButton("PRESS ME");
		panel = new PagePanel();

		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				setup("C:\\Users\\Tahmidul\\Desktop\\test20.pdf");
				frame.setVisible(true);
				
			}
			
		});
		frame.add(button,BorderLayout.NORTH);
		frame.add(new JScrollPane(panel),BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	
	
	public static void setup(String filepath){
		try{
        //load a pdf from a byte buffer
        File file = new File(filepath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        PDFFile pdffile = new PDFFile(buf);

        // show the first page
        page = pdffile.getPage(0);
        panel.showPage(page);
        raf.close();

		}catch(Exception e){
			System.out.println("ERROR SHOWING PDF");
		}
        
	}
}
