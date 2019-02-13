package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FoodDiary extends AppCompatActivity {

    private LinearLayout firstFloor;
    private LinearLayout secondFloor;
    private LinearLayout secondRestaurant;

    ImageView foodDiaryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        firstFloor = (LinearLayout) findViewById(R.id.first_floor);
        secondFloor = (LinearLayout) findViewById(R.id.second_floor);
        secondRestaurant = (LinearLayout) findViewById(R.id.second_restaurant);

        foodDiaryImage = (ImageView)findViewById(R.id.image_food_diary);

        firstFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startMyTask(new FirstRestaurantSaveTask(),"https://www.gist.ac.kr/kr/html/sub05/050601.html");
            }
        });

        secondFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startMyTask(new FirstRestaurantSaveTask(),"https://www.gist.ac.kr/kr/html/sub05/050603.html");
            }
        });

        secondRestaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startMyTask(new SecondRestaurantTask(),"https://www.gist.ac.kr/kr/html/sub05/050602.html");
            }
        });
    }


    //사이트 접속
    public void openWebPage(String url) {
        if (url.contains("https://www.facebook.com")) {
            if (getOpenFacebookIntent(FoodDiary.this, url).contains("https://www.facebook.com")){
                String facebookurl = url.replaceFirst("www.", "m.");
                if(!facebookurl.startsWith("https"))
                    facebookurl = "https://"+facebookurl;
                Intent webView = new Intent(FoodDiary.this, web_interface.class);
                webView.putExtra("Url", facebookurl);
                startActivity(webView);
            }
            else{
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse(getOpenFacebookIntent(FoodDiary.this, url)));
                startActivity(facebookIntent);
            }
        } else {
            Intent webView = new Intent(FoodDiary.this, web_interface.class);
            webView.putExtra("Url", url);
            startActivity(webView);
        }
    }

    //페이스북 앱 연동
    public String getOpenFacebookIntent(Context context, String facebookUrl) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                facebookUrl = facebookUrl.replaceFirst("www.", "m.");
                if(!facebookUrl.startsWith("https"))
                    facebookUrl = "https://"+facebookUrl;
                return "fb://facewebmodal/f?href=" + facebookUrl;
            }
            else
                return "fb://page/" + facebookUrl.replaceFirst("https://www.facebook.com/", "");
        } catch (Exception e) {
            return facebookUrl;
        }
    }

    //1학 학식 사진 열기
    public class FirstRestaurantTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String Address = params[0];
            URL url;
            BufferedReader br;
            HttpURLConnection conn;
            String protocol = "GET";
            try {
                url = new URL(Address);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(protocol);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("												<div class='inner'><img src='")) {
                        line = line.replace("												<div class='inner'><img src='", "/");
                        line = line.replace("' alt='", "Z");
                        line = line.replace("'></div>", "");
                        line = line.replace("amp;", "");
                        line = line.substring(line.indexOf("/"), line.indexOf("Z"));
                        break;
                    }
                }
                if (line == null)
                    return Address;
                return Address + line;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Address;
        }

        @Override
        protected void onPostExecute(String url) {
            openWebPage(url);
        }
    }

    //1학 학식 사진 열기
    public class SecondRestaurantTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            Date today = Calendar.getInstance().getTime();
            Date uploadDay;


            String Address = params[0];
            URL url;
            BufferedReader br;
            HttpURLConnection conn;
            String protocol = "GET";

            String finalLine = null;

            //게시물 날짜가 있는 줄 과 사이트가 있는 줄을 가리기 위한 포인트들
            boolean datePoint = false;
            int datePointBreak = 0;
            boolean point = false;


            try {
                url = new URL(Address);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(protocol);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    if(point){
                        line = line.replace("\t\t\t\t\t\t\t\t\t\t<a href='", "");
                        line = line.replace("'>", "");
                        line = line.replace("amp;","");
                        finalLine = line;
                        point = false;
                        datePoint = true;
                    }
                    if(datePoint) {
                        datePointBreak++;
                        if(datePointBreak==3){
                            line = line.replace("\t\t\t\t\t\t\t\t\t\t\t\t<div class='inner'><img src='/bbs/img/common/noimg.gif' alt='", "");
                            String[] splitString = line.split("~");
                            try {
                                uploadDay = dateFormat.parse(splitString[0]);
                                if(uploadDay.compareTo(today)>0){   //uploadday>today
                                      datePoint = false;
                                      datePointBreak=0;
                                }
                                else{                   //uploadday<=today
                                    break;
                                }

                            }catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    if (line.startsWith("\t\t\t\t\t\t\t\t\t<div class='bd_item_box'>")) {
                        point = true;
                    }

                }
                if (finalLine.isEmpty())
                    return Address;

                return Address+finalLine;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return Address;
        }

        @Override
        protected void onPostExecute(String url) {
            openWebPage(url);
        }
    }

    //------------------------------------------------------------------
    //1학 학식 사진 저장
    public class FirstRestaurantSaveTask extends AsyncTask<String, Void, Bitmap> {

        Bitmap mBitmap;

        @Override
        protected Bitmap doInBackground(String... params) {
            String Address = params[0];
            URL url;
            BufferedReader br;
            HttpURLConnection conn;
            String protocol = "GET";
            try {
                url = new URL(Address);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(protocol);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                boolean findFirstLine = false;
                while ((line = br.readLine()) != null) {
                    if(findFirstLine){
                        line = line.replace("\t\t\t\t\t\t\t\t\t\t<a href='","");
                        line = line.replace("'>","");
                        line = line.replaceAll("amp;","");
                        break;
                    }
                    if (line.startsWith("\t\t\t\t\t\t\t\t\t<div class='bd_item_box'>")) {
                        findFirstLine =  true;
                    }
                }
                if (!line.isEmpty()) {
                    String boardAddress = Address + line;

                    url = new URL(boardAddress);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(protocol);
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                    line = "";
                    int i=0;
                    while ((line = br.readLine()) != null) {
                        i++;
                        if(line.startsWith("\t\t\t<li><a href='")){
                            line = line.replaceFirst("\t\t\t<li><a href='","");
                            line = line.split("' title='")[0];
                            line = line.replaceAll("amp;","");
                            break;
                        }
                        if(line.contains("\"external_image\"")){
                            line = line.split("\"external_image\"")[2];
                            line = line.replace(" src=\"","");
                            line = line.replace("\"></p></p>","");
                            break;
                        }
                    }
                    if(!line.isEmpty()){
                        if(line.contains("gist.ac.kr"))
                            Address = line;
                        else
                            Address += line;
                    }

                    url = new URL("http://www.gist.ac.kr/_prog/download/?editor_image=20190207131423369_J8ACRXX1.png");
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true);
                    conn.connect();
                    BufferedInputStream input = new BufferedInputStream(conn.getInputStream());

                    mBitmap = BitmapFactory.decodeStream(input);
                    input.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mBitmap;
        }



        @Override
        protected void onPostExecute(Bitmap mBitmap) {
            if(mBitmap != null)
                foodDiaryImage.setImageBitmap(mBitmap);
        }
    }

    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }
}
