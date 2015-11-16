package com.chukree.thumbsdown;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

    private Button btnTap, btnHelp, btnSettings; //btnPause;
    int count = 0;
    long timerPeriod = 1000;
    private int multiple = 3, hiScore = 0, lives = 3, level = 1, lastTapNum = 0;
    private static int score = 0;
    TextView tvScore;
    private boolean gameStarted = false;
    private String TAG = GameActivity.class.toString();
    private Handler handler = new Handler();
    private Typeface tfMontserrat;
    private static Animation scaleUp, scaleDown;
    private GradientDrawable bgShape;
    private android.content.Context context;
    private TextView lblLives;
    private String strLives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tfMontserrat = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Hairline.otf");

        btnTap = (Button) findViewById(R.id.button_tap);
        // btnPause = (Button) findViewById(R.id.button_pause);
        tvScore = (TextView) findViewById(R.id.tvScore);
        btnHelp = (Button) findViewById(R.id.button_help);
        lblLives = (TextView) findViewById(R.id.text_view_lives);
        btnTap.setTypeface(tfMontserrat);
        btnSettings = (Button) findViewById(R.id.button_preferences);
        // btnPause.setTypeface(tfMontserrat);
        tvScore.setTypeface(tfMontserrat);
        context = getApplicationContext();

        showOverLay();
        // bgShape = (GradientDrawable) this.getResources().getDrawable(R.drawable.circle);

                btnTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerPeriod > 50 && gameStarted) {
                    timerPeriod -= 50;
                }

                if (gameStarted && count > 0) {

                    bgShape = (GradientDrawable) btnTap.getBackground();

                    // if global count % multiple == 0
                    if (count % multiple == 0) {

                        // TODO: call Sound1.play

                        // if global count ≠ 0

                        lastTapNum = count;
                        score += 1;


                        bgShape.setColor(Color.GREEN);
                        // bgShape.invalidateSelf();

                    }
                    // user tapped on a wrong number!
                    else {

                        /*if(score > 0) score -= 1;
                        lives -= 1;

                        // TODO: call Sound2.play

                        // TODO: set lblLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)
                        String strLives = "";

                        if(lives <= 0)
                            strLives = "♡♡♡";
                        else if(lives >= 3)
                            strLives = "♥♥♥";
                        else
                            strLives = "♡♡♡".substring(0, 3 - lives) + "♥♥♥".substring(0, lives);

                        lblLives.setText(strLives); */

                        // TODO: A mechanism to alert the wrong doing (e.g. changing bg & fg colors)

                        if (lives <= 0) {
                            scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down);
                            scaleDown.setDuration(timerPeriod/2);
                            btnTap.setAnimation(scaleDown);
                        }

                        bgShape.setColor(getResources().getColor(R.color.wrong_red));
                        // bgShape.invalidateSelf();
                    }

                    if (btnTap.getText().toString().equals("Start")) {

                        // set "GameStarted" to true
                        gameStarted = true;

                        // set btnPause.Visible to true

                        // set lastTapNum to 0
                        lastTapNum = 0;

                        // set btnTap.Background to transparent
                        btnTap.setBackgroundResource(R.drawable.circle);
                        scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
                        btnTap.setAnimation(scaleUp);
                        bgShape = (GradientDrawable) btnTap.getBackground();

                        // Set Clock1.TimerEnabled to true
                        runnable.run();

                    }

                }
                // clicked btnTap to RESET the game
                else {

                    gameStarted = true;
                    score = 0;
                    count = 0;
                    lives = 3;
                    level = 1;
                    timerPeriod = 1000;
                    // TODO Set lblLives.Text to ❤❤❤ or ♥♥♥ and ♡♡♡
                    lblLives.setText("♥♥♥");

                    btnTap.setTextSize(96);
                    btnTap.setBackgroundResource(R.drawable.circle);
                    scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
                    bgShape = (GradientDrawable) btnTap.getBackground();
                    bgShape.setColor(Color.WHITE);
                    btnTap.setAnimation(scaleUp);
                    runnable.run();
                }

                // set btnTap.Enabled to false
                btnTap.setEnabled(false);

                // update the score
                tvScore.setText(String.format("%d", score));

            }
        });

        /*btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gameStarted) {
                    gameStarted = false;
                    btnTap.setEnabled(false);
                    btnPause.setText("►");
                    handler.removeCallbacks(runnable);
                }
                else{
                    gameStarted = true;
                    btnPause.setText("❚❚");
                    runnable.run();
                }
            }
        });*/

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStarted = false;
                handler.removeCallbacks(runnable);
                showOverLay();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }

    private void showOverLay(){

        final Dialog dialog = new Dialog(GameActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.help_overlay);

        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.layout_help_overlay);
        TextView lblHelpText = (TextView) layout.findViewById(R.id.textView_help_text);
        lblHelpText.setTypeface(tfMontserrat);

        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {

                gameStarted = true;
                btnTap.setBackgroundResource(R.drawable.circle);
                scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
                btnTap.setAnimation(scaleUp);
                bgShape = (GradientDrawable) btnTap.getBackground();
                runnable.run();
                dialog.dismiss();

            }

        });

        dialog.show();

    }

    private Runnable runnable = new Runnable(){

        @Override
        public void run() {

            count += 1;

            btnTap.setText(String.valueOf(count));
            Log.d(TAG, "Firing after + " + timerPeriod + "ms");
            btnTap.setEnabled(true);

            // TODO: Change back the color of the text to normal (from colors for "tapped wrongly")

            // if count % multiple = 0
            if(count % multiple == 0){
                // TODO: if sound is checked, TextToSpeech1.speak("Now")

                // TODO: if tutorial mode selected, increase font size by 1.25 times and change text color


            }else{
                //TODO: change back the font size and color to normal

            }
            // catching when number is missed
            if(((count - 1) % multiple == 0) && lastTapNum < count - multiple){

                Log.d(TAG, "last tapped number was " + lastTapNum);

                 score -= 1; lives -= 1;
                // TODO: Sound2.play

                // DONE: set lblLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)

                if(lives <= 0)
                    strLives = "♡♡♡";
                else if(lives >= 3)
                    strLives = "♥♥♥";
                else
                    strLives = "♡♡♡".substring(0, 3 - lives) + "♥♥♥".substring(0, lives);

                lblLives.setText(strLives);
                bgShape.setColor(Color.RED);
            }

            // lives exhausted
            if(lives <= 0){

                // set gameStarted to false
                gameStarted = false;

                // set Clock1.Enabled to false
                handler.removeCallbacks(runnable);

                // set btnPause.Visible to false

                // if a new high score has been made TODO: customize for tutorial mode
                if( score > hiScore){

                    // set btnTap.Text to "Awesome! You Nailed It!!!"
                    btnTap.setText("Awesome! You Nailed It!!!");

                }
                // TODO: customize for tutorial mode
                else{
                    // if(isInTutorialMode)// btnTap.setText("Sorry! You are in tutorial mode!");
                    // else btnTap.setText("Sorry! You couldn't beat the high score!");
                }
            }else if(level >= 4){
                // change btnTap background color
                // btnTap.setBackgroundColor(Color.argb(255, 214, 255, 143));
                timerPeriod = 250;
            }else if(level >= 3){
                // change btnTap background color
                // btnTap.setBackgroundColor(Color.argb(255, 203, 255, 143));
                timerPeriod = 500;
                if(score >= 7){
                    level = 4;
                }
            }else if(level >= 2){
                // change btnTap background color
                // btnTap.setBackgroundColor(Color.argb(255, 143, 255, 143));
                timerPeriod = 750;
                if(score >= 5){
                    level = 3;
                }
            }else{
                // btnTap.setBackgroundColor(Color.argb(255, 143, 255, 143));
                timerPeriod = 1000;
                if(score >= 2){
                    level = 2;
                }
            }

            if(gameStarted){
                handler.postDelayed(runnable, timerPeriod);
            }else if(lives <= 0)    // to avoid execution when btnPause pressed
            {
                btnTap.setTextSize(26);
                btnTap.setText("Sorry! You ran out of lives!\n\nTouch to try Again...");
                lastTapNum = 0;
                btnTap.setBackgroundResource(0);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}
