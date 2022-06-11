package com.kaooa.objects;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;

import com.kaooa.GamePage;
import com.kaooa.R;

public class PointView extends AppCompatImageButton {

    String pointId;
    int pointDrawableId;
    public String pointColor;
    public int pointNumber;
    final private float initialScaleX;
    final private float initialScaleY;
    Drawable pointDrawable;

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointView); // typed Array
        try {
            pointDrawableId = typedArray.getResourceId(R.styleable.PointView_point_drawable, R.color.purple_200); // get id of resource
            pointId = getResources().getResourceEntryName(this.getId());
            if (pointId.startsWith("point")) // *** debugging discard later
                pointNumber = Integer.parseInt(pointId.split("_")[1]);
            pointColor = typedArray.getString(R.styleable.PointView_point_color); // getColorCode as String

            pointDrawable = AppCompatResources.getDrawable(context, pointDrawableId); // drawable object

            initialScaleX = this.getScaleX();
            initialScaleY = this.getScaleY();

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

    public void resetPointScale() {
        ObjectAnimator scaleDrawableX = ObjectAnimator.ofFloat(this, "scaleX", this.getScaleX(), initialScaleX);
        ObjectAnimator scaleDrawableY = ObjectAnimator.ofFloat(this, "scaleY", this.getScaleY(), initialScaleY);

        AnimatorSet scaleDrawableSet = new AnimatorSet();
        scaleDrawableSet.playTogether(scaleDrawableX, scaleDrawableY);
        scaleDrawableSet.setDuration(600);

        scaleDrawableSet.start();
    }

    private void increasePointScale() {
        ObjectAnimator scaleDrawableX = ObjectAnimator.ofFloat(this, "scaleX", initialScaleX, 1.2f * initialScaleX);
        ObjectAnimator scaleDrawableY = ObjectAnimator.ofFloat(this, "scaleY", initialScaleY, 1.2f * initialScaleY);

        AnimatorSet scalePointSet = new AnimatorSet();
        scalePointSet.playTogether(scaleDrawableX, scaleDrawableY);
        scalePointSet.setDuration(600);

        scalePointSet.start();
    }

    public void animatePoint() {
        increasePointScale();
        GamePage.animateOnClickPoint(this);
    }
}
