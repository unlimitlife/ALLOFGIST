package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


public class AnonymousForum_BestviewFragment extends Fragment {

    private String id;

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    SimpleDateFormat monthdayformat = new SimpleDateFormat("MM.dd",Locale.KOREA);


    RecyclerView contentList;

    private ArrayList<Forum> forumList;


    public AnonymousForum_BestviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_anonymous_forum_preview,container,false);

        try {
            id = getArguments().getString("ID");
        }catch (Exception e){
            OrangeToast(getContext(),"회원 정보 불러오기를 실패하였습니다.");
        }
        initialSetting(rootView);

        forumList = new ArrayList<Forum>();
        startMyTask(new ForumLoadTask(),"");


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
            try{
                URL serverUrl = new URL("http://13.124.99.123/forum_best_load.php");
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

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line+"\n");
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
            try{

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
                contentList.setAdapter(new ForumListAdapter(getContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getContext());
                contentList.setLayoutManager(layoutManager);


                contentList.addOnItemTouchListener(
                        new RecyclerItemClickListener(getContext(), contentList, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                startMyTask(new AnonymousForum_BestviewFragment.ViewsUploadTask(),position+"",forumList.get(position).getNum()+"");
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
                );

            }catch (Exception e){
                e.printStackTrace();
                OrangeToast(getContext(),"게시판 업로드 에러!");
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
                OrangeToast(getContext(),"게시글 불러오기를 실패하였습니다.");
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
                    OrangeToast(getContext(),"게시글 불러오기를 실패하였습니다.");
                }
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


    public void initialSetting(View rootView){
        contentList = (RecyclerView)rootView.findViewById(R.id.anonymous_forum_preview_content_list);
    }
}
