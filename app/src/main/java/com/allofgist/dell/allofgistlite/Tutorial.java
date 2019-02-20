package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
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

import static com.allofgist.dell.allofgistlite.MainActivity.startMyTask;

public class Tutorial extends AppCompatActivity {

    private List<Site> itemList = null;

    //login information
    private String id = "LOGIN_ERROR";
    private String TAG = "TUTORIAL";
    private ArrayList<Integer> keylist;

    private ArrayList<Integer> officialKeylist;
    private ArrayList<Integer> organizationKeylist;
    private ArrayList<Integer> circleKeylist;

    private Fragment OfficialFragment;
    private Fragment OrganizationFragment;
    private Fragment CircleFragment;

    private TabLayout tabLayout;
    private ViewPager fmViewPager;

    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        GrayToast(Tutorial.this,"좌측 버튼을 누르거나 좌우로 스와이프 하여 사이트 종류를 바꿀 수 있습니다.");

        //로그인 되어있는 ID 불러오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(Tutorial.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();

        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_official_text))));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_organization_text))));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_circle_text))));


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


        fmViewPager = (ViewPager)findViewById(R.id.tutorial_switch_layout);
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

        //즐겨찾기 완료 버튼
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMyTask(new FavoriteInsertTask(),id);
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
            if(s.equals("OK")){
                Intent main = new Intent(Tutorial.this, MainActivity.class);
                main.putExtra("ID",id);
                startActivity(main);
            }
            else
                GrayToast(getApplicationContext(),"서버 접속을 실패하였습니다.");
        }
    }


    public void InitialSetting(){

        tabLayout = (TabLayout)findViewById(R.id.tab_layout_tutorial);

        fmViewPager = (ViewPager)findViewById(R.id.tutorial_switch_layout);
        fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        fmViewPager.setOffscreenPageLimit(2);

        officialKeylist = new ArrayList<Integer>();
        organizationKeylist = new ArrayList<Integer>();
        circleKeylist = new ArrayList<Integer>();

        keylist = new ArrayList<Integer>();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //다음화면으로 갈떄 현재 액티비티를 종료
        finish();
    }


    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }
}
