package com.chukree.thumbsdown;

import android.app.Activity;
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
    int seconds = 0;
    long timerPeriod = 1000;
    private int multiple = 3;
    private int score = 0;
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

                if (btnTap.getText().toString().equals("Start")) {
                    //Enable timer
                    //timer.schedule(myTimerTask, 0, timerPeriod);
                    handler.postDelayed(runnable, timerPeriod);

                    // Enable Pause Button

                    // Set "GameStarted" to true
                    gameStarted = true;

                    // set "lastTapNum" to 0

                } else if (Integer.parseInt(btnTap.getText().toString()) % multiple == 0) {
                    score += 1;
                    tvScore.setText("" + score);
                }
            }
        });
    }

    private Runnable runnable = new Runnable(){

        @Override
        public void run() {
            seconds += 1;
            btnTap.setText(String.valueOf(seconds));
            Log.d(TAG, "Firing after + " + timerPeriod + "ms");
            handler.postDelayed(runnable, timerPeriod);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}
