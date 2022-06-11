package com.kaooa.objects;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;

import com.kaooa.GamePage;
import com.kaooa.R;

import java.util.HashMap;

public class PointView extends AppCompatImageButton {

    Context context;
    String pointId;
    int pointNumber;
    int pointDrawableId;
    String pointColor;
    Drawable pointDrawable;
    static String lastClicked = "";

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context; // current context

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointView); // typed Array
        try {
            pointDrawableId = typedArray.getResourceId(R.styleable.PointView_point_drawable, R.color.purple_200); // get id of resource
            pointId = getResources().getResourceEntryName(this.getId());
            if(pointId.startsWith("point")) // *** debugging discard later
                pointNumber = Integer.parseInt(pointId.split("_")[1]);
            pointColor = typedArray.getString(R.styleable.PointView_point_color); // getColorCode as String

            pointDrawable = AppCompatResources.getDrawable(context, pointDrawableId); // drawable object
            this.changeColor(pointColor);
            setImageDrawable(pointDrawable); // set drawable
        } finally {
            typedArray.recycle(); // garbage collection handler
        }
        this.setOnClickListener(view -> animatePoint());
    }

    public void changeColor(String colorCode) {
        pointDrawable.setTint(Color.parseColor(colorCode));
    }

    void resetPointAnimation(){
        ObjectAnimator scaleDrawableX = ObjectAnimator.ofFloat(this, "scaleX", 2.5f, 2.2f);
        ObjectAnimator scaleDrawableY = ObjectAnimator.ofFloat(this, "scaleY", 2.5f, 2.2f);
        AnimatorSet scaleDrawableSet = new AnimatorSet();
        scaleDrawableSet.playTogether(scaleDrawableX, scaleDrawableY);
        scaleDrawableSet.setDuration(800);
        scaleDrawableSet.start();
    }

    public void animatePoint() {
        if(this.pointId.equals(lastClicked))
            return;

        lastClicked = this.pointId;

        ObjectAnimator scaleDrawableX = ObjectAnimator.ofFloat(this, "scaleX", 2.2f, 2.5f);
        ObjectAnimator scaleDrawableY = ObjectAnimator.ofFloat(this, "scaleY", 2.2f, 2.5f);
        AnimatorSet scaleDrawableSet = new AnimatorSet();
        scaleDrawableSet.playTogether(scaleDrawableX, scaleDrawableY);
        scaleDrawableSet.setDuration(800);
        scaleDrawableSet.start();

        HashMap<String, ProgressBar> pointEdges = GamePage.getPointEdges(pointNumber);
        for(String key: pointEdges.keySet()){
            ProgressBar edgeView = pointEdges.get(key);

            edgeView.setIndeterminate(true); // set edge flow
            edgeView.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor(this.pointColor)));
            String[] dum = key.split("_");
            if(Integer.parseInt(dum[1]) != pointNumber){
                edgeView.setScaleX(-edgeView.getScaleX());
            }
        }
    }
}
