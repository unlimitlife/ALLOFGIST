package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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



public class AnonymousForum_BestviewFragment extends Fragment {

    private String id;
    //ProgressBar progressBar;

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    SimpleDateFormat monthdayformat = new SimpleDateFormat("yy.MM.dd",Locale.KOREA);

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView contentList;

    private ArrayList<Forum> forumList;
    LinearLayoutManager layoutManager;
    ForumListAdapter forumListAdapter;


    ForumLoadTask forumLoadTask;
    ViewsUploadTask viewsUploadTask;


    public AnonymousForum_BestviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        int count = contentList.getChildCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) contentList.getChildAt(i);
                int childSize = viewGroup.getChildCount();
                for (int j = 0; j < childSize; j++) {
                    if (viewGroup.getChildAt(j) instanceof TextView) {
                        ((TextView) viewGroup.getChildAt(j)).setText(null);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        forumLoadTask.cancel(false);
        viewsUploadTask.cancel(false);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                forumList = new ArrayList<Forum>();
                forumLoadTask = new ForumLoadTask();
                forumLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1+"");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_anonymous_forum_preview,container,false);

        try {
            id = getArguments().getString("ID");
        }catch (Exception e){
            GrayToast(getContext(),"회원 정보 불러오기를 실패하였습니다.");
        }
        initialSetting(rootView);

        forumList = new ArrayList<Forum>();
        layoutManager = new LinearLayoutManager(getContext());
        forumListAdapter = new ForumListAdapter(getContext());

        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark
        );


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                forumList = new ArrayList<Forum>();
                contentList.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int current_page) {
                        forumLoadTask = new ForumLoadTask();
                        forumLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,current_page+"");
                    }
                });

                forumLoadTask = new ForumLoadTask();
                forumLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1+"");
            }
        });

        contentList.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                forumLoadTask = new ForumLoadTask();
                forumLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,current_page+"");
            }
        });

        contentList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), contentList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        viewsUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,position+"",forumList.get(position).getNum()+"");
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );


        /*if(!swipeRefreshLayout.isRefreshing())
            progressBar.setVisibility(View.VISIBLE);*/
        //forumLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1+"");

        return rootView;

    }


    public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ForumHolder>{

        ForumListAdapter.ForumHolder forumHolder;
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

            view = LayoutInflater.from(getContext()).inflate(R.layout.recyclerviewitem_anonymous_forum_preview, parent, false);

            forumHolder = new ForumListAdapter.ForumHolder(view);

            return forumHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ForumListAdapter.ForumHolder forumHolder, int position) {

            Forum convertForum = forumList.get(position);
            forumHolder.title.setText(convertForum.getTitle());
            if(convertForum.getComments()>0) {
                forumHolder.commentNumber.setText((convertForum.getComments()+""));
                forumHolder.commentNumber.setVisibility(View.VISIBLE);
            }
            else
                forumHolder.commentNumber.setVisibility(View.GONE);
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



        private class ForumHolder extends RecyclerView.ViewHolder{

            TextView title;
            TextView commentNumber;
            TextView nickname;
            TextView views;
            TextView likes;
            TextView unlikes;
            TextView uploadTime;

            private ForumHolder(View view){
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



    class ForumLoadTask extends AsyncTask<String,Void,JSONArray> {

        JSONArray jsonArray = null;
        @Override
        protected JSONArray doInBackground(String... strings) {
            String data ="";
            String page = strings[0];
            String postParameter = "page="+page;
            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_best_load.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();

                Thread.sleep(300);
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("phptest", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == httpsURLConnection.HTTP_OK) {
                    inputStream = httpsURLConnection.getInputStream();
                } else {
                    inputStream = httpsURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line+"\n");
                    if(forumLoadTask.isCancelled())
                        return jsonArray;
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                jsonArray =  new JSONArray(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Boolean isFirstPage = false;
            try{
                if(forumList.isEmpty())
                    isFirstPage = true;
                else
                    isFirstPage = false;

                for (int i = 0; i<jsonArray.length(); i++){
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
                if(isFirstPage) {
                    contentList.setAdapter(forumListAdapter);
                    contentList.setLayoutManager(layoutManager);
                    /*if(progressBar.getVisibility()==View.VISIBLE)
                        progressBar.setVisibility(View.GONE);*/
                    if(swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    /*if(progressBar.getVisibility()==View.VISIBLE)
                        progressBar.setVisibility(View.GONE);*/
                    forumListAdapter.notifyDataSetChanged();
                }

            }catch (Exception e){
                e.printStackTrace();
                GrayToast(getContext(),"게시판 업로드 에러!");
            }

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
                    if(viewsUploadTask.isCancelled())
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
                GrayToast(getContext(),"게시글 불러오기를 실패하였습니다.");
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

                    Intent AnonymousForumView = new Intent(getActivity(),AnonymousForumActivity_View.class);
                    AnonymousForumView.putExtra("ForumLoadData",forumList.get(forumPosition));
                    AnonymousForumView.putExtra("ID",id);
                    startActivity(AnonymousForumView);
                }catch (Exception e){
                    e.printStackTrace();
                    GrayToast(getContext(),"게시글 불러오기를 실패하였습니다.");
                }
            }
        }
    }



    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }


    public void initialSetting(View rootView){
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.anonymous_forum_preview_swipe_layout);
        contentList = (RecyclerView)rootView.findViewById(R.id.anonymous_forum_preview_content_list);
        forumLoadTask = new ForumLoadTask();
        viewsUploadTask = new ViewsUploadTask();
        /*progressBar = (ProgressBar)rootView.findViewById(R.id.anonymous_forum_preview_progress_bar);
        progressBar.setVisibility(View.GONE);*/
    }
}
