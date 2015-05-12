import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

import javax.print.PrintService;

import org.apache.pdfbox.pdmodel.PDDocument;


public class Random {

	public static void main(String[] args) {
//		try {
//			printPDF("C:\\Users\\Tahmidul\\Documents\\exam_pro\\CHEM5 PDF - Copy\\Questions\\CHEM5_JAN10_Q1_Transition Metals and Aqeous Reactions_7.pdf");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (PrinterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		choosePrinter();
		try {
			PDDocument pd = new PDDocument();
			pd.s
			new PDDocument().print();
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static PrintService choosePrinter() {
	    PrinterJob printJob = PrinterJob.getPrinterJob();
	    if(printJob.printDialog()) {
	        return printJob.getPrintService();          
	    }
	    else {
	        return null;
	    }
	}

	public static void printPDF(String fileName)
	        throws IOException, PrinterException {
	    PDDocument doc = PDDocument.load(fileName);
	    doc.print();
	}
}
