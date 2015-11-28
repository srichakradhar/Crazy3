package com.chuckree.cheesy3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GameActivity extends Activity {

    private static final String HI_SCORE_KEY = "HIGH_SCORE", MY_PREFERENCES = "com.chuckree.cheesy3.GAME_ACTIVITY_PREFERENCES",
            GAME_MODE_KEY = "Game Mode";
    private Button btnTap, btnShare;
    int count = 0;
    long timerPeriod = 333;
    private int multiple = 3, hiScore = 0, lives = 3, level = 1, lastTapNum = 0;
    private static int score = 0;
    TextView tvScore, tvHiScore;
    private boolean gameStarted = false;
    private String TAG = GameActivity.class.toString(), string_img_url = null , string_msg = null;
    private Handler handler = new Handler();
    private Typeface tfMontserrat;
    private static Animation scaleUp, scaleDown;
    private GradientDrawable bgShape;
    private android.content.Context context;
    private TextView lblLives;
    private SharedPreferences preferences, prefs;
    SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    // Replace your KEY here and Run ,
    public final String consumer_key = "z4OC2VzJ0V4gHPAienLHZuTmQ";
    public final String secret_key = "6nbh8ciif5fSq9c7Eho2apQZvRWXHpAiDuHOZSrcxYbwqTHrx6";
    File casted_image;
    Button btnTweet;
    boolean proMode = false, loaded = false;;
    SharedPreferences.Editor editor;
    private SoundPool soundPool;
    private int soundDingID, soundErrID;
    float volume;
    private AdView mAdView;
    private AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.chuckree.cheesy3.R.layout.activity_game);

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load the sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundDingID = soundPool.load(this, R.raw.ding, 1);
        soundErrID = soundPool.load(this, R.raw.err, 1);

        tfMontserrat = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Hairline.otf");
        btnTap = (Button) findViewById(com.chuckree.cheesy3.R.id.button_tap);
        // btnPause = (Button) findViewById(R.id.button_pause);
        btnShare = (Button) findViewById(R.id.button_share);
        tvScore = (TextView) findViewById(R.id.tv_score);
        tvHiScore = (TextView) findViewById(R.id.tv_hi_score);
        Button btnHelp = (Button) findViewById(com.chuckree.cheesy3.R.id.button_help);
        lblLives = (TextView) findViewById(com.chuckree.cheesy3.R.id.text_view_lives);
        btnTap.setTypeface(tfMontserrat);
        btnTweet = (Button) findViewById(R.id.button_tweet);
        // btnPause.setTypeface(tfMontserrat);
        tvScore.setTypeface(tfMontserrat);
        tvHiScore.setTypeface(tfMontserrat);
        context = getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        proMode = prefs.getBoolean(GAME_MODE_KEY, false);
        if(proMode) timerPeriod = 1000 / Integer.parseInt(preferences.getString("speed", "1"));
        multiple = Integer.parseInt(preferences.getString("multiple", "3"));
        hiScore = prefs.getInt(HI_SCORE_KEY, 0);
        tvHiScore.setText(String.format("%d", hiScore));

        if(getIntent().getBooleanExtra("help", false)) showOverLay();
        // bgShape = (GradientDrawable) this.getResources().getDrawable(R.drawable.circle);

                btnTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (timerPeriod > 50 && gameStarted) {
//                    timerPeriod -= 50;
//                }

                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                float actualVolume = (float) audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = (float) audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                volume = actualVolume / maxVolume;

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

                        // Is the sound loaded already?
                        if (loaded)
                            soundPool.play(soundDingID, volume, volume, 1, 0, 1f);

                        Animation expandIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.expand_in);
                        btnTap.startAnimation(expandIn);

                    }
                    // user tapped on a wrong number!
                    else {

                        if(score > 0) score -= 1;
                        lives -= 1;

                        // set lblLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)
                        String strLives = "";

                        if(lives <= 0)
                            strLives = "♡♡♡";
                        else if(lives >= 3)
                            strLives = "♥♥♥";
                        else
                            strLives = "♡♡♡".substring(0, 3 - lives) + "♥♥♥".substring(0, lives);

                        lblLives.setText(strLives);

                        // A mechanism to alert the wrong doing (e.g. changing bg & fg colors)

                        if (lives <= 0) {
                            scaleDown = AnimationUtils.loadAnimation(context, com.chuckree.cheesy3.R.anim.scale_down);
                            scaleDown.setDuration(timerPeriod/2);
                            btnTap.setAnimation(scaleDown);
                        }

                        bgShape.setColor(Color.RED);
                        // bgShape.invalidateSelf();
                        Animation animWobble = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wobble);
                        btnTap.startAnimation(animWobble);

                        // Is the sound loaded already?
                        if (loaded)
                            soundPool.play(soundErrID, volume, volume, 1, 0, 1f);
                    }

                    if (btnTap.getText().toString().equals("Start")) {

                        // set "GameStarted" to true
                        gameStarted = true;

                        // set btnPause.Visible to true

                        // set lastTapNum to 0
                        lastTapNum = 0;

                        // set btnTap.Background to transparent
                        btnTap.setBackgroundResource(com.chuckree.cheesy3.R.drawable.circle);
                        scaleUp = AnimationUtils.loadAnimation(context, com.chuckree.cheesy3.R.anim.scale_up);
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
//                    level = 1;
//                    timerPeriod = 1000;
                    // Set lblLives.Text to ❤❤❤ or ♥♥♥ and ♡♡♡
                    lblLives.setText("♥♥♥");

                    btnTap.setTextSize(96);
                    btnTap.setBackgroundResource(com.chuckree.cheesy3.R.drawable.circle);
                    scaleUp = AnimationUtils.loadAnimation(context, com.chuckree.cheesy3.R.anim.scale_up);
                    bgShape = (GradientDrawable) btnTap.getBackground();
                    bgShape.setColor(Color.WHITE);
                    btnTap.setAnimation(scaleUp);
                    runnable.run();
                }

                // set btnTap.Enabled to false
                btnTap.setEnabled(false);

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

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I've scored " + score + " in \"Tap or Die\"\nUp for a game?\n" + preferences.getString("username", ""));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTwitt();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(proMode) {
            preferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                    if (s.equals("speed"))
                        timerPeriod = 1000 / Integer.parseInt(preferences.getString("speed", "1"));
                    else if (s.equals("multiple"))
                        multiple = Integer.parseInt(preferences.getString("multiple", "3"));
                }
            };
            preferences.registerOnSharedPreferenceChangeListener(preferencesListener);
        }

        if(!btnTap.getText().toString().equals("Start")) {
            gameStarted = true;
            runnable.run();
        }
    }

    private void showOverLay(){

        final Dialog dialog = new Dialog(GameActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(com.chuckree.cheesy3.R.layout.help_overlay);

        LinearLayout layout = (LinearLayout) dialog.findViewById(com.chuckree.cheesy3.R.id.layout_help_overlay);
        TextView lblHelpText = (TextView) layout.findViewById(com.chuckree.cheesy3.R.id.textView_help_text);
        lblHelpText.setTypeface(tfMontserrat);

        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {

                gameStarted = true;
                btnTap.setBackgroundResource(com.chuckree.cheesy3.R.drawable.circle);
                scaleUp = AnimationUtils.loadAnimation(context, com.chuckree.cheesy3.R.anim.scale_up);
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
            btnTap.setEnabled(true);

            // TODO: Change back the color of the text to normal (from colors for "tapped wrongly")

            // if count % multiple = 0
            if(count % multiple == 0){
                // TODO: if sound is checked, TextToSpeech1.speak("Now")

                // TODO: if tutorial mode selected, increase font size by 1.25 times and change text color


            }else{
                //TODO: change back the font size and color to normal

            }
            // catching when number is missed while playing in pro mode
            if(((count - 1) % multiple == 0) && lastTapNum < count - multiple){ // Add && proMode to count skipped multiples

                Log.d(TAG, "last tapped number was " + lastTapNum);

                 if(score > 0) score -= 1; lives -= 1;
                // TODO: Sound2.play

                // DONE: set lblLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)

                String strLives;
                if(lives <= 0)
                    strLives = "♡♡♡";
                else if(lives >= 3)
                    strLives = "♥♥♥";
                else
                    strLives = "♡♡♡".substring(0, 3 - lives) + "♥♥♥".substring(0, lives);

                lblLives.setText(strLives);
                bgShape.setColor(Color.RED);

                if (loaded)
                    soundPool.play(soundErrID, volume, volume, 1, 0, 1f);

                Animation animWobble = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wobble);
                btnTap.startAnimation(animWobble);
            }

            // lives exhausted
            if(lives <= 0){

                // set gameStarted to false
                gameStarted = false;

                // set Clock1.Enabled to false
                handler.removeCallbacks(runnable);

                adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

//                // set btnPause.Visible to false
//
//                // if a new high score has been made TODO: customize for tutorial mode
//                if( score > hiScore){
//
//                    // set btnTap.Text to "Awesome! You Nailed It!!!"
//                    btnTap.setText("Awesome! You Nailed It!!!");
//
//                }
//                // TODO: customize for tutorial mode
//                else{
//                    // if(isInTutorialMode)// btnTap.setText("Sorry! You are in tutorial mode!");
//                    // else btnTap.setText("Sorry! You couldn't beat the high score!");
//                }
            }
// else if(level >= 4){
//                // change btnTap background color
//                // btnTap.setBackgroundColor(Color.argb(255, 214, 255, 143));
//                timerPeriod = 250;
//            }else if(level >= 3){
//                // change btnTap background color
//                // btnTap.setBackgroundColor(Color.argb(255, 203, 255, 143));
//                timerPeriod = 500;
//                if(score >= 7){
//                    level = 4;
//                }
//            }else if(level >= 2){
//                // change btnTap background color
//                // btnTap.setBackgroundColor(Color.argb(255, 143, 255, 143));
//                timerPeriod = 750;
//                if(score >= 5){
//                    level = 3;
//                }
//            }else{
//                // btnTap.setBackgroundColor(Color.argb(255, 143, 255, 143));
//                timerPeriod = 1000;
//                if(score >= 2){
//                    level = 2;
//                }
//            }

            if(gameStarted){
                handler.postDelayed(runnable, timerPeriod);
            }else if(lives <= 0)    // to avoid execution when btnPause pressed
            {
                btnTap.setTextSize(26);
                btnTap.setText("Sorry! You ran out of lives!\n\nTouch to try Again...");
                lastTapNum = 0;
                btnTap.setBackgroundResource(0);
                if(score > 0) btnTweet.setVisibility(View.VISIBLE);
                else btnTweet.setVisibility(View.INVISIBLE);
                if(score > hiScore){
                    hiScore = score;
                    editor = prefs.edit();
                    editor.putInt(HI_SCORE_KEY, hiScore);
                    editor.commit();
                    tvHiScore.setText("| " + hiScore);
                    Toast.makeText(getApplicationContext(), "New High Score!", Toast.LENGTH_SHORT).show();
                }
            }

            // update the score
            tvScore.setText(String.format("%d", score));
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    // Here you can pass the string message & image path which you want to share
    // in Twitter.
    public void onClickTwitt() {
        if (isNetworkAvailable()) {
            Twitt_Sharing twitt = new Twitt_Sharing(GameActivity.this, consumer_key, secret_key);
            string_img_url = "https://sites.google.com/site/srichakra3nsr3/my-profile/nenu.jpg";
            string_msg = "I have scored " + score + " in Tap or Die.\nCan you beat my score?\n" +
                    "Check out: https://play.google.com/store/apps/details?id=com.chuckree.tapordie";
            InputStream in = null;
            OutputStream out = null;
            String filename = "cheesy3.png";
            try {
                in = getAssets().open(filename);
                casted_image = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(casted_image);
                byte[] buffer = new byte[1024];
                int read;
                while((read = in.read(buffer)) != -1){
                    out.write(buffer, 0, read);
                }
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
            //casted_image = new File("/storage/emulated/legacy/nenu.jpg");
            // Now share both message & image to sharing activity
            twitt.shareToTwitter(string_msg, casted_image);

        } else {
            showToast("No Network Connection Available !!!");
        }
    }

    // when user will click on twitter then first that will check that is
    // internet exist or not
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[ ] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }
}
