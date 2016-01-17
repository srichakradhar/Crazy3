package com.chuckree.crazy3;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends BaseGameActivity implements View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener {

    private static final String HI_SCORE_KEY = "HIGH_SCORE", MY_PREFERENCES = "com.chuckree.crazy3.GAME_ACTIVITY_PREFERENCES",
            GAME_MODE_KEY = "Game Mode";
    private static final String SKU_LIFE = "lives_5";
    static final String SKU_REMOVE_ADS = "premium";
    private static final String GAMES_PLAYED = "GAMES_PLAYED";
    private Button btnTap, btnShare, btnFB;
    int count = 0, deaths = 0;
    long timerPeriod = 333;
    private int multiple = 3, hiScore = 0, lives = 3, level = 1, lastTapNum = 0;
    private static int score = 0;
    TextView tvScore, tvHiScore;
    private boolean gameStarted = false;
    private String TAG = GameActivity.class.toString(), string_img_url = null, string_msg = null;
    private Handler handler = new Handler();
    private Typeface tfMontserrat;
    private static Animation scaleUp, scaleDown;
    private GradientDrawable bgShape;
    private android.content.Context context;
    private Button btnLives;
    private LinearLayout layoutShare, layoutGames;
    private SharedPreferences preferences, prefs;
    SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    // Replace your KEY here and Run ,
    public final String consumer_key = "z4OC2VzJ0V4gHPAienLHZuTmQ";
    public final String secret_key = "6nbh8ciif5fSq9c7Eho2apQZvRWXHpAiDuHOZSrcxYbwqTHrx6";
    File casted_image;
    Button btnTweet;
    boolean proMode = false, loaded = false;
    SharedPreferences.Editor editor;
    private SoundPool soundPool;
    private int soundDingID, soundErrID;
    float volume;
    private AdView mAdView;
    private AdRequest adRequest;
    private GoogleApiClient mGoogleApiClient;
    private Facebook mFacebook;
    private String messageToPost;
    private static final String[] PERMISSIONS = new String[]{"publish_actions"};
    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    IabHelper inappBillingHelper;
    private boolean mHasLives = false;
    private IabBroadcastReceiver mBroadcastReceiver;
    private String fbPhotoAddress;
    private Bitmap bitmap;
    private String shareText;

    public boolean saveCredentials(Facebook facebook) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, facebook.getAccessToken());
        editor.putLong(EXPIRES, facebook.getAccessExpires());
        return editor.commit();
    }

    public boolean restoreCredentials(Facebook facebook) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
        facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
        facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
        return facebook.isSessionValid();
    }

    private void queryPurchasedItems() {
        //check if user has bought "remove adds"
        if (inappBillingHelper.isSetupDone() && !inappBillingHelper.isAsyncInProgress()) {
            inappBillingHelper.queryInventoryAsync(mGotInventoryListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryPurchasedItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFacebook = new Facebook(getString(R.string.app_id_fb));
        restoreCredentials(mFacebook);
        setContentView(com.chuckree.crazy3.R.layout.activity_game);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:",
//                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e) {
//        }

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().addTestDevice("269EB77915BDD43FB30DEFD670BA8126").build();
        mAdView.loadAd(adRequest);
        mGoogleApiClient = getApiClient();


        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs4Hd+YXizinNLEOyalSLT13cEKpqTI2w2WsYOp1/BbnAzzn3eAmc1qx0qvjCDyZWuae19geYCXmKgj6s/hktxlNEAJryAkbutg/kJs1TCTX2fipWYITiLt1BCNGumiQo6ufel7bIADftZY8GYHE/s4gm/TckBdXdrQ9zYjLm418YXpT26kLd0rM0pVRC8EPzsZJeUQKRe3oijJN4E4hgU6eSm7vNmsVwtPNT20JXKIjx/KngoYqWUaqtllaBSkbB9075fRMHDwCmXuXwMT8aPY0KuYshwpgmGxFP/Hqg2IOwH6ltTzZSkDllCEGkO1minEfuuQyzOKlI/jYZ0Gt28QIDAQAB";

        // compute your public key and store it in base64EncodedPublicKey
        inappBillingHelper = new IabHelper(this, base64EncodedPublicKey);

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

        tfMontserrat = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Hairline.otf");
        btnTap = (Button) findViewById(com.chuckree.crazy3.R.id.button_tap);
        // btnPause = (Button) findViewById(R.id.button_pause);
        btnShare = (Button) findViewById(R.id.button_share);
        tvScore = (TextView) findViewById(R.id.tv_score);
        tvHiScore = (TextView) findViewById(R.id.tv_hi_score);
        Button btnHelp = (Button) findViewById(com.chuckree.crazy3.R.id.button_help);
        btnLives = (Button) findViewById(R.id.button_lives);
        btnTap.setTypeface(tfMontserrat);
        btnTweet = (Button) findViewById(R.id.button_tweet);
        layoutShare = (LinearLayout) findViewById(R.id.layout_share);
        layoutGames = (LinearLayout) findViewById(R.id.layout_games);
        btnFB = (Button) findViewById(R.id.button_fb);
        // btnPause.setTypeface(tfMontserrat);
        tvScore.setTypeface(tfMontserrat);
        tvHiScore.setTypeface(tfMontserrat);
        btnLives.setTypeface(tfMontserrat);
        ((TextView) findViewById(R.id.text_view_show_off)).setTypeface(tfMontserrat);
        ((Button) findViewById(R.id.button_leaderboard)).setTypeface(tfMontserrat);
        ((Button) findViewById(R.id.button_achievements)).setTypeface(tfMontserrat);
        context = getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        proMode = prefs.getBoolean(GAME_MODE_KEY, false);
        if (proMode) timerPeriod = 1000 / Integer.parseInt(preferences.getString("speed", "1"));
        multiple = Integer.parseInt(preferences.getString("multiple", "3"));
        hiScore = prefs.getInt(HI_SCORE_KEY, 0);
        deaths = prefs.getInt(GAMES_PLAYED, 1);
        tvHiScore.setText(String.format("%d", hiScore));
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.button_leaderboard).setOnClickListener(this);
        findViewById(R.id.button_achievements).setOnClickListener(this);
        btnFB.setOnClickListener(this);
        btnLives.setOnClickListener(this);

        showOverLay();  //if(getIntent().getBooleanExtra("help", false)) showOverLay();
        // bgShape = (GradientDrawable) this.getResources().getDrawable(R.drawable.circle);
        //        SessionStore.restore(mFacebook, context);

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

                        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                            Games.Achievements.unlock(getApiClient(),
                                    getString(R.string.achievement_baby_steps));

                            if (score == 3)
                                Games.Achievements.unlock(mGoogleApiClient,
                                        getString(R.string.achievement_score_3));
                            else if (score == 39)
                                Games.Achievements.unlock(mGoogleApiClient,
                                        getString(R.string.achievement_score_39));
                            else if (score == 99)
                                Games.Achievements.unlock(mGoogleApiClient,
                                        getString(R.string.achievement_score_99));
                        }


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

                        if (extraLives > 0) extraLives -= 1;
                        else lives -= 1; // if(score > 0) score -= 1;

                        // set btnLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)
                        btnLives.setText(String.format("%d", lives + extraLives));

                        // A mechanism to alert the wrong doing (e.g. changing bg & fg colors)

                        if (lives <= 0) {
                            scaleDown = AnimationUtils.loadAnimation(context, com.chuckree.crazy3.R.anim.scale_down);
                            scaleDown.setDuration(timerPeriod / 2);
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
                        btnTap.setBackgroundResource(com.chuckree.crazy3.R.drawable.circle);
                        scaleUp = AnimationUtils.loadAnimation(context, com.chuckree.crazy3.R.anim.scale_up);
                        btnTap.setAnimation(scaleUp);
                        bgShape = (GradientDrawable) btnTap.getBackground();

                        layoutShare.setVisibility(View.VISIBLE);

                        // Set Clock1.TimerEnabled to true
                        runnable.run();

                        deaths += 1;
                        editor = prefs.edit();
                        editor.putInt(GAMES_PLAYED, deaths);
                        editor.commit();
                    }

                }
                // clicked btnTap to RESET the game
                else {

                    if (deaths == 4 && mGoogleApiClient.isConnected())
                        Games.Achievements.unlock(mGoogleApiClient,
                                getString(R.string.achievement_winners_never_quit));

                    gameStarted = true;
                    score = 0;
                    count = 0;
                    lives = 3;
                    btnLives.setText(String.format("%d", lives));
//                    level = 1;
//                    timerPeriod = 1000;
                    // Set btnLives.Text to ❤❤❤ or ♥♥♥ and ♡♡♡
                    // btnLives.setText("♥♥♥");

                    layoutShare.setVisibility(View.GONE);
                    layoutGames.setVisibility(View.GONE);

                    btnTap.setTextSize(96);
                    btnTap.setBackgroundResource(com.chuckree.crazy3.R.drawable.circle);
                    scaleUp = AnimationUtils.loadAnimation(context, com.chuckree.crazy3.R.anim.scale_up);
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
                if (mGoogleApiClient.isConnected())
                    Games.Achievements.unlock(mGoogleApiClient,
                            getString(R.string.achievement_a_good_friend));
                shareText = "My Score : " + score + "\nHigh Score : " + hiScore; // + "\nGames Played : " + deaths;
                Bitmap bmpSharePic = drawMultilineTextToBitmap(context, R.drawable.sharepic, shareText);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I've scored " + score + " in \"Crazy 3\"\nTry to beat me... ;)\nhttps://bit.ly/crazy-3");
                shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bmpSharePic));
                startActivity(Intent.createChooser(shareIntent, "Send to..."));

            }
        });


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTwitt();
            }
        });

        inappBillingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }

                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(GameActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // Hooray, IAB is fully set up!
                Log.d(TAG, "IAB Setup finished!");
                List additionalSkuList = new ArrayList();
                additionalSkuList.add(SKU_LIFE);
                inappBillingHelper.queryInventoryAsync(true, additionalSkuList,
                        mQueryFinishedListener);

            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_image.png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error
                return;
            }

            String lifePrice =
                    inventory.getSkuDetails(SKU_LIFE).getPrice();

            if (!inventory.hasPurchase(SKU_REMOVE_ADS)) {
                Toast.makeText(GameActivity.this, "no premium", Toast.LENGTH_LONG).show();
                mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.setVisibility(View.GONE);
                Toast.makeText(GameActivity.this, "premium", Toast.LENGTH_LONG).show();
            }

            // TODO: update the UI
        }
    };

    private boolean mIsRemoveAdds = false;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            } else if (purchase.getSku().equals(SKU_LIFE)) {
                // consume the gas and update the UI
                extraLives += 5;
                inappBillingHelper.consumeAsync(purchase, mConsumeFinishedListener);

            } else if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
                // consume the gas and update the UI
                mIsRemoveAdds = true;
                mAdView.setVisibility(View.GONE);
                Toast.makeText(GameActivity.this, "Purchase successful", Toast.LENGTH_LONG).show();
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // does the user have the premium upgrade?
            mHasLives = inventory.hasPurchase(SKU_LIFE);
            // update UI accordingly
            if (mHasLives) showToast("you had purchased 5 lives!");
            if (inventory.hasPurchase(SKU_REMOVE_ADS)) showToast("You are a premium member!");
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        // provision the in-app purchase to the user
                        // (for example, credit 50 gold coins to player's character)
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!inappBillingHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        if (inappBillingHelper != null) inappBillingHelper.dispose();
        inappBillingHelper = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryPurchasedItems();

        if (proMode) {
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

        if (!btnTap.getText().toString().equals("Start")) {
            gameStarted = true;
            runnable.run();
        }
// Not Working
//        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
//            Log.d(TAG, "Still Signed in!!!");
//        else{
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.button_leaderboard).setVisibility(View.GONE);
//            findViewById(R.id.button_achievements).setVisibility(View.INVISIBLE);
//        }
    }

    private void showOverLay() {

        final Dialog dialog = new Dialog(GameActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(com.chuckree.crazy3.R.layout.help_overlay);

        LinearLayout layout = (LinearLayout) dialog.findViewById(com.chuckree.crazy3.R.id.layout_help_overlay);
        TextView lblHelpText = (TextView) layout.findViewById(com.chuckree.crazy3.R.id.textView_help_text);
        lblHelpText.setTypeface(tfMontserrat);

        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {

                gameStarted = true;
                btnTap.setBackgroundResource(com.chuckree.crazy3.R.drawable.circle);
                scaleUp = AnimationUtils.loadAnimation(context, com.chuckree.crazy3.R.anim.scale_up);
                btnTap.setAnimation(scaleUp);
                bgShape = (GradientDrawable) btnTap.getBackground();
                runnable.run();
                dialog.dismiss();

            }

        });

        dialog.show();

    }

    private int extraLives = 0;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {

            count += 1;

            btnTap.setText(String.valueOf(count));
            btnTap.setEnabled(true);

            // TODO: Change back the color of the text to normal (from colors for "tapped wrongly")

            // if count % multiple = 0
            if (count % multiple == 0) {
                // TODO: if sound is checked, TextToSpeech1.speak("Now")

                // TODO: if tutorial mode selected, increase font size by 1.25 times and change text color


            } else {
                //TODO: change back the font size and color to normal

            }
            // catching when number is missed while playing in pro mode
            if (((count - 1) % multiple == 0) && lastTapNum < count - multiple) { // Add && proMode to count skipped multiples

                Log.d(TAG, "last tapped number was " + lastTapNum);

                if (extraLives > 0) extraLives -= 1;
                else lives -= 1;
                // if(score > 0) score -= 1;

                // DONE: set btnLives.Text to "♥♥♥".substring(lives) + ♡♡♡.substring(3 - lives)

                btnLives.setText(String.format("%d", lives + extraLives));
                bgShape.setColor(Color.RED);

                if (loaded)
                    soundPool.play(soundErrID, volume, volume, 1, 0, 1f);

                Animation animWobble = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wobble);
                btnTap.startAnimation(animWobble);
            }

            // lives exhausted
            if (lives <= 0) {

                // set gameStarted to false
                gameStarted = false;

                // set Clock1.Enabled to false
                handler.removeCallbacks(runnable);

                adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                if (score > 0) layoutShare.setVisibility(View.VISIBLE);
                else layoutShare.setVisibility(View.GONE);
                layoutGames.setVisibility(View.VISIBLE);

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

            if (gameStarted) {
                handler.postDelayed(runnable, timerPeriod);
            } else if (lives <= 0)    // to avoid execution when btnPause pressed
            {
                btnTap.setTextSize(22);
                btnTap.setEnabled(false);
                new CountDownTimer(4000, 1000) {
                    int count = 3;

                    public void onTick(long millisUntilFinished) {
                        btnTap.setText(String.format("You scored: %d\n%d", score, count--));

                    }

                    public void onFinish() {
                        btnTap.setTextSize(26);
                        btnTap.setText("Replay");
                        btnTap.setEnabled(true);
                    }
                }.start();
                deaths += 1;
                lastTapNum = 0;
                editor = prefs.edit();
                editor.putInt(GAMES_PLAYED, deaths);
                editor.commit();
//                btnTap.setBackgroundResource(0);
                if (score > hiScore) {
                    hiScore = score;
                    editor = prefs.edit();
                    editor.putInt(HI_SCORE_KEY, hiScore);
                    editor.apply();
                    tvHiScore.setText(String.format("| %d |", hiScore));
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        Games.Leaderboards.submitScore(mGoogleApiClient,
                                getString(R.string.global_leaderboard),
                                score);
                    }
                    Toast.makeText(getApplicationContext(), "New High Score!", Toast.LENGTH_SHORT).show();
                }
            }
            // update the score
            tvScore.setText(String.format("%d", score));
        }
    };

    /*@NonNull
    private String getStrLives() {
        String strLives = "";
        if(lives <= 0)
            strLives = "♡♡♡";
        else if(lives == 3)
            strLives = "♥♥♥";
        else if(lives > 3) {
            btnLives.setBackgroundDrawable(getDrawable(R.drawable.little_heart));
            btnLives.setText(String.format("%d", lives + extraLives));
        }
        else
            strLives = "♡♡♡".substring(0, 3 - lives) + "♥♥♥".substring(0, lives);
        return strLives;
    }*/

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
            string_msg = "I have scored " + score + " in \"Crazy 3\".\nCan you beat my score?\n" +
                    "Check out: https://bit.ly/crazy-3";
            InputStream in = null;
            OutputStream out = null;
            String filename = "crazy3.png";
            try {
                in = getAssets().open(filename);
                casted_image = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(casted_image);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
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
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_achievements:
                startActivityForResult(Games.Achievements.getAchievementsIntent(
                        mGoogleApiClient), 1);
                break;
            case R.id.button_leaderboard:
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                        mGoogleApiClient, getString(R.string.global_leaderboard)), 2);
                break;
            case R.id.sign_in_button:
                beginUserInitiatedSignIn();
                break;
            case R.id.button_fb:

                byte[] data = null;
                deaths = prefs.getInt(GAMES_PLAYED, 1);
                shareText = "My Score : " + score + "\nHigh Score : " + hiScore + ";    //\nGames Played : " + deaths;
//                bitmap = drawMultilineTextToBitmap(context, R.drawable.sharepic, shareText);

                messageToPost = "I've scored " + score + " in \"Crazy 3\"\nTry to beat me... ;)\nhttps://bit.ly/crazy-3" + preferences.getString("username", "");
                if (!mFacebook.isSessionValid()) {
                    loginAndPostToWall();
                } else {
                    postToWall(messageToPost);
                }

                /*Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                data = baos.toByteArray();

                Bundle params = new Bundle();
                params.putString("caption", messageToPost);
//                params.putString("method", "photos.upload");
//                params.putByteArray("picture", data);

                AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
                mAsyncRunner.request(null, params, "POST", new SampleUploadListener(), null);*/

                break;

            case R.id.button_lives:
                inappBillingHelper.launchPurchaseFlow(this, SKU_LIFE, 10001,
                        mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                break;
        }
    }

    private Bitmap getBitmap() {
        View view = findViewById(R.id.linear_layout_game_activity);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return bitmap;
    }

    public Bitmap drawMultilineTextToBitmap(Context gContext,
                                            int gResId,
                                            String gText) {

        // prepare canvas
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (28 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.GRAY);
        // font style
        paint.setTypeface(tfMontserrat);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth) / 2;
        float y = (bitmap.getHeight() - textHeight) / 2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    public class SampleUploadListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: (executed in background thread)
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String src = json.getString("src");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."

            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            // TODO Auto-generated method stub

        }
    }

    public abstract class BaseRequestListener implements AsyncFacebookRunner.RequestListener {

        public void onFacebookError(FacebookError e, final Object state) {
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

        public void onFileNotFoundException(FileNotFoundException e,
                                            final Object state) {
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

        public void onIOException(IOException e, final Object state) {
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

        public void onMalformedURLException(MalformedURLException e,
                                            final Object state) {
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.button_leaderboard).setVisibility(View.GONE);
        findViewById(R.id.button_achievements).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSignInSucceeded() {
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.button_leaderboard).setVisibility(View.VISIBLE);
        findViewById(R.id.button_achievements).setVisibility(View.VISIBLE);
    }

    public void doNotShare(View button) {
        finish();
    }

    public void share(View button) {
        if (!mFacebook.isSessionValid()) {
            loginAndPostToWall();
        } else {
            postToWall(messageToPost);
        }
    }

    public void loginAndPostToWall() {
        mFacebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
    }

    public void postToWall(String message) {
//        Bundle parameters = new Bundle();
//        parameters.putString("message", message);
//        parameters.putString("description", "topic share");
        /*Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                // safety check
                if (isFinishing()) {
                    return;
                }
                if (response.getError() != null) {  // [IF Failed Posting]
                    Log.d(TAG, "photo upload problem. Error="+response.getError() );
                }  //  [ENDIF Failed Posting]

                Object graphResponse = response.getGraphObject().getProperty("id");
                if (graphResponse == null || !(graphResponse instanceof String) ||
                        TextUtils.isEmpty((String) graphResponse)) { // [IF Failed upload/no results]
                    Log.d(TAG, "failed photo upload/no response");
                } else {  // [ELSEIF successful upload]
                    fbPhotoAddress = "https://www.facebook.com/photo.php?fbid=" +graphResponse;
                    showToast("Your score screenshot posted successfully!");
                }  // [ENDIF successful posting or not]
            }  // [END onCompleted]
        };
        Request request = Request.newUploadPhotoRequest(mFacebook.getSession(), bitmap, uploadPhotoRequestCallback);
        request.executeAsync();*/
//        new FBPostTask().execute(parameters);

        Bundle parameters = new Bundle();
        parameters.putString("message", messageToPost);
        /*parameters.putString("attachment", "{\"name\":\"Crazy 3\","
                +"\"href\":\""+"http://bit.ly/crazy-3"+"\","
                +"\"media\":[{\"type\":\"image\",\"src\":\""+"http://sites.google.com/site/srichakra3nsr3/my-profile/interests/android/descripic.jpg"+"\",\"href\":\""+"http://bit.ly/crazy-3"+"\"}]"
                +"}");*/
        parameters.putString("link", "https://play.google.com/store/apps/details?id=com.chuckree.tapordie");
        parameters.putString("caption", "Crazy 3 on Google Play");
        parameters.putString("description", messageToPost);
        parameters.putString("picture", "http://sites.google.com/site/srichakra3nsr3/my-profile/interests/android/crazy3_descripic.png");
        parameters.putString("name", "Crazy 3 on Google Play");

        mFacebook.dialog(this, "stream.publish", parameters, new Facebook.DialogListener() {
            @Override
            public void onComplete(Bundle values) {
                showToast("Let's see who's gonna beat your score!");
            }

            @Override
            public void onFacebookError(FacebookError e) {
                showToast("Couldn't post! Check whether everything is fine!");
            }

            @Override
            public void onError(DialogError e) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        inappBillingHelper.queryInventoryAsync(mGotInventoryListener);
    }

    class LoginDialogListener implements Facebook.DialogListener {
        public void onComplete(Bundle values) {
            saveCredentials(mFacebook);
            if (messageToPost != null) {
                postToWall(messageToPost);
            }
        }

        public void onFacebookError(FacebookError error) {
            showToast("Authentication with Facebook failed!");
        }

        public void onError(DialogError error) {
            showToast("Authentication with Facebook failed!");
        }

        public void onCancel() {
            showToast("Authentication with Facebook cancelled!");

        }
    }

    class FBPostTask extends AsyncTask<Bundle, Void, Void> {

        protected Void doInBackground(Bundle... parameters) {
            try {
                mFacebook.request("me");
                String response = mFacebook.request("me/feed", parameters[0], "POST");
                // Log.d("Tests", "got response: " + response);
                if (response == null || response.equals("") ||
                        response.equals("false")) {
                    Log.d(TAG, "Blank response.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Blank response from Facebook!");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Message posted to your facebook wall!");
                        }
                    });
                }

                return null;
            } catch (Exception e) {
                Log.d(TAG, "Failed to post to wall!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Failed to post to wall!");
                    }
                });
                e.printStackTrace();
                return null;
            }
        }
    }
}
