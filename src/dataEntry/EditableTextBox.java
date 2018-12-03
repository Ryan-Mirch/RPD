package dataEntry;

public class EditableTextBox {

    private int id;
    private String name;
    private String value;
    private int x;
    private int y;
    private int w;
    private int h;
    private boolean hover;


    public EditableTextBox() {
        this.name = "";
        this.value = "";
        this.x = 0;
        this.y = 0;
        this.w = 0;
        this.h = 0;
        this.id = 0;

    }

    public EditableTextBox(int id, String name, String value, int x, int y, int w, int h) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void updateHover(double cx, double cy) {
        if(cx>x && cy>y && cx<x+w && cy<y+h) {
            hover = true;
        }
        else {
            hover = false;
        }
    }

    public int getId() {
        return id;
    }

    public boolean getHover() {
        return hover;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
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

    public int getH() {
        return h;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
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

    public void setH(int h) {
        this.h = h;
    }

}
