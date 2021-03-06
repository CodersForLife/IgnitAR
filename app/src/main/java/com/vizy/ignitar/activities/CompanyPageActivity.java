package com.vizy.ignitar.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vizy.ignitar.R;

public class CompanyPageActivity extends AppCompatActivity {

    private CardView viewAd;
    private CardView viewGiveFeedback;
    private CardView viewActionMeter;
    private CardView viewPlayGame;
    private CardView viewReviewOnZomato;
    private CardView viewRateUs;
    private RelativeLayout viewCompanySpecification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewAd = (CardView) findViewById(R.id.view_ad);
        viewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=IIJMBxY3Cmo")));
            }
        });

        viewGiveFeedback = (CardView) findViewById(R.id.view_give_feedback);
        viewGiveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewActionMeter = (CardView) findViewById(R.id.view_my_action_meter);
        viewActionMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyPageActivity.this, MeterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewPlayGame = (CardView) findViewById(R.id.view_play_game);
        viewPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewReviewOnZomato = (CardView) findViewById(R.id.view_review_on_zomato);
        viewReviewOnZomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.zomato.com/ncr/chai-thela-sector-62-noida/menu"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        viewCompanySpecification = (RelativeLayout) findViewById(R.id.company_specification);
        viewCompanySpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageView fab = (ImageView) findViewById(R.id.facebook_icon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.facebook.com/chaithela/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        ImageView fab2 = (ImageView) findViewById(R.id.twitter_icon);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/chaithela"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

}
