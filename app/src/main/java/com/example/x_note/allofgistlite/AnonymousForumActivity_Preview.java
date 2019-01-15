package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AnonymousForumActivity_Preview extends AppCompatActivity {

    String id;
    LinearLayout buttonHome;
    LinearLayout buttonBest;
    ImageView imageViewBest;
    TextView textViewBest;
    int bestButtonClick;
    LinearLayout buttonSearch;
    LinearLayout buttonWrite;

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm",Locale.KOREA);
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    SimpleDateFormat monthdayformat = new SimpleDateFormat("MM.dd",Locale.KOREA);

    RecyclerView contentList;

    private ArrayList<Forum> forumList;

    @Override
    protected void onResume() {
        super.onResume();
        forumList = new ArrayList<Forum>();
        startMyTask(new ForumLoadTask(),"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_preview);

        bestButtonClick = 0;

        id = getIntent().getStringExtra("ID");

        initialSetting();

        buttonHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bestButtonClick%2==0){
                    buttonBest.setSelected(true);
                    imageViewBest.setSelected(true);
                    textViewBest.setText(R.string.menu_button_overview);
                    bestButtonClick++;
                }
                else{
                    buttonBest.setSelected(false);
                    imageViewBest.setSelected(false);
                    textViewBest.setText(R.string.menu_button_best);
                    bestButtonClick++;
                }
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonWrite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent writeActivity = new Intent(AnonymousForumActivity_Preview.this, AnonymousForumActivity_Write.class);
                writeActivity.putExtra("ID",id);
                startActivity(writeActivity);
            }
        });


        contentList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), contentList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        startMyTask(new ViewsUploadTask(),position+"",forumList.get(position).getNum()+"");
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

    }



    public void initialSetting(){
        buttonHome = (LinearLayout)findViewById(R.id.button_home);
        buttonSearch = (LinearLayout)findViewById(R.id.button_search);
        buttonWrite = (LinearLayout)findViewById(R.id.button_write);
        buttonBest = (LinearLayout)findViewById(R.id.button_best);
        textViewBest = (TextView)findViewById(R.id.button_best_textview);
        imageViewBest = (ImageView)findViewById(R.id.button_best_imageview);

        contentList = (RecyclerView)findViewById(R.id.content_forum);
    }




    public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ForumHolder>{

        ForumHolder forumHolder;
        private Context context;

        public ForumListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return forumList.size();
        }

        @NonNull
        @Override
        public ForumListAdapter.ForumHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View view = null;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem_anonymous_forum_preview, parent, false);

            forumHolder = new ForumHolder(view);

            return forumHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ForumListAdapter.ForumHolder forumHolder, int position) {

            Forum convertForum = forumList.get(position);
            forumHolder.title.setText(convertForum.getTitle());
            if(convertForum.getComments()>0)
                forumHolder.commentNumber.setText(("["+convertForum.getComments()+"]"));
            forumHolder.nickname.setText(convertForum.getNickname());
            forumHolder.views.setText(("조회 "+convertForum.getViews()));
            forumHolder.likes.setText(("좋아요 "+convertForum.getLikes()));
            forumHolder.unlikes.setText(("싫어요 "+convertForum.getUnlikes()));
            Calendar calendar = Calendar.getInstance();

            if(!dateFormat.format(convertForum.getUpload_datetime()).equals(dateFormat.format(calendar.getTimeInMillis())))
                forumHolder.uploadTime.setText(monthdayformat.format(convertForum.getUpload_datetime()));
            else
                forumHolder.uploadTime.setText(timeFormat.format(convertForum.getUpload_datetime()));

        }



        public class ForumHolder extends RecyclerView.ViewHolder{

            TextView title;
            TextView commentNumber;
            TextView nickname;
            TextView views;
            TextView likes;
            TextView unlikes;
            TextView uploadTime;

            public ForumHolder(View view){
                super(view);
                title = (TextView)view.findViewById(R.id.title_content_preview);
                commentNumber = (TextView)view.findViewById(R.id.comment_number_preview);
                nickname = (TextView)view.findViewById(R.id.nickname_preview);
                views = (TextView)view.findViewById(R.id.views_number_preview);
                likes = (TextView)view.findViewById(R.id.likes_number_preview);
                unlikes = (TextView)view.findViewById(R.id.unlikes_number_preview);
                uploadTime = (TextView)view.findViewById(R.id.upload_time_preview);
            }

        }

    }


    class ForumLoadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                URL serverUrl = new URL("http://13.124.99.123/forumload.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)serverUrl.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("phptest", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == httpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                String result = replaceLast(sb.toString(),"\n","");
                return result;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                String[] forumDevide = result.split("\\|g\\|e\\|z\\|f\\|s\\|g\\|l\\|d\\|s\\|f\\|g\\|h\\|j\\|e\\|r\\|t\\|");
                for (int i = 0; i<forumDevide.length; i++){
                    String[] forumSplit = forumDevide[i].split("\\|g\\|e\\|z\\|f\\|s\\|g\\|l\\|");
                    Forum forum = new Forum(Integer.parseInt(forumSplit[0]),forumSplit[1],
                            forumSplit[2],forumSplit[3],forumSplit[4],datetimeFormat.parse(forumSplit[5]),
                            Integer.parseInt(forumSplit[6]),Integer.parseInt(forumSplit[7]),
                            Integer.parseInt(forumSplit[8]),Integer.parseInt(forumSplit[9]),forumSplit[10],forumSplit[11]);

                    forumList.add(forum);
                }
                contentList.setAdapter(new ForumListAdapter(getApplicationContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(AnonymousForumActivity_Preview.this);
                contentList.setLayoutManager(layoutManager);
            }catch (Exception e){
                e.printStackTrace();
                OrangeToast(getApplicationContext(),"게시판 업로드 에러!");
            }
        }
    }

    class ViewsUploadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String data = "";

            try{
                String forumPosition = strings[0];
                String forumNum = strings[1];
                String params = "num="+forumNum;
                URL serverUrl = new URL("http://13.124.99.123/views_upload.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)serverUrl.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(params.getBytes("euc-kr"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("viewsUploadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while((line = bufferedReader.readLine())!=null){
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString().trim();

                Log.e("RECV data",data);

                return data+"/"+forumPosition;


            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("")){
                Log.e("Views","upload views fail!");
            }
            else{
                String[] splitResult = result.split("/");
                if(splitResult[0].equals("OK")) {
                    int lastViews = forumList.get(Integer.parseInt(splitResult[1])).getViews();
                    forumList.get(Integer.parseInt(splitResult[1])).setViews(lastViews+1);
                    Intent AnonymousForumView = new Intent(AnonymousForumActivity_Preview.this,AnonymousForumActivity_View.class);
                    AnonymousForumView.putExtra("ForumLoadData",forumList.get(Integer.parseInt(splitResult[1])));
                    AnonymousForumView.putExtra("ID",id);
                    startActivity(AnonymousForumView);
                }
                else
                    OrangeToast(getApplicationContext(),"Error number : "+splitResult[0]);
            }
        }
    }

    private static String replaceLast(String string, String toReplace, String replacement) {

        int pos = string.lastIndexOf(toReplace);

        if (pos > -1) {
            return string.substring(0, pos)+ replacement + string.substring(pos +   toReplace.length(), string.length());
        } else {
            return string;
        }
    }
    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    public void OrangeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.orange_toast_design);
        toast.show();
    }
}


