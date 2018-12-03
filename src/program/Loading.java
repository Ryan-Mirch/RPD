package program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import dataEntry.EditableTextBox;
import windows.Projects;

public class Loading {

    static int undoIndex = -1;

    public static void saveProject(int Undo) throws IOException{
        if(Display.projectOpen == true) {

            File file;
            if(Undo == 1) {
                file = new File(Projects.undoFolder + "//" + Display.currentProject.getName().replaceFirst("[.][^.]+$", "") + System.currentTimeMillis() + ".txt");
                File folder = new File(Projects.undoFolder);
                File[] listOfFiles = folder.listFiles();
                int recentIndex = listOfFiles.length - 1;

                //if a change is made and you arent at the most recent index, delete all states after recent index
                if(undoIndex != recentIndex) {
                    for(int i = recentIndex; i > undoIndex; i-- ) {
                        listOfFiles[i].delete();
                    }
                }
                undoIndex++;
            }
            else file = new File(Projects.projectsFolder + "//" + Display.currentProject.getName());

            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(Display.stoneSelectionMethod);
            pw.println("\n");

            pw.println(Display.xDim + " " + Display.yDim); //saves dimensions
            pw.println("\n");

            for(Stone stone: Display.stones) {	//save stones
                pw.println("-stone-");
                pw.println(stone.getID());
                pw.println(stone.getSize());
                pw.println(stone.getW());
                pw.println(stone.getL());
                pw.println(stone.getX());
                pw.println(stone.getY());
            }
            pw.println("\n");

            for(EditableTextBox textBox: Display.textBoxes) {	//save text boxes
                pw.println("-textBox-");
                pw.println(textBox.getId());
                pw.println(textBox.getValue());
                pw.println("-endOfValue-");
            }

            int index = 0;
            for(Integer i: Display.quantitiesSelected) {	//save quantities
                pw.println("-quantity-");
                pw.println(i);
                pw.println(index);
                pw.println("-endOfValue-");
                index++;
            }

            index = 0;
            for(Boolean b: Display.sizesSelected) {	//save checkBoxes
                pw.println("-checkBox-");
                pw.println(b);
                pw.println(index);
                pw.println("-endOfValue-");
                index++;
            }

            pw.println("-size-");
            pw.println(Display.printSize);
            pw.println("-endOfValue-");

            pw.println("-adjacent-");
            pw.println(Display.adjStonesBoolean);
            pw.println("-endOfValue-");

            pw.println("-4corners-");
            pw.println(Display.allow4CornersBoolean);
            pw.println("-endOfValue-");

            pw.println("-forceV-");
            pw.println(Display.forceVBoolean);
            pw.println("-endOfValue-");

            pw.println("-forceH-");
            pw.println(Display.forceHBoolean);
            pw.println("-endOfValue-");

            pw.println("-numberLabel-");
            pw.println(Display.numberLabelBoolean);
            pw.println("-endOfValue-");

            pw.println("-sizeLabel-");
            pw.println(Display.sizeLabelBoolean);
            pw.println("-endOfValue-");

            pw.println("-preventVJoint-");
            pw.println(Display.preventVJoint);
            pw.println("-endOfValue-");

            pw.println("-allowBorderOverlap-");
            pw.println(Display.allowBorderOverlap);
            pw.println("-endOfValue-");

            pw.println("-fromLeft-");
            pw.println(Display.drawFromLeft);
            pw.println("-endOfValue-");

            pw.println("-fromTop-");
            pw.println(Display.drawFromTop);
            pw.println("-endOfValue-");

            pw.println("-displayInfo-");
            pw.println(Display.displayInfoBoolean);
            pw.println("-endOfValue-");

            pw.println("-displayQuantities-");
            pw.println(Display.displayQuantitiesBoolean);
            pw.println("-endOfValue-");

            pw.println("-sameWidthInRow-");
            pw.println(Display.sameWidthInRow);
            pw.println("-endOfValue-");

            pw.close();
        }

    }

    public static void openProject(File project, int Undo) throws IOException{
        Display.stones.clear();
        Display.obstacles.clear();
        Display.projectOpen = true;
        Display.clear();

        Scanner x = null;
        BufferedReader br = null;

        if(Undo == 0) {
            Display.currentProject = project;
            Display.currentProjectLabel.setText("Current Project: " + Display.currentProject.getName().replaceFirst("[.][^.]+$", ""));

            try {
                x = new Scanner(new File(Projects.projectsFolder + "//" + Display.currentProject.getName()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            br = new BufferedReader(new FileReader(Projects.projectsFolder + "//" + Display.currentProject.getName()));
        }
        else {
            try {
                x = new Scanner(new File(Projects.undoFolder + "//" + project.getName()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            br = new BufferedReader(new FileReader(Projects.undoFolder + "//" + project.getName()));
        }


        if(br.readLine() == null) {
            Display.xDim = 240;
            Display.yDim = 240;
            Display.stoneSelectionMethod = "random";
            Calculate.planking = false;
            Arrays.fill(Display.quantitiesSelected,10);
            Arrays.fill(Display.sizesSelected,true);

            Display.adjStonesBoolean = false;
            Display.allow4CornersBoolean = false;
            Display.forceVBoolean = false;
            Display.forceHBoolean = false;
            Display.preventVJoint = false;
            Display.allowBorderOverlap = false;
            Display.drawFromLeft = false;
            Display.drawFromTop = false;
            Display.printSize = 1;
            Display.sameWidthInRow = false;

            Display.numberLabelBoolean = false;
            Display.sizeLabelBoolean = true;
            Display.displayInfoBoolean = true;
            Display.displayQuantitiesBoolean = true;

        }
        else{

            Calculate.planking = false;

            String selectionMethod = x.next();
            Display.stoneSelectionMethod = selectionMethod;
            if(Display.stoneSelectionMethod.equals("planking"))Calculate.planking = true;
            if(Display.stoneSelectionMethod.equals("addSingleStoneRandom"))Display.stoneSelectionMethod = "random";


            Display.xDim = Integer.parseInt(x.next());
            Display.yDim = Integer.parseInt(x.next());

            while(x.hasNext()) {
                String s = x.nextLine();

                if(s.equals("-stone-")) {
                    int id = Integer.parseInt(x.nextLine());
                    String size = x.nextLine();
                    int w = Integer.parseInt(x.nextLine());
                    int l = Integer.parseInt(x.nextLine());
                    int X = Integer.parseInt(x.nextLine());
                    int Y = Integer.parseInt(x.nextLine());
                    Stone newStone = new Stone(size, X, Y, w, l, id);
                    Display.stones.add(newStone);

                    if(id == 100) {
                        newStone.setColor(4);
                        Display.obstacles.add(newStone);
                    }
                    else {
                        if(Calculate.planking)Display.quantitiesPlanking[newStone.getID()]++;
                        else Display.quantities[newStone.getID()]++;
                        newStone.addSQFT();
                    }
                }

                else if(s.equals("-textBox-")) {

                    EditableTextBox current = null;
                    int textBoxId = Integer.parseInt(x.next());

                    for(EditableTextBox t: Display.textBoxes) {
                        if(textBoxId == t.getId()) {
                            current = t;
                        }
                    }
                    current.setValue("");

                    String word = x.next();
                    while(!word.equals("-endOfValue-")) {
                        current.setValue(current.getValue() + " " + word);
                        word = x.next();
                    }
                }

                else if(s.equals("-checkBox-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    int index = Integer.parseInt(x.nextLine());
                    Display.sizesSelected[index] = current;
                    x.next();
                }

                else if(s.equals("-quantity-")) {
                    int current = Integer.parseInt(x.nextLine());
                    int index = Integer.parseInt(x.nextLine());
                    Display.quantitiesSelected[index] = current;
                    x.next();
                }

                else if(s.equals("-size-")) {
                    int printSize = Integer.parseInt(x.nextLine());
                    Display.printSize = printSize;

                    x.next();
                }

                else if(s.equals("-adjacent-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.adjStonesBoolean = current;
                    x.next();
                }

                else if(s.equals("-4corners-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.allow4CornersBoolean = current;
                    x.next();
                }

                else if(s.equals("-forceV-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.forceVBoolean = current;
                    x.next();
                }

                else if(s.equals("-forceH-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.forceHBoolean = current;
                    x.next();
                }

                else if(s.equals("-preventVJoint-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.preventVJoint = current;
                    x.next();
                }

                else if(s.equals("-allowBorderOverlap-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.allowBorderOverlap = current;
                    x.next();
                }

                else if(s.equals("-fromLeft-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.drawFromLeft = current;
                    x.next();
                }

                else if(s.equals("-fromTop-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.drawFromTop = current;
                    x.next();
                }

                else if(s.equals("-sameWidthInRow-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.sameWidthInRow = current;
                    x.next();
                }


                //display

                else if(s.equals("-numberLabel-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.numberLabelBoolean = current;
                    x.next();
                }

                else if(s.equals("-sizeLabel-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.sizeLabelBoolean = current;
                    x.next();
                }

                else if(s.equals("-displayInfo-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.displayInfoBoolean = current;
                    x.next();
                }

                else if(s.equals("-displayQuantities-")) {
                    Boolean current = Boolean.parseBoolean(x.nextLine());
                    Display.displayQuantitiesBoolean = current;
                    x.next();
                }




                for(EditableTextBox t: Display.textBoxes) { //removes spaces from the beginning and end
                    t.setValue(t.getValue().trim());
                }
            }
        }
        br.close();
    }

    public static void undo() throws IOException{
        File folder = new File(Projects.undoFolder);
        File[] listOfFiles = folder.listFiles();

        if(undoIndex>listOfFiles.length )undoIndex = listOfFiles.length - 1;

        if(undoIndex>0) {
            undoIndex--;
            System.out.println("undo index: " + undoIndex);
            System.out.println("undo list length: " +listOfFiles.length);
            openProject(listOfFiles[undoIndex], 1);
        }
    }

    public static void redo() throws IOException{
        File folder = new File(Projects.undoFolder);
        File[] listOfFiles = folder.listFiles();
        int max = listOfFiles.length;

        if(undoIndex < max - 1) {
            undoIndex++;
            System.out.println(undoIndex);
            openProject(listOfFiles[undoIndex], 1);
        }
    }
}
