package javaFXStuff;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import data.QuestionPaper;

public class FirstTry extends Application {
	Stage window;
	
	
	public static void main(String[] args) {
		launch(args);

	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		QuestionPaper qp1 = new QuestionPaper();
        qp1.setQPPath("Somehwere!!");
        qp1.setQ("Q5");
        qp1.setTopicName("Photosynthesis");
        qp1.setTotalMarks(8);
        qp1.setYear("Jan10");
        QuestionPaper qp2 = new QuestionPaper();
        qp2.setQPPath("Somehwere!!");
        qp2.setQ("Q5");
        qp2.setTopicName("Photosynthesis");
        qp2.setTotalMarks(8);
        qp2.setYear("Jan10");
        
        
        StackPane pane = new StackPane();
        Scene scene = new Scene(pane, 300, 150);
        primaryStage.setScene(scene);
        ObservableList<QuestionPaper> list = FXCollections.observableArrayList(
               qp1, qp2);
        ListView<QuestionPaper> lv = new ListView<>(list);
        lv.setCellFactory(new Callback<ListView<QuestionPaper>, ListCell<QuestionPaper>>() {
            @Override
            public ListCell<QuestionPaper> call(ListView<QuestionPaper> param) {
                return new XCell();
            }
        });
        pane.getChildren().add(lv);
        primaryStage.show();
        
        
        
        
        
        
        
        
        
        
		
		
//		StackPane pane = new StackPane();
//        Scene scene = new Scene(pane, 300, 150);
//        primaryStage.setScene(scene);
//        ObservableList<String> list = FXCollections.observableArrayList(
//                "Item 1", "Item 2", "Item 3", "Item 4");
//        ListView<String> lv = new ListView<>(list);
//        lv.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//            @Override
//            public ListCell<String> call(ListView<String> param) {
//                return new XCell();
//            }
//        });
//        pane.getChildren().add(lv);
//        primaryStage.show();
        
        
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

//        Button button = new Button("+");
        QuestionPaper lastItem;

        public XCell() {
//            super();
//            hbox.getChildren().addAll(label, pane);
//            HBox.setHgrow(pane, Priority.ALWAYS);
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
                lbTopicName.setFont(new Font("Calibri",20));
                lbMarks.setText((item.getTotalMarks()+"")!=null ? (item.getTotalMarks()+"") : "<null>");
                lbMarks.setFont(new Font("Calibri",20));
                lbYear.setText(item.getYear() !=null ? item.getYear() : "<null>");
                lbYear.setFont(new Font("Calibri",20));
                lbQuestion.setText(item.getQ() !=null ? item.getQ() : "<null>");
                lbQuestion.setFont(new Font("Calibri",20));
                setGraphic(bp);
            }
        }
		
		
	}
	
//	static class XCell extends ListCell<QuestionPaper>{
//		
//		HBox hbox = new HBox();
//        Label label = new Label("(empty)");
//        Pane pane = new Pane();
//        Button button = new Button("+");
//        QuestionPaper lastItem;
//
//        public XCell() {
////            super();
//            hbox.getChildren().addAll(label, pane, button);
//            HBox.setHgrow(pane, Priority.ALWAYS);
//            button.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    System.out.println(lastItem + " : " + event);
//                }
//            });
//        }
//
//        @Override
//        protected void updateItem(QuestionPaper item, boolean empty) {
//            super.updateItem(item, empty);
//            setText(null);  // No text in label of super class
//            if (empty) {
//                lastItem = null;
//                setGraphic(null);
//            } else {
//                lastItem = item;
//                label.setText(item!=null ? item.toString() : "<null>");
//                label.setFont(new Font("Calibri",20));
//                setGraphic(hbox);
//            }
//        }
//		
//		
//	}

}
