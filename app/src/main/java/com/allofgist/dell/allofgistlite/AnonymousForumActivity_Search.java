package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;


public class AnonymousForumActivity_Search extends AppCompatActivity {

    private String id;
    ProgressBar progressBar;

    ImageButton backButton;
    RelativeLayout searchClassificationLayout;
    TextView searchClassificationText;

    Switch searchOnlyBestCheckBox;
    TextView searchOnlyBestText;

    EditText searchEditText;
    RelativeLayout searchTextEraseLayout;
    ImageView searchTextEraseButton;
    ImageButton searchButton;

    RecyclerView searchList;
    private ArrayList<Forum> forumList;
    String ID_TAG = "";

    LinearLayout noResultNotice;

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    SimpleDateFormat monthdayformat = new SimpleDateFormat("MM.dd",Locale.KOREA);



    SearchIdLoadTask searchIdLoadTask;
    ViewsUploadTask viewsUploadTask;

    /*@Override
    public void onPause() {
        int count = searchList.getChildCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) searchList.getChildAt(i);
                int childSize = viewGroup.getChildCount();
                for (int j = 0; j < childSize; j++) {
                    if (viewGroup.getChildAt(j) instanceof ImageView) {
                        ((ImageView) viewGroup.getChildAt(j)).setImageBitmap(null);
                    }
                    else if (viewGroup.getChildAt(j) instanceof TextView) {
                        ((TextView) viewGroup.getChildAt(j)).setText(null);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        searchIdLoadTask.cancel(false);
        viewsUploadTask.cancel(false);

        forumList = null;
        super.onPause();
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_search);

        id = getIntent().getStringExtra("ID");

        InitialSettting();

        searchClassificationText.setText("전체");
        searchTextEraseLayout.setVisibility(View.GONE);

        //게시글에서 작성글 검색으로 들어오는 경우
        try {
            ID_TAG = getIntent().getStringExtra("ID_TAG");
            if (!getIntent().getStringExtra("NICKNAME").isEmpty()&&!ID_TAG.equals("")) {
                searchClassificationText.setText(R.string.writer_search);
                searchEditText.setText(getIntent().getStringExtra("NICKNAME"));
                forumList = new ArrayList<Forum>();
                searchIdLoadTask = new SearchIdLoadTask();
                searchIdLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ID_TAG);
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
                if (searchOnlyBestCheckBox.isChecked())
                    onlyBest = 1;
                else
                    onlyBest = 0;
                if(searchEditText.getText().toString().equals("")||searchEditText.getText().length()==0){
                    GrayToast(getApplicationContext(),"한글자 이상의 검색어를 입력해주세요.");
                }
                try{
                    if (!ID_TAG.equals("")) {
                        searchIdLoadTask = new SearchIdLoadTask();
                        searchIdLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchEditText.getText().toString(), ID_TAG);
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                    switch (searchClassificationText.getText().toString()) {
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
                    if (searchEditText.getText().toString().isEmpty())
                        GrayToast(getApplicationContext(), "검색어를 입력해주세요.");
                    else {
                        forumList = new ArrayList<Forum>();
                        progressBar.setVisibility(View.VISIBLE);
                        searchIdLoadTask = new SearchIdLoadTask();
                        searchIdLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, condition, onlyBest + "", searchEditText.getText().toString());
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
            if(forumList.size()==0) {
                noResultNotice.setVisibility(View.VISIBLE);
            }
            else
                noResultNotice.setVisibility(View.GONE);

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
                URL serverUrl = new URL("https://server.allofgist.com/forum_search_load.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("SearchLoadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

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
                if(progressBar.getVisibility()==View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
                searchList.setAdapter(new ForumListAdapter(getApplicationContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getApplicationContext());
                searchList.setLayoutManager(layoutManager);


                searchList.addOnItemTouchListener(
                        new RecyclerItemClickListener(getApplicationContext(), searchList, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                viewsUploadTask = new ViewsUploadTask();
                                viewsUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,position+"",forumList.get(position).getNum()+"");
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
                );

            }catch (NullPointerException e){
                e.printStackTrace();
                //검색 결과가 없는 경우  게시물을 1만개씩 잘라 불러올 경우 불러오기 버튼 넣어야함
                GrayToast(getApplicationContext(), "검색 결과 없음");

            }catch (Exception e){
                e.printStackTrace();
                GrayToast(getApplicationContext(),"게시판 업로드 에러!");
            }
            if(progressBar.getVisibility()==View.VISIBLE)
                progressBar.setVisibility(View.GONE);
        }
    }

    class SearchIdLoadTask extends AsyncTask<String,Void, JSONArray>{
        JSONArray jsonArray = null;

        @Override
        protected JSONArray doInBackground(String... strings) {
            String id = strings[0];
            String data ="";
            String postParameters = "id="+id;
            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_search_id_load.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("SearchLoadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while((line=bufferedReader.readLine())!=null) {
                    if(isCancelled())
                        return jsonArray;
                    stringBuffer.append(line + "\n");
                }

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
                if(progressBar.getVisibility()==View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
                searchList.setAdapter(new ForumListAdapter(getApplicationContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getApplicationContext());
                searchList.setLayoutManager(layoutManager);


                searchList.addOnItemTouchListener(
                        new RecyclerItemClickListener(getApplicationContext(), searchList, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                viewsUploadTask = new ViewsUploadTask();
                                viewsUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,position+"",forumList.get(position).getNum()+"");
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
                );

            }catch (NullPointerException e){
                e.printStackTrace();
                //검색 결과가 없는 경우  게시물을 1만개씩 잘라 불러올 경우 불러오기 버튼 넣어야함
                GrayToast(getApplicationContext(), "검색 결과 없음");

            }catch (Exception e){
                e.printStackTrace();
                GrayToast(getApplicationContext(),"게시판 업로드 에러!");
            }
            if(progressBar.getVisibility()==View.VISIBLE)
                progressBar.setVisibility(View.GONE);

            ID_TAG="";
        }
    }

    class ViewsUploadTask extends AsyncTask<String,Void,JSONArray>{

        JSONArray jsonArray = null;
        @Override
        protected JSONArray doInBackground(String... strings) {

            String data = "";

            try{
                String forumPosition = strings[0];
                String forumNum = strings[1];
                String params = "num="+forumNum+"&forumPosition="+forumPosition;
                URL serverUrl = new URL("https://server.allofgist.com/views_upload.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(params.getBytes("euc-kr"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("viewsUploadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }else{
                    inputStream = httpsURLConnection.getErrorStream();
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while((line = bufferedReader.readLine())!=null){
                    stringBuffer.append(line);
                    if(isCancelled())
                        return jsonArray;
                }

                data = stringBuffer.toString().trim();

                Log.e("RECV data",data);

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
            if(jsonArray.length()==0){
                Log.e("Views","upload views fail!");
                GrayToast(getApplicationContext(),"게시글 불러오기를 실패하였습니다.");
            }
            else{
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int forumPosition = Integer.parseInt(jsonObject.getString("forumPosition"));
                    Forum uploadForum = new Forum(Integer.parseInt(jsonObject.getString("num")),
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
                    forumList.set(forumPosition,uploadForum);

                    Intent AnonymousForumView = new Intent(AnonymousForumActivity_Search.this,AnonymousForumActivity_View.class);
                    AnonymousForumView.putExtra("ForumLoadData",forumList.get(forumPosition));
                    AnonymousForumView.putExtra("ID",id);
                    startActivity(AnonymousForumView);
                }catch (Exception e){
                    e.printStackTrace();
                    GrayToast(AnonymousForumActivity_Search.this,"게시글 불러오기를 실패하였습니다.");
                }
            }
        }
    }


    public void InitialSettting(){
        backButton = (ImageButton)findViewById(R.id.search_back_button);
        searchClassificationLayout = (RelativeLayout)findViewById(R.id.anonymous_forum_search_classification_layout);
        searchClassificationText = (TextView)findViewById(R.id.anonymous_forum_search_classification_text);

        searchOnlyBestCheckBox = (Switch)findViewById(R.id.anonymous_forum_search_only_best_checkbox);
        searchOnlyBestText = (TextView)findViewById(R.id.anonymous_forum_search_only_best_text);

        searchEditText =  (EditText)findViewById(R.id.anonymous_forum_search_text);
        searchTextEraseLayout = (RelativeLayout)findViewById(R.id.anonymous_forum_search_text_erase_layout);
        searchTextEraseButton = (ImageView)findViewById(R.id.anonymous_forum_search_text_erase_button);
        searchButton = (ImageButton)findViewById(R.id.anonymous_forum_search_button);

        searchList = (RecyclerView)findViewById(R.id.anonymous_forum_search_list);

        progressBar = (ProgressBar)findViewById(R.id.anonymous_forum_search_progress_bar);
        progressBar.setVisibility(View.GONE);

        noResultNotice = (LinearLayout)findViewById(R.id.no_search_result_notice);
        noResultNotice.setVisibility(View.GONE);

        searchIdLoadTask = new SearchIdLoadTask();
        viewsUploadTask = new ViewsUploadTask();
    }




    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

}
