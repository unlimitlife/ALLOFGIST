package com.allofgist.dell.allofgistlite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class AnonymousForumActivity_Preview_Bestview extends AppCompatActivity {
    String id;
    Bundle bundle;

    private Fragment BestviewFragment;

    private ImageButton backButton;
    private NonSwipeableViewPager fmViewPager;

    PagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum__preview__bestview);

        id = getIntent().getStringExtra("ID");
        bundle = new Bundle();
        bundle.putString("ID",id);

        BestviewFragment = new AnonymousForum_BestviewFragment();
        BestviewFragment.setArguments(bundle);

        initialSetting();

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        fmViewPager.setAdapter(pagerAdapter);
        fmViewPager.setOffscreenPageLimit(1);
        fmViewPager.setPagingEnabled(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    return BestviewFragment;
                default:
                    return null;
            }

        }


        @Override
        public int getCount() {
            return 1;
        }
    }

    public void initialSetting(){

        backButton = (ImageButton)findViewById(R.id.bestview_back_button);

        fmViewPager = (NonSwipeableViewPager) findViewById(R.id.anonymous_forum_preview_bestview_switch_layout);

    }
}
