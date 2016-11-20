package com.vizy.ignitar.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vizy.ignitar.R;
import com.vizy.ignitar.fragment.HistoryFragment;
import com.vizy.ignitar.fragment.TrendingFragment;
import com.vizy.ignitar.cloud.CloudReco;
import com.vuforia.VirtualButton;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton scan;
    private RelativeLayout search;
    private Button help;
    private int i = 0;
    private Camera mCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeScreen();
        //      Intent i=getIntent();
//        Log.e("dd",i.getStringExtra("videoname"));
        final SharedPreferences sp = getSharedPreferences("ignitar", MODE_PRIVATE);
        if (!sp.getBoolean("first", false)) {
            startActivity(new Intent(HomeActivity.this, Help.class));
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("first", true);
            editor.apply();
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                    Intent intent = new Intent(HomeActivity.this, CloudReco.class);
                    intent.putExtra("ACTIVITY_TO_LAUNCH",
                            "app.CloudRecognition.CloudReco");
                    intent.putExtra("ABOUT_TEXT", "CloudReco/CR_about.html");
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                }
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Help.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent intent = new Intent(HomeActivity.this, CloudReco.class);
                    intent.putExtra("ACTIVITY_TO_LAUNCH",
                            "app.CloudRecognition.CloudReco");
                    intent.putExtra("ABOUT_TEXT", "CloudReco/CR_about.html");
                    startActivity(intent);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 1: {
                if (i == 0) {
                    mCam = Camera.open();
                    Camera.Parameters p = mCam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCam.setParameters(p);
                    SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
                    try {
                        mCam.setPreviewTexture(mPreviewTexture);
                        i = 1;
                    } catch (IOException ex) {
                        // Ignore
                    }
                } else {
                    mCam.startPreview();
                    mCam.stopPreview();
                    mCam.release();
                    i = 0;
                    mCam = null;
                }
            }
            return;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void initializeScreen() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        search = (RelativeLayout) findViewById(R.id.search);
        help = (Button) findViewById(R.id.help);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /**
         * Create SectionPagerAdapter, set it as adapter to viewPager with setOffscreenPageLimit(2)
         **/
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Use positions (0 and 1) to find and instantiate fragments with newInstance()
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {

            Fragment fragment;
            /**
             * Set fragment to different fragments depending on position in ViewPager
             */
            switch (position) {
                case 0:
                    fragment = TrendingFragment.newInstance(null, null);
                    break;
                case 1:
                    fragment = HistoryFragment.newInstance(null, null);
                    break;
                default:
                    fragment = TrendingFragment.newInstance(null, null);
                    break;
            }
            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }

        /**
         * Set string resources as titles for each fragment by it's position
         *
         * @param position
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Trending";
                case 1:
                default:
                    return "History";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.help:
                startActivity(new Intent(HomeActivity.this, Help.class));
                return true;
            case R.id.night_mode:
                if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (i == 0) {
                        mCam = Camera.open();
                        Camera.Parameters p = mCam.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCam.setParameters(p);
                        SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
                        try {
                            mCam.setPreviewTexture(mPreviewTexture);
                            i = 1;
                        } catch (IOException ex) {
                            // Ignore
                        }
                    } else {
                        mCam.startPreview();
                        mCam.stopPreview();
                        mCam.release();
                        i = 0;
                        mCam = null;
                    }
                } else {

                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            1);

                }
                return true;
            case R.id.rate:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
