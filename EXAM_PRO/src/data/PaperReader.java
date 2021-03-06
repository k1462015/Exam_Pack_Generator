package data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFileChooser;


public class PaperReader {

	public ArrayList<QuestionPaper> readPapers() {
		ArrayList<QuestionPaper> qpArray = new ArrayList<QuestionPaper>();
		
		JFileChooser jfcDirectory = new JFileChooser();
		jfcDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if(jfcDirectory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			System.out.println(jfcDirectory.getCurrentDirectory());
			try {
				Files.walk(Paths.get(jfcDirectory.getCurrentDirectory()+"")).forEach(filePath -> {
				    if (Files.isRegularFile(filePath) && (filePath+"").contains(".pdf") && !(filePath+"").contains("QP") && !(filePath+"").contains("ER")) {
				    	String[] name = (filePath+"").substring((filePath+"").lastIndexOf("\\")+1, (filePath+"").indexOf(".pdf")).split("_");
				    	QuestionPaper qp = new QuestionPaper();
				    	qp.setQPPath(filePath.toString());
				    	qp.setYear(name[1].replace(" ", ""));
				    	qp.setQ(name[2]);
				    	qp.setTopicName(name[3]);
				    	qp.setTotalMarks(Integer.parseInt(name[4]));
				    	
				    	qpArray.add(qp);
				        
				    }
				    
				});
				
				System.out.println("Total Numer of question paper found is: "+qpArray.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return qpArray;
	}
	
	public void createFolder(String nameOfFolder){
		File dir = new File(System.getProperty("user.home")+"\\Desktop\\"+nameOfFolder);
		dir.mkdir();
	}

}
