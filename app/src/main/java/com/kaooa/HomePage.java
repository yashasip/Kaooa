package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    TextView gameStartText;
    TextView gameTitle;
    ImageButton gameSettings;
    ImageView bird1, bird2;

    float displayMaxX, displayMaxY;
    AnimatorSet scaleTitleSet, swoopBirdsSet;
    ObjectAnimator blinker;
    AnimatorSet startAnimatorSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        gameStartText = findViewById(R.id.gameStart);
        gameSettings = findViewById(R.id.game_settings);
        gameTitle = findViewById(R.id.gameTitle);
        bird1 = findViewById(R.id.flybird1);
        bird2 = findViewById(R.id.flybird2);

        initializeDisplayDimensions();
        startAnimations();

        gameSettings.setOnClickListener(view->openSettingsMenu());
        gameStartText.setOnClickListener(view -> startGame());
    }

    public void openSettingsMenu() {
        rotateGear();
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void startGame() {
        Intent startGameConfigIntent = new Intent(HomePage.this, GameConfigPage.class);
        startActivity(startGameConfigIntent);
    }

    private void initializeDisplayDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        display.getSize(mdispSize);
        displayMaxX = mdispSize.x;
        displayMaxY = mdispSize.y;
    }

    private void rotateGear(){
        ObjectAnimator rotate = ObjectAnimator.ofFloat(gameSettings, "rotation", 0f, 360f);
        rotate.setDuration(1000);
        rotate.start();
    }

    private void gameTitleAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(gameTitle, "scaleX", 2.0f, gameTitle.getScaleX());
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(gameTitle, "scaleY", 2.0f, gameTitle.getScaleY());
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());

        scaleTitleSet = new AnimatorSet();
        scaleTitleSet.playTogether(scaleX, scaleY);
        scaleTitleSet.setStartDelay(500);
        scaleTitleSet.setDuration(1500);

        scaleTitleSet.start();
    }

    private void blinkText() {
        blinker = ObjectAnimator.ofFloat(gameStartText, "alpha", 1f);

        blinker.setDuration(1400);
        blinker.setRepeatCount(ObjectAnimator.INFINITE);
        blinker.setRepeatMode(ObjectAnimator.REVERSE);
    }

    private void setupBirdAnimation(){
        bird1.setImageResource(R.drawable.avd_bird);
        bird2.setImageResource(R.drawable.avd_bird);

        AnimatedVectorDrawable flyBird1 = (AnimatedVectorDrawable) bird1.getDrawable();
        flyBird1.start();
        AnimatedVectorDrawable flyBird2 = (AnimatedVectorDrawable) bird2.getDrawable();
        flyBird2.start();
        flyBird2.setTint(getResources().getColor(R.color.theme_red));
    }

    private void birdsSwoopAnimation() {
        // setup flying birds
        ObjectAnimator rotateBird1, rotateBird2;
        ObjectAnimator swoopBird1, swoopBird2;
        Path swoopPath1, swoopPath2;
        AnimatorSet flyBirdsSet, rotateBirdsSet;

        swoopBirdsSet = new AnimatorSet();
        setupBirdAnimation();

        swoopPath1 = new Path();
        swoopPath2 = new Path();

        swoopPath1.arcTo(displayMaxX / 2, 0f, displayMaxX, displayMaxY, 270f, -180f, true); // bird on right
        swoopPath2.arcTo(0f, 0f, displayMaxX / 2 - 100, displayMaxY, 270f, 180f, true); // bird on left

        rotateBird1 = ObjectAnimator.ofFloat(bird1, "rotationY", 0f, 180f);
        rotateBird2 = ObjectAnimator.ofFloat(bird2, "rotationY", 180f, 360f);
        rotateBird1.setInterpolator(new LinearInterpolator());
        rotateBird2.setInterpolator(new LinearInterpolator());

        swoopBird1 = ObjectAnimator.ofFloat(bird1, View.X, View.Y, swoopPath1);
        swoopBird2 = ObjectAnimator.ofFloat(bird2, View.X, View.Y, swoopPath2);
        swoopBird1.setInterpolator(new DecelerateInterpolator());
        swoopBird2.setInterpolator(new DecelerateInterpolator());

        flyBirdsSet = new AnimatorSet();
        flyBirdsSet.playTogether(swoopBird1, swoopBird2);

        rotateBirdsSet = new AnimatorSet();
        rotateBirdsSet.playTogether(rotateBird1, rotateBird2);
        rotateBirdsSet.setDuration(1000);
        rotateBirdsSet.setStartDelay(2000);

        flyBirdsSet.setStartDelay(700);
        flyBirdsSet.setDuration(7000);

        swoopBirdsSet.playTogether(flyBirdsSet, rotateBirdsSet);
    }

    private void setupAnimations() {
        gameTitleAnimation();
        birdsSwoopAnimation();
        blinkText();
    }

    private void startAnimations() {
        startAnimatorSet = new AnimatorSet();
        setupAnimations();
        startAnimatorSet.play(scaleTitleSet).with(swoopBirdsSet);
        startAnimatorSet.play(swoopBirdsSet).before(blinker);
        startAnimatorSet.start();
    }

}