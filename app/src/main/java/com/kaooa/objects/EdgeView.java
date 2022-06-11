package com.kaooa.objects;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class EdgeView extends ProgressBar {
    public String edgeId;
    public PointView[] endPoints;
    int length;
    float rotateAngle;

    public EdgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize(String edgeId, PointView p1, PointView p2) {
        this.edgeId = edgeId;

        endPoints = new PointView[2];
        endPoints[0] = p1;
        endPoints[1] = p2;
    }

    public void setupEdge(int[] loc1, int[] loc2) { // calculates length and angle based on point coordinates
        length = (int) Math.sqrt(Math.pow(loc1[0] - loc2[0], 2) + Math.pow(loc1[1] - loc2[1], 2));
        rotateAngle = (float) Math.toDegrees(Math.atan((double) (loc1[1] - loc2[1]) / (loc1[0] - loc2[0])));

        this.setIndeterminate(false); // set flow
        this.setScaleX(length / (float) this.getWidth()); // increase size
        this.setRotation(rotateAngle); // incline
    }
}
