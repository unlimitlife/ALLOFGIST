package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;


public class AcademicCalendarActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ImageButton backButton;


    private TextView month;
    private TextView year;
    private ImageButton back;
    private ImageButton foward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final int month_value = Calendar.getInstance(Locale.KOREA).get(Calendar.MONTH)+1;
        final int year_value = Calendar.getInstance(Locale.KOREA).get(Calendar.YEAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_calendar);

        backButton = (ImageButton)findViewById(R.id.academic_calendar_back_button);
        GrayToast(getApplicationContext(),"좌우로 스와이프하여 날짜를 이동할 수 있습니다.");

        mViewPager = (ViewPager)findViewById(R.id.calenderPager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(6);   //페이지 좌우 준비 갯수 문제가 생긴다면 수정 요망

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        month = (TextView) findViewById(R.id.month_tv_academic_list);
        year = (TextView) findViewById(R.id.year_tv_academic_list);
        back = (ImageButton) findViewById(R.id.back_academic_list);
        foward = (ImageButton) findViewById(R.id.foward_academic_list);
        month.setText((mViewPager.getCurrentItem()+month_value)+"월");
        year.setText(year_value+"년");

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                month.setText((i+month_value)+"월");
                year.setText(year_value+"년");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1, true);
            }
        });
        foward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1, true);
            }
        });

    }

    private class PagerAdapter extends FragmentStatePagerAdapter{

        // get currentMonth
        private Calendar today = Calendar.getInstance();
        int month = today.get(Calendar.MONTH)+1;


        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return AcademicFragment.create(position);
        }



        @Override
        public int getCount() {
            int pageNum = 13-month;
            return pageNum;
        }
    }



    public static void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

}