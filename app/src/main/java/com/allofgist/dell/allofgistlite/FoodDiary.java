package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class FoodDiary extends AppCompatActivity {

    private RelativeLayout firstFloor;
    private RelativeLayout secondFloor;
    private RelativeLayout secondRestaurant;

    TextView noticeTextView;
    ImageButton backButton;
    PhotoView foodDiaryImage;
    ProgressBar progressBar;
    public RequestManager mGlideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        GrayToast(getApplicationContext(),"해당 식단표 파일이 이미지로 불러올 수 없는 형식이라면, 사이트로 접속합니다.");

        mGlideRequestManager = Glide.with(getApplicationContext());
        firstFloor = (RelativeLayout) findViewById(R.id.first_floor);
        secondFloor = (RelativeLayout) findViewById(R.id.second_floor);
        secondRestaurant = (RelativeLayout) findViewById(R.id.second_restaurant);

        foodDiaryImage = (PhotoView) findViewById(R.id.image_food_diary);
        progressBar = (ProgressBar)findViewById(R.id.food_diary_progress_bar);

        noticeTextView = (TextView)findViewById(R.id.notice_food_diary);

        backButton = (ImageButton)findViewById(R.id.food_diary_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firstFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noticeTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                firstFloor.setSelected(true);
                secondFloor.setSelected(false);
                secondRestaurant.setSelected(false);
                new RestaurantLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://www.gist.ac.kr/kr/html/sub05/050601.html");
            }
        });

        secondFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noticeTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                firstFloor.setSelected(false);
                secondFloor.setSelected(true);
                secondRestaurant.setSelected(false);
                new RestaurantLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://www.gist.ac.kr/kr/html/sub05/050603.html");
            }
        });

        secondRestaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noticeTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                firstFloor.setSelected(false);
                secondFloor.setSelected(false);
                secondRestaurant.setSelected(true);
                new RestaurantLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://www.gist.ac.kr/kr/html/sub05/050602.html");
            }
        });
    }


    //1학 학식 사진 저장
    public class RestaurantLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String Address = params[0];
            String original_url = params[0];
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
                        if(line.contains("\"external_image\"")){
                            line = line.split("\"external_image\"")[2];
                            line = line.replace(" src=\"","");
                            line = line.replace("\"></p></p>","");
                            break;
                        }
                    }

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod(protocol);
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    if(line==null){
                        while ((line = br.readLine()) != null) {
                            if(line.contains("<p><p><img")){
                                line = line.split("src=\"")[1];
                                line = line.split("\">")[0];
                                break;
                            }
                        }
                    }

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod(protocol);
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    if(line==null){
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
                        }
                    }
                    if(line!=null){
                        if(line.contains("gist.ac.kr"))
                            Address = line;
                        else
                            Address += line;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return Address+"&&&&&&&&&&"+original_url;
        }

        @Override
        protected void onPostExecute(String url) {
            progressBar.setVisibility(View.GONE);
            foodDiaryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            String[] split = url.split("&&&&&&&&&&");
            try {
                mGlideRequestManager
                        .load(split[0])
                        .into(foodDiaryImage);

                if(!url.contains("png")&&!url.contains("jpg")&&!url.contains("jpeg")){
                    GrayToast(getApplicationContext(),"해당 식단표는 이미지로 불러올 수 없는 형식으로, 사이트로 접속합니다.");
                    openWebPage(split[1]);
                }
            }catch (IllegalArgumentException e){
                e.printStackTrace();
                GrayToast(getApplicationContext(),"식단표를 불러오지 못하였습니다.");
            }
        }
    }

    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }



    //사이트 접속
    public void openWebPage(String url) {

        Intent chrome = new Intent(Intent.ACTION_VIEW);
        chrome.setData(Uri.parse(url));
        startActivity(chrome);
    }

}
