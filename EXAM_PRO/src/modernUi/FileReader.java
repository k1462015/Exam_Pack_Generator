package modernUi;

import java.io.File;
import java.util.ArrayList;

import data.QuestionPaper;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class FileReader {

	public FileReader() {
		
	}

	void readAllFiles(String filePath,ArrayList<QuestionPaper> qpList) {
		ArrayList<String> filePaths = new ArrayList<String>();
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				filePaths.add(listOfFiles[i].getName());
//				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
//				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		getQuestions(filePaths,qpList);
	}

	public void getQuestions(ArrayList<String> filePaths,ArrayList<QuestionPaper> qpList) {
		for (String filePath : filePaths) {
			try{
			if (filePath.contains(".pdf") && (!filePath.contains("MS"))
					&& (!filePath.contains("ER"))) {
				QuestionPaper qp = new QuestionPaper();
				String[] split = (filePath + "").substring(
						(filePath + "").lastIndexOf("\\") + 1,
						(filePath + "").indexOf(".pdf")).split("_");
				qp = new QuestionPaper();
				qp.setQPPath(filePath.toString());
				qp.setYear(split[1].replace(" ", ""));
				qp.setQ(split[2]);
				qp.setTopicName(split[3]);
				qp.setTotalMarks(Integer.parseInt(split[4]));
				qpList.add(qp);

			}
			}catch(Exception e){
				System.out.println("ERROR READING/ADDING QP FILES");
			}
		}
		
		System.out.println("Total QP populated: "+qpList.size());
	}
	

}
