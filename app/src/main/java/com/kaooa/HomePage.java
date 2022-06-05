package com.kaooa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        TextView gameStartText = findViewById(R.id.gameStart);
        gameStartText.setOnClickListener(view -> startGame());
    }

    public void openSettings(View view) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void startGame() {
        Intent startGameIntent = new Intent(HomePage.this, GamePage.class);
        startActivity(startGameIntent);
    }

}