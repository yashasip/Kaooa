package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.kaooa.objects.EdgeView;
import com.kaooa.objects.PointView;

public class GamePage extends AppCompatActivity {
    static EdgeView[] edgeViews;
    static PointView lastClicked;

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

        lastClicked = null;

        initializeEdgeSet();
    }

    public void openSettings(View view) {
        setEdge();
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

    public static void animateOnClickPoint(PointView point) {
        if (point.equals(lastClicked))
            return;

        resetAnimateOnClickPoint();
        lastClicked = point;

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
}