package javaFXStuff;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GridPaneTutorial extends Application {
	Stage window;
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage arg0) throws Exception {
		window = arg0;
		window.setTitle("GridPane - Tutorial");
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);
		
		Label nameLabel = new Label("Username");
		GridPane.setConstraints(nameLabel, 0, 0);
		
		
		
		//NAME iNPUT
		TextField nameInput = new TextField("Bucky");
		GridPane.setConstraints(nameInput, 1, 0);
		
		//Password label
		Label passLabel = new Label("Password:");
		GridPane.setConstraints(passLabel, 0, 2);
		
		//Password input
		TextField passInput = new TextField();
		passInput.setPromptText("Password");
		GridPane.setConstraints(passInput, 1, 2);

		Button loginButton = new Button("Log In");
		GridPane.setConstraints(loginButton, 1, 3);
		
		grid.getChildren().addAll(nameLabel,nameInput,passLabel,passInput,loginButton);
		
		
		Scene sc = new Scene(grid,300,300);
		window.setScene(sc);
		
		
		
		
		
		window.show();

	}

	

}
