package javaFXStuff;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ValidataFX extends Application {
		Stage window;
		Button button;
		
		
	public static void main(String[] args) {
		launch(args);

	}
	@Override
	public void start(Stage arg0) throws Exception {
		window = arg0;
		window.setTitle("Validate user input");
		
		//Form
		TextField nameInput = new TextField();
		
		button = new Button("Click me");
		button.setOnAction(e -> {
			if(validAge(nameInput.getText())){
				System.out.println(nameInput.getText());
			}
		});
		
		//Layout
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20,20,20,20));
		layout.getChildren().addAll(nameInput,button);
		
		Scene sc = new Scene(layout,500,500);
		
		window.setScene(sc);
		window.show();

	}
	
	private boolean validAge(String input){
		if(input.matches("\\d{1,3}")){
			return true;
		}else{
			return false;
		}
	}

	

}
