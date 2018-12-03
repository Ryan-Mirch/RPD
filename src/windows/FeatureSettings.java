package windows;

import java.io.IOException;

import program.Display;
import program.Loading;
import program.Stone;
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

public class FeatureSettings {
	
	static ChoiceBox<String> selectionMethod;
	static Canvas obstaclePicture;
	static final double PW = 350;
	static final double PL = 300;	
	
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
	
	public static void display(Stone stone) {
		
		int oW = stone.getW();			//old width
		int oL = stone.getL();			//old length
		String oN = stone.getSize();	//old name
		
		Stage window = new Stage();
	
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Settings");
		window.setWidth(PW);
		window.setHeight(PL);
		
		//Text Boxes
		
		Label obstacleWidthLabel = new Label();
		obstacleWidthLabel.setText("Length in In.");
		
		Label obstacleLengthLabel = new Label();
		obstacleLengthLabel.setText("Width in In.");
		
		Label obstacleNameLabel = new Label();
		obstacleNameLabel.setText("Name");
		
		TextField obstacleWidthTextField = new TextField(Integer.toString(oW));
		addTextLimiter(obstacleWidthTextField, 3);		
		numbersOnly(obstacleWidthTextField);
		
		TextField obstacleLengthTextField = new TextField(Integer.toString(oL));
		addTextLimiter(obstacleLengthTextField, 3);		
		numbersOnly(obstacleLengthTextField);
		
		TextField obstacleNameTextField = new TextField(stone.getSize());
		
		
		Button confirmButton = new Button("Confirm");
		Button removeButton = new Button("Remove");
		Button cancelButton = new Button("Cancel");
		Button copyButton = new Button("Copy");

		removeButton.setOnAction(e -> {
			stone.remove();
			Display.stones.remove(stone);
			Display.obstacles.remove(stone);
			
			try {Loading.saveProject(1);}
    		catch (IOException e1) {e1.printStackTrace();}
			System.out.println("saved feature remove button");
			
			window.close();
		});
		
		confirmButton.setOnAction(e -> {
					
			if(!obstacleWidthTextField.getText().isEmpty())stone.setW(Integer.parseInt(obstacleWidthTextField.getText()));
			if(!obstacleLengthTextField.getText().isEmpty())stone.setL(Integer.parseInt(obstacleLengthTextField.getText()));
			stone.setSize(obstacleNameTextField.getText());
			
			if(stone.getW() != oW || stone.getL() != oL || !stone.getSize().equals(oN)) {
				try {Loading.saveProject(1);}
	    		catch (IOException e1) {e1.printStackTrace();}
				System.out.println("saved feature confirm button");
			}
			
			window.close();
			
		});

		copyButton.setOnAction(e -> {
			Stone newStone = new Stone(stone.getSize(), stone.getX() + 5, stone.getY() + 5, stone.getW(), stone.getL(), 100);
			Display.stones.add(newStone);
			Display.obstacles.add(newStone);
			newStone.setColor(4);

			try {Loading.saveProject(1);}
			catch (IOException e1) {e1.printStackTrace();}
			System.out.println("saved insert feature");

			window.close();
		});
			
		cancelButton.setOnAction(e -> window.close());
		
		HBox bottomLayout = new HBox(40);
		bottomLayout.getChildren().addAll(confirmButton, cancelButton);
		bottomLayout.setAlignment(Pos.CENTER);
		bottomLayout.setPadding(new Insets(10,10,10,10));

        HBox topLayout = new HBox(40);
        topLayout.getChildren().addAll(copyButton, removeButton);
        topLayout.setAlignment(Pos.CENTER);
        topLayout.setPadding(new Insets(10,10,10,10));

        HBox obstacleWidthLayout = new HBox(5);
		obstacleWidthLayout.getChildren().addAll(obstacleWidthLabel, obstacleWidthTextField);
		obstacleWidthLayout.setAlignment(Pos.CENTER_RIGHT);
		
		HBox obstacleLengthLayout = new HBox(5);
		obstacleLengthLayout.getChildren().addAll(obstacleLengthLabel, obstacleLengthTextField);
		obstacleLengthLayout.setAlignment(Pos.CENTER_RIGHT);
		
		HBox obstacleNameLayout = new HBox(5);
		obstacleNameLayout.getChildren().addAll(obstacleNameLabel, obstacleNameTextField);
		obstacleNameLayout.setAlignment(Pos.CENTER_RIGHT);
		
		VBox textFieldLayout = new VBox(5);
		textFieldLayout.getChildren().addAll(obstacleWidthLayout, obstacleLengthLayout, obstacleNameLayout);
		textFieldLayout.setAlignment(Pos.CENTER);
		
		VBox obstacleLayout = new VBox(20);
		obstacleLayout.getChildren().addAll(topLayout, textFieldLayout,
											bottomLayout);
		
		obstacleLayout.setAlignment(Pos.CENTER);
		obstacleLayout.setPadding(new Insets(10,10,0,20));
	
		
		Scene scene = new Scene(obstacleLayout);
		scene.getStylesheets().add(Display.class.getResource("/customStyle.css").toExternalForm());
		
		scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent event) {
		    	 if(event.getCode() == KeyCode.ENTER) {
		    		 
		    		if(!obstacleWidthTextField.getText().isEmpty())stone.setW(Integer.parseInt(obstacleWidthTextField.getText()));
		 			if(!obstacleLengthTextField.getText().isEmpty())stone.setL(Integer.parseInt(obstacleLengthTextField.getText()));
		 			stone.setSize(obstacleNameTextField.getText());
		 			
		 			if(stone.getW() != oW || stone.getL() != oL || !stone.getSize().equals(oN)) {
		 				try {Loading.saveProject(1);}
			    		catch (IOException e1) {e1.printStackTrace();}		 			
			 			System.out.println("saved feature enter button");
		 			} 			
		 			
		 			window.close();	    		 
		    		 
			     } 
		    }
		});
		
		window.getIcons().add(Display.icon);
		window.setScene(scene);
		window.setResizable(false);
		window.showAndWait();
		
	}
}


