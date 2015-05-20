package modernUi;

import java.util.ArrayList;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import data.QuestionPaper;

public class QuestionList {
	ListView<QuestionPaper> availableList;
	ListView<QuestionPaper> selectedList;
	ArrayList<String> yearFilters;
	ArrayList<String> topicFilters;
	ArrayList<QuestionPaper> allQuestions;
	
	public QuestionList(){
		yearFilters = new ArrayList<String>();
		allQuestions = new ArrayList<QuestionPaper>();
		topicFilters = new ArrayList<String>();
		yearFilters = new ArrayList<String>();
		
		
        availableList = new ListView<>();
        availableList.setMinWidth(400);
        
        selectedList = new ListView<>();
        selectedList.setMinWidth(400);
	}
	
	public ListView<QuestionPaper> getAvailableList(){
		return availableList;
	}
	
	public ListView<QuestionPaper> getSelectedList(){
		return selectedList;
	}
	
	public void addToList(ArrayList<QuestionPaper> qpArray){
		//Maintains a copy of all questions
		allQuestions = (ArrayList<QuestionPaper>) qpArray.clone();
		
		ObservableList<QuestionPaper> items = FXCollections.observableArrayList(qpArray);
        availableList.setItems(items);
        availableList.setCellFactory(new Callback<ListView<QuestionPaper>, ListCell<QuestionPaper>>() {
            @Override
            public ListCell<QuestionPaper> call(ListView<QuestionPaper> param) {
                return new XCell();
            }
        });
        availableList.setOnMouseClicked(e ->{
        	if(e.getClickCount() == 2){
        		System.out.println("Removing question");
        		selectedList.getItems().add((QuestionPaper) availableList.getSelectionModel().getSelectedItem());
        	}
        });
        
        ///SelectedList
        selectedList.setCellFactory(new Callback<ListView<QuestionPaper>, ListCell<QuestionPaper>>() {
            @Override
            public ListCell<QuestionPaper> call(ListView<QuestionPaper> param) {
                return new XCell();
            }
        });
        
	}
	
	public ArrayList<String> getYearList(ArrayList<QuestionPaper> qp){
		ArrayList<String> toReturn = new ArrayList<String>();
		for(int i = 0;i < qp.size();i++){
			QuestionPaper temp = qp.get(i);
			boolean checkIfHave = false;
			
				for(String s:toReturn){
					if(s.equals(temp.getYear())){
						checkIfHave = true;
						break;
					}
				}
				
			if(!checkIfHave){
				toReturn.add(temp.getYear());
			}
		}
		
		System.out.println("Years found: "+toReturn.toString());
		return toReturn;
	}
	
	public ArrayList<String> getTopicList(ArrayList<QuestionPaper> qp){
		ArrayList<String> toReturn = new ArrayList<String>();
		for(int i = 0;i < qp.size();i++){
			QuestionPaper temp = qp.get(i);
			boolean checkIfHave = false;
			
				for(String s:toReturn){
					if(s.equals(temp.getTopicName())){
						checkIfHave = true;
						break;
					}
				}
				
			if(!checkIfHave){
				toReturn.add(temp.getTopicName());
			}
		}
		
		System.out.println("Topics found: "+toReturn.toString());
		return toReturn;
	}
	
	public ArrayList<String> getYearFilters(){
		return yearFilters;
	}
	
	public ArrayList<String> getTopicFilters(){
		return topicFilters;
	}
	
	public void refilter(){
		availableList.getItems().clear();
		for(int i = 0;i < allQuestions.size();i++){
			boolean checkTopic = false;
			QuestionPaper temp = allQuestions.get(i);
			System.out.println("Current filters for topics "+topicFilters.toString());
			for(String topic:topicFilters){
				if(topic.replace(" ", "").equalsIgnoreCase(temp.getTopicName().replace(" ", ""))){
					checkTopic = true;
				}
			}
			
			if(checkTopic){
			System.out.println("Current filter for years "+yearFilters.toString());
				for(String year:yearFilters){
					if(year.replace(" ", "").equalsIgnoreCase(temp.getYear().replace(" ", ""))){
						availableList.getItems().add(temp);
						break;
					}
				}
			}
			
		}
		
		
		
	}
	
	
	

	static class XCell extends ListCell<QuestionPaper>{
		BorderPane bp = new BorderPane();
		HBox hbox = new HBox();
		HBox hbox2 = new HBox();
        Label lbTopicName = new Label("(empty)");
        Label lbMarks = new Label("(empty)");
        Label lbYear = new Label("(empty)");
        Label lbQuestion = new Label("(empty)");
        Pane pane = new Pane();
        Pane pane1 = new Pane();

        QuestionPaper lastItem;

        public XCell() {
        	hbox.getChildren().addAll(lbTopicName, pane,lbMarks);
            HBox.setHgrow(pane, Priority.ALWAYS);
            
            hbox2.getChildren().addAll(lbYear, pane1,lbQuestion);
            hbox2.setHgrow(pane1, Priority.ALWAYS);
            
            bp.setTop(hbox);
            bp.setBottom(hbox2);
        }

        @Override
        protected void updateItem(QuestionPaper item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                lbTopicName.setText(item.getTopicName()!=null ? item.getTopicName() : "<null>");
                lbTopicName.setFont(Font.font("Calibri", FontWeight.BOLD, 18));
                lbMarks.setText((item.getTotalMarks()+" marks")!=null ? (item.getTotalMarks()+" marks") : "<null>");
                lbMarks.setFont(new Font("Calibri",15));
                lbYear.setText(item.getYear() !=null ? item.getYear() : "<null>");
                lbYear.setFont(new Font("Calibri",15));
                lbQuestion.setText(item.getQ() !=null ? item.getQ() : "<null>");
                lbQuestion.setFont(new Font("Calibri",15));
                setGraphic(bp);
            }
        }
		
		
	}

}
