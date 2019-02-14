package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FoodDiary extends AppCompatActivity {

    private RelativeLayout firstFloor;
    private RelativeLayout secondFloor;
    private RelativeLayout secondRestaurant;

    PhotoView foodDiaryImage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        firstFloor = (RelativeLayout) findViewById(R.id.first_floor);
        secondFloor = (RelativeLayout) findViewById(R.id.second_floor);
        secondRestaurant = (RelativeLayout) findViewById(R.id.second_restaurant);

        foodDiaryImage = (PhotoView) findViewById(R.id.image_food_diary);
        progressBar = (ProgressBar)findViewById(R.id.food_diary_progress_bar);

        firstFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                startMyTask(new RestaurantLoadTask(),"https://www.gist.ac.kr/kr/html/sub05/050601.html");
            }
        });

        secondFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                startMyTask(new RestaurantLoadTask(),"https://www.gist.ac.kr/kr/html/sub05/050603.html");
            }
        });

        secondRestaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                startMyTask(new RestaurantLoadTask(),"https://www.gist.ac.kr/kr/html/sub05/050602.html");
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

    //1학 학식 사진 저장
    public class RestaurantLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String Address = params[0];
            java.net.URL url;
            BufferedReader br;
            HttpsURLConnection conn;
            String protocol = "GET";
            try {
                url = new URL(Address);
                conn = (HttpsURLConnection) url.openConnection();
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
                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod(protocol);
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));


                    line = "";
                    while ((line = br.readLine()) != null) {
                        if(line.startsWith("\t\t\t<li><a href='")){
                            String[] imgClassiifier = line.split("<li><a href='");
                            for(int i = 0; i < imgClassiifier.length; i++){
                                if(imgClassiifier[i].contains("class='imgs'")) {
                                    line = imgClassiifier[i];
                                    break;
                                }
                            }
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Address;
        }

        @Override
        protected void onPostExecute(String url) {
            progressBar.setVisibility(View.GONE);
            foodDiaryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Glide.with(FoodDiary.this)
                    .load(url)
                    .into(foodDiaryImage);

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
