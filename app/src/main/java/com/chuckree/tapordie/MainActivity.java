package com.chuckree.tapordie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    public static final String MY_PREFERENCES = "com.chuckree.tapordie.MAIN_ACTIVITY_PREFERENCES",
            GAME_MODE_KEY = "Game Mode";
    private Button btnPlay, btnHelp, btnSettings;
    private RadioGroup radioGameModeGroup;
    private SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Typeface tfMontserrat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.chuckree.tapordie.R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        btnPlay = (Button) findViewById(com.chuckree.tapordie.R.id.button_play);
        btnHelp = (Button) findViewById(com.chuckree.tapordie.R.id.button_help);
        btnSettings = (Button) findViewById(com.chuckree.tapordie.R.id.button_settings);
        radioGameModeGroup = (RadioGroup) findViewById(R.id.radio_grp_game_mode);
        RadioButton radioKids = (RadioButton) radioGameModeGroup.findViewById(R.id.radio_kids);

        tfMontserrat = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Hairline.otf");
        btnPlay.setTypeface(tfMontserrat);
        btnHelp.setTypeface(tfMontserrat);
        btnSettings.setTypeface(tfMontserrat);
        prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = prefs.edit();
        if(radioKids.isChecked()) editor.putBoolean(GAME_MODE_KEY, false);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), GameActivity.class).putExtra("help", false));

            }
        });

        radioGameModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                editor.putBoolean(GAME_MODE_KEY, checkedId == R.id.radio_kids ? false : true);
                editor.commit();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GameActivity.class).putExtra("help", true));
            }
        });
    }
}
