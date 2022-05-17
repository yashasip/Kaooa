package com.kaooa.objects;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;

import com.kaooa.R;

public class PointView extends AppCompatImageButton {

    Context context;
    int pointDrawableId;
    String pointColor;
    Drawable pointDrawable;

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context; // current context

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointView); // typed Array
        try {
            pointDrawableId = typedArray.getResourceId(R.styleable.PointView_point_drawable, R.color.purple_200); // get id of resource
            pointColor = typedArray.getString(R.styleable.PointView_point_color); // getColorCode as String

            pointDrawable = AppCompatResources.getDrawable(context, pointDrawableId); // drawable object
            this.changeColor(pointColor);
            setImageDrawable(pointDrawable); // set drawable
        } finally {
            typedArray.recycle(); // garbage collection handler
        }
        this.setOnClickListener(view -> scalePoint());
    }

    public void changeColor(String colorCode) {
        pointDrawable.setTint(Color.parseColor(colorCode));
    }

    public void scalePoint() {
        ObjectAnimator scaleDrawableX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.25f);
        ObjectAnimator scaleDrawableY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.25f);
        AnimatorSet scaleDrawableSet = new AnimatorSet();
        scaleDrawableSet.playTogether(scaleDrawableX, scaleDrawableY);
        scaleDrawableSet.setDuration(800);
        scaleDrawableSet.start();
    }
}
