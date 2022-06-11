package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kaooa.objects.PointView;

import java.util.HashMap;

public class GamePage extends AppCompatActivity {
    static HashMap<String, ProgressBar> edgeSetMap;

    static boolean[][] starMapMatrix = { // adjacent matrix of the star map
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
        initializeEdgeSet();
    }

    public void openSettings(View view) {
        setEdge();
    }

    private void initializeEdgeSet() {
        edgeSetMap = new HashMap<>(15);
        int edgeId;
        ProgressBar edgeView;
        String p1, p2;

        for (int i = 0; i < 10; i++) {
            for (int j = i; j < 10; j++) {
                if (starMapMatrix[i][j]) { // if edge exists store <edgeId, edgeWidget(ProgressBar)>
                    // the current index value
                    p1 = String.valueOf(i + 1);
                    p2 = String.valueOf(j + 1);

                    edgeId = getResources().getIdentifier("edge_" + p1 + "_" + p2, "id", getPackageName()); // fetch id
                    edgeView = findViewById(edgeId);
                    edgeSetMap.put("edge_" + p1 + "_" + p2, edgeView);
                }
            }
        }
    }

    private String[] getEdgeEndpoints(String edgeId) {
        String[] splitString = edgeId.split("_"); // has {"edge", "point1", "point2"}

        return new String[]{splitString[1], splitString[2]}; // return only point values
    }

    private PointView getPointView(String pointId) {
        int pointResId;

        // fetch integer ids of point
        pointResId = getResources().getIdentifier("point_" + pointId, "id", getPackageName());

        return findViewById(pointResId);
    }

    public static HashMap<String, ProgressBar> getPointEdges(int pointNumber){
        // use edge map to find all the edges
        String edgeId;
        HashMap<String, ProgressBar> pointEdges = new HashMap<>();
        for(int i=0; i<10; i++){
            if(starMapMatrix[pointNumber-1][i]){
                if(pointNumber < i+1) // ensures sorted point number
                    edgeId = "edge_"+ pointNumber +"_"+ (i+1);
                else
                    edgeId = "edge_"+ (i+1) +"_"+ (pointNumber);

                pointEdges.put(edgeId, edgeSetMap.get(edgeId));
            }
        }

        return pointEdges;
    }

    void setEdge() {
        ProgressBar edgeView;
        int[] p1Loc = new int[2];
        int[] p2Loc = new int[2];
        int length;
        float rotateAngle;

        for (String edgeId : edgeSetMap.keySet()) {
            String[] endpoints = getEdgeEndpoints(edgeId);
            edgeView = edgeSetMap.get(edgeId);
            assert edgeView != null; // check for edgeView != null

            PointView point1View = getPointView(endpoints[0]);
            PointView point2View = getPointView(endpoints[1]);

            // get point space coordinates on screen
            point1View.getLocationOnScreen(p1Loc);
            point2View.getLocationOnScreen(p2Loc);

            // extending the edge
            length = (int) Math.sqrt(Math.pow(p1Loc[1] - p2Loc[1], 2) + Math.pow(p1Loc[0] - p2Loc[0], 2));
            edgeView.setScaleX(length / (float) edgeView.getWidth()); // assumes width of edgeView is > 0

            edgeView.setIndeterminate(false);

            // setting the angle
            rotateAngle = (float) Math.toDegrees(Math.atan((double) (p1Loc[1] - p2Loc[1]) / (p1Loc[0] - p2Loc[0])));
            edgeView.setRotation(rotateAngle);
        }
    }
}