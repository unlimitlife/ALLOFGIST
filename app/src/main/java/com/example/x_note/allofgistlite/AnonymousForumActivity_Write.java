package com.example.x_note.allofgistlite;

import android.content.Context;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

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
    private Forum currentForum;
    private boolean editmode = false;

    SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);

    PopupWindow noticePopupWindow;
    TextView noticeText;
    TextView noticeCancel;
    TextView noticeOk;

    @Override
    public void onBackPressed() {
        if(editmode){
            noticeText.setText(R.string.edit_cancel_question);
            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
            noticeOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editmode = false;
                    finish();
                }
            });
            noticeCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticePopupWindow.dismiss();
                }
            });
        }else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_write);

        initialSetting();

        id = getIntent().getStringExtra("ID");
        try{
            currentForum = getIntent().getExtras().getParcelable("ForumLoadData");
            if(currentForum.getNickname().equals("익명"))
                selectNicknameButton.setChecked(false);
            else
                selectNicknameButton.setChecked(true);
            nicknameTextView.setText(currentForum.getNickname());
            titleEditText.setText(currentForum.getTitle());
            contentEditText.setText(currentForum.getContent());
            submitButton.setText(R.string.edit_button);
            editmode = true;
        }catch(NullPointerException e){
            e.printStackTrace();
            selectNicknameButton.setChecked(false);
            nicknameTextView.setText("익명");
            titleEditText.setText("");
            contentEditText.setText("");
            submitButton.setText(R.string.submit_button);
            editmode = false;
        }

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
                    try{
                        if(editmode) {
                            forum.setUpload_datetime(currentForum.getUpload_datetime());
                            startMyTask(new ForumEditTask(), forum.getId(), forum.getTitle(), forum.getContent(), forum.getNickname(), currentForum.getNum() + "");
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        forum.setUpload_datetime(new Date(Calendar.getInstance().getTimeInMillis()));
                        startMyTask(new ForumInsertTask(),forum.getId(),forum.getTitle(),forum.getContent(),forum.getNickname(),datetimeformat.format(forum.getUpload_datetime()));
                    }

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

        popupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        noticePopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)popupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)popupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)popupView.findViewById(R.id.notice_plus_cancel_textview);
        noticePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));
    }


    //Switch 버튼으로 닉네임 표시
    class NickNameLoadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String ID = (String) strings[0];
            String postParameters = "id=" + ID;

            try {
                URL url = new URL("https://server.allofgist.com/nicknameload.php");
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
                Log.d("nicknametest", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

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
    class ForumInsertTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {

            String data ="";
            String ID = strings[0];
            String title = strings[1];
            String content = strings[2];
            String nickname = strings[3];
            String upload_datetime = strings[4];

            String postParameters = "id="+ID+"&title="+title+"&content="+content+"&nickname="+nickname+"&upload_datetime="+upload_datetime;


            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_insert.php");
                HttpsURLConnection httpsURLConnection =  (HttpsURLConnection)serverUrl.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("foruminserttest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();
                bufferedReader.close();

            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK"))
                finish();
            else
                OrangeToast(getApplicationContext(),"서버에 연결을 실패하였습니다.");
        }
    }

    class ForumEditTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {

            String data = "";
            String ID = strings[0];
            String title = strings[1];
            String content = strings[2];
            String nickname = strings[3];
            String num = strings[4];

            String postParameters = "id="+ID+"&title="+title+"&content="+content+"&nickname="+nickname+"&num="+num;

            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_edit.php");
                HttpsURLConnection httpsURLConnection =  (HttpsURLConnection)serverUrl.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("foruminserttest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer stringBuffer = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line);
                }
                data = stringBuffer.toString();

                bufferedReader.close();


            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK")) {
                editmode = false;
                finish();
            }
            else
                OrangeToast(getApplicationContext(),"서버에 연결을 실패하였습니다.");

        }
    }

    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.show();
    }

    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }


}
