package program;

import javafx.scene.paint.Color;
import windows.FeatureSettings;

import java.util.Random;

public class Stone {
    private String size;
    private int x;
    private int y;
    private int w;
    private int l;
    private int id;
    private boolean hover;
    private Color color;
    private int previousX;	//used for stone moving
    private int previousY;
    private boolean surr = false;

    public Stone() {
        setColor(0);
        this.size = "";
        this.x = 0;
        this.y = 0;
        this.w = 0;
        this.l = 0;
        setPrevious();

    }

    public Stone(Stone stone) {

        setColor(0);
        this.size = stone.getSize();
        this.x = stone.getX();
        this.y = stone.getY();
        this.w = stone.getW();
        this.l = stone.getL();
        this.id = stone.getID();
        setPrevious();
    }

    public Stone(String size, int w, int l, int id) {
        setColor(0);
        this.size = size;
        this.x = 0;
        this.y = 0;
        this.w = w;
        this.l = l;
        this.id = id;
        setPrevious();

    }

    public Stone(String size, int x, int y, int w, int l, int id) {

        setColor(0);
        this.size = size;
        this.x = x;
        this.y = y;
        this.w = w;
        this.l = l;
        this.id = id;
        setPrevious();
    }
    public void setPrevious() {
        previousX = x;
        previousY = y;
    }

    public void openStoneSettings() {
        if(id==100)FeatureSettings.display(this);
    }

    public void remove() {

        if(id == 100) {
            x = -9999;
            y = -9999;
            w = 0;
            l = 0;
            Display.obstacles.remove(this);
            hover = false;
        }
        else {
            if(!Display.stoneSelectionMethod.equals("planking")) Display.quantities[id] --;
            else Display.quantitiesPlanking[id] --;
            Display.sqinTotal -= w*l;

            x = -9999;
            y = -9999;
            w = 0;
            l = 0;
            Display.stones.remove(this);
            hover = false;
        }

    }

    public void move(double newX, double newY) {
        if(Calculate.planking) y = Calculate.round6(newY, 5.0);

        else y = Calculate.round6(newY, 6.0);

        x = Calculate.round6(newX, 6.0);
    }


    public void updateHover(double cx, double cy) {
        if(cx>x && cy>y && cx<x+w && cy<y+l) {
            hover = true;
        }
        else {
            hover = false;
        }
    }


    public void addSQFT() {
        Display.sqinTotal = Display.sqinTotal +  l*w;
    }

    public boolean getHover() {
        return hover;
    }

    public void setColor(int i) {
        Random rand = new Random();
        int  choice = rand.nextInt(3);

        choice = i;
        if(id == 100)choice = 4;

        if(choice == 0)color = Color.LIGHTGREY;
        if(choice == 1)color = Color.LIGHTSLATEGREY;
        if(choice == 2)color = Color.SLATEGRAY;
        if(choice == 3)color = Color.GREY;
        if(choice == 4)color = Color.LIGHTBLUE;
    }

    public Color getColor() {
        return color;
    }

    public boolean getSurr() {
        return surr;
    }

    public int getID() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getL() {
        return l;
    }

    public int getPreviousX() {
        return previousX;
    }

    public int getPreviousY() {
        return previousY;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setX(int x) {

        this.x = x;
    }

    public void setY(int y) {

        this.y = y;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setL(int l) {
        this.l = l;
    }

    public void setSurr(boolean b) {
        this.surr = b;
    }



}
