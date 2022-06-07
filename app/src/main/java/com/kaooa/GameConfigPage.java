package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;

public class GameConfigPage extends AppCompatActivity {

    ImageButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_config_page);

        playButton = findViewById(R.id.start_game);
        playButton.setOnClickListener(view -> playGame());
    }

    public void playGame() {
        Intent startGameIntent = new Intent(GameConfigPage.this, GamePage.class);
        startActivity(startGameIntent);
    }
}