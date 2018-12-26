package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodDiary extends AppCompatActivity {

    private Button firstFloor;
    private Button secondFloor;
    private Button secondRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        firstFloor = (Button)findViewById(R.id.first_floor);
        secondFloor = (Button)findViewById(R.id.second_floor);
        secondRestaurant = (Button)findViewById(R.id.second_restaurant);

        firstFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startMyTask(new FirstRestaurantTask(),"https://www.gist.ac.kr/kr/html/sub05/050601.html");
            }
        });

        secondFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startMyTask(new FirstRestaurantTask(),"https://www.gist.ac.kr/kr/html/sub05/050603.html");
            }
        });

        secondRestaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openWebPage("https://www.gist.ac.kr/kr/html/sub05/050602.html");
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



    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

}
