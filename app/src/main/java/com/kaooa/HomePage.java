package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    TextView gameStartText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        gameStartText = findViewById(R.id.gameStart);

        blinkText();

        gameStartText.setOnClickListener(view -> startGame());
    }

    public void openSettings(View view) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void startGame() {
        Intent startGameConfigIntent = new Intent(HomePage.this, GameConfigPage.class);
        startActivity(startGameConfigIntent);
    }

    private void blinkText() {
        ObjectAnimator blinker = ObjectAnimator.ofFloat(gameStartText, "alpha", 0f);

        blinker.setDuration(1300);
        blinker.setRepeatCount(ObjectAnimator.INFINITE);
        blinker.setRepeatMode(ObjectAnimator.REVERSE);

        blinker.start();
    }

}