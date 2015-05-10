import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Testing {

	public static void main(String[] args) {
//		PDFMergerUtility mu = new PDFMergerUtility();
//		mu.addSource("/Users/tahmidulislam/Desktop/2.pdf");
//		mu.addSource("/Users/tahmidulislam/Desktop/B.pdf");
//		mu.setDestinationFileName("/Users/tahmidulislam/Desktop/MERGED.pdf");
//		try {
//			mu.mergeDocuments();
//		} catch (COSVisitorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		try {
			Files.walk(Paths.get("/Users/tahmidulislam/exam_pro/CHEM5/Topic Seperated/Periodicity")).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			        System.out.println(filePath);
			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		File folder = new File("/Users/you/folder/");
//		File[] listOfFiles = folder.listFiles();
//
//		for (File file : listOfFiles) {
//		    if (file.isFile()) {
//		        System.out.println(file.getName());
//		    }
//		}
		//Naming Convention is chem5_JUN09_Q5_TOPICNAME_TOTALMARKS
		
	}

}
