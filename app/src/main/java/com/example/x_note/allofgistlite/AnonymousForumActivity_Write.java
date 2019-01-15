package com.example.x_note.allofgistlite;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnonymousForumActivity_Write extends AppCompatActivity {

    String id;

    ImageButton backButton;
    Button submitButton;
    Switch selectNicknameButton;
    EditText titleEditText;
    EditText contentEditText;

    TextView nicknameTextView;

    PopupWindow popupWindow;
    View popupView;
    TextView noticeTextView;
    TextView okTextView;

    SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_write);

        initialSetting();

        id = getIntent().getStringExtra("ID");

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectNicknameButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startMyTask(new NickNameLoadTask(),id);
                }
                else
                    nicknameTextView.setText("익명");
            }
        });
        popupView = getLayoutInflater().inflate(R.layout.notice_popup_window,null);
        popupWindow = new PopupWindow(popupView,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);
        noticeTextView = (TextView)popupView.findViewById(R.id.notice_text);
        okTextView = (TextView)popupView.findViewById(R.id.notice_ok_textview);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleEditText.getText().toString().isEmpty()) {

                    noticeTextView.setText("제목을 입력해주세요.");
                    popupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);

                    okTextView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                }
                else if(contentEditText.getText().toString().isEmpty()){

                    noticeTextView.setText("내용을 입력해주세요.");
                    popupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);

                    okTextView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
                else {
                    Forum forum = new Forum(
                            id,
                            titleEditText.getText().toString(),
                            contentEditText.getText().toString(),
                            nicknameTextView.getText().toString(),
                            new Date(Calendar.getInstance().getTimeInMillis()));

                    startMyTask(new ForumInsertTask(),forum.getId(),forum.getTitle(),forum.getContent(),forum.getNickname(),datetimeformat.format(forum.getUpload_datetime()));

                    finish();
                }
            }
        });



    }

    public void initialSetting(){
        backButton = (ImageButton)findViewById(R.id.write_back_button);
        submitButton = (Button)findViewById(R.id.write_submit_button);
        selectNicknameButton = (Switch)findViewById(R.id.nickname_select);
        titleEditText = (EditText)findViewById(R.id.write_title);
        contentEditText = (EditText)findViewById(R.id.write_content);
        nicknameTextView = (TextView)findViewById(R.id.nickname_textview);
    }


    //Switch 버튼으로 닉네임 표시
    class NickNameLoadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String ID = (String) strings[0];
            String postParameters = "id=" + ID;

            try {
                URL url = new URL("http://13.124.99.123/nicknameload.php");
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
                Log.d("nicknametest", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == httpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            }
        }


        @Override
        protected void onPostExecute(String nickname) {
            nicknameTextView.setText(nickname);
        }
    }

    //DB 입력
    class ForumInsertTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {

            String ID = strings[0];
            String title = strings[1];
            String content = strings[2];
            String nickname = strings[3];
            String upload_datetime = strings[4];

            String postParameters = "id="+ID+"&title="+title+"&content="+content+"&nickname="+nickname+"&upload_datetime="+upload_datetime;


            try{
                URL serverUrl = new URL("http://13.124.99.123/foruminsert.php");
                HttpURLConnection httpURLConnection =  (HttpURLConnection)serverUrl.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("foruminserttest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
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
