package com.vizy.ignitar.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.vizy.ignitar.R;

public class SplashScreen extends AppCompatActivity {
    Boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isConnected){
                Intent i=new Intent(SplashScreen.this,ScannerActivity.class);
                startActivity(i);
                finish();}
                else{
                     startActivity(new Intent(SplashScreen.this,NoConnection.class));
                }
            }
        },2000);
    }
}
