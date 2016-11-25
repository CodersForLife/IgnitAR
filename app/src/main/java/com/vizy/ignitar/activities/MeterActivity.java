package com.vizy.ignitar.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.vizy.ignitar.R;
import com.vizy.ignitar.preferences.IgnitarStore;
import com.vizy.ignitar.ui.WaveHelper;
import com.vizy.ignitar.ui.customviews.WaveView;

public class MeterActivity extends AppCompatActivity {

    private WaveHelper mWaveHelper;
    private IgnitarStore ignitarStore;
    private int mBorderColor = Color.parseColor("#f16d7a");
    private int mBorderWidth = 10;
    private WaveView waveView;
    private TextView couponCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);

        ignitarStore = new IgnitarStore(MeterActivity.this);
        waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);
        mWaveHelper = new WaveHelper(waveView, ignitarStore.getProductScan());
        couponCount = (TextView) findViewById(R.id.number_of_coupons);
        couponCount.setText(ignitarStore.getCouponCount() + "");
        mWaveHelper.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mWaveHelper.start();
    }
}
