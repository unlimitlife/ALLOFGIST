package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class FavoriteSettingActivity extends AppCompatActivity {

    private List<Site> itemList = null;

    private String id = "LOGIN_ERROR";
    private String TAG = "FAVORITE_SETTING";
    private ArrayList<Integer> keylist;

    private ArrayList<Integer> officialKeylist;
    private ArrayList<Integer> organizationKeylist;
    private ArrayList<Integer> circleKeylist;

    private Fragment OfficialFragment;
    private Fragment OrganizationFragment;
    private Fragment CircleFragment;

    private TabLayout tabLayout;
    private ViewPager fmViewPager;

    private Button completeButton;
    private ImageButton backButton;


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
        new FavoriteLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);


        //즐겨찾기 완료 버튼
        completeButton = (Button)findViewById(R.id.favorite_setting_ok_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FavoriteInsertTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,id);
            }
        });

        backButton = (ImageButton)findViewById(R.id.favorite_setting_back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
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


    //즐겨찾기 DB 불러오기
    class FavoriteLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            String ID = (String)strings[0];

            String serverUrl = "https://server.allofgist.com/favoriteload.php";
            String postParameters = "id="+ID;

            try{
                URL url = new URL(serverUrl);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();


                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }
                else{
                    inputStream = httpsURLConnection.getErrorStream();
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
                GrayToast(getApplicationContext(), "서버 접속을 실패하였습니다.");
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
                bundle.putString("TAG",TAG);
                bundle.putIntegerArrayList("OFFICIAL_KEYLIST",officialKeylist);
                bundle.putIntegerArrayList("ORGANIZATION_KEYLIST",organizationKeylist);
                bundle.putIntegerArrayList("CIRCLE_KEYLIST",circleKeylist);

                OfficialFragment = new OfficialSiteFragment();
                OrganizationFragment = new OrganizationSiteFragment();
                CircleFragment = new CircleSiteFragment();
                OfficialFragment.setArguments(bundle);
                OrganizationFragment.setArguments(bundle);
                CircleFragment.setArguments(bundle);

                tabLayout = (TabLayout)findViewById(R.id.tab_layout_favorite_setting);

                tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_official_text))));
                tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_organization_text))));
                tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_circle_text))));


                fmViewPager = (ViewPager)findViewById(R.id.favorite_setting_switch_layout);
                fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                fmViewPager.setOffscreenPageLimit(2);
                fmViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        try {
                            tabLayout.getTabAt(position).select();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });


                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        fmViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        }
    }

    private View createTabView(String tabName) {

        View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.tab_name);
        txt_name.setText(tabName);
        return tabView;

    }

    //즐겨찾기 DB 저장, 삭제
    class FavoriteInsertTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            String ID = (String)strings[0];
            String serverUrl = "https://server.allofgist.com/favoriterestore.php";
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
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();


                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }
                else{
                    inputStream = httpsURLConnection.getErrorStream();
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
            if(s.equals("OK")) {
                finish();
            }
            else
                GrayToast(getApplicationContext(),"서버 접속을 실패하였습니다.");
        }
    }




    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

    /*public void setData(){
        itemList = new ArrayList<Site>();
        itemList.add(new Site("GIST home", "httpss://www.gist.ac.kr/kr/","httpss://blog.naver.com/gist1993","httpss://www.facebook.com/GIST.ac.kr/","httpss://www.instagram.com/gist1993/", R.drawable.home));
        itemList.add(new Site("Gel","httpss://gel.gist.ac.kr/",R.drawable.gel));
        itemList.add(new Site("Zeus system", "httpss://zeus.gist.ac.kr", R.drawable.zeus));
        itemList.add(new Site("수강 신청 사이트","httpss://zeus.gist.ac.kr/sys/lecture/lecture_main.do",R.drawable.courseregisteration));
        itemList.add(new Site("Portal system", "httpss://portal.gist.ac.kr/intro.jsp", R.drawable.portal));
        itemList.add(new Site("Email system", "httpss://mail.gist.ac.kr/loginl?locale=ko_KR", R.drawable.email));
        itemList.add(new Site("GIST college", "httpss://college.gist.ac.kr/", R.drawable.college));
        itemList.add(new Site("GIST library", "httpss://library.gist.ac.kr/", R.drawable.library));
        itemList.add(new Site("학내공지", "httpss://college.gist.ac.kr/main/Sub040203", R.drawable.haknaegongji));
        itemList.add(new Site("GIST 대학생","httpss://www.facebook.com/groups/giststudent/",R.drawable.giststudent));
        itemList.add(new Site("GIST 대나무숲","httpss://www.facebook.com/GISTIT.ac.kr/",R.drawable.gistdaenamoo));
        itemList.add(new Site("GIST 대나무숲 제보함","https://fbpage.kr/?pi=128#/submit",R.drawable.gistdaenamoojaeboo));
        itemList.add(new Site("언어교육센터","httpss://language.gist.ac.kr/",R.drawable.language));
        itemList.add(new Site("창업진흥센터", "httpss://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup));
        itemList.add(new Site("학과별 사이트",R.drawable.majorset));
        itemList.add(new Site("GIST 총학생회", "httpss://www.facebook.com/gistunion/", R.drawable.gistunion));
        itemList.add(new Site("GIST 동아리연합회", "httpss://www.facebook.com/gistclubunite/", R.drawable.clubnight));
        itemList.add(new Site("GIST 하우스", "httpss://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse));
        itemList.add(new Site("GIST 문화행사위원회", "httpss://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe));
        itemList.add(new Site("GIST 신문","https://gistnews.co.kr/", "httpss://www.facebook.com/GistSinmoon/", R.drawable.gistnews));
        itemList.add(new Site("GIST 홍보대사", "https://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare));
        itemList.add(new Site("춤 동아리 막무가내", "httpss://www.facebook.com/gistmacmoo/","httpss://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae));
        itemList.add(new Site("힙합 동아리 Ignition", "httpss://www.facebook.com/GISTignition/","httpss://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition));
        itemList.add(new Site("노래 동아리 싱송생송", "httpss://www.facebook.com/gistsingsong/","httpss://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong));
        itemList.add(new Site("연극 동아리 지대로", "httpss://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","httpss://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero));
        itemList.add(new Site("오케스트라 동아리 악동", "httpss://www.facebook.com/GISTorchestra/","httpss://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra));
        itemList.add(new Site("기타 동아리 HOTSIX", "httpss://www.facebook.com/gisthotsix/", R.drawable.hotsix));
        itemList.add(new Site("피아노 동아리 GISRI", "httpss://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri));
        itemList.add(new Site("밴드 Main", "httpss://www.facebook.com/MAIN-899414343552162/", R.drawable.main));
        itemList.add(new Site("밴드 도도한 쭈쭈바", "httpss://www.facebook.com/dozzu/", R.drawable.dozzu));
        itemList.add(new Site("영화 동아리 Cinergy", "httpss://www.facebook.com/gistcinergy/", R.drawable.cinergy));
        itemList.add(new Site("영상편집 동아리 The GIST", "httpss://www.facebook.com/Gentletist/","httpss://www.youtube.com/channel/UCMUDHS0SZvQilFe5h6eI9rA/videos", R.drawable.thegist));
        itemList.add(new Site("문예창작 동아리 사각사각", "httpss://www.facebook.com/GIST-%EC%82%AC%EA%B0%81%EC%82%AC%EA%B0%81-238788459851229/", R.drawable.sagaksagak));
        itemList.add(new Site("GIST 고양이 지냥이", "httpss://www.facebook.com/giscats/", R.drawable.giscat));
        itemList.add(new Site("요리동아리 이쑤시개", "httpss://www.facebook.com/%EC%9D%B4%EC%91%A4%EC%8B%9C%EA%B0%9C-%EC%9A%94%EB%A6%AC%ED%95%98%EB%8A%94-GIST%EC%83%9D-272551203239747/?ref=py_c", R.drawable.essosigae));
        itemList.add(new Site("칵테일 동아리 MixoloGIST", "httpss://www.facebook.com/Mixologist-1584231725157207", R.drawable.mixologist));
        itemList.add(new Site("보드게임 동아리 BGM", "httpss://www.facebook.com/GISTBGM", R.drawable.bgm));
        itemList.add(new Site("성소수자 모임 speQtrum", "httpss://www.facebook.com/gistspeqtrum/", R.drawable.speqtrum));
        itemList.add(new Site("만화 동아리 erutlucbus", "httpss://www.facebook.com/Erutlucbus/", R.drawable.eru));
        itemList.add(new Site("천체관측 동아리 SpaceBar", "httpss://www.facebook.com/GISTspacebar/", R.drawable.spacebar));
        itemList.add(new Site("전산 동아리 WING", "httpss://www.facebook.com/GISTWING/", R.drawable.wing));
        itemList.add(new Site("환경 동아리 온새미로", "httpss://www.facebook.com/onsaemiro123/", R.drawable.onsaemiro));
        itemList.add(new Site("축구 동아리 Kickass", "httpss://www.facebook.com/gistkickass/?ref=br_rs", R.drawable.kickass));

    }*/
}
