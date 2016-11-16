package com.vizy.ignitar.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.vizy.ignitar.R;
import com.vizy.ignitar.network.InternetConnection;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final SharedPreferences sp=getSharedPreferences("ignitar",MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(InternetConnection.isConnected(SplashScreen.this)) {
                    if (!sp.getBoolean("signin",false)) {
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
        },2000);
    }
}
