package random;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import data.QuestionPaper;

public class CreateQuestionFront {

	public static String makePaper(QuestionPaper qp,int n,String type) throws IOException, COSVisitorException {
//		String outputFileName = getClass().getResource("/blank.pdf").getPath();
		String outputFileName = "";
		String title = "Question "+n;
		
		if(type.equals("QP")){
			outputFileName = System.getProperty("user.home")+"\\Desktop\\CACHE\\"+n+".pdf";
		}
		if(type.equals("MS")){
			outputFileName = System.getProperty("user.home")+"\\Desktop\\CACHE\\"+n+"_MS.pdf";
			title += " MS";
		}
		if(type.equals("ER")){
			outputFileName = System.getProperty("user.home")+"\\Desktop\\CACHE\\"+n+"_ER.pdf";
			title += " ER";
		}
		

		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A4);
		// PDPage.PAGE_SIZE_LETTER is also possible
		PDRectangle rect = page1.getMediaBox();
		// rect can be used to get the page width and height
		document.addPage(page1);

		// Create a new font object selecting one of the PDF base fonts
		PDFont fontPlain = PDType1Font.COURIER;
		PDFont fontBold = PDType1Font.COURIER_BOLD;
		PDFont fontItalic = PDType1Font.COURIER_OBLIQUE;
		PDFont fontMono = PDType1Font.COURIER;

		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream cos = new PDPageContentStream(document, page1);

		int line = 5;

		// Adds big gap so text is centered
		cos.beginText();
		cos.setFont(fontItalic, 30);
		cos.moveTextPositionByAmount(rect.getWidth() / 4, 1000 - 50 * (++line));
		cos.drawString("");
		cos.endText();
		
		
		
		

		int fontSize = 50; // Or whatever font size you want.
		float titleWidth;
		
		if(type.equals("MS")){
			titleWidth = fontPlain.getStringWidth("Question "+n+" MS") / 1000
					* fontSize;
		}else{
			titleWidth = fontPlain.getStringWidth("Question "+n) / 1000
				* fontSize;
		}
		// Question Title
		cos.beginText();
		cos.setFont(fontPlain, fontSize);
		cos.moveTextPositionByAmount((page1.getMediaBox().getWidth() - titleWidth) / 2,rect.getHeight() - 50 * (++line));
		cos.drawString(title);
		cos.endText();
		
		///Topic Name
		fontSize = 18; // Or whatever font size you want.
		titleWidth = fontPlain.getStringWidth(qp.getTopicName()) / 1000 * fontSize;

		cos.beginText();
		cos.setFont(fontPlain, 18);
		cos.moveTextPositionByAmount((page1.getMediaBox().getWidth() - titleWidth) / 2,rect.getHeight() - 50 * (++line));
		cos.drawString(qp.getTopicName());
		cos.endText();

		
		///Total Marks
		fontSize = 22; // Or whatever font size you want.
		titleWidth = fontPlain.getStringWidth("Total Marks: __/"+qp.getTotalMarks()) / 1000* fontSize;

		cos.beginText();
		cos.setFont(fontMono, fontSize);
		cos.setNonStrokingColor(Color.BLUE);
		cos.moveTextPositionByAmount((page1.getMediaBox().getWidth() - titleWidth) / 2,rect.getHeight() - 50 * (++line));
		cos.drawString("Total Marks: __/"+qp.getTotalMarks());
		cos.endText();
		
		
		
		///YEAR of paper
		fontSize = 12; // Or whatever font size you want.
		titleWidth = fontPlain.getStringWidth(qp.getYear()+" ("+qp.getQ()+")") / 1000 * fontSize;
		
		cos.beginText();
		cos.setFont(fontBold, fontSize);
		cos.moveTextPositionByAmount(
				(page1.getMediaBox().getWidth() - titleWidth) / 2,
				rect.getHeight() - 50 * (15));
		cos.drawString(qp.getYear()+" ("+qp.getQ()+")");
		cos.endText();
		
		
		
		
		
		
		
		
		

		// add an image
		// try {
		// BufferedImage awtImage = ImageIO.read(new
		// File("C:\\Users\\Tahmidul\\Desktop\\examPackIcon.jpg"));
		// PDXObjectImage ximage = new PDPixelMap(document, awtImage);
		// float scale = 0.5f; // alter this value to set the image size
		// cos.drawXObject(ximage, 100, 400, ximage.getWidth()*scale,
		// ximage.getHeight()*scale);
		// } catch (FileNotFoundException fnfex) {
		// System.out.println("No image for you");
		// }

		// Make sure that the content stream is closed:
		cos.close();

		// PDPage page2 = new PDPage(PDPage.PAGE_SIZE_A4);
		// document.addPage(page2);
		// cos = new PDPageContentStream(document, page2);

		// draw a red box in the lower left hand corner
		// cos.setNonStrokingColor(Color.RED);
		// cos.fillRect(10, 10, 100, 100);

		// add two lines of different widths
		// cos.setLineWidth(1);
		// cos.addLine(200, 250, 400, 250);
		// cos.closeAndStroke();
		// cos.setLineWidth(5);
		// cos.addLine(200, 300, 400, 300);
		// cos.closeAndStroke();

		// close the content stream for page 2
		// cos.close();

		// Save the results and ensure that the document is properly closed:
		document.save(outputFileName);
		document.close();
		
		return outputFileName;

	}

}
