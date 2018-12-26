package com.example.x_note.allofgistlite;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class Tutorial extends AppCompatActivity {

    private Fragment OfficialFragment;
    private Fragment OrganizationFragment;
    private Fragment CircleFragment;

    private ViewPager fmViewPager;

    /*//dataStorage
    private ArrayList<Site> itemList;
    private Hashtable<String,Boolean> buttonCount;


    //GridView 데이터 연결
    private GridView siteList;
    private SiteAdapter siteAdapter;*/

    //바로가기 버튼
    private Button official;
    private Button organization;
    private Button circle;

    private Button next;

    //login information
    private String id;

    private int nextClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        OrangeToast(Tutorial.this,"좌측 버튼을 누르거나 좌우로 스와이프 하여 사이트 종류를 바꿀 수 있습니다.");

        //로그인 되어있는 ID 불러오기
        SharedPreferences loginPrefs = getSharedPreferences("LOGIN_ID",MODE_PRIVATE);
        id = loginPrefs.getString("ID","LOGIN_ERROR");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(Tutorial.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        Bundle bundle = new Bundle();
        bundle.putString("ID",id);

        OfficialFragment = new OfficialSiteFragment();
        OrganizationFragment = new OrganizationSiteFragment();
        CircleFragment = new CircleSiteFragment();
        OfficialFragment.setArguments(bundle);
        OrganizationFragment.setArguments(bundle);
        CircleFragment.setArguments(bundle);

        fmViewPager = (ViewPager)findViewById(R.id.tutorial_switch_layout);
        fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        fmViewPager.setOffscreenPageLimit(2);

        /*itemList = new ArrayList<Site>();
        setData(itemList);

        //button 클릭 횟수 초기화
        buttonCount = new Hashtable<String, Boolean>();
        for(int i=0;i<itemList.size();i++)
            buttonCount.put(itemList.get(i).getMsite_name(),false);


        //GridView 데이터 연결
        siteList = (GridView) findViewById(R.id.allsites);
        siteAdapter = new SiteAdapter(getApplicationContext(),itemList);
        siteList.setAdapter(siteAdapter);*/

        //바로가기 버튼
        official = (Button) findViewById(R.id.official_button_tutorial);
        organization = (Button) findViewById(R.id.organization_button_tutorial);
        circle = (Button) findViewById(R.id.circle_button_tutorial);

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

        //즐겨찾기 완료 버튼
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nextClick==0){
                    OrangeToast(Tutorial.this,"마지막 즐겨찾기 선택을 누른 후 3초 정도 기다리시면 안전하게 등록이 가능합니다.\n확인하셨다면 다시 한 번 눌러주세요.");
                }
                else {
                    Intent main = new Intent(Tutorial.this, MainActivity.class);
                    startActivity(main);
                }
                nextClick++;
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




    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

/*
    //gridview - item Adapter
    class SiteAdapter extends ArrayAdapter<Site>{

        private ArrayList<Site> itemList;
        private Context mContext;
        private SiteHolder siteHolder;



        public SiteAdapter(Context context, ArrayList<Site> itemList){
            super(context,0,itemList);
            this.itemList = itemList;
            this.mContext = context;
        }


        //임시로
        protected void AllRemove(){
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }


        @Override
        public int getCount() {
            return itemList.size();
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if(view==null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.gridviewitem_allsites, parent, false);

                siteHolder = new SiteHolder(view);
                view.setTag(siteHolder);

            }
            else
                siteHolder = (SiteHolder) view.getTag();


            siteHolder.mTextView.setText(itemList.get(position).getMsite_name());

            // 비트맵 동그랗게 사이즈 맞게 줄이는 메소드 이용
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            siteHolder.mImageView.setBackground(new ShapeDrawable(new OvalShape()));
            siteHolder.mImageView.setClipToOutline(true);
            siteHolder.mImageView.requestLayout();
            siteHolder.mImageView.setImageBitmap(bitmap);
            siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_unselected_state);

            if(buttonCount.get(siteHolder.mTextView.getText()))
            {
                siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_selected_state);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonCount.put(itemList.get(position).getMsite_name(),!buttonCount.get(itemList.get(position).getMsite_name()));
                    if(buttonCount.get(itemList.get(position).getMsite_name())){
                        FavoriteTask favoriteTask = new FavoriteTask();
                        favoriteTask.execute("http://donggunserver.iptime.org/favoriteinsert.php",id,position+"");
                        view.setBackgroundResource(R.drawable.item_selected_state);
                    }
                    else {
                        FavoriteTask favoriteTask = new FavoriteTask();
                        favoriteTask.execute("http://donggunserver.iptime.org/favoritedelete.php",id,position+"");
                        view.setBackgroundResource(R.drawable.item_unselected_state);
                    }
                }
            });

            return view;
        }

        @Override
        public int getPosition(@Nullable Site item) {
            return itemList.indexOf(item);
        }

    }


    //SiteHolder
    public class SiteHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public LinearLayout mLinearlayout;

        public SiteHolder(View view){
            mImageView = (ImageView) view.findViewById(R.id.image);
            mTextView = (TextView) view.findViewById(R.id.name);
            mLinearlayout = (LinearLayout) view.findViewById(R.id.clickicon);
        }
    }


    //즐겨찾기 DB 저장, 삭제
    class FavoriteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ID = (String)strings[1];
            String siteNum = (String)strings[2];

            String serverUrl = (String)strings[0];
            String postParameters = "id="+ID+"&favoritehomepage="+siteNum;

            try{
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);

                return new String("Error: "+e.getMessage());
            }
        }
    }


    //Bitmap resizing
    private int setSimpleSize(BitmapFactory.Options options, int requestWidth, int requestHeight){
        // 이미지 사이즈를 체크할 원본 이미지 가로/세로 사이즈를 임시 변수에 대입.
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // 원본 이미지 비율인 1로 초기화
        int size = 1;

        // 해상도가 깨지지 않을만한 요구되는 사이즈까지 2의 배수의 값으로 원본 이미지를 나눈다.
        while(requestWidth < originalWidth || requestHeight < originalHeight){
            originalWidth = originalWidth / 2;
            originalHeight = originalHeight / 2;

            size = size * 2;
        }
        return size;
    }

    //데이터 입력
    public void setData(ArrayList<Site> itemList){
        itemList.add(new Site("GIST home", "https://www.gist.ac.kr/kr/","https://blog.naver.com/gist1993","https://www.facebook.com/GIST.ac.kr/","https://www.instagram.com/gist1993/", R.drawable.home));
        itemList.add(new Site("Gel","https://gel.gist.ac.kr/",R.drawable.gel));
        itemList.add(new Site("Zeus system", "https://zeus.gist.ac.kr", R.drawable.zeus));
        itemList.add(new Site("수강 신청 사이트","https://zeus.gist.ac.kr/sys/lecture/lecture_main.do",R.drawable.courseregisteration));
        itemList.add(new Site("Portal system", "https://portal.gist.ac.kr/intro.jsp", R.drawable.portal));
        itemList.add(new Site("Email system", "https://mail.gist.ac.kr/loginl?locale=ko_KR", R.drawable.email));
        itemList.add(new Site("GIST college", "https://college.gist.ac.kr/", R.drawable.college));
        itemList.add(new Site("GIST library", "https://library.gist.ac.kr/", R.drawable.library));
        itemList.add(new Site("학내공지", "https://college.gist.ac.kr/main/Sub040203", R.drawable.haknaegongji));
        itemList.add(new Site("GIST 대학생","https://www.facebook.com/groups/giststudent/",R.drawable.giststudent));
        itemList.add(new Site("GIST 대나무숲","https://www.facebook.com/GISTIT.ac.kr/",R.drawable.gistdaenamoo));
        itemList.add(new Site("GIST 대나무숲 제보함","http://fbpage.kr/?pi=128#/submit",R.drawable.gistdaenamoojaeboo));
        itemList.add(new Site("언어교육센터","https://language.gist.ac.kr/",R.drawable.language));
        itemList.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup));
        itemList.add(new Site("학과별 사이트",R.drawable.majorset));
        itemList.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion));
        itemList.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight));
        itemList.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse));
        itemList.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe));
        itemList.add(new Site("GIST 신문","http://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews));
        itemList.add(new Site("GIST 홍보대사", "http://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare));
        itemList.add(new Site("춤 동아리 막무가내", "https://www.facebook.com/gistmacmoo/","https://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae));
        itemList.add(new Site("힙합 동아리 Ignition", "https://www.facebook.com/GISTignition/","https://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition));
        itemList.add(new Site("노래 동아리 싱송생송", "https://www.facebook.com/gistsingsong/","https://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong));
        itemList.add(new Site("연극 동아리 지대로", "https://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","https://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero));
        itemList.add(new Site("오케스트라 동아리 악동", "https://www.facebook.com/GISTorchestra/","https://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra));
        itemList.add(new Site("기타 동아리 HOTSIX", "https://www.facebook.com/gisthotsix/", R.drawable.hotsix));
        itemList.add(new Site("피아노 동아리 GISRI", "https://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri));
        itemList.add(new Site("밴드 Main", "https://www.facebook.com/MAIN-899414343552162/", R.drawable.main));
        itemList.add(new Site("밴드 도도한 쭈쭈바", "https://www.facebook.com/dozzu/", R.drawable.dozzu));
        itemList.add(new Site("영화 동아리 Cinergy", "https://www.facebook.com/gistcinergy/", R.drawable.cinergy));
        itemList.add(new Site("영상편집 동아리 The GIST", "https://www.facebook.com/Gentletist/","https://www.youtube.com/channel/UCMUDHS0SZvQilFe5h6eI9rA/videos", R.drawable.thegist));
        itemList.add(new Site("문예창작 동아리 사각사각", "https://www.facebook.com/GIST-%EC%82%AC%EA%B0%81%EC%82%AC%EA%B0%81-238788459851229/", R.drawable.sagaksagak));
        itemList.add(new Site("GIST 고양이 지냥이", "https://www.facebook.com/giscats/", R.drawable.giscat));
        itemList.add(new Site("요리동아리 이쑤시개", "https://www.facebook.com/%EC%9D%B4%EC%91%A4%EC%8B%9C%EA%B0%9C-%EC%9A%94%EB%A6%AC%ED%95%98%EB%8A%94-GIST%EC%83%9D-272551203239747/?ref=py_c", R.drawable.essosigae));
        itemList.add(new Site("칵테일 동아리 MixoloGIST", "https://www.facebook.com/Mixologist-1584231725157207", R.drawable.mixologist));
        itemList.add(new Site("보드게임 동아리 BGM", "https://www.facebook.com/GISTBGM", R.drawable.bgm));
        itemList.add(new Site("성소수자 모임 speQtrum", "https://www.facebook.com/gistspeqtrum/", R.drawable.speqtrum));
        itemList.add(new Site("만화 동아리 erutlucbus", "https://www.facebook.com/Erutlucbus/", R.drawable.eru));
        itemList.add(new Site("천체관측 동아리 SpaceBar", "https://www.facebook.com/GISTspacebar/", R.drawable.spacebar));
        itemList.add(new Site("전산 동아리 WING", "https://www.facebook.com/GISTWING/", R.drawable.wing));
        itemList.add(new Site("환경 동아리 온새미로", "https://www.facebook.com/onsaemiro123/", R.drawable.onsaemiro));
        itemList.add(new Site("축구 동아리 Kickass", "https://www.facebook.com/gistkickass/?ref=br_rs", R.drawable.kickass));
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        //다음화면으로 갈떄 현재 액티비티를 종료
        finish();
    }
}
