package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;

public class GameConfigPage extends AppCompatActivity {

    ImageButton playButton;
    ImageButton bird1, bird2;
    private int bird1ColorResId;
    private int bird2ColorResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_config_page);

        playButton = findViewById(R.id.start_game);
        bird1 = findViewById(R.id.play_as_1);
        bird2 = findViewById(R.id.play_as_2);

        bird1ColorResId = getResources().getColor(R.color.theme_red);
        bird2ColorResId = getResources().getColor(R.color.theme_black);

        birdAnimation();

        playButton.setOnClickListener(view -> playGame());
    }

    private void birdAnimation() {
        // set avd resource
        bird1.setImageResource(R.drawable.avd_bird);
        bird2.setImageResource(R.drawable.avd_bird);

        // create AVD object
        AnimatedVectorDrawable flyBird1 = (AnimatedVectorDrawable) bird1.getDrawable();
        AnimatedVectorDrawable flyBird2 = (AnimatedVectorDrawable) bird2.getDrawable();

        flyBird1.setTint(bird1ColorResId); // change bird color
        flyBird2.setTint(bird2ColorResId);

        flyBird1.start();
        flyBird2.start();
    }

    public void playGame() {
        Intent startGameIntent = new Intent(GameConfigPage.this, GamePage.class);
        startActivity(startGameIntent);
    }
}