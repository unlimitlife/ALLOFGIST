package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FavoriteSettingActivity extends AppCompatActivity {

    private List<Site> itemList = null;

    private String id = "LOGIN_ERROR";
    private ArrayList<Integer> keylist;

    private ArrayList<Integer> officialKeylist;
    private ArrayList<Integer> organizationKeylist;
    private ArrayList<Integer> circleKeylist;

    private Fragment OfficialFragment;
    private Fragment OrganizationFragment;
    private Fragment CircleFragment;

    private ViewPager fmViewPager;

    private Button official;
    private Button organization;
    private Button circle;

    private Button completeButton;
    private ImageButton backButton;

    private int nextClick = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*FavoriteRestoreTask favoriteRestoreTask = new FavoriteRestoreTask();
        startMyTask(favoriteRestoreTask,"http://13.124.99.123/favoriterestore.php", id);

        try {
            for (int i = 0; i < itemList.size(); i++) {
                if(keylist.contains(i)){
                    SharedPreferences.Editor favoriteEditor = getSharedPreferences("FAVORITE_KEYLIST",Context.MODE_PRIVATE).edit();
                    favoriteEditor.putString("KEYLIST_"+id+"_"+i,"OK");
                    favoriteEditor.commit();
                }
                else{
                    SharedPreferences.Editor favoriteEditor = getSharedPreferences("FAVORITE_KEYLIST",Context.MODE_PRIVATE).edit();
                    favoriteEditor.putString("KEYLIST_"+id+"_"+i,"NONE");
                    favoriteEditor.commit();
                }

            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_setting);

        //setData();
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(FavoriteSettingActivity.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }
        //keylist = intent.getIntegerArrayListExtra("FAVORITE_KEYLIST");

        officialKeylist = new ArrayList<Integer>();
        organizationKeylist = new ArrayList<Integer>();
        circleKeylist = new ArrayList<Integer>();

        keylist = new ArrayList<Integer>();
        startMyTask(new FavoriteLoadTask(), id);


        //바로가기 버튼
        official = (Button) findViewById(R.id.official_button_favorite_setting);
        organization = (Button) findViewById(R.id.organization_button_favorite_setting);
        circle = (Button) findViewById(R.id.circle_button_favorite_setting);

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


        //즐겨찾기 완료 버튼
        completeButton = (Button)findViewById(R.id.favorite_setting_ok_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nextClick==0){
                    OrangeToast(FavoriteSettingActivity.this,"마지막 즐겨찾기 선택을 누른 후 3초 정도 기다리시면 안전하게 등록이 가능합니다.\n확인하셨다면 다시 한 번 눌러주세요.");
                }
                else {
                    startMyTask(new FavoriteInsertTask(),id);
                }
                nextClick++;
            }
        });

        backButton = (ImageButton)findViewById(R.id.favorite_setting_back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
                /*FavoriteRestoreTask favoriteRestoreTask = new FavoriteRestoreTask();
                startMyTask(favoriteRestoreTask,"http://13.124.99.123/favoriterestore.php", id);

                try {
                    for (int i = 0; i < itemList.size(); i++) {
                        if(keylist.contains(i)){
                            SharedPreferences.Editor favoriteEditor = getSharedPreferences("FAVORITE_KEYLIST",Context.MODE_PRIVATE).edit();
                            favoriteEditor.putString("KEYLIST_"+id+"_"+i,"OK");
                            favoriteEditor.commit();
                        }
                        else{
                            SharedPreferences.Editor favoriteEditor = getSharedPreferences("FAVORITE_KEYLIST",Context.MODE_PRIVATE).edit();
                            favoriteEditor.putString("KEYLIST_"+id+"_"+i,"NONE");
                            favoriteEditor.commit();
                        }

                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }*/
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

    public void editKeyList(String keylistKinds, int key){
        switch(keylistKinds){
            case "OFFICIAL" :
                if(findKeyPos("OFFICIAL",key)==-1)
                    keylist.add(key+1);
                else
                    keylist.remove(findKeyPos("OFFICIAL",key));
                break;

            case "ORGANIZATION" :
                if(findKeyPos("ORGANIZATION",key)==-1)
                    keylist.add(key+16);
                else
                    keylist.remove(findKeyPos("ORGANIZATION",key));
                break;

            case "CIRCLE" :
                if(findKeyPos("CIRCLE",key)==-1)
                    keylist.add(key+22);
                else
                    keylist.remove(findKeyPos("CIRCLE",key));
                break;

            default:
                break;
        }
    }

    public int findKeyPos(String keylistKinds, int key){
        int pos = -1;
        switch (keylistKinds){
            case "OFFICIAL" :
                pos = keylist.indexOf(key+1);
                break;
            case "ORGANIZATION" :
                pos = keylist.indexOf(key+16);
                break;
            case "CIRCLE" :
                pos = keylist.indexOf(key+22);
                break;
            default:
                break;
        }
        return pos;
    }

    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    //즐겨찾기 DB 불러오기
    class FavoriteLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            String ID = (String)strings[0];

            String serverUrl = "http://13.124.99.123/favoriteload.php";
            String postParameters = "id="+ID;

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

                StringBuffer sb = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                data = sb.toString();
                bufferedReader.close();

            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);
            }
            return data;

        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("")) {
                OrangeToast(getApplicationContext(), "서버 접속을 실패하였습니다.");
                finish();
            }
            else{
                String[] splitFavorite = s.split(",");

                for (int i=0; i<splitFavorite.length; i++)
                    keylist.add(Integer.parseInt(splitFavorite[i]));

                //기존의 즐겨찾기 불러오기
                try {
                    for (int i = 0; i < keylist.size(); i++) {
                        if (keylist.get(i) > 0 && keylist.get(i) < 16)
                            officialKeylist.add(keylist.get(i));
                        else if (keylist.get(i) >= 16 && keylist.get(i) < 22)
                            organizationKeylist.add(keylist.get(i) - 16);
                        else if (keylist.get(i) >= 22)
                            circleKeylist.add(keylist.get(i) - 22);
                        else{
                            Log.d("FavoriteSettingActivity","default favorite key selected!");
                        }
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putString("ID",id);
                bundle.putIntegerArrayList("OFFICIAL_KEYLIST",officialKeylist);
                bundle.putIntegerArrayList("ORGANIZATION_KEYLIST",organizationKeylist);
                bundle.putIntegerArrayList("CIRCLE_KEYLIST",circleKeylist);

                OfficialFragment = new OfficialSiteFragment();
                OrganizationFragment = new OrganizationSiteFragment();
                CircleFragment = new CircleSiteFragment();
                OfficialFragment.setArguments(bundle);
                OrganizationFragment.setArguments(bundle);
                CircleFragment.setArguments(bundle);

                fmViewPager = (ViewPager)findViewById(R.id.favorite_setting_switch_layout);
                fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                fmViewPager.setOffscreenPageLimit(2);
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
        }
    }
/*
    //즐겨찾기 DB 저장, 삭제
    class FavoriteRestoreTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ID = (String)strings[1];

            String serverUrl = (String)strings[0];
            String postParameters = "id="+ID+"&favoritehomepage="+"NULL";
            try {
                String keylistString = "NULL";
                for (int i = 0; i < keylist.size(); i++) {
                    if (i == 0)
                        keylistString = keylist.get(i) + "";
                    else
                        keylistString = keylistString + "," + keylist.get(i);
                }
                postParameters = "id="+ID+"&favoritehomepage="+keylistString;
            }catch (NullPointerException e){
                e.printStackTrace();
            }

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

                return line;

            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);

                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            finish();
        }
    }
*/
    //즐겨찾기 DB 저장, 삭제
    class FavoriteInsertTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            String ID = (String)strings[0];
            String serverUrl = "http://13.124.99.123/favoriterestore.php";
            String postParameters;

            String keylistString = "0";
            try {
                for (int i = 0; i < keylist.size(); i++) {
                    if (i == 0)
                        keylistString = keylist.get(i) + "";
                    else
                        keylistString = keylistString + "," + keylist.get(i);
                }
                if(keylist.size()==0)
                    keylistString = "0";
            }catch (NullPointerException e){
                e.printStackTrace();
                keylistString = null;
            }
            postParameters = "id="+ID+"&favoritehomepage="+keylistString;

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

                StringBuffer sb = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();
                data = sb.toString();


            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);
            }
            return data;
        }

    @Override
    protected void onPostExecute(String s) {
        if(s.equals("OK"))
            finish();
        else
            OrangeToast(getApplicationContext(),"서버 접속을 실패하였습니다.");
    }
}

    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    public void setData(){
        itemList = new ArrayList<Site>();
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

    }
}
