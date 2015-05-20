package modernUi;


import java.io.File;
import java.util.ArrayList;






import org.controlsfx.control.CheckComboBox;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import data.QuestionPaper;

public class MainFrame extends Application {
	Stage window;
	ArrayList<QuestionPaper> qpList;
	BorderPane bp;
	QuestionList questionList;
	CheckComboBox<String> checkComboBoxYear;
	CheckComboBox<String> checkComboBoxTopic;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage arg0) throws Exception {
		window = arg0;
		qpList = new ArrayList<QuestionPaper>();
		questionList = new QuestionList();
		
		bp = new BorderPane();
		bp.setCenter(getFileLabel());
		
		//LeftPane
		////Filter Pane
		/////Year CheckBox
		 // Create the CheckComboBox with the data 
		 checkComboBoxYear = new CheckComboBox<String>();
		 checkComboBoxYear.setMaxWidth(400);
		 checkComboBoxYear.setMinWidth(400);
		 checkComboBoxYear.setMaxHeight(400);
		 // and listen to the relevant events (e.g. when the selected indices or 
		 // selected items change).
		 checkComboBoxYear.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
		     public void onChanged(ListChangeListener.Change<? extends String> c) {
		         System.out.println(checkComboBoxYear.getCheckModel().getCheckedItems());
	        	 questionList.getYearFilters().clear();
	        	 questionList.getYearFilters().addAll(checkComboBoxYear.getCheckModel().getCheckedItems());
	        	 questionList.refilter();

		     }
		 });
		 ////Topic Checkbox
		// Create the CheckComboBox with the data 
		 checkComboBoxTopic = new CheckComboBox<String>();
		 checkComboBoxTopic.setMaxWidth(400);
		 checkComboBoxTopic.setMinWidth(400);
		 checkComboBoxTopic.setMaxHeight(400);
		 
		 // and listen to the relevant events (e.g. when the selected indices or 
		 // selected items change).
		 checkComboBoxTopic.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
		     public void onChanged(ListChangeListener.Change<? extends String> c) {
		         System.out.println(checkComboBoxTopic.getCheckModel().getCheckedItems());
	        	 questionList.getTopicFilters().clear();
	        	 questionList.getTopicFilters().addAll(checkComboBoxTopic.getCheckModel().getCheckedItems());
	        	 questionList.refilter();
		     }
		 });
		

		
		
		ViewerFX vfx = new ViewerFX(bp,"C:\\Users\\Tahmidul\\Desktop\\CACHE\\10.pdf");
		vfx.setupViewer();
		
		
		
		////Adding AvailableList
		VBox vboxLeft = new VBox();
		vboxLeft.setVgrow(questionList.getAvailableList(), Priority.ALWAYS);
		vboxLeft.setAlignment(Pos.CENTER);
		vboxLeft.getChildren().addAll(new Label("Year Filter"),checkComboBoxYear,new Label("Topic Filter"),checkComboBoxTopic);
		
		
		Label lbAvailable = new Label("Available Questions");
		lbAvailable.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		lbAvailable.setTextAlignment(TextAlignment.CENTER);
		vboxLeft.getChildren().addAll(lbAvailable, questionList.getAvailableList());
		
		////Adding SelectedList
		vboxLeft.setVgrow(questionList.getSelectedList(), Priority.ALWAYS);
		vboxLeft.setAlignment(Pos.CENTER);
		
		Label lbSelected = new Label("Selected Questions");
		lbSelected.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		lbSelected.setTextAlignment(TextAlignment.CENTER);
		vboxLeft.getChildren().addAll(lbSelected, questionList.getSelectedList());
		
		///Adding leftPane to main borderpane left
		bp.setLeft(vboxLeft);
		
		Scene sc = new Scene(bp,2000,1200);
		
		
		
		window.setScene(sc);
		window.show();
	}

	public Label getFileLabel(){
		FileReader fr = new FileReader();
		Label dragLabel = new Label("Drag folder here");
		dragLabel.setFont(new Font("Calibri",100));
		dragLabel.setText("Drag Folder here");
		dragLabel.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});

		// Dropping over surface
		dragLabel.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					success = true;
					String filePath = null;
					for (File file : db.getFiles()) {
						filePath = file.getAbsolutePath();
						fr.readAllFiles(file.getAbsolutePath(),qpList);
					}
				}
				
				event.setDropCompleted(success);
				event.consume();
				questionList.addToList(qpList);
				bp.getChildren().remove(dragLabel);
				questionList.getYearList(qpList);
				questionList.getTopicList(qpList);
				
//				 final ObservableList<String> strings = FXCollections.observableArrayList();
				
				
				
				checkComboBoxYear.getItems().setAll(questionList.getYearList(qpList));
				checkComboBoxTopic.getItems().setAll(questionList.getTopicList(qpList));

//				questionList.getTopicFilters().add("Photosynthesis");
//				questionList.getYearFilters().add("JUN11");
				questionList.refilter();
				
				window.show();
			}
		});
		
		return dragLabel;
	}
	

	

}
