package com.vizy.ignitar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.vizy.ignitar.R;

public class SplashScreen extends AppCompatActivity {
    private Boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final SharedPreferences sp=getSharedPreferences("ignitar",MODE_PRIVATE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isConnected) {
                    if (!sp.getBoolean("signin",false)) {
                        Intent i = new Intent(SplashScreen.this, SignIn.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this,HomeActivity.class));
                        finish();
                    }
                }
                else{
                     startActivity(new Intent(SplashScreen.this,NoConnection.class));
                }
            }
        },2000);
    }
}
