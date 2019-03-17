package com.allofgist.dell.allofgistlite;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class SignupActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "server.allofgist.com";

    private EditText mEditTextNickname;
    private EditText mEditTextId;
    private EditText mEditTextPassword;
    private EditText mEditTextPasswordCheck;
    private EditText mEditTextEmailId;

    View popupView;
    PopupWindow popupWindow;
    TextView noticeTextView;
    TextView okTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEditTextNickname = (EditText)findViewById(R.id.edittext_nickname);
        mEditTextId = (EditText)findViewById(R.id.editText_ID);
        mEditTextPassword = (EditText)findViewById(R.id.editText_password);
        mEditTextPasswordCheck = (EditText)findViewById(R.id.editText_passwordcheck);
        mEditTextEmailId = (EditText)findViewById(R.id.editText_emailid);

        popupView = getLayoutInflater().inflate(R.layout.notice_popup_window,null);
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT,true);
        noticeTextView = (TextView)popupView.findViewById(R.id.notice_text);
        okTextView = (TextView)popupView.findViewById(R.id.notice_ok_textview);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));


        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String nickname = mEditTextNickname.getText().toString();
                String name = mEditTextId.getText().toString();
                String password = mEditTextPassword.getText().toString();
                String passwordCheck = mEditTextPasswordCheck.getText().toString();
                String emailId = mEditTextEmailId.getText().toString();

                if(password.equals(passwordCheck)&&!password.isEmpty()&&!passwordCheck.isEmpty()&&!name.isEmpty()&&!emailId.isEmpty()&&!nickname.isEmpty()){
                    new IDCheckTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,name,emailId);
                }
                else if (!password.equals(passwordCheck)&&!password.isEmpty()&&!passwordCheck.isEmpty()&&!name.isEmpty()&&!emailId.isEmpty()){
                    GrayToast(SignupActivity.this,"비밀번호가 일치하지 않습니다.");
                }
                else {
                    GrayToast(SignupActivity.this,"모든 정보를 입력해주세요.");
                }
            }
        });
    }

    //회원가입
    public class Signup extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(SignupActivity.this, "Please wait", null, true, true);
        }

        @Override
        protected String doInBackground(String... strings) {
            String nickname = (String)strings[1];
            String id = (String)strings[2];
            String password = (String)strings[3];
            String emailid = (String)strings[4];

            String serverUrl = (String)strings[0];
            String postParameters = "id="+id+"&nickname="+nickname+"&password="+password+"&mailid="+emailid;

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

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);

                return new String("Error: "+e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            noticeTextView.setText("인증 메일을 확인해주세요.\n(메일 도착까지 시간이 좀 걸립니다.)");
            popupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            //GrayToast(SignupActivity.this,"인증 메일 확인을 해주세요.");
            Log.d("phptest","POST response - "+result);

        }
    }

    class IDCheckTask extends AsyncTask<String,Void, String>{
        @Override
        protected String doInBackground(String... strings) {

            String id = strings[0];
            String mailid = strings[1];
            String postParameter = "id="+id+"&mailid="+mailid;

            try{
                URL serverURL = new URL("https://server.allofgist.com/IDcheck.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverURL.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("UTF-8"));
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

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK")){
                String nickname = mEditTextNickname.getText().toString();
                String name = mEditTextId.getText().toString();
                String password = mEditTextPassword.getText().toString();
                String passwordCheck = mEditTextPasswordCheck.getText().toString();
                String emailId = mEditTextEmailId.getText().toString();

                Signup task = new Signup();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://" + IP_ADDRESS + "/insert.php", nickname,name,password,emailId);

                mEditTextNickname.setText("");
                mEditTextId.setText("");
                mEditTextPassword.setText("");
                mEditTextPasswordCheck.setText("");
                mEditTextEmailId.setText("");
            }
            else{
                GrayToast(SignupActivity.this,result);
            }
        }
    }



    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }


}

