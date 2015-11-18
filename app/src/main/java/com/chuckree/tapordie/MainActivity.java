package com.chuckree.tapordie;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay, btnHelp, btnSettings;
    Typeface tfMontserrat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.chuckree.tapordie.R.layout.activity_main);

        btnPlay = (Button) findViewById(com.chuckree.tapordie.R.id.button_play);
        btnHelp = (Button) findViewById(com.chuckree.tapordie.R.id.button_help);
        btnSettings = (Button) findViewById(com.chuckree.tapordie.R.id.button_settings);

        tfMontserrat = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Hairline.otf");
        btnPlay.setTypeface(tfMontserrat);
        btnHelp.setTypeface(tfMontserrat);
        btnSettings.setTypeface(tfMontserrat);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(gameIntent);

            }
        });

    }
}
