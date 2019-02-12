package com.example.x_note.allofgistlite;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class AcademicCalendarActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_calendar);

        OrangeToast(getApplicationContext(),"좌우로 스와이프하여 달을 이동할 수 있습니다.");

        mViewPager = (ViewPager)findViewById(R.id.calenderPager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(6);   //페이지 좌우 준비 갯수 문제가 생긴다면 수정 요망

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

    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.show();
    }
}