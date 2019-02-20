package com.allofgist.dell.allofgistlite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.setSimpleSize;
import static com.allofgist.dell.allofgistlite.MainActivity.startMyTask;

public class StartScreen extends AppCompatActivity {

    //SImple Image Resizer variables
    public static int REQUEST_WIDTH = 256;
    public static int REQUEST_HEIGHT = 256;

    public EditText login_id, login_pw;
    public String id, pw;

    public String auto_login_id;

    SharedPreferences auto_login;
    SharedPreferences.Editor saveUser;

    TextView titleView;
    ImageView titleIcon;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        AutoLoginCheck();

        login_id = (EditText) findViewById(R.id.login_input_ID);
        login_pw = (EditText) findViewById(R.id.login_input_PASSWORD);
        titleView = (TextView) findViewById(R.id.GIST);
        titleIcon = (ImageView) findViewById(R.id.title_startscreen);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.gist,options);
        BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.gist,options);
        BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.gist,options);
        options.inSampleSize = setSimpleSize(options,REQUEST_WIDTH, REQUEST_HEIGHT);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.gist,options);
        //siteHolder.mImageView.setBackground(new ShapeDrawable(new OvalShape()));
        titleIcon.setClipToOutline(true);
        titleIcon.requestLayout();

        titleIcon.setImageBitmap(bitmap);
        //Glide.with(getApplicationContext()).load(getDrawable(R.drawable.gist)).into(titleIcon);
        //MultipleColorInOneText(Title,titleView);

        //button animation  추후에 수정해야 할듯(그라데이션 느낌으로)
        Button button = (Button) findViewById(R.id.ring_button_start);
        //final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        //MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);  애니메이션 효과
        //myAnim.setInterpolator(interpolator);  애니메이션 효과
        //button.startAnimation(myAnim);


        //회원가입 button
        TextView signupButton = (TextView) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartScreen.this, SignupActivity.class));
            }
        });


        //tutorial && main screen
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    id = login_id.getText().toString();
                    pw = login_pw.getText().toString();

                } catch (NullPointerException e) {
                    Log.e("err", e.getMessage());
                }

                loginDB lDB = new loginDB();
                startMyTask(lDB, id, pw);

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
            Log.e("POST", param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://server.allofgist.com/login.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
                // Log.e("RECV DATA",data);

                if (data.equals(params[1])) {
                    Log.e("RESULT", "성공적으로 처리되었습니다!");
                    return 1;
                } else if (data.equals("Unverfied ID")) {

                    Log.e("RESULT", "에러 발생! ERRCODE = " + data);
                    return 2;
                } else {
                    Log.e("RESULT", "에러 발생! ERRCODE = " + data);
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
            int initialUserCheck = 0;

            progressDialog.dismiss();

            if (result == 1) {
                Log.e("RESULT", "성공적으로 처리되었습니다!");
                TutorialCheck tutorialCheck = new TutorialCheck();
                startMyTask(tutorialCheck, id);

            } else if (result == 0) {
                Log.e("RESULT", "비밀번호가 일치하지 않습니다.");
                GrayToast(StartScreen.this, "비밀번호가 일치하지 않습니다.");
            } else if (result == 2) {
                Log.e("RESULT", "인증이 완료 되지 않은 ID 입니다.");
                GrayToast(StartScreen.this, "인증이 완료 되지 않은 ID 입니다.");
            } else {
                Log.e("RESULT", "에러 발생! ERRCODE = " + result);
                GrayToast(StartScreen.this, "에러 발생! ERRCODE = " + result);
            }
        }

    }

    public class TutorialCheck extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0] + "";
            Log.e("POST", param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://server.allofgist.com/tutorialcheck.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
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
                if (responseStatusCode == conn.HTTP_OK)
                    is = conn.getInputStream();
                else
                    is = conn.getErrorStream();

                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA", data);
                if (data.equals("0"))
                    return 0;
                if (data.equals("1"))
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
            if (result == 1) {
                Intent main = new Intent(StartScreen.this, MainActivity.class);
                main.putExtra("ID", id);
                auto_login = getSharedPreferences("AUTO_LOGIN", Activity.MODE_PRIVATE);
                saveUser = auto_login.edit();
                saveUser.putString("USER_ID",id);
                saveUser.apply();

                GrayToast(StartScreen.this, "성공적으로 로그인되었습니다!");
                startActivity(main);
            }


            //처음인 회원
            else if (result == 0) {
                Intent tutorial = new Intent(StartScreen.this, Tutorial.class);
                tutorial.putExtra("ID", id);
                auto_login = getSharedPreferences("AUTO_LOGIN", Activity.MODE_PRIVATE);
                saveUser = auto_login.edit();
                saveUser.putString("USER_ID",id);
                saveUser.apply();
                GrayToast(StartScreen.this, "성공적으로 로그인되었습니다!");
                startActivity(tutorial);
            } else {
                Log.e("RESULT", "에러 발생! ERRCODE = " + result);
                GrayToast(StartScreen.this, "에러 발생! ERRCODE = " + result);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //다음화면으로 갈떄 현재 액티비티를 종료
        finish();
    }

    public void AutoLoginCheck(){
        auto_login = getSharedPreferences("AUTO_LOGIN", Activity.MODE_PRIVATE);
        auto_login_id = auto_login.getString("USER_ID","NO_ID");
        try {
            if (!auto_login_id.equals("NO_ID")) {
                Intent main = new Intent(StartScreen.this, MainActivity.class);
                main.putExtra("ID", auto_login_id);
                startActivity(main);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

}


/*
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
}*/

