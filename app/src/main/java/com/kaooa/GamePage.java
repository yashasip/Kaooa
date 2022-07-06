package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kaooa.dialogs.WinDialog;
import com.kaooa.enums.State;
import com.kaooa.objects.EdgeView;
import com.kaooa.objects.PointView;


public class GamePage extends AppCompatActivity {
    EdgeView[] edgeViews;
    PointView lastClicked;
    PointView[] pointViews;
    ImageButton pauseBtn;

    // game parameters
    // game termination
    boolean gameOver = false;
    State winningBird = State.NONE;
    // turn
    boolean crowsTurn = true; // crow starts game
    boolean vulturesTurn = false;

    // placement
    boolean crowsPlaced = false;
    boolean vulturePlaced = false;

    int unplacedCrowCount = 7; // crow count

    // Headers
    TextView turnHeader, turnGuideline; // used weak reference

    // Header and Guideline
    String crowTurnHeaderText;
    String vultureTurnHeaderText;
    String crowPlaceGuideline;
    String vulturePlaceGuideline;
    String crowMoveGuideline;
    String vultureMoveGuideline;

    // star map colors
    int crowColor;
    int vultureColor;
    int noColor;

    // game result parameters
    int crowKillCount=0; // crow kill counter

    State[] placeMatrix = {State.NONE, State.NONE, State.NONE, State.NONE, State.NONE, State.NONE, State.NONE, State.NONE, State.NONE, State.NONE};

    boolean[][] starMapMatrix = { // adjacent matrix of the star map
            {false, true, false, false, false, false, false, false, false, true},
            {true, false, true, true, false, false, false, false, false, true},
            {false, true, false, true, false, false, false, false, false, false},
            {false, true, true, false, true, true, false, false, false, false},
            {false, false, false, true, false, true, false, false, false, false},
            {false, false, false, true, true, false, true, true, false, false},
            {false, false, false, false, false, true, false, true, false, false},
            {false, false, false, false, false, true, true, false, true, true},
            {false, false, false, false, false, false, false, true, false, true},
            {true, true, false, false, false, false, false, true, true, false},
    };

    int[][] starStraightLinePaths = { // straightLines Possible in Star Map
            {1, 2, 4, 5},
            {5, 6, 8, 9},
            {9, 10, 2, 3},
            {3, 4, 6, 7},
            {7, 8, 10, 1}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_page);

        // initialize
        lastClicked = null;
        crowTurnHeaderText = getResources().getString(R.string.crows_turn_header); // Load Constant Strings
        vultureTurnHeaderText = getResources().getString(R.string.vultures_turn_header);
        crowPlaceGuideline = getResources().getString(R.string.crow_place_guideline);
        vulturePlaceGuideline = getResources().getString(R.string.vulture_place_guideline);
        crowMoveGuideline = getResources().getString(R.string.crow_move_guideline);
        vultureMoveGuideline = getResources().getString(R.string.vulture_move_guideline);
        crowColor = getResources().getColor(R.color.theme_black); // Load Color Values
        vultureColor = getResources().getColor(R.color.theme_red);
        noColor = getResources().getColor(R.color.theme_white);

        pauseBtn = findViewById(R.id.gamePause);
        turnHeader = findViewById(R.id.turn_header);
        turnGuideline = findViewById(R.id.turn_guideline);

        initializeHeaders();
        initializePointSet();
        initializeEdgeSet();

        pauseBtn.setOnClickListener(view -> openPauseMenu());
    }

    public void openPauseMenu() {
        // pending
    }

    private void initializePointSet() {
        int pointResId;

        pointViews = new PointView[10];

        // fetch integer ids of point
        for (int i = 0; i < 10; i++) {
            pointResId = getResources().getIdentifier("point_" + (i + 1), "id", getPackageName());
            pointViews[i] = findViewById(pointResId);
            pointViews[i].setOnClickListener(point -> updateGameState((PointView) point));
        }
    }

    private void initializeEdgeSet() {
        edgeViews = new EdgeView[15];
        int edgeIndex = 0;
        int edgeResId;
        String edgeId;
        ViewTreeObserver edgeObserver;

        for (int i = 0; i < 10; i++) {
            for (int j = i; j < 10; j++) {
                if (starMapMatrix[i][j]) { // if edge exists store <edgeId, edgeWidget(EdgeView)>
                    // the current index value
                    int finalEdgeIndex = edgeIndex; // declaring effectively final variable for lambda expression
                    edgeId = "edge_" + (i + 1) + "_" + (j + 1);
                    edgeResId = getResources().getIdentifier(edgeId, "id", getPackageName()); // fetch id
                    edgeViews[edgeIndex] = findViewById(edgeResId);

                    edgeObserver = edgeViews[i].getViewTreeObserver();
                    edgeObserver.addOnGlobalLayoutListener(() -> initializeEdgeViewSetup(finalEdgeIndex)); // setGlobalLayoutListener for initial setup

                    edgeViews[edgeIndex].initialize(edgeId, pointViews[i], pointViews[j]);
                    edgeIndex++; // increment for next edge
                }
            }
        }
    }

    void initializeEdgeViewSetup(int edgeIndex) { // sets angle, length of the EdgeView
        if(edgeViews[edgeIndex].initialized) // if already initialized return
            return;

        int[] loc1 = new int[2];
        int[] loc2 = new int[2];

        // get coordinates (must be calculated on the go)
        edgeViews[edgeIndex].endPoints[0].getLocationOnScreen(loc1);
        edgeViews[edgeIndex].endPoints[1].getLocationOnScreen(loc2);

        edgeViews[edgeIndex].setupEdge(loc1, loc2);
    }

    void resetAnimateOnClickPoint() {
        if (lastClicked == null) // for first point click
            return;

        lastClicked.resetPointScale();

        // edge flow starts after bird placement
        if ((!crowsPlaced && crowsTurn) || (!vulturePlaced && vulturesTurn))
            return;

        for (EdgeView edgeView : edgeViews) {
            if (edgeView.hasEndpoint(lastClicked))
                continue;

            edgeView.setIndeterminate(false); // reset edge flow

            // reset inverted edges
            if (!edgeView.endPoints[0].equals(lastClicked)) {
                edgeView.setScaleX(-edgeView.getScaleX());
            }
        }
    }

    private void setNextTurn() {
        if (crowsTurn) {
            crowsTurn = false; // swap turn
            vulturesTurn = true;

            // set next turn headers
            turnHeader.setText(vultureTurnHeaderText);
            if (!vulturePlaced)
                turnGuideline.setText(vulturePlaceGuideline);
            else
                turnGuideline.setText(vultureMoveGuideline);
        } else if (vulturesTurn) {
            vulturesTurn = false; // swap turn
            crowsTurn = true;

            // set next turn headers
            turnHeader.setText(crowTurnHeaderText);
            if (!crowsPlaced)
                turnGuideline.setText(crowPlaceGuideline);
            else
                turnGuideline.setText(crowMoveGuideline);
        }
    }

    void setTurn(PointView point) {
        if (crowsTurn) {
            if (!crowsPlaced) { // color is set in placement phase only
                point.changeColor(crowColor);
                placeMatrix[point.pointNumber - 1] = State.CROW;
            }
            if (--unplacedCrowCount == 0) { // reduce placed crow count
                crowsPlaced = true; // when all crows are placed
            }
        } else if (vulturesTurn) {
            if (!vulturePlaced) { // color is set in placement phase only
                point.changeColor(vultureColor);
                placeMatrix[point.pointNumber - 1] = State.VULTURE;
                vulturePlaced = true; // when one vulture is placed
            }
        }
    }

    void initializeHeaders() { // initialize headers when Game Begins
        turnHeader.setText(crowTurnHeaderText); // crow starts
        turnGuideline.setText(crowPlaceGuideline);
    }

    private void animateOnClickPoint(PointView point) {
        // edge flow starts after bird placement
        if ((!crowsPlaced && crowsTurn) || (!vulturePlaced && vulturesTurn)) {
            point.resetPointScale();
            return;
        }

        point.increasePointScale();

        for (EdgeView edgeView : edgeViews) {
            if (edgeView.hasEndpoint(point))
                continue;

            edgeView.setIndeterminate(true); // set edge flow
            edgeView.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor(point.pointColor))); // set color

            if (!edgeView.endPoints[0].equals(point)) { // if starts with the point edge should be inverted
                edgeView.setScaleX(-edgeView.getScaleX());
            }
        }
    }

    public PointView getVulturePoint() {
        for (int i = 0; i < 10; i++) {
            if (placeMatrix[i].equals(State.VULTURE)) { // search based on placeMatrix
                return pointViews[i];
            }
        }
        return null;
    }

    public PointView getCrowPoint() { // returns the crow point
        for (int i = 0; i < 10; i++) {
            if (placeMatrix[i].equals(State.CROW)) { // search based on placeMatrix
                return pointViews[i];
            }
        }
        return null;
    }

    boolean isFlightKill(int toPointNo) {
        if (!placeMatrix[toPointNo - 1].equals(State.NONE)) // if not empty space
            return false;

        int vultureNo = getVulturePoint().pointNumber;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                // if connecting node not crow
                if (!placeMatrix[starStraightLinePaths[i][j + 1] - 1].equals(State.CROW))
                    continue;

                // check if straight line path
                if (starStraightLinePaths[i][j] == vultureNo && starStraightLinePaths[i][j + 2] == toPointNo)
                    return true;
                else if (starStraightLinePaths[i][j] == toPointNo && starStraightLinePaths[i][j + 2] == vultureNo) // reverse order check
                    return true;
            }
        }
        return false;
    }

    public void movePoint(PointView fromPoint, PointView toPoint) {
        String tempColor = fromPoint.pointColor; // swap colors
        fromPoint.changeColor(toPoint.pointColor);
        toPoint.changeColor(tempColor);

        State tempState = placeMatrix[fromPoint.pointNumber - 1]; // swap state
        placeMatrix[fromPoint.pointNumber - 1] = placeMatrix[toPoint.pointNumber - 1];
        placeMatrix[toPoint.pointNumber - 1] = tempState;
    }

    public void killCrow(PointView point) {
        point.changeColor(noColor); // remove bird
        placeMatrix[point.pointNumber - 1] = State.NONE; // remove bird from placeMatrix
        crowKillCount++; // increment KilledCrow count
    }

    private boolean isAdjacent(int pointNo1, int pointNo2){ // check if path exists between two points
        return starMapMatrix[pointNo1 - 1][pointNo2 - 1];
    }

    public boolean isAdjacentNone(int pointNo1, int pointNo2) { // if egde present between points
        // if path exists and if empty point return true else false
        return isAdjacent(pointNo1, pointNo2) && placeMatrix[pointNo2 - 1].equals(State.NONE);
    }

    private boolean canVultureMove(){ // check if vulture can move
        int vulturePointNo = getVulturePoint().pointNumber;
        for(int i=0; i<10; i++){
            if(isAdjacentNone(vulturePointNo, i+1))
                return true;
            else if(isAdjacent(vulturePointNo, i+1) && placeMatrix[i].equals(State.CROW)){
                for(int j=0; j<10; j++){
                    if(!isAdjacent(i+1,j+1))
                        continue;
                    if(isFlightKill(j+1)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public PointView getKilledCrowPoint(int toPointNo) { // returns pointview of the crow which is to be killed
        int vultureNo = getVulturePoint().pointNumber;
        for (int i = 0; i < 10; i++) { // search based on common connecting node, returns it if found
            if (starMapMatrix[vultureNo - 1][i] && starMapMatrix[toPointNo - 1][i])
                return pointViews[i];
        }
        return null;
    }

    private void vultureMoveTurnSetup(){
        PointView p = getVulturePoint();
        animateOnClickPoint(p); // direct next turn to vulture
        lastClicked = p;
    }

    private void crowMoveTurnSetup() {
        PointView p = getCrowPoint();
        animateOnClickPoint(p);
        lastClicked = p;
    }

    private boolean isGameOver(){
        if(!vulturePlaced) // vulture should be placed
            return false;

        if(crowKillCount == 3) {
            winningBird = State.VULTURE; // declare winner
            return true;
        } else if(!canVultureMove()){
            winningBird = State.CROW;
            return true;
        }
        return false;
    }

    public void updateGameState(PointView point) {
        if(gameOver)
            return;

        if (vulturesTurn && vulturePlaced) { // if vulture move
            PointView vulturePoint = getVulturePoint(); // gets vulture point location
            if (isAdjacentNone(vulturePoint.pointNumber, point.pointNumber)) {
                movePoint(vulturePoint, point);
                resetAnimateOnClickPoint();
                setNextTurn();
                lastClicked = point;
            } else if (isFlightKill(point.pointNumber)) {
                PointView killedCrowPoint = getKilledCrowPoint(point.pointNumber);
                movePoint(vulturePoint, point);
                killCrow(killedCrowPoint);
                resetAnimateOnClickPoint();
                setNextTurn();
                lastClicked = point;
            }
            if (crowsTurn && crowsPlaced) { // for crows next move turn
                crowMoveTurnSetup();
            }
        } else if (crowsTurn && crowsPlaced) {
            if (placeMatrix[point.pointNumber - 1].equals(State.CROW)) {
                resetAnimateOnClickPoint();
                animateOnClickPoint(point);
                lastClicked = point;
            } else if (isAdjacentNone(lastClicked.pointNumber, point.pointNumber)) {
                movePoint(lastClicked, point);
                resetAnimateOnClickPoint();
                setNextTurn();
                lastClicked = point;
            }
            if (vulturesTurn && vulturePlaced) { // keeps the next turn of vulture ready, if its placed
                vultureMoveTurnSetup();
            }
        } else {
            if (point.equals(lastClicked)) // place phase
                return;

            resetAnimateOnClickPoint();
            lastClicked = point;

            if (placeMatrix[point.pointNumber - 1] != State.NONE)
                return;

            animateOnClickPoint(point);
            setTurn(point);
            setNextTurn();

            if (vulturesTurn && vulturePlaced) { // keeps the next turn of vulture ready, if its placed
                vultureMoveTurnSetup();
            }
        }
        if(isGameOver()){ // check after each turn for game over
            gameOver = true;
            resetAnimateOnClickPoint();
            // create winner result pop up
            WinDialog winnerDialog = new WinDialog(this, winningBird);
            winnerDialog.show(getSupportFragmentManager(), "winnerDialog");
        }

    }
}