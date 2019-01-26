package com.example.x_note.allofgistlite;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnonymousForumActivity_Preview extends AppCompatActivity {

    String id;
    LinearLayout buttonHome;
    LinearLayout buttonBest;
    ImageView imageViewBest;
    TextView textViewBest;
    int bestButtonClick;
    LinearLayout buttonSearch;
    LinearLayout buttonWrite;

    private Fragment OverviewFragment;
    private Fragment BestviewFragment;

    private ViewPager fmViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_preview);

        bestButtonClick = 0;

        id = getIntent().getStringExtra("ID");
        Bundle bundle = new Bundle();
        bundle.putString("ID",id);

        OverviewFragment = new AnonymousForum_OverviewFragment();
        BestviewFragment = new AnonymousForum_BestviewFragment();

        OverviewFragment.setArguments(bundle);
        BestviewFragment.setArguments(bundle);

        initialSetting();

        fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        fmViewPager.setOffscreenPageLimit(1);



        buttonHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bestButtonClick%2==0){
                    buttonBest.setSelected(true);
                    imageViewBest.setSelected(true);
                    textViewBest.setText(R.string.menu_button_overview);
                    bestButtonClick++;
                    fmViewPager.setCurrentItem(1);
                }
                else{
                    buttonBest.setSelected(false);
                    imageViewBest.setSelected(false);
                    textViewBest.setText(R.string.menu_button_best);
                    bestButtonClick++;
                    fmViewPager.setCurrentItem(0);
                }
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonWrite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent writeActivity = new Intent(AnonymousForumActivity_Preview.this, AnonymousForumActivity_Write.class);
                writeActivity.putExtra("ID",id);
                startActivity(writeActivity);
            }
        });


    }

    private class PagerAdapter extends FragmentStatePagerAdapter {


        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return OverviewFragment;
                case 1:
                    return BestviewFragment ;
                default:
                    return null;
            }

        }


        @Override
        public int getCount() {
            return 2;
        }
    }

    public void initialSetting(){
        buttonHome = (LinearLayout)findViewById(R.id.button_home);
        buttonSearch = (LinearLayout)findViewById(R.id.button_search);
        buttonWrite = (LinearLayout)findViewById(R.id.button_write);
        buttonBest = (LinearLayout)findViewById(R.id.button_best);
        textViewBest = (TextView)findViewById(R.id.button_best_textview);
        imageViewBest = (ImageView)findViewById(R.id.button_best_imageview);

        fmViewPager = (ViewPager)findViewById(R.id.anonymous_forum_preview_switch_layout);

    }





}


