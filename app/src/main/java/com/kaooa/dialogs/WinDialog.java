package com.kaooa.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.kaooa.HomePage;
import com.kaooa.R;
import com.kaooa.enums.State;

public class WinDialog extends DialogFragment {
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    Context context;
    View dialogView;
    TextView winMessageView;
    ImageView birdView;
    ImageButton restartBtn, goToMenuBtn;
    State winningBird;

    @SuppressLint("InflateParams") // fix this
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity()); // setup builder for dialog
        inflater = LayoutInflater.from(context); // inflater set
        dialogView = inflater.inflate(R.layout.win_dialog, null);

        // all views of dialog box
        winMessageView = dialogView.findViewById(R.id.win_message);
        birdView = dialogView.findViewById(R.id.winner_bird);
        restartBtn = dialogView.findViewById(R.id.restart_button);
        goToMenuBtn = dialogView.findViewById(R.id.exit_button);

        setWinMessage(); //set the win parameters
        setBirdAnimation();

        restartBtn.setOnClickListener(button -> restartGame());
        goToMenuBtn.setOnClickListener(button -> exitGame());
        this.setCancelable(false); // prevents on touch outside dismiss

        builder.setView(dialogView);
        return builder.create(); // creates the dialog box
    }

    private void restartGame() {
        // animation
        restartBtn.setImageResource(R.drawable.avd_retry);
        AnimatedVectorDrawable restartLoop = (AnimatedVectorDrawable) restartBtn.getDrawable();

        restartLoop.start();

        Activity restart = (Activity) this.context; // restarts
        restart.finish();
    }

    private void exitGame() {
        goToMenuBtn.setImageResource(R.drawable.avd_menu);
        AnimatedVectorDrawable menuScale = (AnimatedVectorDrawable) goToMenuBtn.getDrawable();

        menuScale.start();

        Intent intent= new Intent(this.context, HomePage.class); // goes to home page
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clears history of activity stack
        startActivity(intent);
    }

    public WinDialog(Context context, State winningBird){
        this.context = context;
        this.winningBird = winningBird;
    }

    private void setWinMessage(){
        if(winningBird.equals(State.CROW))
            winMessageView.setText(getResources().getString(R.string.crow_wins));
        else if(winningBird.equals(State.VULTURE))
            winMessageView.setText(getResources().getString(R.string.vulture_wins));
    }

    private void setBirdAnimation(){
        int winBirdColorResId = 0;
        birdView.setImageResource(R.drawable.avd_bird);
        // create AVD object
        AnimatedVectorDrawable flyWinBird = (AnimatedVectorDrawable) birdView.getDrawable(); // bird animation

        if(winningBird.equals(State.CROW)) // sets color based on winner bird
            winBirdColorResId = getResources().getColor(R.color.theme_black);
        else if(winningBird.equals(State.VULTURE))
            winBirdColorResId = getResources().getColor(R.color.theme_red);

        flyWinBird.setTint(winBirdColorResId); // change bird color
        flyWinBird.start();
    }
}