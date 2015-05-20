package javaFXStuff;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
	
	public static void display(String title,String message){
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);

		
		Label l1 = new Label(message);
		Button closeBtn = new Button("Close the window");
		closeBtn.setOnAction(e -> window.close());
		
		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(l1,closeBtn);
		vbox.setAlignment(Pos.CENTER);
		
		Scene sc = new Scene(vbox);
		
		
		
		window.setScene(sc);
		window.setTitle(title);
		window.setMinWidth(250);
		window.showAndWait();
	}

}
