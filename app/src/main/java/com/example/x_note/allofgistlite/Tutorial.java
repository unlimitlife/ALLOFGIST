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
import android.os.Build;
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

import javax.net.ssl.HttpsURLConnection;

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

    private ViewPager fmViewPager;

    //바로가기 버튼
    private Button official;
    private Button organization;
    private Button circle;

    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        OrangeToast(Tutorial.this,"좌측 버튼을 누르거나 좌우로 스와이프 하여 사이트 종류를 바꿀 수 있습니다.");

        //로그인 되어있는 ID 불러오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(Tutorial.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();

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


    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void InitialSetting(){

        fmViewPager = (ViewPager)findViewById(R.id.tutorial_switch_layout);
        fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        fmViewPager.setOffscreenPageLimit(2);


        //바로가기 버튼
        official = (Button) findViewById(R.id.official_button_tutorial);
        organization = (Button) findViewById(R.id.organization_button_tutorial);
        circle = (Button) findViewById(R.id.circle_button_tutorial);

        official.setSelected(true);
        organization.setSelected(false);
        circle.setSelected(false);
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
}
