package com.vizy.ignitar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

        viewAd=(CardView)findViewById(R.id.view_ad);
        viewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewGiveFeedback=(CardView)findViewById(R.id.view_give_feedback);
        viewGiveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewActionMeter=(CardView)findViewById(R.id.view_my_action_meter);
        viewActionMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewPlayGame=(CardView)findViewById(R.id.view_play_game);
        viewPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewReviewOnZomato=(CardView)findViewById(R.id.view_review_on_zomato);
        viewReviewOnZomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewCompanySpecification=(RelativeLayout)findViewById(R.id.company_specification);
        viewCompanySpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}