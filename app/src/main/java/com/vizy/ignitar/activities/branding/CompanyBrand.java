package com.vizy.ignitar.activities.branding;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.vizy.ignitar.R;
import com.vizy.ignitar.ui.customviews.CameraView;

public class CompanyBrand extends AppCompatActivity {

    private final static String TAG = "CompanyBrand";
    private View mContentView;
    private Camera camera;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_company_brand);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        camera = getCameraInstance();
        if (camera != null) {
            cameraView = new CameraView(this, camera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_view);
            preview.addView(cameraView);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(TAG, e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
}
