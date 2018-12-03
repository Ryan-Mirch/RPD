package windows;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import program.Calculate;
import program.Display;

import java.util.ArrayList;

public class Information {
	
	static ChoiceBox<String> selectionMethod;
	static Canvas obstaclePicture;
	static final double PW = 400;
	static final double PL = 500;
	static String prevThickness = Display.textBoxes.get(3).getValue();
	
	public static void addTextLimiter(final TextField tf, final int maxLength) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(tf.getText().length() > maxLength) {
					String s = tf.getText().substring(0, maxLength);
					tf.setText(s);
				}
			}
			
		});
		
	}
	
	private static void numbersOnly(TextField textField) {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if((!newValue.matches("\\d*")) ) {
					textField.setText(newValue.replaceAll("\\D",""));
				}
			}
		});
	}
	
	public static void display() {
		
		Stage window = new Stage();
	
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Information");
		window.setWidth(PW);
		window.setHeight(PL);
		
		//Text Boxes
		
		Label obstacleWidthLabel = new Label();
		obstacleWidthLabel.setText(Display.textBoxes.get(0).getName());
		
		ArrayList<Label> labels = new ArrayList<>();
		for(int i = 0; i < 11; i++) {
			Label l = new Label();
			l.setText(Display.textBoxes.get(i).getName());
			labels.add(l);
		}
		
		ArrayList<TextField> textFields = new ArrayList<>();
		for(int i = 0; i < 11; i++) {
			TextField t = new TextField();
			t.setText(Display.textBoxes.get(i).getValue());
			textFields.add(t);
		}		
		
		Button confirmButton = new Button("Confirm");
		Button cancelButton = new Button("Cancel");	
		
		confirmButton.setOnAction(e -> {			
			if(Calculate.isFloat(textFields.get(3).getText())){
				Display.textBoxes.get(3).setValue(textFields.get(3).getText());
				
				for(int i = 0; i < 11; i++) {
					Display.textBoxes.get(i).setValue(textFields.get(i).getText());
				}				
				window.close();
			}
			else {
				textFields.get(3).setText(prevThickness);
				AlertBox.display("Input Error", "Thickness must be a decimal, '1.5' for example.", 300, 150);
				
			}	
		});
			
		cancelButton.setOnAction(e -> window.close());
		
		HBox bottomLayout = new HBox(40);
		bottomLayout.getChildren().addAll(confirmButton, cancelButton);
		bottomLayout.setAlignment(Pos.CENTER);
		bottomLayout.setPadding(new Insets(10,10,10,10));	
		
		ArrayList<HBox> hBoxes = new ArrayList<>();
		for(int i = 0; i < 11; i++) {
			HBox h = new HBox(20);
			TextField t = textFields.get(i);
			Label l = labels.get(i);			
			h.getChildren().addAll(l,t);
			h.setAlignment(Pos.CENTER_RIGHT);
			hBoxes.add(h);
			
		}
		
		VBox textFieldsLayout = new VBox(5);
		for(HBox h: hBoxes) {
			textFieldsLayout.getChildren().add(h);
		}		
		textFieldsLayout.setAlignment(Pos.CENTER_RIGHT);		
		
		VBox mainLayout = new VBox(20);
		mainLayout.getChildren().addAll(textFieldsLayout, bottomLayout);
		
		mainLayout.setAlignment(Pos.CENTER_RIGHT);
		mainLayout.setPadding(new Insets(10,40,0,20));
	
		
		Scene scene = new Scene(mainLayout);
		scene.getStylesheets().add(Display.class.getResource("customStyle.css").toExternalForm());
		
		scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent event) {
		    	 if(event.getCode() == KeyCode.ENTER) {   		 
						
						if(Calculate.isFloat(textFields.get(3).getText())){
							Display.textBoxes.get(3).setValue(textFields.get(3).getText());
							
							for(int i = 0; i < 11; i++) {
								Display.textBoxes.get(i).setValue(textFields.get(i).getText());
							}				
							window.close();
						}
						else {
							textFields.get(3).setText(prevThickness);
							AlertBox.display("Input Error", "Thickness must be a decimal, '1.5' for example.", 300, 150);
						} 
		    	 }		    	
		    }
		});
		
		window.getIcons().add(Display.icon);
		window.setScene(scene);
		window.setResizable(false);
		window.showAndWait();
		
	}
}
