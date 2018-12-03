package windows;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;

import dataEntry.ChangeValue;
import program.Calculate;
import program.Display;
import program.Loading;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Projects {
	
	static Canvas obstaclePicture;
	static final double PW = 450; //page width
	static final double PH = 540; // page height
	static ListView<String> listView; //list of projects
	static ChangeValue cv = new ChangeValue();
	public static String projectsFolder;
	public static String undoFolder;
	
	private static void updateListView() {
		
		
		String path = System.getenv("APPDATA") + "//RPM";
		projectsFolder = path + "//Projects";
		undoFolder = path + "//Undo";
		new File(path).mkdirs();
		new File(projectsFolder).mkdirs();
		new File(undoFolder).mkdirs();
		
		listView.getItems().clear();	
		
		File folder = new File(projectsFolder);
		File[] listOfFiles = folder.listFiles();	
			
		if(listOfFiles.length>0) {
			for(int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].isFile())listView.getItems().add(listOfFiles[i].getName().replaceFirst("[.][^.]+$", ""));		
			}
		}
		
	}
	
		
	public static void display() {
		
		Stage window = new Stage();
	
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Projects");
		window.setWidth(PW);
		window.setHeight(PH);
				
		Button createNewButton = new Button("Create New");
		Button copyButton = new Button("Copy");
		Button openButton = new Button("Open");
		Button renameButton = new Button("Rename");
		Button deleteButton = new Button("Delete");
		
		listView = new ListView<>();		
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		updateListView();
		
		createNewButton.setOnAction(e -> {
			cv.change("Enter Project Name", "Create New Project");
			if(!cv.getString().equals("**canceled**")) {
				final Formatter x;		
				try {
					x = new Formatter(projectsFolder + "//" + cv.getString() + ".txt");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				updateListView();
			}			
		});
		
		copyButton.setOnAction(e -> {	
			if(!listView.getSelectionModel().isEmpty()) {
				cv.change("Enter Projects Name", "Copy Project");
				if(!cv.getString().equals("**canceled**")) {
					String fileName = listView.getSelectionModel().getSelectedItem() + ".txt";
					File source = new File(projectsFolder + "//" + fileName);
					File dest = new File(projectsFolder + "//" + cv.getString() + ".txt");
					
					if(dest.exists())
						try {
							throw new IOException("file exists");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
					try {
						Files.copy(source.toPath(), dest.toPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					updateListView();
				}				
			}	
			else AlertBox.display("No Project Selected", "Please select a project to copy.", 300 , 200);
		});
		
		openButton.setOnAction(e -> {
			if(!listView.getSelectionModel().isEmpty()) {
				try {
					Loading.openProject(new File(projectsFolder + "//" + listView.getSelectionModel().getSelectedItem()+ ".txt"), 0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				File undoFolderFile = new File(undoFolder);
				for(File file: undoFolderFile.listFiles()) 
				    file.delete();
				
				try {Loading.saveProject(1);}
				catch (IOException e1) {e1.printStackTrace();}
				System.out.println("saved project opened");
				
				window.close();
			}
			else AlertBox.display("No Project Selected", "Please select a project to open.", 300 , 200);

			
			
		});
		
		renameButton.setOnAction(e -> {	
			if(!listView.getSelectionModel().isEmpty()) {
				cv.change("Enter Project Name", "Rename Project");
				if(!cv.getString().equals("**canceled**")) {
					String fileName = listView.getSelectionModel().getSelectedItem()+ ".txt";
					File file = new File(projectsFolder + "//" + fileName);
					File fileNew = new File(projectsFolder + "//" + cv.getString() + ".txt");
					
					if(fileNew.exists())
						try {
							throw new IOException("file exists");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
					file.renameTo(fileNew);
					updateListView();
				}
			}	
			else AlertBox.display("No Project Selected", "Please select a project to rename.", 300 , 200);
		});
		
		deleteButton.setOnAction(e -> {	
			if(!listView.getSelectionModel().isEmpty()) {
				String fileName = listView.getSelectionModel().getSelectedItem()+ ".txt";
				File file = new File(projectsFolder + "//" + fileName);
				
				if(Display.projectOpen == true) {
					if(file.getName().equals(Display.currentProject.getName())) {
						AlertBox.display("Cannot Delete Current Project", "You cannot delete the project \n that is currently open", 300 , 200);
					}
					else {
						file.delete();	
						updateListView();				
					}					
				}
				else if(Display.projectOpen == false) {
					file.delete();					
					updateListView();				
				}				
			}
			else AlertBox.display("No Project Selected", "Please select a project to delete.", 300 , 200);
				
			
		});
			
		
		
		//////////////////////////////////////General Layout/////////////////////////////		
			
			
		
		HBox row1 = new HBox(20);
		row1.getChildren().addAll(openButton, createNewButton, copyButton, renameButton, deleteButton);
		row1.setAlignment(Pos.CENTER);
		row1.setPadding(new Insets(0,20,0,20));

		HBox row2 = new HBox(40);
		//row2.getChildren().addAll( openButton, renameButton, deleteButton);
		row2.setAlignment(Pos.CENTER);
		
		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(listView, row1);	
				
		
		BorderPane mainLayout = new BorderPane();
		
		mainLayout.setCenter(layout);
		
		Scene scene = new Scene(mainLayout);
		scene.getStylesheets().add(Display.class.getResource("/customStyle.css").toExternalForm());
		window.getIcons().add(Display.icon);
		window.setScene(scene);
		window.setResizable(false);
		window.showAndWait();
		
	}
	
	
}


