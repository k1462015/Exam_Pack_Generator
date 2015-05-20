package javaFXStuff;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
	static boolean ans;
	
	public static boolean display(String title,String message){
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);

		
		Label l1 = new Label(message);
		
		//Create 2 buttons
		Button yesBtn = new Button("Yes");
		Button noBtn = new Button("No");

		yesBtn.setOnAction(e -> {
			ans = true;
			window.close();
		});
		
		noBtn.setOnAction(e ->{
			ans = false;
			window.close();
		});
		
		
		
		
		HBox hbox = new HBox(10);
		hbox.getChildren().addAll(l1,yesBtn,noBtn);
		hbox.setAlignment(Pos.CENTER);
		
		Scene sc = new Scene(hbox);
		
		
		
		window.setScene(sc);
		window.setTitle(title);
		window.setMinWidth(250);
		window.showAndWait();
		
		return ans;
		
	}

}
