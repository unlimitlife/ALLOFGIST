package com.allofgist.dell.allofgistlite;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

//import static com.allofgist.dell.allofgistlite.MainActivity.MultipleColorInOneText;

public class AnonymousForumActivity_Preview extends AppCompatActivity {

    TextView titleAnonymousForum;

    String id;
    Bundle bundle;
    //LinearLayout buttonHome;
    //LinearLayout buttonBest;
    //ImageView imageViewBest;
    //TextView textViewBest;
    //int bestButtonClick;
    //LinearLayout buttonSearch;
    //LinearLayout buttonWrite;
    ImageButton backButton;
    ImageButton moreButton;

    private FloatingActionButton writeButton;

    private Fragment OverviewFragment;
    //private Fragment BestviewFragment;

    private NonSwipeableViewPager fmViewPager;
    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_preview);

        //bestButtonClick = 0;

        id = getIntent().getStringExtra("ID");
        bundle = new Bundle();
        bundle.putString("ID",id);

        OverviewFragment = new AnonymousForum_OverviewFragment();
        OverviewFragment.setArguments(bundle);
        //BestviewFragment = new AnonymousForum_BestviewFragment();
        //BestviewFragment.setArguments(bundle);

        initialSetting();

        //MultipleColorInOneText(Title,titleAnonymousForum);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        fmViewPager.setAdapter(pagerAdapter);
        fmViewPager.setOffscreenPageLimit(1);
        fmViewPager.setPagingEnabled(false);



        /*buttonHome.setOnClickListener(new View.OnClickListener(){
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
                    textViewBest.setTextColor(Color.WHITE);
                    bestButtonClick++;

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.detach(BestviewFragment);
                    ft.attach(BestviewFragment).commit();
                    fmViewPager.setCurrentItem(1);
                }
                else{
                    buttonBest.setSelected(false);
                    imageViewBest.setSelected(false);
                    textViewBest.setTextColor(Color.parseColor("#585858"));
                    textViewBest.setText(R.string.menu_button_best);
                    bestButtonClick++;

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.detach(OverviewFragment);
                    ft.attach(OverviewFragment).commit();
                    fmViewPager.setCurrentItem(0);
                }
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchAtivity = new Intent(AnonymousForumActivity_Preview.this, AnonymousForumActivity_Search.class);
                searchAtivity.putExtra("ID",id);
                startActivity(searchAtivity);

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

*/
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMine = new PopupMenu(getApplicationContext(), v);
                MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                menuInflater.inflate(R.menu.anonymous_forum_preview_more_popup_menu, popupMine.getMenu());
                popupMine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bestview:
                                Intent BestViewActivity = new Intent(AnonymousForumActivity_Preview.this, AnonymousForumActivity_Preview_Bestview.class);
                                BestViewActivity.putExtra("ID",id);
                                startActivity(BestViewActivity);
                                break;

                            case R.id.search:
                                Intent searchActivity = new Intent(AnonymousForumActivity_Preview.this, AnonymousForumActivity_Search.class);
                                searchActivity.putExtra("ID",id);
                                startActivity(searchActivity);
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMine.show();
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
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
                //case 1:
                    //return OverviewFragment;
                    //return BestviewFragment ;
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

        backButton = (ImageButton)findViewById(R.id.bulletin_board_back_button);
        moreButton = (ImageButton)findViewById(R.id.bulletin_board_more);
        writeButton = (FloatingActionButton)findViewById(R.id.write_bulletin_board);

        /*buttonHome = (LinearLayout)findViewById(R.id.button_home);
        buttonSearch = (LinearLayout)findViewById(R.id.button_search);
        buttonWrite = (LinearLayout)findViewById(R.id.button_write);
        buttonBest = (LinearLayout)findViewById(R.id.button_best);
        textViewBest = (TextView)findViewById(R.id.button_best_textview);
        imageViewBest = (ImageView)findViewById(R.id.button_best_imageview);*/

        fmViewPager = (NonSwipeableViewPager) findViewById(R.id.anonymous_forum_preview_switch_layout);

    }

}


