package random;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class OpenAdobe {

	public static void main(String[] args) {
		if (Desktop.isDesktopSupported()) {
		    try {
		        File myFile = new File("C:\\Users\\Tahmidul\\Desktop\\PROJECT RELATED\\test.pdf");
		        Desktop.getDesktop().open(myFile);
		    } catch (IOException ex) {
		        // no application registered for PDFs
		    }
		}

	}

}
