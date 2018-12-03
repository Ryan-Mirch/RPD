package windows;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import program.Calculate;
import program.Display;

import java.util.ArrayList;
import java.util.Collections;

public class SettingsBox {
	
	static ChoiceBox<String> selectionMethod;
	static ChoiceBox<String> printRatioChoiceBox;
	static ChoiceBox<String> printSizeChoiceBox;
	static Canvas obstaclePicture;
	static final double PW = 900; //page width
	static final double PH = 700; // page height
	
	static int stoneId;
	
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
		window.setTitle("Settings");
		window.setWidth(PW);
		window.setHeight(PH);
		
		
		//Size Checkboxes		
		ArrayList<CheckBox> checkboxes = new ArrayList<>();
		CheckBox s12x12 = new CheckBox("12x12");checkboxes.add(s12x12);
		CheckBox s12x18 = new CheckBox("12x18");checkboxes.add(s12x18);
		CheckBox s12x24 = new CheckBox("12x24");checkboxes.add(s12x24);
		CheckBox s12x30 = new CheckBox("12x30");checkboxes.add(s12x30);
		CheckBox s12x36 = new CheckBox("12x36");checkboxes.add(s12x36);
		CheckBox s12x42 = new CheckBox("12x42");checkboxes.add(s12x42);
		CheckBox s12x48 = new CheckBox("12x48");checkboxes.add(s12x48);
		CheckBox s18x18 = new CheckBox("18x18");checkboxes.add(s18x18);
		CheckBox s18x24 = new CheckBox("18x24");checkboxes.add(s18x24);
		CheckBox s18x30 = new CheckBox("18x30");checkboxes.add(s18x30);
		CheckBox s18x36 = new CheckBox("18x36");checkboxes.add(s18x36);
		CheckBox s18x42 = new CheckBox("18x42");checkboxes.add(s18x42);
		CheckBox s18x48 = new CheckBox("18x48");checkboxes.add(s18x48);
		CheckBox s24x24 = new CheckBox("24x24");checkboxes.add(s24x24);
		CheckBox s24x30 = new CheckBox("24x30");checkboxes.add(s24x30);
		CheckBox s24x36 = new CheckBox("24x36");checkboxes.add(s24x36);
		CheckBox s24x42 = new CheckBox("24x42");checkboxes.add(s24x42);
		CheckBox s24x48 = new CheckBox("24x48");checkboxes.add(s24x48);
		CheckBox s30x30 = new CheckBox("30x30");checkboxes.add(s30x30);
		CheckBox s30x36 = new CheckBox("30x36");checkboxes.add(s30x36);
		CheckBox s30x42 = new CheckBox("30x42");checkboxes.add(s30x42);
		CheckBox s30x48 = new CheckBox("30x48");checkboxes.add(s30x48);
		CheckBox s36x36 = new CheckBox("36x36");checkboxes.add(s36x36);
		CheckBox s36x42 = new CheckBox("36x42");checkboxes.add(s36x42);
		CheckBox s36x48 = new CheckBox("36x48");checkboxes.add(s36x48);
		CheckBox s42x42 = new CheckBox("42x42");checkboxes.add(s42x42);
		CheckBox s42x48 = new CheckBox("42x48");checkboxes.add(s42x48);
		CheckBox s48x48 = new CheckBox("48x48");checkboxes.add(s48x48);
		
		//General Settings
		CheckBox AdjStonesCheckBox = new CheckBox("Allow identical adjacent stones");
		AdjStonesCheckBox.setSelected(Display.adjStonesBoolean);
		
		CheckBox Allow4CornersCheckBox = new CheckBox("Allow 4 corners to touch");
		Allow4CornersCheckBox.setSelected(Display.allow4CornersBoolean);
		
		CheckBox forceVCheckBox = new CheckBox("Force vertical orientation");
		forceVCheckBox.setSelected(Display.forceVBoolean);
		
		CheckBox forceHCheckBox = new CheckBox("Force horizontal orientation");
		forceHCheckBox.setSelected(Display.forceHBoolean);
		
		CheckBox preventVJointCheckBox = new CheckBox("Prevent any vertical joint from forming");
		preventVJointCheckBox.setSelected(Display.preventVJoint);
		
		CheckBox borderOverlapCheckBox = new CheckBox("Allow stones to overlap border");
		borderOverlapCheckBox.setSelected(Display.allowBorderOverlap);
		
		CheckBox fromLeftCheckBox = new CheckBox("Draw stones from left to right");
		fromLeftCheckBox.setSelected(Display.drawFromLeft);
		
		CheckBox fromTopCheckBox = new CheckBox("Draw stones from top to bottom");
		fromTopCheckBox.setSelected(Display.drawFromTop);
		
		CheckBox sameWidthInRowCheckBox = new CheckBox("Force identical width in same row");
		sameWidthInRowCheckBox.setSelected(Display.sameWidthInRow);
		
		
		//Display Settings
		CheckBox numberLabelCheckBox = new CheckBox("Label stones with a number");
		numberLabelCheckBox.setSelected(Display.numberLabelBoolean);
		
		CheckBox sizeLabelCheckBox = new CheckBox("Label stones based on their size");
		sizeLabelCheckBox.setSelected(Display.sizeLabelBoolean);
		
		CheckBox displayInfoCheckBox = new CheckBox("Show the title block");
		displayInfoCheckBox.setSelected(Display.displayInfoBoolean);
		
		CheckBox displayQuantitiesCheckBox = new CheckBox("Show the quantities/sqft/tonnage block");
		displayQuantitiesCheckBox.setSelected(Display.displayQuantitiesBoolean);
		
		ArrayList<Label> labels = new ArrayList<>();
		Label l12x12 = new Label("12x12");labels.add(l12x12);
		Label l12x18 = new Label("12x18");labels.add(l12x18);
		Label l12x24 = new Label("12x24");labels.add(l12x24);
		Label l12x30 = new Label("12x30");labels.add(l12x30);
		Label l12x36 = new Label("12x36");labels.add(l12x36);
		Label l12x42 = new Label("12x42");labels.add(l12x42);
		Label l12x48 = new Label("12x48");labels.add(l12x48);
		Label l18x18 = new Label("18x18");labels.add(l18x18);
		Label l18x24 = new Label("18x24");labels.add(l18x24);
		Label l18x30 = new Label("18x30");labels.add(l18x30);
		Label l18x36 = new Label("18x36");labels.add(l18x36);
		Label l18x42 = new Label("18x42");labels.add(l18x42);
		Label l18x48 = new Label("18x48");labels.add(l18x48);
		Label l24x24 = new Label("24x24");labels.add(l24x24);
		Label l24x30 = new Label("24x30");labels.add(l24x30);
		Label l24x36 = new Label("24x36");labels.add(l24x36);
		Label l24x42 = new Label("24x42");labels.add(l24x42);
		Label l24x48 = new Label("24x48");labels.add(l24x48);
		Label l30x30 = new Label("30x30");labels.add(l30x30);
		Label l30x36 = new Label("30x36");labels.add(l30x36);
		Label l30x42 = new Label("30x42");labels.add(l30x42);
		Label l30x48 = new Label("30x48");labels.add(l30x48);
		Label l36x36 = new Label("36x36");labels.add(l36x36);
		Label l36x42 = new Label("36x42");labels.add(l36x42);
		Label l36x48 = new Label("36x48");labels.add(l36x48);
		Label l42x42 = new Label("42x42");labels.add(l42x42);
		Label l42x48 = new Label("42x48");labels.add(l42x48);
		Label l48x48 = new Label("48x48");labels.add(l48x48);
		
		Label checkBoxLabel = new Label("CheckBoxes, used in Balanced and Random");
		Label quantitiesLabel = new Label("Stone Quantities, used in Quantities");
		quantitiesLabel.setPadding(new Insets(50,0,0,0));
		
		for(int i = 0; i<Display.sizesSelected.length; i++) {
			if(Display.sizesSelected[i])checkboxes.get(i).setSelected(true);
		}
		
		//textFields
		ArrayList<TextField> textFields = new ArrayList<>();
		for(int i = 0; i<Display.sizesSelected.length; i++) {
			TextField newTF = new TextField(Integer.toString(Display.quantitiesSelected[i]));
			newTF.setMaxSize(50, 20); 
			newTF.setMinSize(50, 20);
			numbersOnly(newTF);
			textFields.add(newTF);
		}
		
		//Buttons
		ArrayList<Button> presetButtons = new ArrayList<>();
		Button TiledPreset = new Button("Tiled");presetButtons.add(TiledPreset);
		Button RunningBondFLFWPreset = new Button("Running Bond, Fixed Length, Fixed Width");presetButtons.add(RunningBondFLFWPreset);
		Button RunningBondRLFWPreset = new Button("Running Bond, Random Length, Fixed Width");presetButtons.add(RunningBondRLFWPreset);
		Button RunningBondRLRWPreset = new Button("Running Bond, Random Length, Random Width");presetButtons.add(RunningBondRLRWPreset);
		Button RandomRectangularPreset = new Button("Random Rectangular");presetButtons.add(RandomRectangularPreset);
		
		for(Button b: presetButtons) {
			b.setMinWidth(300);
			b.setMinHeight(30);
		}
		
		Label PresetsLabel = new Label("Pressing one of the buttons below will change the settings in a way that will produce the pattern described.\n\n");
		PresetsLabel.setWrapText(true);
		PresetsLabel.setPadding(new Insets(20,80,20,80));
		
		TiledPreset.setOnAction(e -> {
			for(CheckBox c: checkboxes) {
				c.setSelected(false);
			}			
			checkboxes.get(9).setSelected(true);
			
			selectionMethod.setValue("Random");
			
			AdjStonesCheckBox.setSelected(true);			
			Allow4CornersCheckBox.setSelected(true);			
			forceVCheckBox.setSelected(true);
			forceHCheckBox.setSelected(false);		
			preventVJointCheckBox.setSelected(false);
			borderOverlapCheckBox.setSelected(true);
			fromLeftCheckBox.setSelected(false);
			fromTopCheckBox.setSelected(true);
			sameWidthInRowCheckBox.setSelected(false);
			
			AlertBox.display("Settings Changed To Tiled", "Settings have been changed in a way to produce a tiled pattern with 12x18 sized stone. ", 300, 250);
		});
		
		RandomRectangularPreset.setOnAction(e -> {
			for(CheckBox c: checkboxes) {
				c.setSelected(true);
			}			
			checkboxes.get(25).setSelected(false);
			checkboxes.get(26).setSelected(false);
			checkboxes.get(27).setSelected(false);
			
			selectionMethod.setValue("Random");
			
			AdjStonesCheckBox.setSelected(false);			
			Allow4CornersCheckBox.setSelected(false);			
			forceVCheckBox.setSelected(false);
			forceHCheckBox.setSelected(false);		
			preventVJointCheckBox.setSelected(false);
			borderOverlapCheckBox.setSelected(false);
			fromLeftCheckBox.setSelected(false);
			fromTopCheckBox.setSelected(false);
			sameWidthInRowCheckBox.setSelected(false);
			
			AlertBox.display("Settings Changed To Random Rectangular", "Settings have been changed in a way to produce a random rectangular pattern. ", 300, 250);
		});
		
		RunningBondFLFWPreset.setOnAction(e -> {
			for(CheckBox c: checkboxes) {
				c.setSelected(false);
			}			
			checkboxes.get(0).setSelected(true);
			checkboxes.get(2).setSelected(true);
			
			selectionMethod.setValue("Random");
			
			AdjStonesCheckBox.setSelected(true);			
			Allow4CornersCheckBox.setSelected(false);			
			forceVCheckBox.setSelected(false);
			forceHCheckBox.setSelected(true);		
			preventVJointCheckBox.setSelected(true);
			borderOverlapCheckBox.setSelected(false);
			fromLeftCheckBox.setSelected(true);
			fromTopCheckBox.setSelected(false);
			sameWidthInRowCheckBox.setSelected(false);
			
			AlertBox.display("Settings Changed To Running Bond", "Settings have been changed in a way to produce a running bond pattern with stones of a fixed width and length. ", 300, 250);
		});
		
		RunningBondRLFWPreset.setOnAction(e -> {
			for(CheckBox c: checkboxes) {
				c.setSelected(false);
			}			
			checkboxes.get(0).setSelected(true);
			checkboxes.get(1).setSelected(true);
			checkboxes.get(2).setSelected(true);
			checkboxes.get(3).setSelected(true);
			checkboxes.get(4).setSelected(true);
			checkboxes.get(5).setSelected(true);
			checkboxes.get(6).setSelected(true);
			
			selectionMethod.setValue("Random");
			
			AdjStonesCheckBox.setSelected(true);			
			Allow4CornersCheckBox.setSelected(false);			
			forceVCheckBox.setSelected(false);
			forceHCheckBox.setSelected(true);		
			preventVJointCheckBox.setSelected(true);
			borderOverlapCheckBox.setSelected(false);
			fromLeftCheckBox.setSelected(false);
			fromTopCheckBox.setSelected(true);
			sameWidthInRowCheckBox.setSelected(false);
			
			AlertBox.display("Settings Changed To Running Bond", "Settings have been changed in a way to produce a running bond pattern with stones of a fixed width and random length. ", 300, 250);
		});
		
		RunningBondRLRWPreset.setOnAction(e -> {
			for(CheckBox c: checkboxes) {
				c.setSelected(true);
			}			
			checkboxes.get(25).setSelected(false);
			checkboxes.get(26).setSelected(false);
			checkboxes.get(27).setSelected(false);
			
			selectionMethod.setValue("Random");
			
			AdjStonesCheckBox.setSelected(true);			
			Allow4CornersCheckBox.setSelected(false);			
			forceVCheckBox.setSelected(false);
			forceHCheckBox.setSelected(true);		
			preventVJointCheckBox.setSelected(true);
			borderOverlapCheckBox.setSelected(false);
			fromLeftCheckBox.setSelected(false);
			fromTopCheckBox.setSelected(true);
			sameWidthInRowCheckBox.setSelected(true);
			
			AlertBox.display("Settings Changed To Running Bond", "Settings have been changed in a way to produce a running bond pattern with stones of a random width and random length. ", 300, 250);
		});
		
		
		
		selectionMethod = new ChoiceBox<>();
		selectionMethod.setMaxWidth(100);
		selectionMethod.setMinWidth(100);
		selectionMethod.getItems().addAll("Balanced", "Random", "Planking", "Quantities");
		if(Display.stoneSelectionMethod.equals("random"))selectionMethod.setValue("Random");
		if(Display.stoneSelectionMethod.equals("planking"))selectionMethod.setValue("Planking");
		if(Display.stoneSelectionMethod.equals("balanced"))selectionMethod.setValue("Balanced");
		if(Display.stoneSelectionMethod.equals("quantities"))selectionMethod.setValue("Quantities");
		selectionMethod.setTooltip(new Tooltip("Stone Placement Method\n"
				+"\n - Balanced: an equal amount of all stones are selected"
				+"\n - Random: stones are selected at random"
				+"\n - Planking: different stone sizes are used, and are selected randomly"
				+"\n - Quantities: The amount of each stone placed is based on the quantities you input"));
		
		printRatioChoiceBox = new ChoiceBox<>();
		printRatioChoiceBox.getItems().addAll("8.5 x 11", "8.5 x 14", "11 x 17");
		if(Display.printRatio == 1)printRatioChoiceBox.setValue("8.5 x 11");
		if(Display.printRatio == 2)printRatioChoiceBox.setValue("8.5 x 14");
		if(Display.printRatio == 3)printRatioChoiceBox.setValue("11 x 17");
		printRatioChoiceBox.setTooltip(new Tooltip("Picture Ratio \n - The height to width ratio of the picture that will be created"));
		
		printSizeChoiceBox = new ChoiceBox<>();
		printSizeChoiceBox.getItems().addAll("Small", "Medium", "Large", "Huge");
		if(Display.printSize == 1)printSizeChoiceBox.setValue("Small");
		if(Display.printSize == 2)printSizeChoiceBox.setValue("Medium");
		if(Display.printSize == 3)printSizeChoiceBox.setValue("Large");
		if(Display.printSize == 4)printSizeChoiceBox.setValue("Huge");
		printSizeChoiceBox.setTooltip(new Tooltip("Picture Size \n - How many pixels in the picture that will be created"));
		
		
			
		Button confirmButton = new Button("Confirm");
		Button checkAllButton = new Button("Uncheck All");
		
		checkAllButton.setOnAction(e -> {
			if(checkAllButton.getText().equals("Uncheck All")) {
				for(CheckBox c: checkboxes) {
					c.setSelected(false);
				}
				checkAllButton.setText("Check All");
			}
			
			else if(checkAllButton.getText().equals("Check All")) {
				for(CheckBox c: checkboxes) {
					c.setSelected(true);
				}
				checkAllButton.setText("Uncheck All");
			}
			
		});
				
		confirmButton.setOnAction(e -> {		
			
			Display.adjStonesBoolean = AdjStonesCheckBox.isSelected();			
			Display.allow4CornersBoolean = Allow4CornersCheckBox.isSelected();			
			Display.forceVBoolean = forceVCheckBox.isSelected();
			Display.forceHBoolean = forceHCheckBox.isSelected();		
			Display.preventVJoint = preventVJointCheckBox.isSelected();
			Display.allowBorderOverlap = borderOverlapCheckBox.isSelected();
			Display.drawFromLeft = fromLeftCheckBox.isSelected();
			Display.drawFromTop = fromTopCheckBox.isSelected();
			Display.sameWidthInRow = sameWidthInRowCheckBox.isSelected();
			
			Display.numberLabelBoolean = numberLabelCheckBox.isSelected();			
			Display.sizeLabelBoolean = sizeLabelCheckBox.isSelected();
			Display.displayInfoBoolean = displayInfoCheckBox.isSelected();
			Display.displayQuantitiesBoolean = displayQuantitiesCheckBox.isSelected();
			

			if(Display.numberLabelBoolean && Display.sizeLabelBoolean) {
				Display.numberLabelBoolean = false;
			}
			
			if(Display.forceVBoolean && Display.forceHBoolean) {
				Display.forceVBoolean = false;
				Display.forceHBoolean = false;
			}
			
			if(Calculate.planking) {
				Display.productNames.clear();
				Collections.addAll(Display.productNames, 
				"5x12", "5x18", "5x24", "5x30", "5x36", "5x42", "5x48",
				"10x12", "10x18", "10x24", "10x30", "10x36", "10x42", "10x48",
				"15x12", "15x18", "15x24", "15x30", "15x36", "15x42", "15x48"
				);	
			}
			else {
				Display.productNames.clear();
				Collections.addAll(Display.productNames, 
						"12x12", "12x18", "12x24", "12x30", "12x36", "12x42", "12x48",
						"18x18", "18x24", "18x30", "18x36", "18x42", "18x48",
						"24x24", "24x30", "24x36", "24x42", "24x48",
						"30x30", "30x36", "30x42", "30x48",
						"36x36", "36x42", "36x48",
						"42x42", "42x48",
						"48x48");
			}
				
			if(printRatioChoiceBox.getValue() == ("8.5 x 11"))Display.printRatio = 1;
			if(printRatioChoiceBox.getValue() == ("8.5 x 14"))Display.printRatio = 2;
			if(printRatioChoiceBox.getValue() == ("11 x 17"))Display.printRatio = 3;
			
			if(printSizeChoiceBox.getValue() == ("Small"))Display.printSize = 1;
			if(printSizeChoiceBox.getValue() == ("Medium"))Display.printSize = 2;
			if(printSizeChoiceBox.getValue() == ("Large"))Display.printSize = 3;
			if(printSizeChoiceBox.getValue() == ("Huge"))Display.printSize = 4;
			
			if(selectionMethod.getValue() == "Random")Display.stoneSelectionMethod = "random";
			if(selectionMethod.getValue() == "Balanced")Display.stoneSelectionMethod = "balanced";
			if(selectionMethod.getValue() == "Quantities")Display.stoneSelectionMethod = "quantities";
			
			if(selectionMethod.getValue() == "Planking") {
				Display.stoneSelectionMethod = "planking";
				Display.forceVBoolean = false;
				Calculate.planking = true;
				
			}
			else Calculate.planking = false;
			
			for(int i = 0; i<Display.sizesSelected.length; i++) {
				Display.sizesSelected[i] = checkboxes.get(i).isSelected();
				if(!textFields.get(i).getText().equals("")) {
					Display.quantitiesSelected[i] = Integer.parseInt(textFields.get(i).getText());
				}
				else Display.quantitiesSelected[i] = 0;
			}
			
			Calculate.updateStonesAvailable();
			Display.updateAddStoneMB();	
			
			window.close();
			
		});
		
		
		Button cancelButton = new Button("Cancel");		
		cancelButton.setOnAction(e -> window.close());
		
		HBox bottomLayout = new HBox(40);
		bottomLayout.getChildren().addAll(confirmButton, cancelButton);
		bottomLayout.setAlignment(Pos.CENTER);
		bottomLayout.setPadding(new Insets(20,20,10,20));
		
		
		//////////////////////////////////////CheckBox Layout/////////////////////////////		
		
		int boxSpacing = 40;
		
		HBox w12 = new HBox(boxSpacing);
		w12.setAlignment(Pos.BOTTOM_RIGHT);
		w12.getChildren().addAll(s12x12, s12x18, s12x24, s12x30, s12x36, s12x42, s12x48);

		HBox w18 = new HBox(boxSpacing);
		w18.setAlignment(Pos.BOTTOM_RIGHT);
		w18.getChildren().addAll(s18x18, s18x24, s18x30, s18x36, s18x42, s18x48);
		
		HBox w24 = new HBox(boxSpacing);
		w24.setAlignment(Pos.BOTTOM_RIGHT);
		w24.getChildren().addAll(s24x24, s24x30, s24x36, s24x42, s24x48);
		
		HBox w30 = new HBox(boxSpacing);
		w30.setAlignment(Pos.BOTTOM_RIGHT);
		w30.getChildren().addAll(s30x30, s30x36, s30x42, s30x48);
		
		HBox w36 = new HBox(boxSpacing);
		w36.setAlignment(Pos.BOTTOM_RIGHT);
		w36.getChildren().addAll(s36x36, s36x42, s36x48);
		
		HBox w42 = new HBox(boxSpacing);
		w42.setAlignment(Pos.BOTTOM_RIGHT);
		w42.getChildren().addAll(s42x42, s42x48);
		
		HBox w48 = new HBox(boxSpacing);
		w48.setAlignment(Pos.BOTTOM_RIGHT);
		w48.getChildren().addAll(s48x48);
		
		
		VBox checkBoxLayout = new VBox(20);
		checkBoxLayout.getChildren().addAll(checkBoxLabel, checkAllButton, w12, w18, w24, w30, w36, w42, w48);
		checkBoxLayout.setAlignment(Pos.CENTER);
		checkBoxLayout.setPadding(new Insets(20,20,20,20));
		
		////////////////////////////TextField layout////////////////////////////////////
		ArrayList<VBox> VBoxes = new ArrayList<>();
		
		for(int i = 0; i < Display.sizesSelected.length; i++) {
			VBoxes.add(new VBox(0));
		}
		
		for(int i = 0; i < Display.sizesSelected.length; i++) {
			VBoxes.get(i).getChildren().addAll(labels.get(i), textFields.get(i));
			VBoxes.get(i).setAlignment(Pos.CENTER);
		}
		
		HBox tfw12 = new HBox(boxSpacing);
		tfw12.getChildren().addAll(VBoxes.get(0),VBoxes.get(1),VBoxes.get(2),VBoxes.get(3),VBoxes.get(4),VBoxes.get(5),VBoxes.get(6));
		tfw12.setAlignment(Pos.BOTTOM_RIGHT);	
		
		HBox tfw18 = new HBox(boxSpacing);
		tfw18.getChildren().addAll(VBoxes.get(7),VBoxes.get(8),VBoxes.get(9),VBoxes.get(10),VBoxes.get(11),VBoxes.get(12));
		tfw18.setAlignment(Pos.BOTTOM_RIGHT);
		
		HBox tfw24 = new HBox(boxSpacing);
		tfw24.getChildren().addAll(VBoxes.get(13),VBoxes.get(14),VBoxes.get(15),VBoxes.get(16),VBoxes.get(17));
		tfw24.setAlignment(Pos.BOTTOM_RIGHT);
		
		HBox tfw30 = new HBox(boxSpacing);
		tfw30.getChildren().addAll(VBoxes.get(18),VBoxes.get(19),VBoxes.get(20),VBoxes.get(21));
		tfw30.setAlignment(Pos.BOTTOM_RIGHT);
		
		HBox tfw36 = new HBox(boxSpacing);
		tfw36.getChildren().addAll(VBoxes.get(22),VBoxes.get(23),VBoxes.get(24));
		tfw36.setAlignment(Pos.BOTTOM_RIGHT);
		
		HBox tfw42 = new HBox(boxSpacing);
		tfw42.getChildren().addAll(VBoxes.get(25),VBoxes.get(26));
		tfw42.setAlignment(Pos.BOTTOM_RIGHT);
		
		HBox tfw48 = new HBox(boxSpacing);
		tfw48.getChildren().addAll(VBoxes.get(27));
		tfw48.setAlignment(Pos.BOTTOM_RIGHT);
		
		VBox textFieldLayout = new VBox(20);
		textFieldLayout.getChildren().addAll(quantitiesLabel, tfw12, tfw18, tfw24, tfw30, tfw36, tfw42, tfw48);
		textFieldLayout.setAlignment(Pos.CENTER);
		textFieldLayout.setPadding(new Insets(20,60,20,60));
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
	
		
		VBox optionsCol1 = new VBox(10);
		optionsCol1.getChildren().addAll(	AdjStonesCheckBox, 
											Allow4CornersCheckBox, 
											forceVCheckBox, forceHCheckBox, 
											numberLabelCheckBox,
											preventVJointCheckBox,
											borderOverlapCheckBox,
											fromLeftCheckBox,
											fromTopCheckBox,
											sameWidthInRowCheckBox);
		
		optionsCol1.setAlignment(Pos.CENTER_LEFT);
		
		VBox optionsCol2 = new VBox(20);
		optionsCol2.getChildren().addAll( selectionMethod, printRatioChoiceBox, printSizeChoiceBox);
		optionsCol2.setAlignment(Pos.CENTER_RIGHT);
		
		VBox displayOptions = new VBox(20);
		displayOptions.setAlignment(Pos.CENTER_LEFT);
		displayOptions.setPadding(new Insets(50,50,50,150));
		displayOptions.getChildren().addAll(numberLabelCheckBox, 
											sizeLabelCheckBox,
											displayInfoCheckBox,
											displayQuantitiesCheckBox);
		
		
		
		VBox presetsOptions = new VBox(20);
		presetsOptions.setAlignment(Pos.CENTER);
		presetsOptions.setPadding(new Insets(50,50,70,50));
		presetsOptions.getChildren().addAll(PresetsLabel,
											RandomRectangularPreset,
											TiledPreset,
											RunningBondFLFWPreset,
											RunningBondRLFWPreset,
											RunningBondRLRWPreset);
		
		HBox options = new HBox(30);
		options.getChildren().addAll( optionsCol1, optionsCol2);
		options.setAlignment(Pos.CENTER);
		
		Tab presetsTab = new Tab("Presets");
		presetsTab.setContent(presetsOptions);
		presetsTab.setClosable(false);
		
		Tab optionsTab = new Tab("General");
		optionsTab.setContent(options);
		optionsTab.setClosable(false);
		
		Tab displayTab = new Tab("Display");
		displayTab.setContent(displayOptions);
		displayTab.setClosable(false);

		Tab checkBoxTab = new Tab("CheckBoxes");
		checkBoxTab.setContent(checkBoxLayout);
		checkBoxTab.setClosable(false);
		
		Tab quantitiesTab = new Tab("Quantities");
		quantitiesTab.setContent(textFieldLayout);
		quantitiesTab.setClosable(false);
		
		TabPane generalLayout = new TabPane();
		generalLayout.getTabs().addAll(presetsTab, optionsTab, displayTab, checkBoxTab, quantitiesTab );
		generalLayout.setPadding(new Insets(0,0,0,0));
		
		
		
		BorderPane mainLayout = new BorderPane();
		
		mainLayout.setBottom(bottomLayout);
		mainLayout.setCenter(generalLayout);
		
		Scene scene = new Scene(mainLayout);
		scene.getStylesheets().add(Display.class.getResource("/customStyle.css").toExternalForm());
		window.getIcons().add(Display.icon);
		window.setScene(scene);
		window.setResizable(false);
		window.showAndWait();
		
	}
}

