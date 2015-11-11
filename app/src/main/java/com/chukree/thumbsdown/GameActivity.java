package com.chukree.thumbsdown;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

public class GameActivity extends Activity {

    Button btnTap;
    Timer timer = new Timer();
    int count = 0;
    long timerPeriod = 1000;
    private int multiple = 3, score = 0, hiScore = 0, lives = 3, level = 1, lastTapNum = 0;
    TextView tvScore;
    private boolean gameStarted = false;
    private String TAG = GameActivity.class.toString();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        btnTap = (Button) findViewById(R.id.button_tap);
        tvScore = (TextView) findViewById(R.id.tvScore);

        btnTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerPeriod > 50 && gameStarted) {
                    timerPeriod -= 50;
                }

                if(gameStarted){

                    // if global count % multiple == 0
                    if (count % multiple == 0) {

                        // TODO: call Sound1.play

                        // if global count ≠ 0
                        if(count != 0){
                            lastTapNum = count;
                            score += 1;
                        }

                        tvScore.setText("" + score);
                    }
                    // user tapped on a wrong number!
                    else{

                        score -= 1; lives -= 1;

                        // TODO: call Sound2.play

                        // TODO: set lblLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)

                        // TODO: A mechanism to alert the wrong doing (e.g. changing bg & fg colors)
                    }

                    if (btnTap.getText().toString().equals("Start")) {

                        // Set Clock1.TimerEnabled to true
                        handler.postDelayed(runnable, timerPeriod);

                        // set btnPause.Visible to true

                        // set "GameStarted" to true
                        gameStarted = true;

                        // set lastTapNum to 0
                        lastTapNum = 0;

                    }
                }
                // clicked btnTap to RESET the game
                else{

                    gameStarted = true; score = 0;  count = 0;  lives = 3;  level = 1;
                    timerPeriod = 1000;
                    // TODO Set lblLives.Text to ❤❤❤ or ♥♥♥ and ♡♡♡

                }

                // set btnTap.Enabled to false
                btnTap.setEnabled(false);
            }
        });
    }

    //
    private Runnable runnable = new Runnable(){

        @Override
        public void run() {
            count += 1;
            btnTap.setText(String.valueOf(count));
            Log.d(TAG, "Firing after + " + timerPeriod + "ms");

            // TODO: Change back the color of the text to normal (from colors for "tapped wrongly")

            // if count % multiple = 0
            if(count % multiple == 0){
                // TODO: if sound is checked, TextToSpeech1.speak("Now")

                // TODO: if tutorial mode selected, increase font size by 1.25 times and change text color


            }else{
                //TODO: change back the font size and color to normal

            }
            // catching when wrong number is tapped - difficult mode only
            if(((count - 1) % multiple == 0) && lastTapNum < count - multiple){

                // score -= 1; lives -= 1;
                // TODO: Sound2.play

                // TODO: set lblLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)
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
                btnTap.setBackgroundColor(Color.argb(255, 214, 255, 143));
                timerPeriod = 250;
            }else if(level >= 3){
                // change btnTap background color
                btnTap.setBackgroundColor(Color.argb(255, 203, 255, 143));
                timerPeriod = 500;
                if(score >= 7){
                    level = 4;
                }
            }else if(level >= 2){
                // change btnTap background color
                btnTap.setBackgroundColor(Color.argb(255, 143, 255, 143));
                timerPeriod = 750;
                if(score >= 5){
                    level = 3;
                }
            }else{
                btnTap.setBackgroundColor(Color.argb(255, 143, 255, 143));
                timerPeriod = 1000;
                if(score >= 2){
                    level = 2;
                }
            }

            handler.postDelayed(runnable, timerPeriod);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}
