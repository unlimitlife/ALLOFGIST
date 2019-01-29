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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class AnonymousForumActivity_Search extends AppCompatActivity {

    private String id;

    ImageButton backButton;
    RelativeLayout searchClassificationLayout;
    TextView searchClassificationText;

    CheckBox searchOnlyBestCheckBox;
    TextView searchOnlyBestText;

    EditText searchEditText;
    RelativeLayout searchTextEraseLayout;
    ImageView searchTextEraseButton;
    ImageButton searchButton;

    RecyclerView searchList;
    private ArrayList<Forum> forumList;

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    SimpleDateFormat monthdayformat = new SimpleDateFormat("MM.dd",Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum__search);

        id = getIntent().getStringExtra("ID");

        InitialSettting();

        searchClassificationText.setText("전체");
        searchTextEraseLayout.setVisibility(View.GONE);

        //게시글에서 작성글 검색으로 들어오는 경우
        try {
            if (!getIntent().getStringExtra("NICKNAME").isEmpty()) {
                searchClassificationText.setText(R.string.writer_search);
                searchEditText.setText(getIntent().getStringExtra("NICKNAME"));
                forumList = new ArrayList<Forum>();
                startMyTask(new SearchLoadTask(), "w", 0 + "", searchEditText.getText().toString());
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        searchOnlyBestText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchOnlyBestCheckBox.setChecked(!searchOnlyBestCheckBox.isChecked());
            }
        });

        /*searchOnlyBestCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnlyBestCheckBox.setChecked(!searchOnlyBestCheckBox.isChecked());
            }
        });*/

        searchClassificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupC = new PopupMenu(getApplicationContext(), v);
                MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                menuInflater.inflate(R.menu.anonymous_forum_search_classification_menu, popupC.getMenu());
                popupC.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.search_all:
                                searchClassificationText.setText(R.string.all_search);
                                break;
                            case R.id.search_title:
                                searchClassificationText.setText(R.string.title_search);
                                break;
                            case R.id.search_content:
                                searchClassificationText.setText(R.string.content_search);
                                break;
                            case R.id.search_writer:
                                searchClassificationText.setText(R.string.writer_search);
                                break;
                            case R.id.search_title_with_content:
                                searchClassificationText.setText(R.string.title_with_content_search);
                                break;
                        }

                        return false;
                    }
                });
                popupC.show();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0)
                    searchTextEraseLayout.setVisibility(View.VISIBLE);
                else if(count==0)
                    searchTextEraseLayout.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchTextEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int onlyBest;
                String condition;
                if(searchOnlyBestCheckBox.isChecked())
                    onlyBest = 1;
                else
                    onlyBest = 0;
                switch (searchClassificationText.getText().toString()){
                    case "전체":
                        condition = "o";
                        break;
                    case "제목":
                        condition = "t";
                        break;
                    case "내용":
                        condition = "c";
                        break;
                    case "글쓴이":
                        condition = "w";
                        break;
                    case "제목+내용":
                        condition = "twc";
                        break;
                    default:
                        condition = "o";
                        break;
                }
                if(searchEditText.getText().toString().isEmpty())
                    OrangeToast(getApplicationContext(),"검색어를 입력해주세요.");
                else {
                    forumList = new ArrayList<Forum>();
                    startMyTask(new SearchLoadTask(), condition, onlyBest + "", searchEditText.getText().toString());
                }
            }
        });

    }
    public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ForumHolder>{

        ForumListAdapter.ForumHolder forumHolder;
        private Context context;

        private ForumListAdapter(Context context) {
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

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.recyclerviewitem_anonymous_forum_preview, parent, false);

            forumHolder = new ForumListAdapter.ForumHolder(view);

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

            if(convertForum.getLikes()>=10)
                forumHolder.bestIcon.setVisibility(View.VISIBLE);
            else
                forumHolder.bestIcon.setVisibility(View.GONE);

            if(!dateFormat.format(convertForum.getUpload_datetime()).equals(dateFormat.format(calendar.getTimeInMillis())))
                forumHolder.uploadTime.setText(monthdayformat.format(convertForum.getUpload_datetime()));
            else
                forumHolder.uploadTime.setText(timeFormat.format(convertForum.getUpload_datetime()));

        }



        private class ForumHolder extends RecyclerView.ViewHolder{

            TextView title;
            TextView commentNumber;
            TextView nickname;
            TextView views;
            TextView likes;
            TextView unlikes;
            TextView uploadTime;
            ImageView bestIcon;

            private ForumHolder(View view){
                super(view);
                title = (TextView)view.findViewById(R.id.title_content_preview);
                commentNumber = (TextView)view.findViewById(R.id.comment_number_preview);
                nickname = (TextView)view.findViewById(R.id.nickname_preview);
                views = (TextView)view.findViewById(R.id.views_number_preview);
                likes = (TextView)view.findViewById(R.id.likes_number_preview);
                unlikes = (TextView)view.findViewById(R.id.unlikes_number_preview);
                uploadTime = (TextView)view.findViewById(R.id.upload_time_preview);
                bestIcon = (ImageView)view.findViewById(R.id.best_icon_preview);
            }

        }

    }

    class SearchLoadTask extends AsyncTask<String,Void, JSONArray>{
        JSONArray jsonArray = null;
        @Override
        protected JSONArray doInBackground(String... strings) {
            String condition = strings[0];
            String onlyBest = strings[1];
            String text = strings[2];
            String data ="";
            String postParameters = "condition="+condition+"&onlyBest="+onlyBest+"&text="+text;
            try{
                URL serverUrl = new URL("http://13.124.99.123/forum_search_load.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)serverUrl.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("SearchLoadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while((line=bufferedReader.readLine())!=null)
                    stringBuffer.append(line+"\n");

                data = stringBuffer.toString();
                bufferedReader.close();

            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                jsonArray = new JSONArray(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                if(jsonArray.length()==0) {
                    //검색 결과가 없는 경우  게시물을 1만개씩 잘라 불러올 경우 불러오기 버튼 넣어야함
                    OrangeToast(getApplicationContext(),"검색 결과 없음");
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Forum forum = new Forum(Integer.parseInt(jsonObject.getString("num")),
                            jsonObject.getString("id"),
                            jsonObject.getString("title"),
                            jsonObject.getString("content"),
                            jsonObject.getString("nickname"),
                            datetimeFormat.parse(jsonObject.getString("upload_datetime")),
                            Integer.parseInt(jsonObject.getString("views")),
                            Integer.parseInt(jsonObject.getString("likes")),
                            Integer.parseInt(jsonObject.getString("unlikes")),
                            Integer.parseInt(jsonObject.getString("comments")),
                            jsonObject.getString("like_select"),
                            jsonObject.getString("unlike_select"));
                    forumList.add(forum);

                }
                searchList.setAdapter(new ForumListAdapter(getApplicationContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getApplicationContext());
                searchList.setLayoutManager(layoutManager);


                searchList.addOnItemTouchListener(
                        new RecyclerItemClickListener(getApplicationContext(), searchList, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                startMyTask(new ViewsUploadTask(),position+"",forumList.get(position).getNum()+"");
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
                );

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
                    Intent AnonymousForumView = new Intent(getApplicationContext(),AnonymousForumActivity_View.class);
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

    public void InitialSettting(){
        backButton = (ImageButton)findViewById(R.id.search_back_button);
        searchClassificationLayout = (RelativeLayout)findViewById(R.id.anonymous_forum_search_classification_layout);
        searchClassificationText = (TextView)findViewById(R.id.anonymous_forum_search_classification_text);

        searchOnlyBestCheckBox = (CheckBox)findViewById(R.id.anonymous_forum_search_only_best_checkbox);
        searchOnlyBestText = (TextView)findViewById(R.id.anonymous_forum_search_only_best_text);

        searchEditText =  (EditText)findViewById(R.id.anonymous_forum_search_text);
        searchTextEraseLayout = (RelativeLayout)findViewById(R.id.anonymous_forum_search_text_erase_layout);
        searchTextEraseButton = (ImageView)findViewById(R.id.anonymous_forum_search_text_erase_button);
        searchButton = (ImageButton)findViewById(R.id.anonymous_forum_search_button);

        searchList = (RecyclerView)findViewById(R.id.anonymous_forum_search_list);
    }
}
