package dataEntry;

import program.Display;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChangeValue {

    static TextField textField;
    static int returnInt = -1;
    static String returnString = "";

    private static void addTextLimiter(final TextField tf, final int maxLength) {
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

    private static Stage setWindow(int width) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);

        window.setWidth(115);
        window.setHeight(80);
        window.setResizable(false);
        //Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        //window.setX(mouseLocation.getX());
        //window.setY(mouseLocation.getY()-60);
        return window;
    }

    public String getString() {
        return returnString;
    }

    public int getInt() {
        return returnInt;
    }

    //change an int
    public void change(int value, String title, int limit) {

        returnInt = value;

        Stage window = setWindow(130);
        if(!title.equals("Create Border"))window.setWidth(130);
        else window.setWidth(150);
        window.setTitle(title);

        textField = new TextField(Integer.toString(value));
        addTextLimiter(textField, limit);
        numbersOnly(textField);

        HBox layout = new HBox(5);
        layout.getChildren().addAll(textField, Display.confirmButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(0,0,0,10));
        //HBox.setHgrow(textField, Priority.ALWAYS);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Display.class.getResource("customStyle.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    if(!textField.getText().isEmpty())returnInt = Integer.parseInt(textField.getText());
                    if(!title.equals("Create Border"))Display.clear();
                    window.close();
                }
            }
        });

        Display.confirmButton.setOnAction(e -> {
            if(!textField.getText().isEmpty())returnInt = Integer.parseInt(textField.getText());
            if(!title.equals("Create Border"))Display.clear();
            window.close();
        });

        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                returnInt = -9999;
            }
        });

        window.setScene(scene);
        window.showAndWait();
    }

    //change a string
    public void change(String value, String title) {

        returnString = value;

        Stage window = setWindow(300);
        window.setWidth(300);
        window.setTitle(title);

        textField = new TextField(value);

        HBox layout = new HBox(5);
        layout.getChildren().addAll(textField, Display.confirmButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(0,0,0,10));

        HBox.setHgrow(textField, Priority.ALWAYS);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Display.class.getResource("customStyle.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    returnString = textField.getText();
                    window.close();
                }
            }
        });

        Display.confirmButton.setOnAction(e -> {
            returnString = textField.getText();
            window.close();
        });

        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                returnString = "**canceled**";
            }
        });

        window.setScene(scene);
        window.showAndWait();
    }
}