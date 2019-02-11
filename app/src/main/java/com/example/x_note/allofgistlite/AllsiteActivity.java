package com.example.x_note.allofgistlite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class AllsiteActivity extends AppCompatActivity {

    private Fragment OfficialFragment;
    private Fragment OrganizationFragment;
    private Fragment CircleFragment;

    private ViewPager fmViewPager;


    //바로가기 버튼
    private Button official;
    private Button organization;
    private Button circle;

    private ImageButton beforeButton;

    //login information
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_site);


        //로그인 되어있는 ID 불러오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(AllsiteActivity.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        Bundle bundle = new Bundle();
        bundle.putString("ID",id);

        OfficialFragment = new Allsite_OfficialSiteFragment();
        OrganizationFragment = new Allsite_OrganizationSiteFragment();
        CircleFragment = new Allsite_CircleSiteFragment();
        OfficialFragment.setArguments(bundle);
        OrganizationFragment.setArguments(bundle);
        CircleFragment.setArguments(bundle);

        fmViewPager = (ViewPager)findViewById(R.id.allsite_switch_layout);
        fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        fmViewPager.setOffscreenPageLimit(2);

        beforeButton = (ImageButton)findViewById(R.id.allsite_back_button);

        beforeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //바로가기 버튼
        official = (Button) findViewById(R.id.official_button_allsite);
        organization = (Button) findViewById(R.id.organization_button_allsite);
        circle = (Button) findViewById(R.id.circle_button_allsite);

        official.setSelected(true);
        organization.setSelected(false);
        circle.setSelected(false);


        //바로가기 설정
        official.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                official.setSelected(true);
                organization.setSelected(false);
                circle.setSelected(false);
                fmViewPager.setCurrentItem(0);
            }
        });
        organization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                official.setSelected(false);
                organization.setSelected(true);
                circle.setSelected(false);
                fmViewPager.setCurrentItem(1);
            }
        });
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                official.setSelected(false);
                organization.setSelected(false);
                circle.setSelected(true);
                fmViewPager.setCurrentItem(2);
            }
        });


        fmViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        official.setSelected(true);
                        organization.setSelected(false);
                        circle.setSelected(false);
                        break;
                    case 1:
                        official.setSelected(false);
                        organization.setSelected(true);
                        circle.setSelected(false);
                        break;
                    case 2:
                        official.setSelected(false);
                        organization.setSelected(false);
                        circle.setSelected(true);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

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
                    return OfficialFragment;
                case 1:
                    return OrganizationFragment;
                case 2:
                    return CircleFragment;
                default:
                    return null;
            }

        }


        @Override
        public int getCount() {
            return 3;
        }
    }



}
