package com.example.x_note.allofgistlite;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PopupWindow food_diary;
    private Canvas mCanvas;
    private Paint mPaint;

    private TextView scheduleText;
    private ListView schedulePreview;
    private CalendarListAdapter calendarListAdapter;
    private ArrayList<Schedule> calendarListDate_pre;
    private ArrayList<Schedule> alarmListData;
    private ArrayList<AlarmManager> alarmManagers;
    private Hashtable<Date, ArrayList<Schedule>> calendarListData;
    String[] day = {"일", "월", "화", "수", "목", "금", "토"};

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;

    private RelativeLayout fullscreen;

    //하단 메뉴 창 관련
    private RelativeLayout calendarCategory;
    private RelativeLayout allsiteCategory;
    private RelativeLayout foodDiaryCategory;
    private RelativeLayout favoriteSettingCategory;
    private RelativeLayout anonymousForumCategory;

    //학식 팝업 관련
    private View popupView;
    private Button firstFloor;
    private Button secondFloor;
    private Button secondRestaurant;

    //사이트 정보
    private List<Site> itemList = null;
    private ArrayList<Site> major_set = null;

    //로그인 정보
    String id;

    //즐겨찾기 keylist
    ArrayList<Integer> keylist;

    //뒤로가기 버튼 관련
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    //새로고침 (즐겨찾기)
    private ImageButton reloadFavorite;

    @Override
    protected void onResume() {
        super.onResume();

        setData();

        //오늘의 일정 불러오기
        Calendar today = Calendar.getInstance();
        Date todaykey = today.getTime();
        int dayNum = today.get(Calendar.DAY_OF_WEEK);
        scheduleText.setText((today.get(Calendar.MONTH)+1)+"월 "+today.get(Calendar.DAY_OF_MONTH)+"일 "+ day[dayNum - 1]+"요일");
        calendarListDate_pre = new ArrayList<Schedule>();
        calendarListData = new Hashtable<Date, ArrayList<Schedule>>();
        String completedate = date.format(todaykey);
        ScheduleLoadTask scheduleLoadTask = new ScheduleLoadTask();
        startMyTask(scheduleLoadTask,id,completedate);

        //DB에 저장된 즐겨찾기 key(번호) 저장하는 배열
        keylist = new ArrayList<Integer>();

        SharedPreferences favoritePrefs = getSharedPreferences("FAVORITE_KEYLIST",MODE_PRIVATE);
        for(int i = 0; i<itemList.size(); i++){
            Boolean isFavorite = (favoritePrefs.getString("KEYLIST_"+id+"_"+i,"NONE") == "OK");
            if(isFavorite) {
                keylist.add(i);
            }
        }


        favoriteAdapter = new FavoriteAdapter(MainActivity.this,itemList,major_set);
        recyclerView.setAdapter(favoriteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        //recyclerview 스크롤 딱 맞아떨어지게 하는 도구
        SnapToBlock snapToBlock = new SnapToBlock(4);
        if(keylist.size()>4)
            snapToBlock.attachToRecyclerView(recyclerView);
        //if(keylist.isEmpty())
            //startMyTask(new FavoriteLoad(),id);

        startMyTask(new TokenLoadTask(),"http://13.124.99.123/tokenload.php",id);

    }

    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로그인 정보 가져오기
        SharedPreferences loginPrefs = getSharedPreferences("LOGIN_ID",MODE_PRIVATE);
        id = loginPrefs.getString("ID","LOGIN_ERROR");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(MainActivity.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();



        setData();

        calendarCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent calendarMain = new Intent(MainActivity.this,CalendarMain.class);
                calendarMain.putExtra("ID",id);
                startActivity(calendarMain);
            }
        });

        allsiteCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent allsiteActivity = new Intent(MainActivity.this, AllsiteActivity.class);
                allsiteActivity.putExtra("ID",id);
                startActivity(allsiteActivity);
            }
        });

        foodDiaryCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent foodDiary = new Intent(MainActivity.this, FoodDiary.class);
                startActivity(foodDiary);
            }
        });

        favoriteSettingCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent favoriteSettingActivity = new Intent(MainActivity.this, FavoriteSettingActivity.class);
                favoriteSettingActivity.putExtra("ID",id);
                favoriteSettingActivity.putIntegerArrayListExtra("FAVORITE_KEYLIST",keylist);
                startActivity(favoriteSettingActivity);
            }
        });

        anonymousForumCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anonymousForumActivity = new Intent(MainActivity.this, AnonymousForumActivity_Preview.class);
                anonymousForumActivity.putExtra("ID",id);
                startActivity(anonymousForumActivity);
            }
        });

        //즐겨찾기 불러오기(다시)
        /*reloadFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                recyclerView.setAdapter(favoriteAdapter);
            }
        });*/



        //ring button 관련 코드

        /*ringControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //모든 사이트 보기
            }
        });

        ringControl.setOnTouchListener(new View.OnTouchListener() {
            private float initialX, initialY, finalX, finalY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = motionEvent.getX() - (fullscreen.getWidth()) / 2;
                        initialY = fullscreen.getHeight() - motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        finalX = motionEvent.getX() - (fullscreen.getWidth()) / 2;
                        finalY = fullscreen.getHeight() - motionEvent.getY();
                        float minimumDistance = ringControl.getWidth();
                        double distance = Math.sqrt(Math.pow((finalX - initialX), 2) + Math.pow((finalY - initialY), 2));
                        double angle = (360 + Math.toDegrees(Math.atan2(finalY - initialY, finalX - initialX))) % 360;

                        //0도~45도 : 학생식당 식단표
                        if (((angle >= 0 && angle <= 45) || (angle >= 270 && angle < 360)) && distance >= minimumDistance / 2) {
                            if (!food_diary.isShowing()) {
                                food_diary.showAtLocation(popupView, Gravity.CENTER, 0, -40);

                                //1학생식당 1층
                                firstFloor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new FirstRestaurantTask("https://www.gist.ac.kr/kr/html/sub05/050601.html").execute();
                                    }
                                });

                                //1학생식당 2층
                                secondFloor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new FirstRestaurantTask("https://www.gist.ac.kr/kr/html/sub05/050603.html").execute();
                                    }
                                });

                                //2학생식당 1층
                                secondRestaurant.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openWebPage("https://www.gist.ac.kr/kr/html/sub05/050602.html");
                                    }
                                });

                            }
                            break;
                        }


                        //135도~180도 : 학사일정
                        if (angle >= 135 && angle <= 270 && distance >= minimumDistance / 2) {
                            Intent calendarMain = new Intent(MainActivity.this,CalendarMain.class);
                            calendarMain.putExtra("ID",id);
                            if (food_diary.isShowing()) {
                                food_diary.dismiss();
                            }
                            startActivity(calendarMain);
                        }
                        break;
                }
                return false;
            }
        });*/


    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if(0<=intervalTime&&FINISH_INTERVAL_TIME>=intervalTime)
            super.onBackPressed();
        else {
            backPressedTime = tempTime;
            OrangeToast(getApplicationContext(),"한번 더 뒤로가기를 누르면 종료됩니다.");
        }
    }

    public class ControlButton extends View {

        public ControlButton(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            int radius;
            radius = 100;
        }
    }


    //사이트 접속
    public void openWebPage(String url) {
        if (url.contains("https://www.facebook.com")) {
            if (getOpenFacebookIntent(MainActivity.this, url).contains("https://www.facebook.com")){
                String facebookurl = url.replaceFirst("www.", "m.");
                if(!facebookurl.startsWith("https"))
                    facebookurl = "https://"+facebookurl;
                Intent webView = new Intent(MainActivity.this, web_interface.class);
                webView.putExtra("Url", facebookurl);
                startActivity(webView);
            }
            else{
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse(getOpenFacebookIntent(MainActivity.this, url)));
                startActivity(facebookIntent);
            }
        } else {
            Intent webView = new Intent(MainActivity.this, web_interface.class);
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
    public class FirstRestaurantTask extends AsyncTask<Integer, Void, String> {

        String url;

        FirstRestaurantTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Integer... params) {
            String Address = this.url;
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


    public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoritesHolder> {

        //SImple Image Resizer variables
        private final int REQUEST_WIDTH = 256;
        private final int REQUEST_HEIGHT = 256;

        private List<Site> mDataBaseset;
        private ArrayList<Site> major_set;
        private Context mContext;

        private Site DEFAULT_DATA = new Site("",null,R.drawable.default_favorite); //null로 했을 때 오류 나는 지 확인(안되면 Site 생성자 추가), image도 제작후 변경

        public FavoriteAdapter(Context context, List<Site> sites, ArrayList<Site> major_set) {
            mDataBaseset = sites;
            mContext = context;
            this.major_set = major_set;

        }

        @Override
        public void onBindViewHolder(final FavoriteAdapter.FavoritesHolder favoritesHolder, int position) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Site currentsite = mDataBaseset.get(keylist.get(position));
            BitmapFactory.decodeResource(mContext.getResources(), currentsite.getMsite_imagesource(), options);
            options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), currentsite.getMsite_imagesource(), options);
            favoritesHolder.mImageView.setBackground(new ShapeDrawable(new OvalShape()));
            favoritesHolder.mImageView.setClipToOutline(true);
            favoritesHolder.mImageView.requestLayout();
            favoritesHolder.mImageView.setImageBitmap(bitmap);
            favoritesHolder.mTextView.setText(currentsite.getMsite_name());

            //즐겨찾기 접속 기능
            favoritesHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Site site = mDataBaseset.get(keylist.get(favoritesHolder.getAdapterPosition()));
                    if (site.getMsite_name() == "학과별 사이트") {
                        PopupMenu popupMj = new PopupMenu(mContext, view);
                        MenuInflater menuInflater = new MenuInflater(mContext);
                        menuInflater.inflate(R.menu.major_site_set_popup_menu, popupMj.getMenu());
                        popupMj.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    for (int i = 0; i < major_set.size(); i++) {
                                        if (major_set.get(i).getMsite_name().equals(menuItem.getTitle().toString()))
                                            openWebPage(major_set.get(i).getMsite_url());
                                    }
                                    return false;
                                }
                        });
                        popupMj.show();
                    } else if (site.getMsite_name() == "GIST home") {
                        PopupMenu popupIFWB = new PopupMenu(mContext, view);
                        MenuInflater menuInflater = new MenuInflater(mContext);
                        menuInflater.inflate(R.menu.insta_facebook_web_blog_popup_menu, popupIFWB.getMenu());
                        popupIFWB.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.webIFWB:
                                        openWebPage(site.getMsite_urlW());
                                        break;
                                    case R.id.blogIFWB:
                                        openWebPage(site.getMsite_urlB());
                                        break;
                                    case R.id.facebookIFWB:
                                        openWebPage(site.getMsite_urlF());
                                        break;
                                    case R.id.instaIFWB:
                                        openWebPage(site.getMsite_urlI());
                                        break;
                                }
                                return false;
                            }
                        });
                        popupIFWB.show();
                    } else if (site.getMsite_name() == "GIST 신문") {

                        PopupMenu popupWF = new PopupMenu(mContext, view);
                        MenuInflater menuInflater = new MenuInflater(mContext);
                        menuInflater.inflate(R.menu.web_facebook_popup_menu, popupWF.getMenu());
                        popupWF.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.webWF:
                                        openWebPage(site.getMsite_urlF());
                                        break;
                                    case R.id.facebookWF:
                                        openWebPage(site.getMsite_urlY());
                                        break;
                                }
                                return false;
                            }
                        });
                        popupWF.show();
                    } else if (site.getMsite_name() == "춤 동아리 막무가내" || site.getMsite_name() == "힙합 동아리 Ignition" || site.getMsite_name() == "노래 동아리 싱송생송" || site.getMsite_name() == "오케스트라 동아리 악동" || site.getMsite_name() == "연극 동아리 지대로" || site.getMsite_name() == "영상편집 동아리 The GIST") {

                        PopupMenu popupYF = new PopupMenu(mContext, view);
                        MenuInflater menuInflater = new MenuInflater(mContext);
                        menuInflater.inflate(R.menu.youtube_facebook_popup_menu, popupYF.getMenu());
                        popupYF.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.facebookYF:
                                        openWebPage(site.getMsite_urlF());
                                        break;
                                    case R.id.youtubeYF:
                                        openWebPage(site.getMsite_urlY());
                                        break;
                                }
                                return false;
                            }
                        });
                        popupYF.show();
                    } else
                        openWebPage(site.getMsite_url());
                }
            });
        }


        @NonNull
        @Override
        public FavoritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridviewitem_allsites, parent, false);
            FavoritesHolder favoritesHolder = new FavoritesHolder(view);

            return favoritesHolder;
        }

        @Override
        public int getItemCount() {
            return keylist.size();
        }


        public class FavoritesHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView mTextView;

            public FavoritesHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.image);
                mTextView = (TextView) view.findViewById(R.id.name);
            }
        }
    }

    public class FavoriteLoad extends AsyncTask<String, Integer, String> {

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

            if(result.equals("NULL"))
                OrangeToast(MainActivity.this,"즐겨찾기를 추가해주세요.");
            else{
                String[] splitlist;
                splitlist=result.split(",");

                for(String wo : splitlist) {
                    keylist.add(Integer.parseInt(wo));
                }
            }

            favoriteAdapter = new FavoriteAdapter(MainActivity.this,itemList,major_set);
            recyclerView.setAdapter(favoriteAdapter);
        }

    }
    //AsyncTask 사용은 가능 but, 두번이상 사용한다는 점에서 적절한 방법은 아님
    public class ScheduleLoadTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0] +"&complete_date="+params[1];
            Log.e("POST", param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/user_diary_schedule_list_load.php");
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
                Log.v("RECV DATA", data);
                return (params[1]+"|||||"+data);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return (params[1]+"|||||"+null);
        }

        @Override
        protected void onPostExecute(String result) {
            String[] resultSplit = result.split("\\|\\|\\|\\|\\|");
            Schedule schedule;
            alarmListData = new ArrayList<Schedule>();
            alarmManagers = new ArrayList<AlarmManager>();
            if (resultSplit.length==1){
                calendarListDate_pre.add(new Schedule(0,"일정이 없습니다.", null,null,null, null,null,null,null,null,null));
                calendarListAdapter = new CalendarListAdapter(calendarListDate_pre);
                schedulePreview.setAdapter(calendarListAdapter);
            }
            else {
                String[] splitlist;
                splitlist = resultSplit[1].split("\n");
                java.sql.Date sqlStartDate;
                java.sql.Date sqlEndDate;
                try {
                    for(int i=0;i<splitlist.length;i++) {
                        String[] splitstring=splitlist[i].split("/");
                        java.util.Date transCompleteDateTime = datetime.parse(splitstring[3]);
                        if(splitstring.length<=7){
                            sqlStartDate = null;
                            sqlEndDate = null;
                        }
                        else{
                            java.util.Date transStartDate = date.parse(splitstring[7]);
                            java.util.Date transEndDate = date.parse(splitstring[8]);
                            sqlStartDate = new java.sql.Date(transStartDate.getTime());
                            sqlEndDate = new java.sql.Date(transEndDate.getTime());
                        }
                        if(splitstring.length<=9)
                            schedule = new Schedule(Integer.parseInt(splitstring[0]),splitstring[1],splitstring[2],transCompleteDateTime,splitstring[4],splitstring[5],splitstring[6],sqlStartDate,sqlEndDate,null,null);
                        else
                            schedule = new Schedule(Integer.parseInt(splitstring[0]),splitstring[1],splitstring[2],transCompleteDateTime,splitstring[4],splitstring[5],splitstring[6],sqlStartDate,sqlEndDate,splitstring[9],splitstring[10]);

                        if(!splitstring[4].equals("없음")){
                            alarmListData.add(schedule);
                        }

                        calendarListDate_pre.add(schedule);
                        Date completeDate = date.parse(resultSplit[0]);
                        calendarListData.put(completeDate, calendarListDate_pre);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                try {
                    java.util.Date completedatekey = date.parse(resultSplit[0]);
                    calendarListAdapter = new CalendarListAdapter(calendarListData.get(completedatekey));
                    schedulePreview.setAdapter(calendarListAdapter);
                }catch(Exception e){
                    e.printStackTrace();
                }
/*
                int i = 0;
                while(!alarmListData.isEmpty()&&i<alarmListData.size()) {
                    Intent resultIntent = new Intent(MainActivity.this, FoodDiary.class);
                    String calculateAlarm = alarmListData.get(i % alarmListData.size()).getAlarm_calculate();
                    String[] seperateDateTime = calculateAlarm.split(" ");
                    String[] seperateDate = seperateDateTime[0].split("-");
                    String[] seperateTime = seperateDateTime[1].split(":");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.parseInt(seperateDate[0]),Integer.parseInt(seperateDate[1])-1,Integer.parseInt(seperateDate[2]),Integer.parseInt(seperateTime[0]),Integer.parseInt(seperateTime[1]),Integer.parseInt(seperateTime[2]));
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    int _id = (int)System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,_id,resultIntent,PendingIntent.FLAG_NO_CREATE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                    alarmManagers.add(alarmManager);
                    i++;
                }*/
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.

                //startMyTask(new PushNotification(), "");

            }
        }
    }


    public class PushNotification extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            int i = 0;
            while(!alarmListData.isEmpty()){
                if (alarmListData.get(i % alarmListData.size()).getAlarm_calculate().equals(datetime.format(Calendar.getInstance().getTime())))
                    break;
                i++;
            }

            /* 인풋 파라메터값 생성 */
            String data = "";
            //String param = "id=" + params[0] + "&password=" + params[1] + "";
            //Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/push_notification.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                //outs.write(param.getBytes("UTF-8"));
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
                 Log.e("RECV DATA",data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return -1;
        }


    }


    public class TokenLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String id = (String)strings[1];

            String serverUrl = (String)strings[0];
            String postParameters = "id="+id;

            try{
                URL url = new URL(serverUrl);
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
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
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
        protected void onPostExecute(String tokenInServer) {
            SharedPreferences prefs = getSharedPreferences("TOKEN_PREF",MODE_PRIVATE);
            String token = prefs.getString("token","");

            if(TextUtils.isEmpty(tokenInServer)) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();

                        Log.e("newToken", newToken);
                        SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF",MODE_PRIVATE).edit();
                        editor.putString("token",newToken);
                        editor.apply();

                        startMyTask(new TokenToServerTask(),"http://13.124.99.123/tokeninsert.php",id,newToken);
                    }
                });
            }
            else if(!token.equals(tokenInServer)){
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();

                        Log.e("newToken", newToken);
                        SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF",MODE_PRIVATE).edit();
                        editor.putString("token",newToken);
                        editor.apply();

                        startMyTask(new TokenToServerTask(),"http://13.124.99.123/tokeninsert.php",id,newToken);
                    }
                });
            }
            else{}
        }
    }

    public class TokenToServerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String id = (String)strings[1];
            String token = (String)strings[2];

            String serverUrl = (String)strings[0];
            String postParameters = "id="+id+"&token="+token;

            try{
                URL url = new URL(serverUrl);
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
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
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
    }


    public class CalendarListAdapter extends BaseAdapter {

        private ArrayList<Schedule> calendarAdapterData;

        public CalendarListAdapter(ArrayList<Schedule> calendarAdapterData){
            this.calendarAdapterData = calendarAdapterData;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getCount() {
            return calendarAdapterData.size();
        }

        @Override
        public Schedule getItem(int position) {
            return calendarAdapterData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.calendar_list_preview_item, parent, false);
            }

            TextView contextTextView = (TextView)convertView.findViewById(R.id.preview_diary_context_textview);

            contextTextView.setText(calendarAdapterData.get(position).getContext());
            return convertView;
        }
    }


    //Bitmap resizing
    private int setSimpleSize(BitmapFactory.Options options, int requestWidth, int requestHeight){
        // 이미지 사이즈를 체크할 원본 이미지 가로/세로 사이즈를 임시 변수에 대입.
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // 원본 이미지 비율인 1로 초기화
        int size = 1;

        // 해상도가 깨지지 않을만한 요구되는 사이즈까지 2의 배수의 값으로 원본 이미지를 나눈다.
        while(requestWidth < originalWidth || requestHeight < originalHeight){
            originalWidth = originalWidth / 2;
            originalHeight = originalHeight / 2;

            size = size * 2;
        }
        return size;
    }


    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.show();
    }

    public void InitialSetting(){

        scheduleText = (TextView)findViewById(R.id.schedule_text);
        schedulePreview = (ListView)findViewById(R.id.schedule_preview);

       // fullscreen = (RelativeLayout) findViewById(R.id.fullscreen);
        //popupView = getLayoutInflater().inflate(R.layout.food_diary_popup_window, null);
        //food_diary = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        //final Button ringControl = (Button) findViewById(R.id.ring_button_control);

        //학식 버튼
        //firstFloor = (Button) popupView.findViewById(R.id.first_floor);
        //secondFloor = (Button) popupView.findViewById(R.id.second_floor);
        //secondRestaurant = (Button) popupView.findViewById(R.id.second_restaurant);


        //즐겨찾기 관련 코드
        //reloadFavorite = (ImageButton) findViewById(R.id.reload_favorite);
        recyclerView = (RecyclerView) findViewById(R.id.favorite_layout);

        //하단 카테고리 버튼들
        calendarCategory = (RelativeLayout)findViewById(R.id.category_calendar);
        allsiteCategory = (RelativeLayout)findViewById(R.id.category_allsite);
        foodDiaryCategory = (RelativeLayout)findViewById(R.id.category_food_diary);
        favoriteSettingCategory = (RelativeLayout)findViewById(R.id.category_favorite_setting);
        anonymousForumCategory = (RelativeLayout)findViewById(R.id.category_anonymous_forum);
    }

    public void setData(){
        itemList = new ArrayList<Site>();
        itemList.add(new Site("GIST home", "https://www.gist.ac.kr/kr/","https://blog.naver.com/gist1993","https://www.facebook.com/GIST.ac.kr/","https://www.instagram.com/gist1993/", R.drawable.home));
        itemList.add(new Site("Gel","https://gel.gist.ac.kr/",R.drawable.gel));
        itemList.add(new Site("Zeus system", "https://zeus.gist.ac.kr", R.drawable.zeus));
        itemList.add(new Site("수강 신청 사이트","https://zeus.gist.ac.kr/sys/lecture/lecture_main.do",R.drawable.courseregisteration));
        itemList.add(new Site("Portal system", "https://portal.gist.ac.kr/intro.jsp", R.drawable.portal));
        itemList.add(new Site("Email system", "https://mail.gist.ac.kr/loginl?locale=ko_KR", R.drawable.email));
        itemList.add(new Site("GIST college", "https://college.gist.ac.kr/", R.drawable.college));
        itemList.add(new Site("GIST library", "https://library.gist.ac.kr/", R.drawable.library));
        itemList.add(new Site("학내공지", "https://college.gist.ac.kr/main/Sub040203", R.drawable.haknaegongji));
        itemList.add(new Site("GIST 대학생","https://www.facebook.com/groups/giststudent/",R.drawable.giststudent));
        itemList.add(new Site("GIST 대나무숲","https://www.facebook.com/GISTIT.ac.kr/",R.drawable.gistdaenamoo));
        itemList.add(new Site("GIST 대나무숲 제보함","http://fbpage.kr/?pi=128#/submit",R.drawable.gistdaenamoojaeboo));
        itemList.add(new Site("언어교육센터","https://language.gist.ac.kr/",R.drawable.language));
        itemList.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup));
        itemList.add(new Site("학과별 사이트",R.drawable.majorset));
        itemList.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion));
        itemList.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight));
        itemList.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse));
        itemList.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe));
        itemList.add(new Site("GIST 신문","http://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews));
        itemList.add(new Site("GIST 홍보대사", "http://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare));
        itemList.add(new Site("춤 동아리 막무가내", "https://www.facebook.com/gistmacmoo/","https://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae));
        itemList.add(new Site("힙합 동아리 Ignition", "https://www.facebook.com/GISTignition/","https://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition));
        itemList.add(new Site("노래 동아리 싱송생송", "https://www.facebook.com/gistsingsong/","https://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong));
        itemList.add(new Site("연극 동아리 지대로", "https://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","https://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero));
        itemList.add(new Site("오케스트라 동아리 악동", "https://www.facebook.com/GISTorchestra/","https://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra));
        itemList.add(new Site("기타 동아리 HOTSIX", "https://www.facebook.com/gisthotsix/", R.drawable.hotsix));
        itemList.add(new Site("피아노 동아리 GISRI", "https://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri));
        itemList.add(new Site("밴드 Main", "https://www.facebook.com/MAIN-899414343552162/", R.drawable.main));
        itemList.add(new Site("밴드 도도한 쭈쭈바", "https://www.facebook.com/dozzu/", R.drawable.dozzu));
        itemList.add(new Site("영화 동아리 Cinergy", "https://www.facebook.com/gistcinergy/", R.drawable.cinergy));
        itemList.add(new Site("영상편집 동아리 The GIST", "https://www.facebook.com/Gentletist/","https://www.youtube.com/channel/UCMUDHS0SZvQilFe5h6eI9rA/videos", R.drawable.thegist));
        itemList.add(new Site("문예창작 동아리 사각사각", "https://www.facebook.com/GIST-%EC%82%AC%EA%B0%81%EC%82%AC%EA%B0%81-238788459851229/", R.drawable.sagaksagak));
        itemList.add(new Site("GIST 고양이 지냥이", "https://www.facebook.com/giscats/", R.drawable.giscat));
        itemList.add(new Site("요리동아리 이쑤시개", "https://www.facebook.com/%EC%9D%B4%EC%91%A4%EC%8B%9C%EA%B0%9C-%EC%9A%94%EB%A6%AC%ED%95%98%EB%8A%94-GIST%EC%83%9D-272551203239747/?ref=py_c", R.drawable.essosigae));
        itemList.add(new Site("칵테일 동아리 MixoloGIST", "https://www.facebook.com/Mixologist-1584231725157207", R.drawable.mixologist));
        itemList.add(new Site("보드게임 동아리 BGM", "https://www.facebook.com/GISTBGM", R.drawable.bgm));
        itemList.add(new Site("성소수자 모임 speQtrum", "https://www.facebook.com/gistspeqtrum/", R.drawable.speqtrum));
        itemList.add(new Site("만화 동아리 erutlucbus", "https://www.facebook.com/Erutlucbus/", R.drawable.eru));
        itemList.add(new Site("천체관측 동아리 SpaceBar", "https://www.facebook.com/GISTspacebar/", R.drawable.spacebar));
        itemList.add(new Site("전산 동아리 WING", "https://www.facebook.com/GISTWING/", R.drawable.wing));
        itemList.add(new Site("환경 동아리 온새미로", "https://www.facebook.com/onsaemiro123/", R.drawable.onsaemiro));
        itemList.add(new Site("축구 동아리 Kickass", "https://www.facebook.com/gistkickass/?ref=br_rs", R.drawable.kickass));

        //학과별 사이트 Array
        major_set = new ArrayList<Site>();

        major_set.add(new Site("전기전자컴퓨터공학부", "https://eecs.gist.ac.kr/"));
        major_set.add(new Site("신소재공학부", "https://mse.gist.ac.kr"));
        major_set.add(new Site("기계공학부", "https://me.gist.ac.kr"));
        major_set.add(new Site("지구·환경공학부", "https://env1.gist.ac.kr"));
        major_set.add(new Site("생명과학부", "https://life1.gist.ac.kr"));
        major_set.add(new Site("물리·광과학과", "https://phys.gist.ac.kr/"));
        major_set.add(new Site("화학과", "https://chem.gist.ac.kr"));
        major_set.add(new Site("융합기술원", "https://iit.gist.ac.kr/"));
        major_set.add(new Site("나노바이오재료전자공학과", "https://wcu.gist.ac.kr/"));
    }


    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

}










