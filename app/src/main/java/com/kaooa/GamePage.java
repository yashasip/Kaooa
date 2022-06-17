package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kaooa.objects.EdgeView;
import com.kaooa.objects.PointView;

public class GamePage extends AppCompatActivity {
    static EdgeView[] edgeViews;
    static PointView lastClicked;
    ImageButton pauseBtn;

    // game parameters
    // turn
    static boolean crowsTurn = true; // crow starts game
    static boolean vulturesTurn = false;

    // placement
    static boolean crowsPlaced = false;
    static boolean vulturePlaced = false;

    static int unplacedCrowCount = 7; // crow count

    // Headers
    TextView turnHeader, turnGuideline;

    // Header and Guideline
    static String crowTurnHeaderText;
    static String vultureTurnHeaderText;
    static String crowPlaceGuideline;
    static String vulturePlaceGuideline;
    static String crowMoveGuideline;
    static String vultureMoveGuideline;

    // bird colors
    static int crowColor;
    static int vultureColor;


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

        pauseBtn = findViewById(R.id.gamePause);
        turnHeader = findViewById(R.id.turn_header);
        turnGuideline = findViewById(R.id.turn_guideline);

        initializeHeaders();
        initializeEdgeSet();

        pauseBtn.setOnClickListener(view -> openPauseMenu());
    }

    public void openPauseMenu() {
        setEdge(); // testing & debugging
    }

    private void initializeEdgeSet() {
        edgeViews = new EdgeView[15];
        int edgeAddCount = 0;
        int edgeResId;
        String edgeId;
        String p1, p2;
        PointView point1View, point2View;

        for (int i = 0; i < 10; i++) {
            for (int j = i; j < 10; j++) {
                if (starMapMatrix[i][j]) { // if edge exists store <edgeId, edgeWidget(EdgeView)>
                    // the current index value
                    p1 = String.valueOf(i + 1);
                    p2 = String.valueOf(j + 1);
                    edgeId = "edge_" + p1 + "_" + p2;
                    edgeResId = getResources().getIdentifier(edgeId, "id", getPackageName()); // fetch id
                    edgeViews[edgeAddCount] = findViewById(edgeResId);
                    point1View = getPointView(p1);
                    point2View = getPointView(p2);

                    edgeViews[edgeAddCount].initialize(edgeId, point1View, point2View);
                    edgeAddCount++;
                }
            }
        }
    }

    private PointView getPointView(String pointId) {
        int pointResId;

        // fetch integer ids of point
        pointResId = getResources().getIdentifier("point_" + pointId, "id", getPackageName());

        return findViewById(pointResId);
    }

    void setEdge() {
        int[] loc1 = new int[2];
        int[] loc2 = new int[2];
        for (int i = 0; i < 15; i++) {
            // get coordinates (must be calculated on the go)
            edgeViews[i].endPoints[0].getLocationOnScreen(loc1);
            edgeViews[i].endPoints[1].getLocationOnScreen(loc2);

            edgeViews[i].setupEdge(loc1, loc2);
        }
    }

    static void resetAnimateOnClickPoint() {
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

    private static void setNextTurn(TextView turnHeader, TextView turnGuideline) {
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

    static void setTurn(PointView point) {
        if (crowsTurn) {
            if (!crowsPlaced) // color is set in placement phase only
                point.changeColor(crowColor);
            if (--unplacedCrowCount == 0) { // reduce placed crow count
                crowsPlaced = true; // when all crows are placed
            }
        } else if (vulturesTurn) {
            if (!vulturePlaced) { // color is set in placement phase only
                point.changeColor(vultureColor);
                vulturePlaced = true; // when one vulture is placed
            }
        }
    }

    void initializeHeaders() { // initialize headers when Game Begins
        if (crowsTurn) {
            turnHeader.setText(crowTurnHeaderText);
            turnGuideline.setText(crowMoveGuideline);
        } else if (vulturesTurn) {
            turnHeader.setText(vultureTurnHeaderText);
            turnGuideline.setText(vultureMoveGuideline);
        }
    }

    private static void animateOnClickPoint(PointView point) {
        if (point.equals(lastClicked))
            return;

        resetAnimateOnClickPoint();
        lastClicked = point;

        // edge flow starts after bird placement
        if ((!crowsPlaced && crowsTurn) || (!vulturePlaced && vulturesTurn)) {
            point.resetPointScale();
            return;
        }

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

    public static void updateGameState(PointView point, TextView turnHeader, TextView turnGuideline) {
        animateOnClickPoint(point);
        setTurn(point);
        setNextTurn(turnHeader, turnGuideline);
    }
}