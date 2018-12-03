package windows;

import program.Display;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertBox {
	
	public static void display(String title, String message, int width, int height) {
		Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setWidth(width);
		window.setHeight(height);
		
		Label label = new Label();
		label.setText(message);
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.JUSTIFY);
		label.setPadding(new Insets(0,20,0,20));
		
		Button closeButton = new Button("Ok");
		closeButton.setOnAction(e -> window.close());
		closeButton.setMinWidth(60);
		closeButton.setMinHeight(30);
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);
		label.setPadding(new Insets(10,10,10,10));
		
		Scene scene = new Scene(layout);
		scene.getStylesheets().add(Display.class.getResource("customStyle.css").toExternalForm());
		window.getIcons().add(Display.icon);
		window.setScene(scene);
		window.setResizable(false);
		window.showAndWait();
		
	}

}
