package javaFXStuff;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckBoxFX extends Application {
	Stage window;
	Button button;
	
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage arg0) throws Exception {
		window = arg0;
		window.setTitle("Checkbox - Tutorial");
		
		
		//Checkboxes
		CheckBox box1 = new CheckBox("Chicken");
		CheckBox box2 = new CheckBox("Cheese");
		
		
		
		
		
		//Button
		button = new Button("Order now");
		button.setOnAction(e -> {
			if(box1.isSelected()){
				System.out.println("CHICLEN!!!");
			}
			if(box2.isSelected()){
				System.out.println("LOLZY CHEESY");
			}
		});
		
		
		
		//Layout
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20,20,20,20));
		layout.getChildren().addAll(box1,box2,button);
		
		Scene sc = new Scene(layout,500,500);
		
		window.setScene(sc);
		window.show();

	}

	

}
