package program;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dataEntry.ChangeValue;
import dataEntry.EditableTextBox;
import windows.AlertBox;
import windows.Projects;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Display extends Application {

    private static final double MIN_SCALE = .1;
    private static final double MAX_SCALE = 10;
    private static final String PROGRAM_NAME = "RPD v1.3.0";

    private static final boolean TEST = true;
    private static final boolean SKIP_LOGIN = false;

    static double test1 = 0;
    static double test2 = 0;

    static Stage window;
    static Scene scene;
    static Canvas canvas = new Canvas();
    static Pane canvasPane = new Pane();

    static Button DrawPatternButton;
    static Button insertFeatureButton;
    static Button SaveButton;
    static Button SettingsButton;
    static Button centerButton;
    static Button ProjectsButton;
    static Button undoButton;
    static Button redoButton;
    static Button clearButton;
    static Button cancelButton;
    public static MenuButton addStoneMB;
    static Button addRandomButton;
    static Button helpButton;
    static Button createBorderButton;
    static Button informationButton;
    static Button saveQuantitiesButton;

    static ArrayList<Button> buttons = new ArrayList<>();

    public static Button confirmButton;

    static Stone stoneHovered = new Stone();		//current stone that mouse is hovering

    public static int xDim = 240;		//size of patio x dimension
    public static int yDim = 240;		//size of patio y dimension
    public static int sqinTotal = 0;	//current sqin of stones placed
    public static int startingStonesAmount;	//starting amount of stones when draw button is pressed
    public static int previousBorderWidth = 0; // what the default border width will be when the button is pressed

    static int undoAmount;		//current amount of stones to delete if clearRecent() is called
    static int backoff;
    static int checkpoint = 0;		//current amount of stones placed without an error occuring

    static int titleBlockWMul = 30;		// width of each textBox unit
    static int titleBlockHMul = 14;		// height of each textBox unit

    public static int printSize = 2; // 1: small, 2:medium, 3:large, 4:huge
    public static int printRatio = 1;	//1, 2 or 3.determines how big save to png picture is.

    static double cx;		//cursor x
    static double cy;		//cursor y
    static double cxReal;		//cursor x relative to scene
    static double cyReal;		//cursor y relative to scene
    static double scale = 2;		//multiplied to patio and stones to draw them bigger or smaller
    static double offsetX = 0;		//where origin X of patio is located on the canvas
    static double offsetY = 0;		//where origin Y of patio is located on the canvas
    static double previousX;		//mouse X coordinate before mouse is dragged
    static double previousY;		//mouse Y coordinate before mouse is dragged
    static double xDimLocationX;
    static double xDimLocationY;
    static double yDimLocationX;
    static double yDimLocationY;
    static double sideToCursorX;
    static double sideToCursorY;

    public static boolean[] sizesSelected = new boolean[28];
    static boolean fill;
    static boolean xDimHover;
    static boolean yDimHover;

    static boolean draggingDone = true;
    static boolean styleAdded = false;

    public static boolean justSaved = false;

    //general settings
    public static boolean adjStonesBoolean = false;
    public static boolean allow4CornersBoolean = false;
    public static boolean forceVBoolean = false;
    public static boolean forceHBoolean = false;
    public static boolean preventVJoint = false;
    public static boolean allowBorderOverlap = false;
    public static boolean drawFromLeft = false;
    public static boolean drawFromTop = false;
    public static boolean sameWidthInRow = false;

    //display settings
    public static boolean numberLabelBoolean = false;
    public static boolean sizeLabelBoolean = false;
    public static boolean displayInfoBoolean = true;
    public static boolean displayQuantitiesBoolean = true;

    public static int[] quantities = new int[28];
    public static int[] quantitiesPlanking = new int[20];
    public static int[] quantitiesSelected = new int[28];

    public static ArrayList<Stone> stones = new ArrayList<Stone>();
    public static ArrayList<Stone> obstacles = new ArrayList<Stone>();
    public static ArrayList<String> productNames;
    public static ArrayList<Stone> stonesToRemove = new ArrayList<>();

    static ArrayList<Stone> stonesAvailable = new ArrayList<Stone>();
    public static ArrayList<Stone> allStones = new ArrayList<Stone>();
    public static ArrayList<EditableTextBox> textBoxes = new ArrayList<EditableTextBox>();


    static ListView<String> sqftList;
    static ListView<String> sqftLabel;

    public static String stoneSelectionMethod = "random";
    public static String oldStoneSelectionMethod = "random";
    public static String drawStyle = "default";

    public static String username;
    public static String password;

    static HBox topLayout = new HBox(15);

    static ChangeValue cv = new ChangeValue();

    static ContextMenu stoneRightClick;
    static MenuItem remove;
    static MenuItem rotate;
    static MenuItem copy;

    public static File currentProject;
    public static boolean projectOpen;

    public static Label currentProjectLabel;

    public static Image icon;

    static void setScale(double printW, double printH) {
        double widthQuotient = printW / xDim;
        double heightQuotient = printH / yDim;

        if(widthQuotient + 1 < heightQuotient)
            scale = widthQuotient * .85;
        else
            scale = heightQuotient * .75;

    }

    public static void center() {
        offsetX = (canvas.getWidth()-xDim*scale)*.5;
        offsetY = -50 + (canvas.getHeight()-yDim*scale)*.5;
    }

    private static void drawDrawingStatus() {
        if(fill) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Courier New", 15));
            gc.fillText("Drawing...  Attempt " + Calculate.attempts + "/" + Calculate.maxAttempts, 20,20, 300);

        }

    }

    private static void drawDimensions(int x, int y, String style) {
        if(style.equals("fullscreenQuantities"))return;

        xDim = x;
        yDim = y;

        xDimLocationX = (xDim)*scale*.5 + 1 + offsetX;
        xDimLocationY = -18 + offsetY;

        yDimLocationX = offsetX - 70;
        yDimLocationY = yDim*scale*.5 + offsetY;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(1 + offsetX, 1 + offsetY, xDim*scale, yDim*scale);

        gc.setLineWidth(1);

        if(xDimHover) {
            gc.setStroke(Color.BLACK);
            gc.strokeRect(xDimLocationX-25, xDimLocationY-8, 50, 20); //xDim box
        }


        if(yDimHover) {
            gc.setStroke(Color.BLACK);
            gc.strokeRect(yDimLocationX+18, yDimLocationY-12, 50, 20); //yDim box
        }


        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Courier New", 12));
        gc.fillText(Integer.toString(xDim) + "\"", xDimLocationX -18, xDimLocationY + 6, 70); //xDim number
        gc.fillText(Integer.toString(yDim) + "\"", yDimLocationX + 27, yDimLocationY + 3, 70); //yDim number

        drawArrow(gc,yDimLocationX+52,yDimLocationY-20,yDimLocationX+52,offsetY+2);			//y dim arrows
        drawArrow(gc,yDimLocationX+52,yDimLocationY+20,yDimLocationX+52,yDim*scale +offsetY-2);

        drawArrow(gc,xDimLocationX-28, xDimLocationY,offsetX+2,xDimLocationY);			//x dim arrows
        drawArrow(gc,xDimLocationX+28,xDimLocationY,xDim*scale +offsetX-2, xDimLocationY);

        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(xDim*scale+offsetX, offsetY-5, xDim*scale+offsetX, offsetY - 30); 	//x right |
        gc.strokeLine(offsetX, offsetY-5, offsetX, offsetY - 30); 						//x left |
        gc.strokeLine(offsetX - 5,offsetY, offsetX - 30, offsetY); 												//y top _
        gc.strokeLine(offsetX - 5, yDim*scale + offsetY, offsetX - 30, yDim*scale + offsetY); 					//y bot _

    }

    private static void drawCanvas(String style) {
        int o = 0; //canvas border thickness
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0+o, 0+o, canvas.getWidth()-2*o, canvas.getHeight()-2*o);
    }

    private static void drawCanvasBorder() {
        int o = 1; //canvas border thickness
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(o);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0+o*.5, 0+o*.5, canvas.getWidth()-o, canvas.getHeight()-o);
    }

    static void drawInformation(String style) {

        double fsm = 0.05; //fullscreen multiplier

        double boxH = 30;

        double idW = 26;
        double nameW = 55;
        double amountW = 50;

        int fontSize = 14;

        int infoY = 1;

        if(style.equals("fullscreenQuantities")) {


            int uniqueStones = 0;
            for(Stone stone:allStones) {
                if(quantities[stone.getID()] > 0) uniqueStones++;
            }
            boxH = canvasPane.getHeight()/(uniqueStones+7) ;
            if(boxH > 86) boxH = 86;

            idW = idW*boxH*fsm;
            nameW = nameW*boxH*fsm;
            amountW = amountW*boxH*fsm;

            fontSize = (int) (boxH/1.5);

            infoY = (int) boxH;
        }





        double idX = (int) (canvas.getWidth()-idW-nameW-amountW);
        if(style.equals("fullscreenQuantities")) idX = (canvas.getWidth()/2)-((idW+nameW+amountW)/2);

        double nameX = (int) (idX+idW);
        double amountX = (int) (nameX+nameW);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        if(numberLabelBoolean) {
            drawBox(gc, Color.BLACK, Color.WHITE, idX, infoY, idW, boxH);
            drawText(gc, fontSize, "ID", idX +.1*idW, infoY+boxH/1.6, 100);
        }

        drawBox(gc, Color.BLACK, Color.WHITE, nameX, infoY, nameW, boxH);
        drawText(gc, fontSize, "Size", nameX +.1*nameW, infoY+boxH/1.6, 100);

        drawBox(gc, Color.BLACK, Color.WHITE, amountX, infoY, amountW, boxH);
        drawText(gc, fontSize, "Qty", amountX +.2*amountW, infoY+boxH/1.6, 100);

        infoY+=boxH;

        for(Stone stone:allStones) {
            if(quantities[stone.getID()] > 0) {
                if(numberLabelBoolean) {
                    drawBox(gc, Color.BLACK, Color.WHITE, idX, infoY, idW, boxH);
                    drawText(gc, fontSize, Integer.toString(stone.getID()+1), idX +.1*idW, infoY+boxH/1.6, 500);
                }


                drawBox(gc, Color.BLACK, Color.WHITE, nameX, infoY, nameW, boxH);
                drawText(gc, fontSize, stone.getSize(), nameX +.1*nameW, infoY+boxH/1.6, 500);

                drawBox(gc, Color.BLACK, Color.WHITE, amountX, infoY, amountW, boxH);
                if(!Calculate.planking)
                    drawText(gc, fontSize, Integer.toString(quantities[stone.getID()]), amountX +.2*amountW, infoY+boxH/1.6, 500);
                else drawText(gc, fontSize, Integer.toString(quantitiesPlanking[stone.getID()]), amountX +.2*amountW, infoY+boxH/1.6, 500);

                infoY+=boxH;
            }
        }

        infoY+=boxH;

        drawBox(gc, Color.BLACK, Color.WHITE, nameX, infoY, nameW, boxH);
        drawText(gc, fontSize, "sqft", nameX +.1*nameW, infoY+boxH/1.6, 500);

        drawBox(gc, Color.BLACK, Color.WHITE, amountX, infoY, amountW, boxH);
        drawText(gc, fontSize, Integer.toString(sqinTotal/144), amountX +.2*amountW, infoY+boxH/1.6, 500);

        infoY+=2*boxH;

        drawBox(gc, Color.BLACK, Color.WHITE, nameX, infoY, nameW, boxH);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Courier New", fontSize - 1));
        gc.fillText("Tons", nameX +.1*nameW, infoY+boxH/1.6, 500);

        float thickness = 0;

        for(EditableTextBox t: textBoxes) {
            if(t.getId() == 4) {
                thickness = Float.parseFloat(t.getValue());
            }
        }

        drawBox(gc, Color.BLACK, Color.WHITE, amountX, infoY, amountW, boxH);
        gc.setFill(Color.BLACK);
        gc.fillText(Double.toString(round((double)((sqinTotal*thickness*167)/1728)/2000, 2)),  amountX +.2*amountW - 4, infoY+boxH/1.6, 500);

        infoY+=boxH;
    }

    private static double round(double value, int places) {
        if(places<0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp/factor;
    }

    private static void drawTitleblock(String style) {
        if(style.equals("fullscreenQuantities"))return;

        GraphicsContext gc = canvas.getGraphicsContext2D();



        double oX = 1; // origin x
        double oY = canvas.getHeight() - 9*titleBlockHMul -1; // origin y


        for(EditableTextBox box: textBoxes) {
            double x = oX + box.getX()*titleBlockWMul;
            double y = oY + box.getY()*titleBlockHMul;
            double w = box.getW()*titleBlockWMul;
            double h = box.getH()*titleBlockHMul;
            int nameLength = box.getName().length();
            int valueLength = box.getValue().length();

            if(!box.getHover()) {
                drawBox(gc, Color.BLACK, Color.WHITE, x, y, w, h);
            }
            else{
                if(box.getName() == "Company") {
                    drawBox(gc, Color.BLACK, Color.LIGHTGREY, x, y, w, h);
                }
                else {
                    drawBox(gc, Color.BLACK, Color.WHITE, x, y, w, h-13);
                    drawBox(gc, Color.BLACK, Color.LIGHTGREY, x, y+13, w, h-14);
                }

            }

            if(box.getName() == "Company") {
                if(valueLength<20)drawText(gc, 2.5*titleBlockHMul, box.getValue(), x+.5*(w-(valueLength*1.4*titleBlockHMul)), y+2.1*titleBlockHMul, w-20);
                else drawText(gc, 2.5*titleBlockHMul, box.getValue(), x+10, y+2.1*titleBlockHMul, w-20);

            }

            else if(box.getName()=="Notes") {
                gc.setLineWidth(.5);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(x, y+13, x+w, y+13);
                gc.strokeLine(x+16, y+h-13, x+w*0.75, y+h-13);
                gc.strokeLine(x+w*0.8, y+h-13, x+w-16, y+h-13);

                drawText(gc, 10, box.getName(), x-nameLength*2.8+.5*w, y+9, w-6);

                int splitValue = (int) (w*.12);
                String text = box.getValue();

                List<String> ret = new ArrayList<String>((text.length() + splitValue - 1) / splitValue);

                for (int start = 0; start < text.length(); start += splitValue) {
                    ret.add(text.substring(start, Math.min(text.length(), start + splitValue)));
                }

                int max = ret.size();
                if(max>5)max=5;

                for(int i=0; i<max; i++) {
                    drawText(gc, 0.5*titleBlockWMul, ret.get(i), x+5, y+32+(i*titleBlockHMul), w-10);
                }

                drawText(gc, 9, "Customer Approval", x+w*0.3, canvas.getHeight()-6, 100);
                drawText(gc, 9, "Date", x+w*0.85, canvas.getHeight()-6, 100);
            }
            else {
                drawText(gc, 10, box.getName(), x-nameLength*2.8+.5*w, y+9, w-6);
                gc.setLineWidth(.5);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(x, y+13, x+w, y+13);
                if(valueLength<w/14.3)drawText(gc, 1.2*titleBlockHMul, box.getValue(), x+.5*(w-(valueLength*0.71*titleBlockHMul)), y+2.1*titleBlockHMul, w-10);
                else drawText(gc, 1.2*titleBlockHMul, box.getValue(), x+5, y+2.1*titleBlockHMul, w-10);
            }

        }
    }

    private static void drawBox(GraphicsContext gc, Color outer, Color inner, double x, double y, double w, double h) {
        gc.setLineWidth(2);
        gc.setStroke(outer);
        gc.strokeRect(x, y, w, h);

        gc.setFill(inner);
        gc.fillRect(x, y, w, h);
    }

    private static void drawText(GraphicsContext gc, double fontSize, String text, double x, double y, double w) {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Courier New", fontSize));
        gc.fillText(text, x, y, w);
    }

    private static void drawStones(String style) {
        if(style.equals("fullscreenQuantities"))return;


        for(Stone stone: stones) {

            GraphicsContext gc = canvas.getGraphicsContext2D();
            double x = stone.getX()*scale;
            double y = stone.getY()*scale;
            double w = stone.getW()*scale;
            double l = stone.getL()*scale;

            String stoneLabel;
            double fontSize;
            double xLoc;
            double xLocF = offsetX + x+2*scale;
            double sw; // size width
            double smw; // size max width
            double fmw; // feature max width



            if(w>l) {
                smw = .75* l;
            }
            else {
                smw = .75* w;
            }



            if(numberLabelBoolean && !sizeLabelBoolean) {
                stoneLabel = Integer.toString(stone.getID()+1);
                sw = stoneLabel.length();
                if(Calculate.planking)fontSize = 5*scale*.825;
                else fontSize = 12*scale*.825;
                xLoc =offsetX + x + (w/2)*0.4 - (sw/2)*0.2*scale;
            }
            else if(!numberLabelBoolean && sizeLabelBoolean){
                stoneLabel = stone.getSize();
                sw = stoneLabel.length();
                fontSize = .75*0.6*12*scale;
                xLoc =offsetX + x + (w/2)*0.4 - (sw/2)*0.2*scale;
            }
            else {
                stoneLabel = "";
                sw = stoneLabel.length();
                fontSize = .75*0.6*12*scale;
                xLoc =offsetX + x + (w/2)*0.4 - (sw/2)*0.2*scale;
            }



            if(!stone.getHover()) {
                gc.setFill(Color.BLACK);
                gc.fillRect(x+1 + offsetX, y+1 + offsetY, w, l);
                gc.setFill(stone.getColor());
                gc.fillRect(x+2 + offsetX, y+2 + offsetY, w-2, l-2);
                if(stone.getID()!= 100) {
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("Courier New", fontSize));
                    gc.fillText(stoneLabel, xLoc, y+(l/1.2) + offsetY, smw);
                }
                else {
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("Courier New", fontSize));
                    gc.fillText(stone.getSize(), xLocF, y+(l/1.1) + offsetY, w-(5*scale));
                    gc.fillText(stone.getW() + "\" x " + stone.getL() + "\"", xLocF, y+(l/2) + offsetY, w-(5*scale));
                }
            }
            if(stone.getHover()){
                if(stone.getID()!=100) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x+1 + offsetX, y+1 + offsetY, w, l);
                    gc.setFill(Color.WHITE);
                    gc.fillRect(x+2 + offsetX, y+2 + offsetY, w-2, l-2);
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("Courier New", fontSize));
                    gc.fillText(stoneLabel, xLoc, y+(l/1.2) + offsetY, smw);
                }
                else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x+1 + offsetX, y+1 + offsetY, w, l);
                    gc.setFill(Color.AZURE);
                    gc.fillRect(x+2 + offsetX, y+2 + offsetY, w-2, l-2);
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("Courier New", fontSize));
                    gc.fillText(stone.getSize(), xLocF, y+(l/1.1) + offsetY, w-(5*scale));
                    gc.fillText(stone.getW() + "\" x " + stone.getL() + "\"", xLocF, y+(l/2) + offsetY, w-(5*scale));
                }
            }
        }
    }

    public static void clear() {

        stones.clear();
        for(Stone stone: obstacles) {
            stones.add(stone);
        }
        Arrays.fill(quantities,0);
        Arrays.fill(quantitiesPlanking,0);
        sqinTotal = 0;
        undoAmount = 2;
        checkpoint = 0;
    }

    public static void clearRecent(boolean all) {
        int sa = stones.size(); //stone amount
        //if it gets past checkpoint, or if it undo's past 50% of the checkpoint, reset undo amount
        if(sa>checkpoint || sa <= checkpoint * .25) {
            checkpoint = sa;
            undoAmount = 5;
        }
        else undoAmount += 3;

        int undoMax = sa - startingStonesAmount;

        if(undoAmount>undoMax)undoAmount = undoMax;
        int amountToClear = undoAmount;
        if(all)amountToClear = undoMax + 1;
        for(int i = sa-1; i > sa - amountToClear; i--) {
            if(stones.get(i).getID() != 100) {
                stones.get(i).remove();
            }
        }
    }

    private static void demoCheck() {
        if(Display.username.equals("demo")) {
            xDim = 240;
            yDim = 240;
        }
    }


    public static void draw(String style) {
        demoCheck();
        drawCanvas(style);
        drawStones(style);
        drawDimensions(xDim, yDim, style);
        drawObstacleDistances(stoneHovered);
        if(displayQuantitiesBoolean)drawInformation(style);
        if(displayInfoBoolean)drawTitleblock(style);
        drawCanvasBorder();
        drawDrawingStatus();
    }

    public static void printStonesAvailable() {
        System.out.println("\nStones available: ");
        for(Stone stone: stonesAvailable) {
            System.out.printf(stone.getSize() + ", ");
        }
        System.out.println("\n");
    }

    public static void updateAddStoneMB() {
        addStoneMB.getItems().clear();
        for(Stone stone: stonesAvailable) {
            addStoneMB.getItems().add(new MenuItem(stone.getSize()));
        }


        addStoneMB.showingProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    if(!projectOpen) {
                        addStoneMB.hide();
                        AlertBox.display("No Project Open", "Please open a project", 300, 150);
                        Projects.display();
                    }
                }
            }
        });

        for(MenuItem x: addStoneMB.getItems()) {
            x.setOnAction(event -> {
                String choice = x.getText();
                System.out.println(choice);
                for(Stone s: stonesAvailable) {
                    if(choice.equals(s.getSize())){
                        Stone singleStone = new Stone(s);
                        singleStone.setX(-singleStone.getW()-20);
                        singleStone.setY((int)(yDim*.5 - singleStone.getW()));
                        stones.add(singleStone);

                        if(!Calculate.planking)quantities[singleStone.getID()]++;
                        if(Calculate.planking) {
                            Calculate.rotate(singleStone);
                            quantitiesPlanking[singleStone.getID()]++;
                        }
                        singleStone.addSQFT();

                        try {Loading.saveProject(1);}
                        catch (IOException e) {e.printStackTrace();}
                        System.out.println("saved add single stone");
                    }
                }
            });
        }
    }

    public static void printStonesPlaced() {
        System.out.println("\nStones placed: ");
        for(Stone stone: stones) {
            System.out.printf(stone.getSize() + ", ");
        }
        System.out.println("\n");
    }

    private static void drawObstacleDistances(Stone obstacle) {
        if(obstacle.getID()==100) {

            int[] testCoord = Calculate.coordinates(obstacle);
            double tx1 = testCoord[0]*scale;
            double ty1 = testCoord[1]*scale;
            double tx2 = testCoord[2]*scale;
            double ty2 = testCoord[3]*scale;
            double tx3 = testCoord[4]*scale;
            double ty3 = testCoord[5]*scale;
            double tx4 = testCoord[6]*scale;
            double ty4 = testCoord[7]*scale;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Courier New", 7*scale));

            double leftLeft = offsetX + 5;
            double leftMiddleLeft = offsetX + tx1*.5 - 11*scale;
            double leftMiddleRight = offsetX + tx1*.5 + 11*scale;
            double leftRight = offsetX - 3 + tx1;
            double leftY = offsetY + obstacle.getY()*scale + obstacle.getL()/2*scale;

            double rightRight = offsetX + xDim*scale - 5;
            double rightMiddleLeft = offsetX + tx2 + (xDim*scale - tx2)*.5 + 11*scale;
            double rightMiddleRight = offsetX + tx2 + (xDim*scale - tx2)*.5 - 11*scale;
            double rightLeft = offsetX + 3 + tx2;
            double rightY = offsetY + obstacle.getY()*scale + obstacle.getL()/2*scale;

            double topTop = offsetY + 5;
            double topMiddleTop= offsetY + obstacle.getY()*scale*.5 - 5*scale;
            double topMiddleBot = offsetY + obstacle.getY()*scale*.5 + 5*scale;
            double topBot = offsetY - 3 + obstacle.getY()*scale;
            double topX = offsetX + tx1 + obstacle.getW()/2*scale;

            double botBot = offsetY - 5 + yDim*scale;
            double botMiddleTop= offsetY + ty3 + (yDim*scale - ty3)*.5 - 11*scale;
            double botMiddleBot = offsetY + ty3 + (yDim*scale - ty3)*.5 + 11*scale;
            double botTop = offsetY + 3 + ty3;
            double botX = offsetX + tx1 + obstacle.getW()/2*scale;

            if(tx1>0) {
                drawArrow(gc,leftMiddleLeft,leftY,leftLeft,leftY);		//left side arrows
                drawArrow(gc,leftMiddleRight,leftY,leftRight,leftY);
                gc.fillText(Integer.toString(obstacle.getX()), obstacle.getX()*.5*scale + offsetX - 6*scale, leftY, 60);
            }

            if(tx2<xDim*scale) {
                drawArrow(gc,rightMiddleLeft,rightY,rightRight,rightY);		//right side arrows
                drawArrow(gc,rightMiddleRight,rightY,rightLeft,rightY);
                gc.fillText(Integer.toString((int)(xDim-tx2/scale)), offsetX + tx2 + (xDim*scale - tx2)*.5 - 4*scale, rightY, 60);
            }

            if(ty1>0) {
                drawArrow(gc,topX,topMiddleTop,topX,topTop);		//top side arrows
                drawArrow(gc,topX,topMiddleBot,topX,topBot);
                gc.fillText(Integer.toString(obstacle.getY()), topX - 4*scale, obstacle.getY()*.5*scale + offsetY + 2*scale, 60);
            }

            if(ty4<yDim*scale) {
                drawArrow(gc,botX,botMiddleTop,botX,botTop);		//bot side arrows
                drawArrow(gc,botX,botMiddleBot,botX,botBot);
                gc.fillText(Integer.toString((int)(yDim-ty3/scale)), botX - 4*scale, offsetY + ty3 + (yDim*scale - ty3)*.5 + 2*scale, 60);
            }

        }

    }

    private static void drawArrow(GraphicsContext gc, double node1X, double node1Y, double node2X, double node2Y) {
        double arrowAngle = Math.toRadians(45.0);
        double arrowLength = 3*scale;
        double dx = node1X - node2X;
        double dy = node1Y - node2Y;
        double angle = Math.atan2(dy, dx);
        double x1 = Math.cos(angle + arrowAngle) * arrowLength + node2X;
        double y1 = Math.sin(angle + arrowAngle) * arrowLength + node2Y;

        double x2 = Math.cos(angle - arrowAngle) * arrowLength + node2X;
        double y2 = Math.sin(angle - arrowAngle) * arrowLength + node2Y;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(node2X, node2Y, x1, y1);
        gc.strokeLine(node2X, node2Y, x2, y2);
        gc.strokeLine(node1X, node1Y, node2X, node2Y);
    }

    public static void createTextBoxes(){
        textBoxes.add(new EditableTextBox(1, "Notes","" , 0, 0, 12, 9));
        textBoxes.add(new EditableTextBox(2, "Edges","" , 12, 0, 12, 3));
        textBoxes.add(new EditableTextBox(3, "Job Name","" , 24, 0, 12, 3));
        textBoxes.add(new EditableTextBox(4, "Thickness","1" , 12, 3, 5, 3));
        textBoxes.add(new EditableTextBox(5, "Company","Company Name" , 17, 3, 14, 3));
        textBoxes.add(new EditableTextBox(6, "Purchase Order #","" , 31, 3, 5, 3));
        textBoxes.add(new EditableTextBox(7, "Type of Stone","" , 12, 6, 13, 3));
        textBoxes.add(new EditableTextBox(8, "Drawn By","" , 25, 6, 4, 3));
        textBoxes.add(new EditableTextBox(9, "Date","" , 29, 6, 3, 3));
        textBoxes.add(new EditableTextBox(10, "Sheet #","" , 32, 6, 2, 3));
        textBoxes.add(new EditableTextBox(11, "Rev.","" , 34, 6, 2, 3));
        return;
    }

    private static void updateButtonSize() {

        double x = window.getWidth()*(0.01*(-7.2)+0.1);
        topLayout.setSpacing(window.getWidth()*(0.01) - 7);


        for(Button b: buttons) {
            ImageView i = (ImageView) b.getGraphic();
            i.setFitHeight(x);
            i.setFitWidth(x);
            b.setGraphic(i);
        }

        ImageView i = (ImageView) addStoneMB.getGraphic();
        i.setFitHeight(x);
        i.setFitWidth(x);
        addStoneMB.setGraphic(i);

    }

    private void setMouseEvents() {

        window.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.DIGIT1)test1 -= 0.1;
                if(event.getCode() == KeyCode.DIGIT2)test1 -= 1;
                if(event.getCode() == KeyCode.DIGIT3)test1 += 1;
                if(event.getCode() == KeyCode.DIGIT4)test1 += 0.1;

                if(event.getCode() == KeyCode.D)Buttons.drawPattern();
                if(event.getCode() == KeyCode.C)Buttons.clear();
                if(event.getCode() == KeyCode.X)Buttons.cancel();
                if(event.getCode() == KeyCode.I)Buttons.addRandom();
                if(event.getCode() == KeyCode.F)Buttons.insertFeature();
                if(event.getCode() == KeyCode.P)Buttons.save();
                if(event.getCode() == KeyCode.M)Buttons.center();
                if(event.getCode() == KeyCode.B)Buttons.createBorder();
                if(event.getCode() == KeyCode.U)Buttons.undo();
                if(event.getCode() == KeyCode.R)Buttons.redo();
                if(event.getCode() == KeyCode.O)Buttons.projects();
                if(event.getCode() == KeyCode.S)Buttons.settings();
                if(event.getCode() == KeyCode.H)Buttons.help();
                if(event.getCode() == KeyCode.J)Buttons.information();

                System.out.println("test1: " + test1);
                System.out.println("test2: " + test2);
            }
        });

        window.addEventFilter(KeyEvent.ANY, event -> {
            if(TEST) {
                if(event.getCode() == KeyCode.UP) {
                    offsetY+=20;
                }
                if(event.getCode() == KeyCode.DOWN) {
                    offsetY-=20;
                }
                if(event.getCode() == KeyCode.RIGHT) {
                    offsetX-=20;
                }
                if(event.getCode() == KeyCode.LEFT) {
                    offsetX+=20;
                }
            }
            event.consume();
        });

        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stoneHovered.remove();
                try {Loading.saveProject(1);}
                catch (IOException e) {e.printStackTrace();}
                System.out.println("saved remove stone");
            }
        });

        rotate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for(Stone s: Calculate.adjacents(stoneHovered)) {
                    System.out.println(s.getSize());
                }

                System.out.println("index: " + stones.indexOf(stoneHovered));


                int oW = stoneHovered.getW();
                int oL = stoneHovered.getL();
                stoneHovered.setW(oL);
                stoneHovered.setL(oW);
                try {Loading.saveProject(1);}
                catch (IOException e) {e.printStackTrace();}
                System.out.println("saved rotate stone");


            }
        });

        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int oW = stoneHovered.getW();
                int oL = stoneHovered.getL();

                Stone singleStone = new Stone(stoneHovered);
                stoneHovered.addSQFT();

                int xPlacement = -oW-20;
                int yPlacement = (int)(Display.yDim*.5 - oW);

                while(Calculate.pointOverlap(xPlacement, yPlacement)>0) {
                    xPlacement -= 6;
                }

                singleStone.setX(xPlacement);
                singleStone.setY(yPlacement);

                stones.add(new Stone(singleStone));


                if(!Calculate.planking)Display.quantities[singleStone.getID()]++;
                if(Calculate.planking)Display.quantitiesPlanking[singleStone.getID()]++;

                try {Loading.saveProject(1);}
                catch (IOException e) {e.printStackTrace();}
                System.out.println("saved copy stone");

            }
        });

        canvas.setOnMouseMoved(new EventHandler<MouseEvent>(){
            @Override public void handle(MouseEvent event) {
                cx = (event.getX() - offsetX)/scale;
                cy = (event.getY() - offsetY)/scale;
                cxReal = event.getX();
                cyReal = event.getY();

                previousX = event.getX();
                previousY = event.getY();
                sideToCursorX = cx - stoneHovered.getX();
                sideToCursorY = cy - stoneHovered.getY();


                for(Stone stone: stones) {
                    if(!stoneRightClick.isShowing() && fill == false) {
                        stone.updateHover(cx, cy);
                        if(stone.getHover())stoneHovered = stone;
                    }

                }

                for(EditableTextBox box: textBoxes) {
                    box.updateHover(cxReal/titleBlockWMul, (cyReal - canvas.getHeight() + 9*titleBlockHMul +1)/titleBlockHMul);
                }

                if(!stoneHovered.getHover())stoneHovered = new Stone();

                for(Stone stone: stones) {
                    if(stoneHovered != stone)stone.updateHover(-999, -999);

                }
                if(event.getX()>xDimLocationX-25 && event.getX()<xDimLocationX + 25 && event.getY()>xDimLocationY-8 && event.getY()<xDimLocationY+12) {
                    xDimHover = true;
                }
                else xDimHover = false;

                if(event.getX()>yDimLocationX+18 && event.getX()<yDimLocationX + 68 && event.getY()>yDimLocationY-12 && event.getY()<yDimLocationY+8) {
                    yDimHover = true;
                }
                else yDimHover = false;


            }
        });

        canvas.setOnMouseExited(new EventHandler<MouseEvent>(){
            @Override public void handle(MouseEvent event) {

            }
        });

        canvas.setOnScroll(new EventHandler<ScrollEvent>(){
            @Override public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY()/100;
                scale += deltaY;

                if(scale<MIN_SCALE)scale = MIN_SCALE;
                if(scale>MAX_SCALE)scale = MAX_SCALE;

                if(scale>MIN_SCALE && scale<MAX_SCALE) {
                    offsetX -= cx*deltaY;
                    offsetY -= cy*deltaY;
                }
            }
        });

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                MouseButton button = event.getButton();
                stoneRightClick.hide();

                if(button==MouseButton.PRIMARY){



                    if(xDimHover) {
                        if(!username.equals("demo")) {
                            cv.change(xDim, "Set X", 4);
                            if(cv.getInt() != -9999) {
                                xDim = cv.getInt();
                                setScale(canvas.getWidth(), canvas.getHeight());
                                center();
                                try {Loading.saveProject(1);}
                                catch (IOException e) {e.printStackTrace();}
                                System.out.println("saved xDim changed");
                            }
                        }
                        else {
                            AlertBox.display("Requires Paid Version", "You cannot change the patio dimensions when logged into a demo account."
                                    + " \n\nPlease email randompatiodesigner@gmail.com to purchase an account.", 450, 300);
                        }
                    }
                    if(yDimHover) {
                        if(!username.equals("demo")) {
                            cv.change(yDim, "Set Y", 4);
                            if(cv.getInt() != -9999) {
                                yDim = cv.getInt();
                                setScale(canvas.getWidth(), canvas.getHeight());
                                center();
                                try {Loading.saveProject(1);}
                                catch (IOException e) {e.printStackTrace();}
                                System.out.println("saved yDim changed");
                            }
                        }
                        else {
                            AlertBox.display("Requires Paid Version", "You cannot change the patio dimensions when logged into a demo account."
                                    + " \n\nPlease email randompatiodesigner@gmail.com to purchase an account.", 450, 300);
                        }

                    }

                    for(EditableTextBox box: textBoxes) {
                        if(box.getHover()) {
                            if(!box.getName().equals("Thickness")) {
                                cv.change(box.getValue(), box.getName());
                                if(!cv.getString().equals("**canceled**")) {
                                    box.setValue(cv.getString());
                                    try {Loading.saveProject(1);}
                                    catch (IOException e) {e.printStackTrace();}
                                    System.out.println("saved text box edited");
                                }
                            }
                            if(box.getName().equals("Thickness")) {
                                cv.change(box.getValue(), box.getName());
                                if(!cv.getString().equals("**canceled**")) {
                                    if(Calculate.isFloat(cv.getString())){
                                        box.setValue(cv.getString());
                                        try {Loading.saveProject(1);}
                                        catch (IOException e) {e.printStackTrace();}
                                        System.out.println("saved text box edited");
                                    }
                                    else {
                                        AlertBox.display("Input Error", "Thickness must be a decimal, '1.5' for example.", 300, 150);
                                    }
                                }
                            }
                        }
                    }
                }
                else if(button==MouseButton.SECONDARY){
                    if(stoneHovered.getHover()) {
                        if(stoneHovered.getID() == 100)stoneHovered.openStoneSettings();
                        else stoneRightClick.show(canvasPane, event.getScreenX(), event.getScreenY());
                    }

                }
                else if(button==MouseButton.MIDDLE){

                }
            }
        });
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                MouseButton button = event.getButton();
                if(button==MouseButton.PRIMARY){
                    if(stoneHovered.getHover()) {
                        if(stoneHovered.getX() != stoneHovered.getPreviousX() || stoneHovered.getY() != stoneHovered.getPreviousY()) {
                            stoneHovered.setPrevious();
                            try {Loading.saveProject(1);}
                            catch (IOException e) {e.printStackTrace();}
                            System.out.println("saved stone moved");
                        }
                    }

                }
                else if(button==MouseButton.SECONDARY){


                }
                else if(button==MouseButton.MIDDLE){

                }
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                double distanceX = event.getX()-previousX;
                double distanceY = event.getY()-previousY;
                cx = (event.getX() - offsetX)/scale;
                cy = (event.getY() - offsetY)/scale;
                if(event.isPrimaryButtonDown()) {
                    if(stoneHovered.getHover()) {
                        int index = stones.indexOf(stoneHovered);	//makes stone hovered draw ontop of all other stones
                        if(index != stones.size()-1) {
                            stones.remove(index);
                            stones.add(stoneHovered);
                        }
                        draggingDone = false;
                        fill = false;
                        stoneHovered.move(cx - sideToCursorX, cy - sideToCursorY);


                    }
                }
                if(event.isMiddleButtonDown()) {
                    offsetX += distanceX;
                    offsetY += distanceY;
                }
                previousX = event.getX();
                previousY = event.getY();
            }
        });
    }

    private void startAnimation() {
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(drawStyle);
                if(fill) {
                    if(Calculate.create()) { //create true = finished
                        fill = false;
                    }
                    else {
                        //fill = true;		//true = 1 placement per button false = place until done
                    }
                }

                try {
                    Loading.saveProject(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        };
        timer.start();
    }

    private void displayLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Display.class.getResource("/customStyle.css").toExternalForm());

        Stage window = new Stage();
        window.setResizable(false);

        window.setOnCloseRequest(we -> System.exit(0));

        window.setScene(scene);
        window.getIcons().add(Display.icon);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Login");
        window.showAndWait();

    }

    private static void initialize() {
        createTextBoxes();
        Arrays.fill(sizesSelected,true);
        Arrays.fill(quantities,0);
        Arrays.fill(quantitiesPlanking,0);
        Arrays.fill(quantitiesSelected,10);
        projectOpen = false;
        currentProject = null;
    }

    public static void main(String[] args) {
        initialize();
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Calculate.updateStonesAvailable();


        window = primaryStage;
        window.setTitle("RandomizedPatternMaker");

        //stone right click menu//
        stoneRightClick = new ContextMenu();
        remove = new MenuItem("Remove");
        rotate = new MenuItem("Rotate");
        copy = new MenuItem("Copy");
        stoneRightClick.getItems().addAll(remove, rotate, copy);
        stoneRightClick.setAutoHide(true);


        Image confirmImage = new Image(getClass().getResourceAsStream("/confirm.png"));
        ImageView confirmImageView = new ImageView(confirmImage);
        confirmImageView.setFitWidth(40);
        confirmImageView.setFitHeight(40);
        confirmButton = new Button("",confirmImageView);
        confirmButton.setPadding(Insets.EMPTY);
        confirmButton.getStyleClass().add("button-borderless");

        Image drawImage = new Image(getClass().getResourceAsStream("/edit.png"));
        ImageView drawImageView = new ImageView(drawImage);
        DrawPatternButton = new Button("",drawImageView);
        DrawPatternButton.setPadding(Insets.EMPTY);
        DrawPatternButton.setTooltip(new Tooltip("Draw Pattern (D) \n - Starts creating a pattern. "
                + "\n - Pressing this button again will pause this proccess. Once paused, \n"
                + "\tthe stones that are currently placed will never go away unless the \n"
                + "\t'clear' button is pressed."));
        DrawPatternButton.getStyleClass().add("button-borderless");
        buttons.add(DrawPatternButton);



        Image featureImage = new Image(getClass().getResourceAsStream("/feature.png"));
        ImageView featureImageView = new ImageView(featureImage);
        insertFeatureButton = new Button("",featureImageView);
        insertFeatureButton.setPadding(Insets.EMPTY);
        insertFeatureButton.setTooltip(new Tooltip("Insert Feature(F) \n - Creates a feature, which can be placed and resized. "
                + "\n - Features can be used to represent gardens, pools, obstacles, etc."));
        insertFeatureButton.getStyleClass().add("button-borderless");
        buttons.add(insertFeatureButton);


        Image saveImage = new Image(getClass().getResourceAsStream("/picture.png"));
        ImageView saveImageView = new ImageView(saveImage);
        SaveButton = new Button("", saveImageView);
        SaveButton.setPadding(Insets.EMPTY);
        SaveButton.setTooltip(new Tooltip("Save as PNG (P)\n - Save the current project as a PNG."));
        SaveButton.getStyleClass().add("button-borderless");
        buttons.add(SaveButton);


        Image settingsImage = new Image(getClass().getResourceAsStream("/settings.png"));
        ImageView settingsImageView = new ImageView(settingsImage);
        SettingsButton = new Button("", settingsImageView);
        SettingsButton.setPadding(Insets.EMPTY);
        SettingsButton.setTooltip(new Tooltip("Settings (S) \n - Open the settings menu."));
        SettingsButton.getStyleClass().add("button-borderless");
        buttons.add(SettingsButton);

        Image centerImage = new Image(getClass().getResourceAsStream("/center.png"));
        ImageView centerImageView = new ImageView(centerImage);
        centerButton = new Button("", centerImageView);
        centerButton.setPadding(Insets.EMPTY);
        centerButton.setTooltip(new Tooltip("Center (M) \n - Center the screen and zoom in on the project."));
        centerButton.getStyleClass().add("button-borderless");
        buttons.add(centerButton);

        Image projectImage = new Image(getClass().getResourceAsStream("/folder.png"));
        ImageView projectImageView = new ImageView(projectImage);
        ProjectsButton = new Button("",projectImageView);
        ProjectsButton.setPadding(Insets.EMPTY);
        ProjectsButton.setTooltip(new Tooltip("Projects (O)\n - Open the projects menu to open and create a new project."));
        ProjectsButton.getStyleClass().add("button-borderless");
        buttons.add(ProjectsButton);

        Image undoImage = new Image(getClass().getResourceAsStream("/undo.png"));
        ImageView undoImageView = new ImageView(undoImage);
        undoButton = new Button("",undoImageView);
        undoButton.setPadding(Insets.EMPTY);
        undoButton.setTooltip(new Tooltip("Undo (U)"));
        undoButton.getStyleClass().add("button-borderless");
        buttons.add(undoButton);

        Image redoImage = new Image(getClass().getResourceAsStream("/redo.png"));
        ImageView redoImageView = new ImageView(redoImage);
        redoButton = new Button("",redoImageView);
        redoButton.setPadding(Insets.EMPTY);
        redoButton.setTooltip(new Tooltip("Redo (R)"));
        redoButton.getStyleClass().add("button-borderless");
        buttons.add(redoButton);

        Image clearImage = new Image(getClass().getResourceAsStream("/clear.png"));
        ImageView clearImageView = new ImageView(clearImage);
        clearButton = new Button("",clearImageView);
        clearButton.setPadding(Insets.EMPTY);
        clearButton.setTooltip(new Tooltip("Clear (C) \n - Stops current drawing and clears all stones in the current project "));
        clearButton.getStyleClass().add("button-borderless");
        buttons.add(clearButton);

        currentProjectLabel = new Label("Current Project: N/A");
        currentProjectLabel.setStyle("-fx-font-size: 20px;-fx-padding: 0 0 0 20;");
        //currentProjectLabel.setStyle("");

        Image cancelImage = new Image(getClass().getResourceAsStream("/cancel.png"));
        ImageView cancelImageView = new ImageView(cancelImage);
        cancelButton = new Button("",cancelImageView);
        cancelButton.setPadding(Insets.EMPTY);
        cancelButton.setTooltip(new Tooltip("Cancel (X) \n - If drawing, removes the stones that were drawn and stops drawing"));
        cancelButton.getStyleClass().add("button-borderless");
        buttons.add(cancelButton);

        Image addStoneImage = new Image(getClass().getResourceAsStream("/insertSingleStone.png"));
        ImageView addStoneImageView = new ImageView(addStoneImage);
        addStoneMB = new MenuButton("",addStoneImageView);
        addStoneMB.setPadding(Insets.EMPTY);
        addStoneMB.getStyleClass().addAll("menu-button-borderless");
        addStoneMB.setTooltip(new Tooltip("Add a single stone\n - 'a x b' places a stone outside of the patio for you to drag and drop"));
        updateAddStoneMB();

        Image addRandomImage = new Image(getClass().getResourceAsStream("/insertRandomStone.png"));
        ImageView addRandomImageView = new ImageView(addRandomImage);
        addRandomButton = new Button("",addRandomImageView);
        addRandomButton.setPadding(Insets.EMPTY);
        addRandomButton.setTooltip(new Tooltip("Insert Random Stone (I)\n - Attempts to insert a single random stone \n - This will only place stones that are checked in the settings"));
        addRandomButton.getStyleClass().add("button-borderless");
        buttons.add(addRandomButton);

        Image helpImage = new Image(getClass().getResourceAsStream("/help.png"));
        ImageView helpImageView = new ImageView(helpImage);
        helpButton = new Button("",helpImageView);
        helpButton.setPadding(Insets.EMPTY);
        helpButton.setTooltip(new Tooltip("Help (H)\n - Press to find out how to recieve help"));
        helpButton.getStyleClass().add("button-borderless");
        buttons.add(helpButton);

        Image createBorderImage = new Image(getClass().getResourceAsStream("/createBorder.png"));
        ImageView createBorderImageView = new ImageView(createBorderImage);
        createBorderButton = new Button("",createBorderImageView);
        createBorderButton.setPadding(Insets.EMPTY);
        createBorderButton.setTooltip(new Tooltip("Create Border (B) \n - Extends X and Y dimensions to allow a border to be placed"));
        createBorderButton.getStyleClass().add("button-borderless");
        buttons.add(createBorderButton);

        Image informationImage = new Image(getClass().getResourceAsStream("/information.png"));
        ImageView informationImageView = new ImageView(informationImage);
        informationButton = new Button("",informationImageView);
        informationButton.setPadding(Insets.EMPTY);
        informationButton.setTooltip(new Tooltip("Information (J) \n - Opens a window that allows you to fill in the titlebox information"));
        informationButton.getStyleClass().add("button-borderless");
        buttons.add(informationButton);

        Image saveQuantitiesImage = new Image(getClass().getResourceAsStream("/quantitiesList.png"));
        ImageView saveQuantitiesImageView = new ImageView(saveQuantitiesImage);
        saveQuantitiesButton = new Button("",saveQuantitiesImageView);
        saveQuantitiesButton.setPadding(Insets.EMPTY);
        saveQuantitiesButton.setTooltip(new Tooltip("Save Quantities List (Q) \n - Saves the quantities list as a png"));
        saveQuantitiesButton.getStyleClass().add("button-borderless");
        buttons.add(saveQuantitiesButton);

        DrawPatternButton.setOnAction(e -> {Buttons.drawPattern();});
        helpButton.setOnAction(e -> {Buttons.help();});
        addRandomButton.setOnAction(e -> {Buttons.addRandom();});
        insertFeatureButton.setOnAction(e -> {Buttons.insertFeature();});
        centerButton.setOnAction(e -> {Buttons.center();});
        undoButton.setOnAction(e -> {Buttons.undo();});
        redoButton.setOnAction(e -> {Buttons.redo();});
        SettingsButton.setOnAction(e -> {Buttons.settings();});
        ProjectsButton.setOnAction(e -> {Buttons.projects();});
        clearButton.setOnAction(e -> {Buttons.clear();});
        cancelButton.setOnAction(e -> {Buttons.cancel();});
        createBorderButton.setOnAction(e -> {Buttons.createBorder();});
        SaveButton.setOnAction(e ->{Buttons.save();});
        saveQuantitiesButton.setOnAction(e ->{Buttons.saveQuantities();});
        informationButton.setOnAction(e ->{Buttons.information();});

        productNames = new ArrayList<>();
        Collections.addAll(Display.productNames,
                "12x12", "12x18", "12x24", "12x30", "12x36", "12x42", "12x48",
                "18x18", "18x24", "18x30", "18x36", "18x42", "18x48",
                "24x24", "24x30", "24x36", "24x42", "24x48",
                "30x30", "30x36", "30x42", "30x48",
                "36x36", "36x42", "36x48",
                "42x42", "42x48",
                "48x48");


        sqftLabel = new ListView<>();
        sqftLabel.setMaxWidth(50);
        sqftLabel.setMaxHeight(25);
        sqftLabel.getItems().clear();
        sqftLabel.getItems().add("sqft");


        sqftList = new ListView<>();
        sqftList.setMaxWidth(70);
        sqftList.setMaxHeight(25);

        topLayout.getChildren().addAll(	undoButton, redoButton,
                DrawPatternButton,
                cancelButton,
                clearButton,
                addStoneMB,
                addRandomButton,
                insertFeatureButton,
                centerButton,
                createBorderButton,
                SaveButton,
                saveQuantitiesButton,
                ProjectsButton,
                informationButton,
                SettingsButton,
                helpButton,
                currentProjectLabel);

        topLayout.setAlignment(Pos.BOTTOM_LEFT);
        topLayout.setPadding(new Insets(10,10,10,10));

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(0,20,20,20));

        canvasPane.getChildren().add(canvas);
        canvasPane.getStyleClass().add("canvas-pane");
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        window.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateButtonSize();
            center();
        });

        mainLayout.setTop(topLayout);
        mainLayout.setCenter(canvasPane);
        scene = new Scene(mainLayout, 1120, 850);


        scene.getStylesheets().add(Display.class.getResource("/customStyle.css").toExternalForm());

        icon = new Image(getClass().getResourceAsStream("/icon.png"));
        window.getIcons().add(icon);
        window.setScene(scene);
        window.setTitle(PROGRAM_NAME);

        if(SKIP_LOGIN) {
            username = "ryan";
            password = "xld29smc";
        }
        else displayLogin();

        window.show();
        window.setMaximized(true);
        updateButtonSize();
        setMouseEvents();
        startAnimation();
        setScale(canvas.getWidth(), canvas.getHeight());
        center();
        Projects.display();
    }
}
