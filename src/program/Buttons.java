package program;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import windows.AlertBox;
import windows.Information;
import windows.Projects;
import windows.SettingsBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;

public class Buttons {
    /*
    static final double PRINT_W1 = 2048;
    static final double PRINT_W2 = 2700;
    static final double PRINT_W3 = 3400;

    static final double PRINT_H1 = 2048;
    static final double PRINT_H2 = 2700;
    static final double PRINT_H3 = 2200;
    */
    static final double PRINT_H1 = 8.5;
    static final double PRINT_H2 = 8.5;
    static final double PRINT_H3 = 11;

    static final double PRINT_W1 = 11;
    static final double PRINT_W2 = 14;
    static final double PRINT_W3 = 17;

    public static void drawPattern(){
        if(Display.projectOpen) {
            if(Display.fill == true) {
                try {Loading.saveProject(1);}
                catch (IOException e1) {e1.printStackTrace();}
                System.out.println("saved drawing, now paused");
            }

            Display.fill = !Display.fill;
            Display.startingStonesAmount = Display.stones.size();
            Calculate.attempts = 0;

        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void information() {
        Information.display();
    }

    public static void help(){
        if(Display.projectOpen) {
            Display.fill = false;
            AlertBox.display("Help", "\n\n\n Please email 'randompatiodesigner@gmail.com' for assistance.\n\n", 600, 300);
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void addRandom(){
        if(Display.projectOpen) {
            Display.fill=false;
            if(!Display.stoneSelectionMethod.equals("addSingleStoneRandom")){
                Display.oldStoneSelectionMethod = Display.stoneSelectionMethod;
            }
            Display.stoneSelectionMethod = "addSingleStoneRandom";
            Calculate.create();
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void insertFeature(){
        if(Display.projectOpen) {
            Display.fill=false;
            Stone newStone = new Stone("Feature", -90, 0, 72, 72, 100);
            Display.stones.add(newStone);
            Display.obstacles.add(newStone);
            newStone.setColor(4);

            try {Loading.saveProject(1);}
            catch (IOException e1) {e1.printStackTrace();}
            System.out.println("saved insert feature");
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void center(){
        Display.setScale(Display.canvas.getWidth(), Display.canvas.getHeight());
        Display.center();
    }

    public static void undo(){
        if(Display.projectOpen) {
            try {
                Loading.undo();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void redo(){
        if(Display.projectOpen) {
            try {
                Loading.redo();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void settings(){
        if(Display.projectOpen) {
            Display.fill=false;
            SettingsBox.display();
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void projects(){
        Display.fill=false;
        Projects.display();
    }

    public static void clear(){
        if(Display.projectOpen) {
            Display.fill = false;
            if(Display.stones.size()>0) {
                Display.clear();
                try {Loading.saveProject(1);}
                catch (IOException e1) {e1.printStackTrace();}
                System.out.println("saved clear button");
            }

        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void cancel(){
        if(Display.projectOpen) {
            if(Display.fill) {
                Display.fill=false;
                Display.clearRecent(true);
                try {Loading.saveProject(1);}
                catch (IOException e1) {e1.printStackTrace();}
                System.out.println("saved clear button");
            }
        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void createBorder(){
        if(Display.projectOpen && !Display.username.equals("demo")) {
            Display.fill=false;
            Display.cv.change(Display.previousBorderWidth, "Create Border", 6);
            if(Display.cv.getInt() != -9999) {
                int d = Display.cv.getInt();
                Display.previousBorderWidth = d;
                Display.xDim += d*2;
                Display.yDim += d*2;
                Display.setScale(Display.canvas.getWidth(), Display.canvas.getHeight());
                Display.center();


                for(Stone s: Display.stones) {
                    s.setX(s.getX() + d);
                    s.setY(s.getY() + d);
                }
                try {Loading.saveProject(1);}
                catch (IOException e1) {e1.printStackTrace();}
                System.out.println("saved xDim changed");
            }
        }
        else if(Display.username.equals("demo")) {
            AlertBox.display("Requires Paid Version", "You cannot change the patio dimensions when logged into a demo account."
                    + " \n\nPlease email randompatiodesigner@gmail.com to purchase an account.", 450, 300);

        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }

    public static void saveQuantities() {
        Display.canvasPane.setMinWidth(1632);
        Display.canvasPane.setMinHeight(1632);
        Display.canvasPane.setMaxWidth(2112);
        Display.canvasPane.setMaxHeight(2112);

        Display.drawStyle = "fullscreenQuantities";
        Display.draw(Display.drawStyle);


        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        File file = fileChooser.showSaveDialog(Display.window);

        Display.center();
        Display.stoneHovered = new Stone();
        Display.draw(Display.drawStyle);

        if(file != null){
            try {


                double pixelScale = 2;
                WritableImage writableImage = new WritableImage
                        ((int)Math.rint(pixelScale*Display.canvas.getWidth()),
                                (int)Math.rint(pixelScale*Display.canvas.getHeight()));

                SnapshotParameters spa = new SnapshotParameters();
                spa.setTransform(Transform.scale(pixelScale, pixelScale));
                writableImage =  Display.canvas.snapshot(spa, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        Display.canvasPane.setMinWidth(100);
        Display.canvasPane.setMinHeight(100);
        Display.canvasPane.setMaxWidth(100000);
        Display.canvasPane.setMaxHeight(100000);

        Display.drawStyle = "default";
        Display.draw(Display.drawStyle);


    }

    public static void save() {
        if(Display.projectOpen && !Display.username.equals("demo")) {
            double oldWidth = Display.canvasPane.getWidth();
            double oldHeight = Display.canvasPane.getHeight();

            Display.titleBlockHMul +=7;
            Display.titleBlockWMul +=10;

            double multiplier = 1;
            if(Display.printRatio == 1) {
                if(Display.printSize == 1)multiplier = 1700/11;
                if(Display.printSize == 2)multiplier = 2000/11;
                if(Display.printSize == 3)multiplier = 2300/11;
                if(Display.printSize == 4)multiplier = 2700/11;

                Display.canvasPane.setMinWidth(PRINT_W1*multiplier);
                Display.canvasPane.setMinHeight(PRINT_H1*multiplier);
                Display.canvasPane.setMaxWidth(PRINT_W1*multiplier);
                Display.canvasPane.setMaxHeight(PRINT_H1*multiplier);
                Display.setScale(PRINT_W1*multiplier, PRINT_H1*multiplier);
            }
            if(Display.printRatio == 2) {
                if(Display.printSize == 1)multiplier = 1700/14;
                if(Display.printSize == 2)multiplier = 2000/14;
                if(Display.printSize == 3)multiplier = 2300/14;
                if(Display.printSize == 4)multiplier = 2700/14;

                Display.canvasPane.setMinWidth(PRINT_W2*multiplier);
                Display.canvasPane.setMinHeight(PRINT_H2*multiplier);
                Display.canvasPane.setMaxWidth(PRINT_W2*multiplier);
                Display.canvasPane.setMaxHeight(PRINT_H2*multiplier);
                Display.setScale(PRINT_W2*multiplier, PRINT_H2*multiplier);
            }
            if(Display.printRatio == 3) {
                if(Display.printSize == 1)multiplier = 1700/17;
                if(Display.printSize == 2)multiplier = 2000/17;
                if(Display.printSize == 3)multiplier = 2300/17;
                if(Display.printSize == 4)multiplier = 2700/17;

                Display.canvasPane.setMinWidth(PRINT_W3*multiplier);
                Display.canvasPane.setMinHeight(PRINT_H3*multiplier);
                Display.canvasPane.setMaxWidth(PRINT_W3*multiplier);
                Display.canvasPane.setMaxHeight(PRINT_H3*multiplier);
                Display.setScale(PRINT_W3*multiplier, PRINT_H3*multiplier);
            }

            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            //Show save file dialog
            File file = fileChooser.showSaveDialog(Display.window);

            Display.center();
            Display.stoneHovered = new Stone();
            Display.draw(Display.drawStyle);

            if(file != null){
                try {


                    double pixelScale = 2;
                    WritableImage writableImage = new WritableImage
                            ((int)Math.rint(pixelScale*Display.canvas.getWidth()),
                                    (int)Math.rint(pixelScale*Display.canvas.getHeight()));

                    SnapshotParameters spa = new SnapshotParameters();
                    spa.setTransform(Transform.scale(pixelScale, pixelScale));
                    writableImage =  Display.canvas.snapshot(spa, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Display.titleBlockHMul -=7;
            Display.titleBlockWMul -=10;
            Display.canvasPane.setMinWidth(100);
            Display.canvasPane.setMinHeight(100);
            Display.canvasPane.setMaxWidth(100000);
            Display.canvasPane.setMaxHeight(100000);
            Display.setScale(oldWidth, oldHeight);
            Display.offsetX = (oldWidth-Display.xDim*Display.scale)*.5;
            Display.offsetY = -50 + (oldHeight-Display.yDim*Display.scale)*.5;
        }
        else if(Display.username.equals("demo")) {
            AlertBox.display("Requires Paid Version", "You cannot export as a png when logged into a demo account."
                    + " \n\nPlease email randompatiodesigner@gmail.com to purchase an account.", 450, 300);

        }
        else {
            AlertBox.display("No Project Open", "Please open a project", 300, 150);
            Projects.display();
        }
    }
}
