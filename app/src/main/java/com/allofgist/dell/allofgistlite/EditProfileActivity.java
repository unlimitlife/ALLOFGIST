package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class EditProfileActivity extends AppCompatActivity {

    //로그인 정보
    public static String id = "LOGIN_ERROR";
    public String nickname;

    private EditText nicknameInput;
    private TextView sizeOfNickname;
    private LinearLayout checkButton;
    private ImageButton backButton;
    private ImageButton profileDeleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        //로그인 정보 가져오기
        id = getIntent().getStringExtra("ID");
        nickname = getIntent().getStringExtra("Nickname");

        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(EditProfileActivity.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();

        nicknameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String size = s.length()+"/50";
                sizeOfNickname.setText(size);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nicknameInput.getText().toString().equals("")){
                    GrayToast(EditProfileActivity.this,"닉네임을 입력해주세요.");
                }else {
                    new NickNameEditTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id, nicknameInput.getText().toString());
                }
            }
        });

        profileDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nicknameInput.setText("");
            }
        });

    }

    private void InitialSetting() {
        nicknameInput = (EditText)findViewById(R.id.nickname_edit_profile);
        sizeOfNickname = (TextView)findViewById(R.id.size_of_nickname);
        checkButton = (LinearLayout)findViewById(R.id.check_edit_profile);
        backButton = (ImageButton)findViewById(R.id.edit_profile_back_button);
        profileDeleteButton = (ImageButton)findViewById(R.id.profile_id_delete);
        nicknameInput.setText(nickname);
        String size = nickname.length()+"/50";
        sizeOfNickname.setText(size);

    }

    class NickNameEditTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String ID = (String) strings[0];
            String NICKNAME = (String) strings[1];
            String postParameters = "id=" + ID + "&nickname=" + NICKNAME;

            try {
                URL url = new URL("https://server.allofgist.com/nickname_edit.php");
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
        protected void onPostExecute(String result) {
            if(result.equals("OK")){
                finish();
            }
            else
                GrayToast(getApplicationContext(),"서버 연결에 실패하였습니다.");
        }
    }


    public void GrayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }
}
