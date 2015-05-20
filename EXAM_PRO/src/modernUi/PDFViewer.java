package modernUi;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

public class PDFViewer {
	static PDFPage page;
	static PagePanel panel;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public PDFViewer(){
		panel = new PagePanel();
	}
	
	public PagePanel getPDFPanel(){
		return panel;
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
