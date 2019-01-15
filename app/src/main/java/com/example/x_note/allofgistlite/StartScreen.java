package com.example.x_note.allofgistlite;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StartScreen extends AppCompatActivity {

    public EditText login_id, login_pw;
    public String id,pw;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        login_id = (EditText)findViewById(R.id.login_input_ID);
        login_pw = (EditText)findViewById(R.id.login_input_PASSWORD);



        //button animation  추후에 수정해야 할듯(그라데이션 느낌으로)
        Button button = (Button) findViewById(R.id.ring_button_start);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);





        //회원가입 button
        TextView signupButton = (TextView)findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartScreen.this, SignupActivity.class));
            }
        });




        //tutorial && main screen
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    id = login_id.getText().toString();
                    pw = login_pw.getText().toString();

                }catch (NullPointerException e)
                {
                    Log.e("err",e.getMessage());
                }

                loginDB lDB = new loginDB();
                startMyTask(lDB,id,pw);

            }
        });
    }

    public class loginDB extends AsyncTask<String, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(StartScreen.this, "Please wait", null, true, true);
        }
        @Override
        protected Integer doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0] + "&password=" + params[1] + "";
            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
               // Log.e("RECV DATA",data);

                if(data.equals(params[1]))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    return 1;
                }
                else if(data.equals("Unverfied ID")){

                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    return 2;
                }
                else
                {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    return 0;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return -1;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            int initialUserCheck=0;

            progressDialog.dismiss();

            if(result==1)
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                TutorialCheck tutorialCheck = new TutorialCheck();
                startMyTask(tutorialCheck,id);

            }
            else if(result==0)
            {
                Log.e("RESULT","비밀번호가 일치하지 않습니다.");
                OrangeToast(StartScreen.this,"비밀번호가 일치하지 않습니다.");
            }
            else if(result==2)
            {
                Log.e("RESULT","인증이 완료 되지 않은 ID 입니다.");
                OrangeToast(StartScreen.this,"인증이 완료 되지 않은 ID 입니다.");
            }
            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + result);
                OrangeToast(StartScreen.this,"에러 발생! ERRCODE = " +result);
            }
        }

    }

    public class TutorialCheck extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0]+ "";
            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/tutorialcheck.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("euc-kr"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                int responseStatusCode = conn.getResponseCode();
                if(responseStatusCode == conn.HTTP_OK)
                    is = conn.getInputStream();
                else
                    is = conn.getErrorStream();

                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA",data);
                if(data.equals("0"))
                    return 0;
                if(data.equals("1"))
                    return 1;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return -1;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // 사용경험이 있는 회원
            if(result==1) {


                SharedPreferences.Editor loginEditor = getSharedPreferences("LOGIN_ID",MODE_PRIVATE).edit();
                loginEditor.clear();
                loginEditor.putString("ID",id);
                loginEditor.apply();

                startMyTask(new FavoriteLoad(),id);

            }

            //처음인 회원
            else if(result==0){
                Intent tutorial = new Intent(StartScreen.this,Tutorial.class);
                SharedPreferences.Editor loginEditor = getSharedPreferences("LOGIN_ID",MODE_PRIVATE).edit();
                loginEditor.clear();
                loginEditor.putString("ID",id);
                loginEditor.apply();


                SharedPreferences.Editor favoriteEditor = getSharedPreferences("FAVORITE_KEYLIST",MODE_PRIVATE).edit();
                favoriteEditor.clear();
                favoriteEditor.apply();



                OrangeToast(StartScreen.this,"성공적으로 로그인되었습니다!");
                startActivity(tutorial);
            }

            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + result);
                OrangeToast(StartScreen.this,"에러 발생! ERRCODE = " +result);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //다음화면으로 갈떄 현재 액티비티를 종료
        finish();
    }
    public class FavoriteLoad extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = progressDialog.show(StartScreen.this, "Loading favorites...", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0]+ "";
            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/favoriteload.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("euc-kr"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                int responseStatusCode = conn.getResponseCode();
                if(responseStatusCode == conn.HTTP_OK)
                    is = conn.getInputStream();
                else
                    is = conn.getErrorStream();

                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA",data);
                return data;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if(result.equals("NULL")){}
            else{
                String[] splitlist;
                splitlist=result.split(",");

                //즐겨찾기 DB 불러오기
                SharedPreferences.Editor favoriteEditor = getSharedPreferences("FAVORITE_KEYLIST",MODE_PRIVATE).edit();
                favoriteEditor.clear();
                for(String wo : splitlist) {
                    favoriteEditor.putString("KEYLIST_"+id+"_"+wo,"OK");
                }
                favoriteEditor.apply();

                Intent main = new Intent(StartScreen.this, MainActivity.class);
                OrangeToast(StartScreen.this,"성공적으로 로그인되었습니다!");
                startActivity(main);
            }

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



//버튼 애니메이션
class MyBounceInterpolator implements android.view.animation.Interpolator {
    private double mAmplitude = 1;
    private double mFrequency = 10;

    MyBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) * Math.cos(mFrequency * time) + 1);
    }
}

