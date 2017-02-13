package com.vizy.ignitar.activities.branding;

import android.animation.Animator;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.vizy.ignitar.R;
import com.vizy.ignitar.ui.customviews.CameraView;

public class CompanyBrand extends AppCompatActivity {

    private final static String TAG = "CompanyBrand";
    private View mContentView;
    private Camera camera;
    private CameraView cameraView;

    private ImageView kfcCal;
    private ImageView kfcFB;
    private ImageView kfcTWITTER;
    private ImageView kfcLogo;
    private ImageView kfcOfferButton;
    private ImageView kfcReviewUs;
    private ImageView kfcSelfie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_company_brand);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        kfcCal = (ImageView) findViewById(R.id.kfc_calendar);
        kfcFB = (ImageView) findViewById(R.id.kfc_facebook);
        kfcTWITTER = (ImageView) findViewById(R.id.kfc_twitter);
        kfcOfferButton = (ImageView) findViewById(R.id.kfc_offer);
        kfcReviewUs = (ImageView) findViewById(R.id.kfc_review);
        kfcSelfie = (ImageView) findViewById(R.id.kfc_selfie);


        camera = getCameraInstance();
        if (camera != null) {
            cameraView = new CameraView(this, camera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_view);
            preview.addView(cameraView);
        }


        kfcCal.animate().rotationYBy(45f).rotationXBy(180f).setDuration(9000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        kfcTWITTER.animate().translationX(-150f).setDuration(9000);
        kfcFB.animate().translationX(150f).setDuration(9000);
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
