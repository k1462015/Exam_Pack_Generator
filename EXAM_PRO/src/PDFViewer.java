import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.SwingUtilities;

import Viewer.Main;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;


public class PDFViewer extends PagePanel{

	public void loadPDF(String filePath) throws IOException{
		//load a pdf from a byte buffer
        File file = new File(filePath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        PDFFile pdffile = new PDFFile(buf);

        // show the first page
        PDFPage page = pdffile.getPage(0);
        showPage(page);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main.setup();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
	}

}
