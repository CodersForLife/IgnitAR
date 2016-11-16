package com.vizy.ignitar.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.vizy.ignitar.R;
import com.vizy.ignitar.constants.IgnitarConstants;
import com.vizy.ignitar.network.InternetConnection;
import com.vizy.ignitar.preferences.IgnitarStore;

public class SplashScreen extends AppCompatActivity {

    private IgnitarStore ignitarStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ignitarStore=new IgnitarStore(SplashScreen.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(InternetConnection.isConnected(SplashScreen.this)) {
                    if (!ignitarStore.getTourTaken()) {
                        Intent i = new Intent(SplashScreen.this, SignIn.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this,HomeActivity.class).putExtra("videoname",""));
                        finish();
                    }
                }
                else{
                     startActivity(new Intent(SplashScreen.this,NoConnection.class));
                }
            }
        }, IgnitarConstants.DEFAULT_LOADING_TIME);
    }
}
