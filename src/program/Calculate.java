package program;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Calculate {

    private static final int JOINT_MAX = 3;
    private static final boolean DEBUG = false;

    static int attempts;
    static int maxAttempts = 500;
    public static boolean planking = false;
    static Stone coordStone = new Stone(); //stone that the nextCoord is connected too
    static int checkpoint;

    public static boolean create() {

        int stonesPlaced = 0;
        checkpoint = 1;
        double drawFrequency;

        if((Display.xDim + Display.yDim) < 300) drawFrequency = 6;
        else drawFrequency = (Display.xDim + Display.yDim)/20;

        if(Display.stoneSelectionMethod.equals("addSingleStoneRandom")) {
            drawFrequency = 0; 	//add one stone before returning
            attempts = 0;
        }




        while(true) {
            if(attempts > maxAttempts) {
                try {Loading.saveProject(1);}
                catch (IOException e) {e.printStackTrace();}
                if(DEBUG)System.out.println("saved drawing finished with error");

                for(Stone stone: Display.stones) {
                    stone.setPrevious();
                }
                return true;
            }

            int i = newStone(); //chooses point, chooses stone, places stone, returns what happened.

            if(i == 0) {				//stone placed but not full
                stonesPlaced++;
                if(stonesPlaced>drawFrequency) {
                    stonesPlaced = 0;

                    if(Display.stoneSelectionMethod == "addSingleStoneRandom" ) {
                        Display.stoneSelectionMethod = Display.oldStoneSelectionMethod;
                        try {Loading.saveProject(1);}
                        catch (IOException e1) {e1.printStackTrace();}
                        if(DEBUG)System.out.println("saved insert random stone");
                    }

                    return false;
                }
            }

            if(i == 1) {	//stone placed patio full
                if(DEBUG)System.out.println("Pattern Completed");
                if(stonesPlaced > 0) {
                    try {Loading.saveProject(1);}
                    catch (IOException e) {e.printStackTrace();}
                    if(DEBUG)System.out.println("saved drawing finished");
                    for(Stone stone: Display.stones) {
                        stone.setPrevious();
                    }
                }
                return true;		//full
            }

            if(i == 2) {				//error
                attempts++;
                if(Display.stoneSelectionMethod != "addSingleStoneRandom") {
                    if(!(attempts > maxAttempts))Display.clearRecent(false);
                }
                else Display.stoneSelectionMethod = Display.oldStoneSelectionMethod;
                return false;

            }
        }
    }



    private static int newStone() {
        Random rand = new Random();

        int[] p1 = new int[2];
        p1 = findNextCoord();

        if(p1[0] == 9999) {
            return 1;	//no more open points
        }

        if(Display.stoneSelectionMethod.equals("quantities")) {
            boolean everyStoneUsed = true;
            for(int i = 0; i < Display.quantities.length; i++) {
                if(Display.quantities[i] < Display.quantitiesSelected[i])everyStoneUsed = false;
            }

            if(everyStoneUsed)return 1;		//no more stones left to place
        }


        updateStonesAvailable();
        Stone newStone = new Stone();

        boolean canPlace = false;
        boolean attemptJointFix = true;

        while(!canPlace) {

            newStone = chooseStone();
            newStone.setX(p1[0]);
            newStone.setY(p1[1]);


            if(rand.nextInt(2) == 1) {
                rotate(newStone);
            }

            rotate(newStone); 	//planking needs stones horizontal.

            int longJoint = createsLongJoint(newStone); //returns side with longest joint, or -1

            boolean cantPlace = false;

            if(Display.preventVJoint) { //if trying to prevent vertical joints
                if(jointLength(newStone, 1, "", true)>1) { //if right side joint length is 2
                    rotate(newStone);
                    if(jointLength(newStone, 1, "", true)>1) {//if right side joint length is still 2
                        cantPlace = true; //prevent stone from placing no matter what
                    }
                }
            }

            //checking if the stone can possibly work if sameWidthInRow is true
            if(Display.sameWidthInRow) {
                for(Stone s: adjacents(newStone)) {
                    if(adjacent(newStone,s,3)) {
                        if(s.getID() != 100) {
                            if((	s.getW() != newStone.getW() &&
                                    s.getW() != newStone.getL() &&
                                    s.getL() != newStone.getW() &&
                                    s.getL() != newStone.getL()))cantPlace = true;
                        }
                    }
                }
            }

            if(!cantPlace) { //if you can possibly place it...
                if(attemptJointFix && longJoint != -1) { //long joint returns 0 1 2 or 3, meaning there is a long joint
                    //when attemptJointFix is true, it checks if a stone breaks a joint
                    if((!createsThirdCorner(newStone))
                            && (!stoneOverlapsAny(newStone))
                            && (!createsBadGap(newStone))
                            && (!borderOverlapTooLarge(newStone))
                            && (!identicalAdjacents(newStone))
                            && (sameWidthInRow(newStone))) {
                        if(longJoint == -1)canPlace = true;
                        else if(edgeFlush(newStone, longJoint)) {
                            canPlace = true;
                        }
                    }
                    if(canPlace)break;
                    //rotates and tries again
                    rotate(newStone); // tries same stone but rotated
                    if((!createsThirdCorner(newStone))
                            && (!stoneOverlapsAny(newStone))
                            && (!createsBadGap(newStone))
                            && (!borderOverlapTooLarge(newStone))
                            && (!identicalAdjacents(newStone))
                            && (sameWidthInRow(newStone))) {
                        if(longJoint == -1)canPlace = true;
                        else if(edgeFlush(newStone, longJoint)) {
                            canPlace = true;
                        }
                    }
                    if(canPlace)break;
                }


                //if it cant, places a stone that doesnt create a long joint, or just any stone that works
                if(!attemptJointFix || longJoint == -1) {
                    // attempting to place, ignoring joints 1
                    if((!createsThirdCorner(newStone))
                            && (!stoneOverlapsAny(newStone))
                            && (!createsBadGap(newStone))
                            && (!borderOverlapTooLarge(newStone))
                            && (!identicalAdjacents(newStone))
                            && (sameWidthInRow(newStone))) {
                        canPlace = true;
                    }
                    if(canPlace)break;

                    // attempting to place, ignoring joints 2
                    rotate(newStone);
                    if((!createsThirdCorner(newStone))
                            && (!stoneOverlapsAny(newStone))
                            && (!createsBadGap(newStone))
                            && (!borderOverlapTooLarge(newStone))
                            && (!identicalAdjacents(newStone))
                            && (sameWidthInRow(newStone))) {
                        canPlace = true;
                    }

                    if(canPlace)break;
                }
            }


            //if still cant place, delete from stones available and try a different stone
            for(int i = 0; i<Display.stonesAvailable.size(); i++) {
                if(newStone.getID() == Display.stonesAvailable.get(i).getID()) {
                    Display.stonesAvailable.remove(Display.stonesAvailable.get(i));
                }
            }

            //If there are no more stones available, it is finished
            if(Display.stonesAvailable.size() == 0) {
                if(attemptJointFix) {
                    attemptJointFix = false;
                    updateStonesAvailable();
                }
                else return 2;
            }

        }

        if(planking == false)Display.quantities[newStone.getID()] ++;
        else Display.quantitiesPlanking[newStone.getID()] ++;

        Display.stones.add(newStone);	//stone added
        newStone.addSQFT();
        return 0;
    }

    public static boolean sameWidthInRow(Stone stone) {
        int[] testCoord = coordinates(stone);
        int ty1 = testCoord[1];
        int ty3 = testCoord[5];

        if(stone.getX() == 0)return true;

        if(Display.sameWidthInRow == false)return true;

        for(Stone s: adjacents(stone)) {
            int[] checkCoord = coordinates(s);
            int cy1 = checkCoord[1];
            int cy3 = checkCoord[5];

            if(adjacent(stone,s,3)){
                if(s.getID() == 100) {
                    if(ty1 >= cy1 && ty3 <= cy3)return true;
                }
                if(cy1 == ty1 && cy3 == ty3)return true;
            }
        }
        return false;
    }


    public static int round6(double num, double multiple) {

        int temp = (int) (num%multiple);
        if (temp<multiple/2)
            return (int) (num-temp);
        else
            return (int) (num+multiple-temp);
    }

    public static double clamp(double value, double min, double max) {
        if(value<min)return min;
        if(value>max)return max;
        return value;
    }

    public static boolean isBetween(double value, double min, double max) {
        if(value<min)return false;
        if(value>=max)return false;
        return true;
    }

    private static boolean identicalAdjacents(Stone stone) {
        if(Display.adjStonesBoolean)return false;//user doesnt care if there are adjacents

        boolean IAExists = false;
        ArrayList<Stone> adjacentStones = adjacents(stone);

        for(Stone s: adjacentStones) {
            if(s.getW() == stone.getW() && s.getL() == stone.getL()) {
                IAExists = true;
            }
        }
        if(DEBUG) {
            if(IAExists)System.out.println("error border overlap too large");
        }

        return IAExists;
    }

    private static boolean edgeFlush(Stone stone, int side) {//0top, 1right, 2bot, 3left
        int[] testCoord = coordinates(stone);
        int tx1 = testCoord[0];
        int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        int ty2 = testCoord[3];
        int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        //int tx4 = testCoord[6];
        //int ty4 = testCoord[7];


        for(Stone checkStone: Display.stones) {
            if(adjacent(checkStone,stone)) {
                int[] checkCoord = coordinates(checkStone);
                //int cx1 = checkCoord[0];
                //int cy1 = checkCoord[1];
                int cx2 = checkCoord[2];
                //int cy2 = checkCoord[3];
                //int cx3 = checkCoord[4];
                int cy3 = checkCoord[5];
                int cx4 = checkCoord[6];
                int cy4 = checkCoord[7];


                if(side == 0) {
                    if(tx2 == cx2 && ty1 == cy4) {
                        return true;	//break joint along top edge
                    }
                }
                if(side == 1) {
                    if(ty2 == cy4 && tx2 != cx4) {
                        return true;	//break joint along right edge
                    }
                }
                if(side == 2) {
                    boolean stoneUnderCheck = false;
                    for(Stone check: adjacents(checkStone)) {
                        int[] ccheckCoord = coordinates(check);
                        //int ccx1 = ccheckCoord[0];
                        int ccy1 = ccheckCoord[1];
                        int ccx2 = ccheckCoord[2];
                        //int ccy2 = ccheckCoord[3];
                        //int ccx3 = ccheckCoord[4];
                        int ccy3 = ccheckCoord[5];
                        //int ccx4 = ccheckCoord[6];
                        //int ccy4 = ccheckCoord[7];

                        if(ccy1 == cy3) {
                            if(jointLength(check, 2, "", true) > 2) {
                                stoneUnderCheck = true;
                                if(ty3 != ccy3 && tx1 == ccx2) {
                                    return true;	//break joint along bot edge
                                }
                            }
                        }
                    }
                    if(!stoneUnderCheck) {
                        if(ty3 != cy3 && tx1 == cx2) {
                            return true;	//break joint along bot edge
                        }
                    }

                }
                if(side == 3) {
                    if(ty3 == cy3 && tx3 == cx4) {
                        return true;	//break joint along left edge
                    }
                }
            }
        }
        return false;
    }

    private static boolean borderOverlapTooLarge(Stone stone) {

        if(Display.allowBorderOverlap) return false;

        int[] testCoord = coordinates(stone);
        //int tx1 = testCoord[0];
        //int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        //int ty2 = testCoord[3];
        //int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        //int tx4 = testCoord[6];
        //int ty4 = testCoord[7];

        boolean overlapTooLarge = false;



        if(!planking) {
            if(Display.xDim%12 == 0) {
                if(tx2 - Display.xDim > 5 )overlapTooLarge = true;
            }
            else if(tx2 - Display.xDim > 11 )overlapTooLarge = true;

            if(Display.yDim%12 == 0) {
                if(ty3 - Display.yDim > 5)overlapTooLarge = true;
            }
            else if(ty3 - Display.yDim > 11)overlapTooLarge = true;
        }

        if(planking) {
            if(Display.xDim%12 == 0) {
                if(tx2 - Display.xDim > 5 )overlapTooLarge = true;
            }
            else if(tx2 - Display.xDim > 11 )overlapTooLarge = true;

            if(ty3 - Display.yDim > 4)overlapTooLarge = true;
        }

        if(DEBUG) {
            if(overlapTooLarge)System.out.println("error border overlap too large");
        }
        return overlapTooLarge;
    }


    private static int createsLongJoint(Stone stone) {
        int jl0 = 0;
        int jl1 = 0;
        int jl2 = 0;
        int jl3 = 0;

        int maxJoint = JOINT_MAX;
        if(planking) maxJoint = maxJoint*3;

        int[] testCoord = coordinates(stone);
        int tx2 = testCoord[2];
        int ty3 = testCoord[5];

        if(DEBUG)System.out.println("\n\n\n\n testing size: " + stone.getSize() + "\n");
        if(DEBUG)System.out.println("\nX:" + stone.getX() + "\nW:" + stone.getW() + "\nTX:" + tx2);

        if(stone.getY() != 0)	jl0 = jointLength(stone, 0, "", true);//0 top
        if(tx2 != Display.xDim) jl1 = jointLength(stone, 1, "", true);//1 right
        if(ty3 != Display.yDim) jl2 = jointLength(stone, 2, "", true);//2 bot
        if(stone.getX() != 0)	jl3 = jointLength(stone, 3, "", true);//3 left



        int longestJL = jl0;
        if(jl1 > longestJL)longestJL = jl1;
        if(jl2 > longestJL)longestJL = jl2;
        if(jl3 > longestJL)longestJL = jl3;

        if(longestJL >= JOINT_MAX) {
            if(longestJL == jl0) {
                return 0;
            }
            if(longestJL == jl1) {
                return 1;
            }
            if(longestJL == jl2) {
                return 2;
            }
            if(longestJL == jl3) {
                return 3;
            }
        }

        return -1;
    }

    private static int jointLength(Stone stone, int side, String direction, boolean original) {//0top, 1right, 2bot, 3left
        int[] testCoord = coordinates(stone);
        int tx1 = testCoord[0];
        int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        int ty2 = testCoord[3];
        int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        int tx4 = testCoord[6];
        int ty4 = testCoord[7];

        if(original) {
            if(DEBUG)System.out.print("(" + stone.getX() + "," + stone.getY() + ")");
            if(side == 0) {//top, check joint right and left
                int jl = jointLength(stone, side, "left", false) + jointLength(stone, side, "right", false) - 1;
                if(DEBUG)System.out.println("top jl: " + jl);
                return 	jl;
            }
            else if(side == 1) {//right, check joint up and down
                for(Stone f: Display.obstacles) {
                    if(adjacent(stone,f,1)) return 1;
                }
                int jl = jointLength(stone, side, "up", false) + jointLength(stone, side, "down", false) - 1;
                if(DEBUG)System.out.println("right jl: " + jl);
                return 	jl;
            }
            else if(side == 2) {//bottom, check joint right and left
                int jl = jointLength(stone, side, "left", false) + jointLength(stone, side, "right", false) - 1;
                if(DEBUG)System.out.println("bottom jl: " + jl);
                return jl;
            }
            else if(side == 3) {//left, check joint up and down
                int jl = jointLength(stone, side, "up", false) + jointLength(stone, side, "down", false) - 1;
                if(DEBUG)System.out.println("left jl: " + jl);
                return jl;
            }
        }

        for(Stone checkStone: Display.stones) {

            int[] checkCoord = coordinates(checkStone);
            int cx1 = checkCoord[0];
            int cy1 = checkCoord[1];
            int cx2 = checkCoord[2];
            //int cy2 = checkCoord[3];
            //int cx3 = checkCoord[4];
            //int cy3 = checkCoord[5];
            int cx4 = checkCoord[6];
            int cy4 = checkCoord[7];



            if(side == 0) { // top

                if(ty1 == 0) return 1;
                if(pointOverlapFeaturePoint(tx1,ty1) || pointOverlapFeaturePoint(tx2,ty2)) {
                    if(DEBUG)System.out.println("stonePointOverlapsFeaturePoint");
                    return 0;
                }
                if(direction.equals("left")) {//check in left direction
                    if(ty1 == cy1 && tx1 == cx2 )return 1 + jointLength(checkStone, side, direction, false);
                }
                if(direction.equals("right")) {//check in right direction
                    if(ty1 == cy1 && tx2 == cx1 )return 1 + jointLength(checkStone, side, direction, false);
                }
            }
            else if(side == 1) { // right



                if(tx2 >= Display.xDim) return 1;
                if(		pointOverlapFeaturePoint(tx2,ty2) &&
                        !pointOverlapSpecificFeaturePoint(tx2,ty2,3) &&
                        !pointOverlapSpecificFeaturePoint(tx2,ty2,4) ) {
                    if(DEBUG)System.out.println("stonePointOverlapsFeaturePoint");
                    return 0;
                }

                if(direction.equals("up")) {//check in up direction
                    if(tx2 == cx2 && ty1 == cy4)return 1 + jointLength(checkStone, side, direction, false);	//right
                }
                if(direction.equals("down")) {//check in down direction
                    if(tx2 == cx2 && ty3 == cy1)return 1 + jointLength(checkStone, side, direction, false);	//right
                }

            }
            else if(side == 2) { // bot

                if(ty3 >= Display.yDim) return 1;
                if(pointOverlapFeaturePoint(tx3,ty3) || pointOverlapFeaturePoint(tx4,ty4)) {
                    if(DEBUG)System.out.println("stonePointOverlapsFeaturePoint");
                    return 0;
                }
                if(direction.equals("left")) {//check in left direction
                    if(ty3 == cy4 && tx1 == cx2)return 1 + jointLength(checkStone, side, direction, false);	//bot
                }
                if(direction.equals("right")) {//check in right direction
                    if(ty3 == cy4 && tx2 == cx1)return 1 + jointLength(checkStone, side, direction, false);	//bot
                }

            }
            else if(side == 3) { // left


                if(tx1 == 0) return 1;
                if(pointOverlapFeaturePoint(tx1,ty1) || pointOverlapFeaturePoint(tx3,ty3)) {
                    if(DEBUG)System.out.println("stonePointOverlapsFeaturePoint");
                    return 0;
                }
                if(direction.equals("up")) {//check in up direction
                    if(tx1 == cx1 && ty1 == cy4)return 1 + jointLength(checkStone, side, direction, false);	//left
                }
                if(direction.equals("down")) {//check in down direction
                    if(tx1 == cx1 && ty3 == cy1)return 1 + jointLength(checkStone, side, direction, false);	//left
                }

            }
        }
        return 1;
    }

    private static boolean adjacent(Stone stone1, Stone stone2, int side) {
        int[] coordA = coordinates(stone1);
        int[] coordB = coordinates(stone2);

        int ax1 = coordA[0];
        int ay1 = coordA[1];
        int ax2 = coordA[2];
        int ay2 = coordA[3];
        int ax3 = coordA[4];
        int ay3 = coordA[5];
        int ax4 = coordA[6];
        int ay4 = coordA[7];

        int bx1 = coordB[0];
        int by1 = coordB[1];
        int bx2 = coordB[2];
        int by2 = coordB[3];
        int bx3 = coordB[4];
        int by3 = coordB[5];
        int bx4 = coordB[6];
        int by4 = coordB[7];

        if(side == 0) { // top
            if(ay1 == by3 && bx2 > ax1 && bx1 < ax2) return true;
            else return false;
        }

        if(side == 1) { // right
            if(ax2 == bx1 && by1 < ay3 && by3 > ay1) return true;
            else return false;
        }

        if(side == 2) { // bot
            if(ay3 == by1 && bx2 > ax1 && bx1 < ax2) return true;
            else return false;
        }

        if(side == 3) { // left
            if(ax1 == bx2 && by1 < ay3 && by3 > ay1) return true;
            else return false;
        }

        return false;
    }

    private static boolean adjacent(Stone stone1, Stone stone2) {
        int[] coordA = coordinates(stone1);
        int[] coordB = coordinates(stone2);

        int ax1 = coordA[0];
        int ay1 = coordA[1];
        int ax2 = coordA[2];
        //int ay2 = coordA[3];
        //int ax3 = coordA[4];
        int ay3 = coordA[5];
        //int ax4 = coordA[6];
        //int ay4 = coordA[7];

        //int bx1 = coordB[0];
        int by1 = coordB[1];
        int bx2 = coordB[2];
        //int by2 = coordB[3];
        int bx3 = coordB[4];
        int by3 = coordB[5];
        //int bx4 = coordB[6];
        //int by4 = coordB[7];

        if(ay1 > by3 || ax2 < bx3 || ay3 < by1 || ax1 > bx2)return false;

        return true;
    }

    static ArrayList<Stone> adjacents(Stone test) {
        ArrayList<Stone> ret = new ArrayList<>();
        int[] coordA = coordinates(test);
        int ax1 = coordA[0];
        int ay1 = coordA[1];
        int ax2 = coordA[2];
        //int ay2 = coordA[3];
        //int ax3 = coordA[4];
        int ay3 = coordA[5];
        //int ax4 = coordA[6];
        //int ay4 = coordA[7];

        for(Stone check: Display.stones) {
            int[] coordB = coordinates(check);
            int bx1 = coordB[0];
            int by1 = coordB[1];
            int bx2 = coordB[2];
            //int by2 = coordB[3];
            int bx3 = coordB[4];
            int by3 = coordB[5];
            //int bx4 = coordB[6];
            //int by4 = coordB[7];

            if(bx1 != ax1 || by1 != ay1) {
                if(!(ay1 > by3 || ax2 < bx3 || ay3 < by1 || ax1 > bx2))ret.add(check);
            }

        }
        return ret;
    }

    private static boolean createsBadGap(Stone stone) {
        if(createsHorizontalGap(stone) || createsVerticalGap(stone)) {
            if(DEBUG)System.out.println("error creates bad gap");
            return true;
        }
        return false;
    }

    private static boolean createsVerticalGap(Stone stone) {
        if(stone.getID()==100)return false;

        int[] testCoord = coordinates(stone);
        int tx1 = testCoord[0];
        int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        //int ty2 = testCoord[3];
        int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        int tx4 = testCoord[6];
        //int ty4 = testCoord[7];

        for(Stone checkStone: Display.stones) {
            int[] checkCoord = coordinates(checkStone);
            //int cx1 = checkCoord[0];
            int cy1 = checkCoord[1];
            int cx2 = checkCoord[2];
            //int cy2 = checkCoord[3];
            int cx3 = checkCoord[4];
            int cy3 = checkCoord[5];
            int cx4 = checkCoord[6];
            //int cy4 = checkCoord[7];

            if((ty1 == cy3) && (tx2<cx4) && (tx2>cx3)) {
                if(((cx4-tx2)!=6) || (RSBottomEdgeMatch(checkStone, 2)) || pointAgainstSide(stone, 4, "right"));
                else {
                    if(DEBUG)System.out.println("error creates bad vertical gap 1");
                    return true;
                }
            }

            if((ty1 == cy3) && (cx4<tx2) && (cx4>tx1)) {
                if((tx2-cx4)!=6 || (RSBottomEdgeMatch(checkStone, 2)));
                else {
                    if(DEBUG)System.out.println("error creates bad vertical gap 2");
                    return true;
                }
            }

            if((ty3 == cy1) && (cx2<tx4) && (cx2>tx3)) {
                if(((tx4-cx2)!=6)||(RSBottomEdgeMatch(stone, 1)) || pointAgainstSide(stone, 4, "bottom"));
                else {
                    if(DEBUG)System.out.println("error creates bad vertical gap 3");
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean createsHorizontalGap(Stone stone) {
        if(stone.getID()==100)return false;

        int[] testCoord = coordinates(stone);
        int tx1 = testCoord[0];
        //int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        int ty2 = testCoord[3];
        //int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        //int tx4 = testCoord[6];
        int ty4 = testCoord[7];

        for(Stone checkStone: Display.stones) {
            int[] checkCoord = coordinates(checkStone);
            int cx1 = checkCoord[0];
            //int cy1 = checkCoord[1];
            int cx2 = checkCoord[2];
            int cy2 = checkCoord[3];
            //int cx3 = checkCoord[4];
            int cy3 = checkCoord[5];
            //int cx4 = checkCoord[6];
            int cy4 = checkCoord[7];

            if((tx1 == cx2) && (ty3<cy4) && (ty3>cy2)) {
                if((cy4-ty3) != 6 || (BSRightEdgeMatch(checkStone, 2)) || pointAgainstSide(stone, 4, "bottom"));
                else{
                    if(DEBUG)System.out.println("error creates bad horizontal gap 1");
                    return true;
                }
            }

            if((tx1 == cx2) && (cy4<ty3) && (cy4>ty2)) {
                if((ty3-cy4) != 6 || (BSRightEdgeMatch(checkStone, 2)));
                else{
                    if(DEBUG)System.out.println("error creates bad horizontal gap 2");
                    return true;
                }
            }

            if((tx2 == cx1) && (cy3<ty4) && (cy3>ty2)) {
                if(((ty4-cy3) != 6)||(BSRightEdgeMatch(stone, 1)) || pointAgainstSide(stone, 4, "right"));
                else{
                    if(DEBUG)System.out.println("error creates bad horizontal gap 3");
                    return true;
                }
            }

        }
        return false;
    }
    private static boolean pointAgainstSide(Stone stone, int point, String side) {

        int x;
        int y;

        int[] testCoord = coordinates(stone);
        int tx1 = testCoord[0];
        int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        int ty2 = testCoord[3];
        int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        int tx4 = testCoord[6];
        int ty4 = testCoord[7];

        for(Stone s: adjacents(stone)) {
            int[] checkCoord = coordinates(s);
            int cx1 = checkCoord[0];
            int cy1 = checkCoord[1];
            int cx2 = checkCoord[2];
            int cy2 = checkCoord[3];
            int cx3 = checkCoord[4];
            int cy3 = checkCoord[5];
            int cx4 = checkCoord[6];
            int cy4 = checkCoord[7];


            if(point == 4) {
                if(side.equals("right")) {
                    if(tx2 == cx1) {
                        if(cy1 < ty3 && cy3 > ty3) return true;
                    }
                }

                if(side.equals("bottom")) {
                    if(ty3 == cy1) {
                        if(cx1 < tx2 && cx2 > tx2) return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean RSBottomEdgeMatch(Stone stone,int amount) {

        int[] testCoord = coordinates(stone);

        int tx4 = testCoord[6];
        int ty4 = testCoord[7];

        if(pointOverlap(tx4,ty4) == amount)return true;
        else return false;
    }

    private static boolean BSRightEdgeMatch(Stone stone, int amount) {

        int[] testCoord = coordinates(stone);

        int tx4 = testCoord[6];
        int ty4 = testCoord[7];

        if(pointOverlap(tx4,ty4) == amount)return true;
        else return false;
    }

    private static int[] findNextCoord() {
        int[] nextCoord = new int[2];
        nextCoord[0] = 0;
        nextCoord[1] = 0;
        int x = 9999;
        int y = 9999;

        int xMult = 1;
        int yMult = 1;

        if(Display.drawFromLeft)xMult = 15;
        if(Display.drawFromTop)yMult = 15;

        if(pointOverlap(0,0) == 0)return nextCoord;

        if(Display.allow4CornersBoolean) {
            for(Stone stone: Display.stones) {		//finds coord closest to 0,0 that a stone can be placed
                int[] testCoord = coordinates(stone);
                int tx2 = testCoord[2];
                int ty2 = testCoord[3];
                int tx3 = testCoord[4];
                int ty3 = testCoord[5];


                if(!stoneAtPoint(tx2,ty2)) {
                    boolean pointCovered = false;
                    for(Stone test: adjacents(stone)) {
                        int[] testC = coordinates(test);
                        int testx2 = testC[2];
                        int testy2 = testC[3];
                        int testx3 = testC[4];
                        int testy3 = testC[5];
                        if(tx2 == testx3 && testy3 > ty2 && testy2 < ty2)pointCovered = true;
                    }
                    if(!pointCovered) {
                        if((	pathag(tx2*xMult, ty2*yMult)< pathag(x*xMult, y*yMult)) &&
                                isBetween(tx2,0,Display.xDim) &&
                                isBetween(ty2,0,Display.yDim))
                        {
                            x = tx2;
                            y = ty2;
                        }
                    }
                }

                if(!stoneAtPoint(tx3,ty3)) {
                    boolean pointCovered = false;
                    for(Stone test: adjacents(stone)) {
                        int[] testC = coordinates(test);
                        int testx2 = testC[2];
                        int testy2 = testC[3];
                        int testx3 = testC[4];
                        int testy3 = testC[5];
                        if(ty3 == testy2 && testx2 > tx3 && testx3 < tx3)pointCovered = true;
                    }
                    if(!pointCovered) {
                        if((pathag(tx3*xMult, ty3*yMult)< pathag(x*xMult, y*yMult)) &&
                                isBetween(tx3,0,Display.xDim) &&
                                isBetween(ty3,0,Display.yDim))
                        {
                            x = tx3;
                            y = ty3;
                        }
                    }
                }
            }
            nextCoord[0] = x;
            nextCoord[1] = y;
            return nextCoord;
        }


        for(Stone stone: Display.stones) {		//finds coord closest to 0,0 that a stone can be placed
            int[] testCoord = coordinates(stone);
            int tx2 = testCoord[2];
            int ty2 = testCoord[3];
            int tx3 = testCoord[4];
            int ty3 = testCoord[5];





            if(pointOverlap(tx2,ty2) == 1) {
                if((pathag(tx2*xMult, ty2*yMult)< pathag(x*xMult, y*yMult)) &&
                        isBetween(tx2,0,Display.xDim) &&
                        isBetween(ty2,0,Display.yDim))
                {
                    x = tx2;
                    y = ty2;
                }
            }
            if(pointOverlap(tx3,ty3) == 1) {
                if((pathag(tx3*xMult, ty3*yMult)< pathag(x*xMult, y*yMult)) &&
                        isBetween(tx3,0,Display.xDim) &&
                        isBetween(ty3,0,Display.yDim))
                {
                    x = tx3;
                    y = ty3;
                }
            }

            if(pointOverlap(tx2,ty2) == 3 && pointOverlapFeaturePoint(tx2,ty2)) {
                if((pathag(tx2*xMult, ty2*yMult)< pathag(x*xMult, y*yMult)) &&
                        isBetween(tx2,0,Display.xDim) &&
                        isBetween(ty2,0,Display.yDim))
                {
                    x = tx2;
                    y = ty2;
                }
            }

            if(pointOverlap(tx3,ty3) == 3 && pointOverlapFeaturePoint(tx3,ty3)) {
                if((pathag(tx3*xMult, ty3*yMult)< pathag(x*xMult, y*yMult)) &&
                        isBetween(tx3,0,Display.xDim) &&
                        isBetween(ty3,0,Display.yDim))
                {
                    x = tx3;
                    y = ty3;
                }
            }
        }
        nextCoord[0] = x;
        nextCoord[1] = y;
        return nextCoord;
    }

    private static double pathag(int x, int y) {
        //return Math.pow(Math.pow(x, 2) + Math.pow(y, 2), .5);
        return (x+y)/2;
    }

    private static boolean stoneOverlapsAny(Stone testStone) {		// checks if a stone overlaps another
        for(Stone stone: Display.stones) {
            if(stonesOverlap(testStone, stone )) {
                if(DEBUG)System.out.println("error stone overlaps stone");
                return true;
            }
        }
        return false;
    }

    private static boolean stonesOverlap(Stone testStone, Stone checkStone) {

        int[] testCoord = coordinates(testStone);
        int tX = testCoord[0];
        int tY = testCoord[1];
        int tA = testCoord[6];
        int tB = testCoord[7];

        int[] checkCoord = coordinates(checkStone);
        int cX = checkCoord[0];
        int cY = checkCoord[1];
        int cA = checkCoord[6];
        int cB = checkCoord[7];

        if(tA<=cX || cA<=tX || tB<=cY || cB<=tY)return false;
        return true;

    }

    private static boolean stoneAtPoint(int x, int y) {//checks if a stone exists at given point

        for(Stone stone: Display.stones) {
            if(stone.getX() == x && stone.getY() == y) {
                return true;
            }
        }
        return false;

    }

    public static int pointOverlap(int x, int y) {		// returns how many times a point is shared.
        int shared = 0 ; // how many times the point is shared
        for(Stone stone: Display.stones) {
            int[] testCoord = coordinates(stone);
            int tx1 = testCoord[0];
            int ty1 = testCoord[1];
            int tx2 = testCoord[2];
            int ty2 = testCoord[3];
            int tx3 = testCoord[4];
            int ty3 = testCoord[5];
            int tx4 = testCoord[6];
            int ty4 = testCoord[7];

            if(x == tx1 && y == ty1)shared++;
            if(x == tx2 && y == ty2)shared++;
            if(x == tx3 && y == ty3)shared++;
            if(x == tx4 && y == ty4)shared++;

        }
        return shared;
    }

    public static int pointOverlapStonePoint(int x, int y) {		// returns how many times a point is shared
        int shared = 0 ; // how many times the point is shared
        for(Stone stone: Display.stones) {
            int[] testCoord = coordinates(stone);
            int tx1 = testCoord[0];
            int ty1 = testCoord[1];
            int tx2 = testCoord[2];
            int ty2 = testCoord[3];
            int tx3 = testCoord[4];
            int ty3 = testCoord[5];
            int tx4 = testCoord[6];
            int ty4 = testCoord[7];
            if(stone.getID() != 100) {
                if(x == tx1 && y == ty1)shared++;
                if(x == tx2 && y == ty2)shared++;
                if(x == tx3 && y == ty3)shared++;
                if(x == tx4 && y == ty4)shared++;
            }
        }
        return shared;
    }

    public static boolean pointOverlapFeaturePoint(int x, int y) {		// returns how many times a point is shared
        int shared = 0 ; // how many times the point is shared
        for(Stone stone: Display.obstacles) {
            int[] testCoord = coordinates(stone);
            int tx1 = testCoord[0];
            int ty1 = testCoord[1];
            int tx2 = testCoord[2];
            int ty2 = testCoord[3];
            int tx3 = testCoord[4];
            int ty3 = testCoord[5];
            int tx4 = testCoord[6];
            int ty4 = testCoord[7];

            if(x == tx1 && y == ty1)return true;
            if(x == tx2 && y == ty2)return true;
            if(x == tx3 && y == ty3)return true;
            if(x == tx4 && y == ty4)return true;

        }
        return false;
    }

    public static boolean pointOverlapSpecificFeaturePoint(int x, int y, int point) {		// returns how many times a point is shared
        int shared = 0 ; // how many times the point is shared
        for(Stone stone: Display.obstacles) {
            int[] testCoord = coordinates(stone);
            int tx1 = testCoord[0];
            int ty1 = testCoord[1];
            int tx2 = testCoord[2];
            int ty2 = testCoord[3];
            int tx3 = testCoord[4];
            int ty3 = testCoord[5];
            int tx4 = testCoord[6];
            int ty4 = testCoord[7];

            if(point == 1) {
                if(x == tx1 && y == ty1)return true;
            }
            if(point == 2) {
                if(x == tx2 && y == ty2)return true;
            }
            if(point == 3) {
                if(x == tx3 && y == ty3)return true;
            }
            if(point == 4) {
                if(x == tx4 && y == ty4)return true;
            }





        }
        return false;
    }

    public static boolean stonePointOverlapsFeaturePoint(Stone stone) {		// returns how many times a point is shared
        int[] checkCoord = coordinates(stone);
        int cx1 = checkCoord[0];
        int cy1 = checkCoord[1];
        int cx2 = checkCoord[2];
        int cy2 = checkCoord[3];
        int cx3 = checkCoord[4];
        int cy3 = checkCoord[5];
        int cx4 = checkCoord[6];
        int cy4 = checkCoord[7];


        if(pointOverlapFeaturePoint(cx1,cy1))return true;
        if(pointOverlapFeaturePoint(cx2,cy2))return true;
        if(pointOverlapFeaturePoint(cx3,cy3))return true;
        if(pointOverlapFeaturePoint(cx4,cy4))return true;
        return false;

    }

    private static boolean createsThirdCorner(Stone stone) {		//checks to see if the stone will create a third corner
        if(Display.allow4CornersBoolean) return false; //User allows 4 corners

        int[] testCoord = coordinates(stone);
        int tx1 = testCoord[0];
        int ty1 = testCoord[1];
        int tx2 = testCoord[2];
        int ty2 = testCoord[3];
        int tx3 = testCoord[4];
        int ty3 = testCoord[5];
        int tx4 = testCoord[6];
        int ty4 = testCoord[7];


        if(pointOverlapStonePoint(tx2,ty2)>1) {
            if(!pointOverlapFeaturePoint(tx2,ty2)) {
                if(DEBUG)System.out.println("error: bottom left point overlaps");
                return true;
            }
        }

        if(pointOverlapStonePoint(tx3,ty3)>1) {
            if(!pointOverlapFeaturePoint(tx3,ty3)) {
                if(DEBUG)System.out.println("error: top right pointoverlap");
                return true;
            }

        }

        if(pointOverlapStonePoint(tx4,ty4)>1) {
            if(!pointOverlapFeaturePoint(tx4,ty4)) {
                if(DEBUG)System.out.println("error: bottom right point overlaps");
                return true;
            }
        }
        return false;
    }

    private static boolean topRightPointOverlap(int cx3, int cy3) {

        for(Stone stone: Display.stones) {
            int[] testCoord = coordinates(stone);
            int tx2 = testCoord[2];
            int ty2 = testCoord[3];

            boolean FeaturePoint = pointOverlapFeaturePoint(cx3,cy3);

            if(cx3 == tx2 && cy3 == ty2 && !FeaturePoint)return true;
        }

        return false;
    }

    private static boolean bottomRightOverlapsTopLeft(int x, int y) {

        for(Stone stone: Display.stones) {
            int[] testCoord = coordinates(stone);
            int tx1 = testCoord[0];
            int ty1 = testCoord[1];

            boolean FeaturePoint = pointOverlapFeaturePoint(x,y);

            if(x == tx1 && y == ty1 && !FeaturePoint)return true;
        }

        return false;
    }

    private static boolean bottomLeftPointOverlap(int x, int y) {
        for(Stone stone: Display.stones) {
            int[] testCoord = coordinates(stone);
            int tx3 = testCoord[4];
            int ty3 = testCoord[5];

            boolean FeaturePoint = pointOverlapFeaturePoint(x,y);

            if(x == tx3 && y == ty3 && !FeaturePoint)return true;
        }

        return false;
    }

    public static void rotate(Stone stone) {
        int w = stone.getW();
        int l = stone.getL();

        if(Display.forceVBoolean && !Display.forceHBoolean && !planking) { //all stones vertical
            if(w > l) { // if width is greater than length, swap.
                stone.setL(w);
                stone.setW(l);
            }
        }

        else if(!Display.forceVBoolean && Display.forceHBoolean && !planking) { //all stones horizontal
            if(w < l) { // if width is less than length, swap.
                stone.setL(w);
                stone.setW(l);
            }
        }

        else if(!Display.forceVBoolean && !Display.forceHBoolean && !planking) { //normal rotate function
            stone.setL(w);
            stone.setW(l);
        }

        else if(planking) { //normal rotate function
            if(w == 5 || w == 10 || w == 15) {
                stone.setL(w);
                stone.setW(l);
            }
        }
    }

    private static Stone chooseStone() {
        Random rand = new Random();
        ArrayList<Stone> stonesAvailableSQ = new ArrayList<Stone>(); //smallest quanity
        ArrayList<Stone> probability = new ArrayList<Stone>();
        int  randomStone = rand.nextInt(Display.stonesAvailable.size());
        int  randomStoneSQ = 0;

        //System.out.println("choosing stone");

        //Balanced////////////////////
        if(Display.stoneSelectionMethod.equals("balanced")) {
            int sq = 100000; // stone quantity
            int quantity = 0;
            for(Stone stone: Display.stonesAvailable) {	//find smallest quantity
                quantity = Display.quantities[stone.getID()];
                if(quantity < sq) {
                    sq = quantity;
                }
            }
            for(Stone stone: Display.stonesAvailable) {	//find all stones with smallest quantity
                quantity = Display.quantities[stone.getID()];
                if(quantity == sq) {
                    stonesAvailableSQ.add(stone);
                }
            }
            randomStoneSQ = rand.nextInt(stonesAvailableSQ.size());
        }
        //////////////////////////////

        //quantities//////////////////
        if(Display.stoneSelectionMethod.equals("quantities")) {

            for(Stone stone: Display.stonesAvailable) {//for every stone available

                int amountPlaced = Display.quantities[stone.getID()];
                int amountDesired = Display.quantitiesSelected[stone.getID()];

                //System.out.println(stone.getSize() + ": " + amountPlaced + "/" + amountDesired);



                for(int i = amountPlaced; i < amountDesired; i++) {
                    probability.add(new Stone(stone));		//adds the stone to probability, amountplaced - amountDesired
                }
            }
        }
        /////////////////////////////


        if(Display.stoneSelectionMethod.equals("balanced")) {
            if(stonesAvailableSQ.size() < 1) return new Stone(Display.allStones.get(0));
            return new Stone(stonesAvailableSQ.get(randomStoneSQ));
        }

        if(Display.stoneSelectionMethod.equals("quantities")) {
            if(probability.size() < 1) {
                return new Stone(Display.allStones.get(0));
            }
            else return new Stone(probability.get(rand.nextInt(probability.size())));
        }

        if(Display.stoneSelectionMethod.equals("planking")) {//3 4 5 6 10 11 12 13 17 18 19 20
            ArrayList<Stone> plankingProbability = new ArrayList<Stone>();
            plankingProbability.addAll(Display.stonesAvailable);
            for(int i = 0; i<3; i++) {
                for(Stone s: Display.stonesAvailable) {

                    if (s.getID() == 3)plankingProbability.add(new Stone(s));
                    if (s.getID() == 4)plankingProbability.add(new Stone(s));
                    if (s.getID() == 5)plankingProbability.add(new Stone(s));
                    if (s.getID() == 6)plankingProbability.add(new Stone(s));
                    if (s.getID() == 10)plankingProbability.add(new Stone(s));
                    if (s.getID() == 11)plankingProbability.add(new Stone(s));
                    if (s.getID() == 12)plankingProbability.add(new Stone(s));
                    if (s.getID() == 13)plankingProbability.add(new Stone(s));
                }
            }

            for(int i = 0; i<1; i++) {
                for(Stone s: Display.stonesAvailable) {
                    if (s.getID() == 3)plankingProbability.add(new Stone(s));
                    if (s.getID() == 4)plankingProbability.add(new Stone(s));
                    if (s.getID() == 5)plankingProbability.add(new Stone(s));
                    if (s.getID() == 6)plankingProbability.add(new Stone(s));
                }
            }
            if(plankingProbability.size() < 1) {
                return new Stone(Display.allStones.get(0));
            }
            else return new Stone(plankingProbability.get(rand.nextInt(plankingProbability.size())));

        }

        //random
        return new Stone (Display.stonesAvailable.get(randomStone));

    }



    public static void updateStonesAvailable() {
        Display.stonesAvailable.clear();
        updateAllStones();

        //System.out.println("updating stones available");

        //using checkboxes
        if(!Display.stoneSelectionMethod.equals("quantities")) {
            if(planking == false) {
                for(int i = 0; i<Display.allStones.size(); i++) {
                    if(Display.sizesSelected[i])Display.stonesAvailable.add(new Stone(Display.allStones.get(i)));
                }
            }
        }

        //using quantities
        else if(Display.stoneSelectionMethod.equals("quantities")) {
            if(planking == false) {
                for(int i = 0; i<Display.allStones.size(); i++) {
                    System.out.println(Display.allStones.get(i).getSize());
                    System.out.println("	amount placed: " + Display.quantities[i]);
                    System.out.println("	amount desired: " + Display.quantitiesSelected[i]);

                    if(Display.quantitiesSelected[i]>0 && Display.quantities[i] < Display.quantitiesSelected[i]) {
                        Display.stonesAvailable.add(new Stone(Display.allStones.get(i)));

                    }
                }
            }
        }

        //if planking, doesnt matter
        if(planking == true) {
            for(int i = 0; i<Display.allStones.size(); i++) {
                Display.stonesAvailable.add(new Stone(Display.allStones.get(i)));
            }
        }
    }

    public static void updateAllStones() {
        Display.allStones.clear();

        if(planking == false) {
            Display.allStones.add(new Stone("12x12",12,12,0));
            Display.allStones.add(new Stone("12x18",12,18,1));
            Display.allStones.add(new Stone("12x24",12,24,2));
            Display.allStones.add(new Stone("12x30",12,30,3));
            Display.allStones.add(new Stone("12x36",12,36,4));
            Display.allStones.add(new Stone("12x42",12,42,5));
            Display.allStones.add(new Stone("12x48",12,48,6));

            Display.allStones.add(new Stone("18x18",18,18,7));
            Display.allStones.add(new Stone("18x24",18,24,8));
            Display.allStones.add(new Stone("18x30",18,30,9));
            Display.allStones.add(new Stone("18x36",18,36,10));
            Display.allStones.add(new Stone("18x42",18,42,11));
            Display.allStones.add(new Stone("18x48",18,48,12));

            Display.allStones.add(new Stone("24x24",24,24,13));
            Display.allStones.add(new Stone("24x30",24,30,14));
            Display.allStones.add(new Stone("24x36",24,36,15));
            Display.allStones.add(new Stone("24x42",24,42,16));
            Display.allStones.add(new Stone("24x48",24,48,17));

            Display.allStones.add(new Stone("30x30",30,30,18));
            Display.allStones.add(new Stone("30x36",30,36,19));
            Display.allStones.add(new Stone("30x42",30,42,20));
            Display.allStones.add(new Stone("30x48",30,48,21));

            Display.allStones.add(new Stone("36x36",36,36,22));
            Display.allStones.add(new Stone("36x42",36,42,23));
            Display.allStones.add(new Stone("36x48",36,48,24));

            Display.allStones.add(new Stone("42x42",42,42,25));
            Display.allStones.add(new Stone("42x48",42,48,26));

            Display.allStones.add(new Stone("48x48",48,48,27));
        }


        if(planking == true) {
            Display.allStones.add(new Stone("5x12",5,12,0));
            Display.allStones.add(new Stone("5x18",5,18,1));
            Display.allStones.add(new Stone("5x24",5,24,2));
            Display.allStones.add(new Stone("5x30",5,30,3));
            Display.allStones.add(new Stone("5x36",5,36,4));
            Display.allStones.add(new Stone("5x42",5,42,5));
            Display.allStones.add(new Stone("5x48",5,48,6));

            Display.allStones.add(new Stone("10x12",10,12,7));
            Display.allStones.add(new Stone("10x18",10,18,8));
            Display.allStones.add(new Stone("10x24",10,24,9));
            Display.allStones.add(new Stone("10x30",10,30,10));
            Display.allStones.add(new Stone("10x36",10,36,11));
            Display.allStones.add(new Stone("10x42",10,42,12));
            Display.allStones.add(new Stone("10x48",10,48,13));

            Display.allStones.add(new Stone("15x18",15,18,14));
            Display.allStones.add(new Stone("15x24",15,24,15));
            Display.allStones.add(new Stone("15x30",15,30,16));
            Display.allStones.add(new Stone("15x36",15,36,17));
            Display.allStones.add(new Stone("15x42",15,42,18));
            Display.allStones.add(new Stone("15x48",15,48,19));
        }
    }

    public static int[] coordinates(Stone stone) {  	//coords [0   1   2   3   4   5   6   7]
        int[] coords = new int[8];						//		  x1  y1  x2  y2  x3  y3  x4  y4
        int x = stone.getX();
        int y = stone.getY();
        int w = stone.getW();
        int l = stone.getL();

        coords[0] = x;		//p1						p1---------------p2
        coords[1] = y;		//							|				 |
        coords[2] = x+w;	//p2						|				 |
        coords[3] = y;		//							p3---------------p4
        coords[4] = x;		//p3
        coords[5] = y+l;
        coords[6] = x+w;	//p4
        coords[7] = y+l;


        return coords;
    }

    public static boolean isFloat( String input ) {
        try {
            Float.parseFloat( input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }
}
