package javaFXStuff;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{
	Stage window;
	Scene scene1, scene2;
	Button btn;
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("LOOOL");
		
		MenuBar menuBar = new MenuBar();
		 
        // --- Menu File
        Menu menuFile = new Menu("File");
 
        // --- Menu Edit
        Menu menuEdit = new Menu("Edit");
 
        // --- Menu View
        Menu menuView = new Menu("View");
 
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
 
 
		
		
		btn = new Button("Click Me");
		btn.setOnAction(e ->{
			boolean result = ConfirmBox.display("Confirm", "Are you sure you want to?");
			if(result){
				System.out.println("PRESS NO NEXT TIME!");
			}
		});
		VBox layout = new VBox();
		Scene scene = new Scene(layout,300,250);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar);

		layout.getChildren().add(btn);
		
		
		
		
		
		
		

		window.setScene(scene);
		window.show();
		
		window.setOnCloseRequest(e -> {
			closeProgram();
			e.consume();
		});
		
		 
	}
	
	private void closeProgram(){
		boolean ans = ConfirmBox.display("", "Are you sure you want to exit?");
		if(ans){
			window.close();
		}
	}
	



}
