package com.vizy.ignitar.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vizy.ignitar.R;
import com.vizy.ignitar.cloud.CloudReco;
import com.vizy.ignitar.constants.IgnitarConstants;
import com.vizy.ignitar.utils.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ScannerActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Button startScan, toHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        startScan = (Button) findViewById(R.id.start_scan);
        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScannerActivity.this, CloudReco.class);
                intent.putExtra("ACTIVITY_TO_LAUNCH",
                        "app.CloudRecognition.CloudReco");
                intent.putExtra("ABOUT_TEXT", "CloudReco/CR_about.html");
                startActivity(intent);
            }
        });
        toHomeActivity = (Button) findViewById(R.id.home);
        toHomeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScannerActivity.this, HomeActivity.class));
                finish();
            }
        });
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.facebook.samples.hellofacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (@NonNull PackageManager.NameNotFoundException e) {
            Log.d(TAG, StringUtils.isNullOrEmpty(e.getMessage()) ? IgnitarConstants.Exceptions.NAME_NOT_FOUND_EXCEPTION
                    : e.getMessage());
        } catch (@NonNull NoSuchAlgorithmException e) {
            Log.d(TAG, StringUtils.isNullOrEmpty(e.getMessage()) ? IgnitarConstants.Exceptions.NO_SUCH_ALGORITHM_EXCEPTION
                    : e.getMessage());
        }
    }
}
