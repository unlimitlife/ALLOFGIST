package com.allofgist.dell.allofgistlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class MyActivity extends AppCompatActivity {

    //로그인 정보
    public static String id = "LOGIN_ERROR";
    private TextView nicknameView;

    ImageButton backButton;


    SharedPreferences auto_login;
    SharedPreferences.Editor saveUser;

    LinearLayout editProfile;
    LinearLayout logout;
    LinearLayout deleteUser;


    //로그 아웃 팝업
    PopupWindow noticePopupWindow;
    View popupView;

    TextView noticeText;
    TextView noticeCancel;
    TextView noticeOk;


    @Override
    protected void onResume() {
        super.onResume();

        new NickNameLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //로그인 정보 가져오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(MyActivity.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }
        InitialSetting();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noticeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticePopupWindow.isShowing())
                    noticePopupWindow.dismiss();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(MyActivity.this, EditProfileActivity.class);
                editProfile.putExtra("ID",id);
                editProfile.putExtra("Nickname",nicknameView.getText());
                startActivity(editProfile);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeText.setText(R.string.logout_notice);
                noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auto_login = getSharedPreferences("AUTO_LOGIN", Activity.MODE_PRIVATE);
                        saveUser = auto_login.edit();
                        saveUser.remove("USER_ID");
                        saveUser.apply();
                        noticePopupWindow.dismiss();
                        finish();
                    }
                });

            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeText.setText(R.string.delete_user_notice);
                noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new UserDeleteTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,id);
                    }
                });

            }
        });



    /*@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        if (itemId == R.id.nav_academic_calendar) {
            startActivity(new Intent(MainActivity.this, AcademicCalendarActivity.class));
        } else if (itemId == R.id.nav_credit_calculator) {
            Intent calculator = new Intent(MainActivity.this,CreditCalculatorAddMain.class);
            calculator.putExtra("ID",id);
            startActivity(calculator);
        } else if (itemId == R.id.nav_edit_nickname) {
            nicknameText.setText(R.string.nickname_notice);
            nicknamePopupWindow.showAtLocation(nicknamePopupView,Gravity.CENTER,0,0);
            nicknameOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nicknameEditText.getText().toString().isEmpty()){
                        GrayToast(getApplicationContext(),"변경할 닉네임을 입력해주세요.");
                    }
                    else{
                        new NickNameEditTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id, nicknameEditText.getText().toString());
                    }
                }
            });

        } else if (itemId == R.id.nav_logout) {
            noticeText.setText(R.string.logout_notice);
            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
            noticeOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auto_login = getSharedPreferences("AUTO_LOGIN", Activity.MODE_PRIVATE);
                    saveUser = auto_login.edit();
                    saveUser.remove("USER_ID");
                    saveUser.apply();
                    finish();
                }
            });
        } else if (itemId == R.id.nav_delete_user) {
            noticeText.setText(R.string.delete_user_notice);
            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
            noticeOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UserDeleteTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,id);
                }
            });

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
*/

    }

    private void InitialSetting() {
        backButton = (ImageButton)findViewById(R.id.my_view_back_button);
        nicknameView = (TextView)findViewById(R.id.nickname_my);
        editProfile = (LinearLayout)findViewById(R.id.edit_profile);
        logout = (LinearLayout)findViewById(R.id.logout);
        deleteUser = (LinearLayout)findViewById(R.id.delete_user);


        //logout popup menu
        popupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        noticePopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)popupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)popupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)popupView.findViewById(R.id.notice_plus_cancel_textview);
        noticePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));



    }

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
            nicknameView.setText(nickname);
        }
    }






    class UserDeleteTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String ID = (String) strings[0];
            String postParameters = "id=" + ID;

            try {
                URL url = new URL("https://server.allofgist.com/user_delete.php");
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
                auto_login = getSharedPreferences("AUTO_LOGIN", Activity.MODE_PRIVATE);
                saveUser = auto_login.edit();
                saveUser.remove("USER_ID");
                saveUser.apply();
                finish();
            }
            else
                GrayToast(getApplicationContext(),"서버 연결을 실패하였습니다.");
        }
    }


    public void GrayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }
}
