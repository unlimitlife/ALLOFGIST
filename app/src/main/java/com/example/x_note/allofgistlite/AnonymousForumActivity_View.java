package com.example.x_note.allofgistlite;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

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

public class AnonymousForumActivity_View extends AppCompatActivity {

    String id;
    Forum currentForum;

    ImageButton backButton;
    ImageButton moreButton;
    TextView topTitle;

    TextView title;
    TextView nickname;
    TextView upload_datetime;
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.KOREA);
    TextView viewsAndComments;
    ImageButton zoomOut;
    ImageButton zoomIn;

    TextView content;
    TextView nicknameBottom;
    TextView nicknameSearch;

    LinearLayout likeButton;
    ImageView likeIcon;
    TextView likeText;
    int likeClick=0;
    LinearLayout unlikeButton;
    ImageView unlikeIcon;
    TextView unlikeText;
    int unlikeClick=0;
    LinearLayout commentButton;

    LinearLayout stateLayout;
    ImageView likeStateIcon;
    TextView likeState;
    ImageView unlikeStateIcon;
    TextView unlikeState;
    TextView nonelikeState;


    RecyclerView commentList;
    private ArrayList<Comment> forumCommentList;

    EditText commentInput;
    ImageButton commentInputButton;

    ScrollView scrollView;
    LinearLayout fullScreen;
    LinearLayout commentInputLayout;


    @Override
    protected void onResume() {
        super.onResume();
        forumCommentList = new ArrayList<Comment>();
        startMyTask(new CommentLoadTask(),currentForum.getNum()+"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum__view);

        id = getIntent().getStringExtra("ID");
        try {
            currentForum = getIntent().getExtras().getParcelable("ForumLoadData");
        }catch (Exception e){
            e.printStackTrace();
        }

        InitialSetting();

        //좋아요, 싫어요 눌렀는 지 확인
        String[] likeSelectSplit = currentForum.getLike_select().split(",");
        String[] unlikeSelectSplit = currentForum.getUnlike_select().split(",");
        for (int i=0;i<likeSelectSplit.length;i++){
            if(likeSelectSplit[i].equals(id)){
                likeIcon.setSelected(true);
                likeText.setTextColor(Color.parseColor("#FF7F00"));
                likeClick++;
                break;
            }
        }

        for (int i=0;i<unlikeSelectSplit.length;i++){
            if(unlikeSelectSplit[i].equals(id)){
                unlikeIcon.setSelected(true);
                unlikeText.setTextColor(Color.parseColor("#FF7F00"));
                unlikeClick++;
                break;
            }
        }

//        DataBinding(currentForum);

        /*scrollView.setOnTouchListener(new View.OnTouchListener(){
            private float initialY, finalY;



            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        initialY = event.getY();
                        commentInput.setHint("initialY : " + initialY);
                        break;
                    case MotionEvent.ACTION_UP :
                        finalY = fullScreen.getHeight() - event.getY();
                        commentInput.setHint("initialY : " + initialY +"finalY : " + finalY);

                        if(finalY>initialY)
                            commentInputLayout.setVisibility(View.GONE);
                        else
                            commentInputLayout.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_MOVE :

                }
                return false;
            }
        });*/


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spValue = pixtosp(getApplicationContext(),content.getTextSize());
                if(spValue > 15){
                    content.setTextSize(TypedValue.COMPLEX_UNIT_SP,spValue-3);
                    zoomOut.setImageDrawable(getDrawable(R.drawable.zoom_out_orange));
                    zoomIn.setImageDrawable(getDrawable(R.drawable.zoom_in_orange));
                    if(spValue==18)
                        zoomOut.setImageDrawable(getDrawable(R.drawable.zoom_out_gray));
                }

            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spValue = pixtosp(getApplicationContext(),content.getTextSize());
                if(spValue < 24){
                    content.setTextSize(TypedValue.COMPLEX_UNIT_SP,spValue+3);
                    zoomIn.setImageDrawable(getDrawable(R.drawable.zoom_in_orange));
                    zoomOut.setImageDrawable(getDrawable(R.drawable.zoom_out_orange));
                    if(spValue==21)
                        zoomIn.setImageDrawable(getDrawable(R.drawable.zoom_in_gray));
                }

            }
        });

        nicknameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMyTask(new LikeUploadTask(),(currentForum.getNum()+""),id,((likeClick%2)+""));

            }
        });

        unlikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMyTask(new UnlikeUploadTask(),(currentForum.getNum()+""),id,((unlikeClick%2)+""));

            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        commentInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentInput.getText().toString().equals(""))
                    OrangeToast(getApplicationContext(),"댓글을 입력해주세요.");
                else{
                    String num = currentForum.getNum()+"";
                    String depth_input ="0";
                    String num_group_input ="0";
                    String id_input=id;
                    String content_input = commentInput.getText().toString();
                    String upload_datetime_input = datetimeFormat.format(Calendar.getInstance().getTime());
                    startMyTask(new CommentInsertTask(),num,depth_input,num_group_input,id_input,content_input,upload_datetime_input);
                }
            }
        });


    }
    //스크롤 새로 고침 필요
    public void DataBinding(Forum forum) {
        topTitle.setText(forum.getTitle());

        title.setText(forum.getTitle());
        nickname.setText(forum.getNickname());
        upload_datetime.setText(datetimeFormat.format(forum.getUpload_datetime()));
        viewsAndComments.setText(("조회 " + forum.getViews() + " 댓글 " + forum.getComments()));

        content.setText(forum.getContent());
        nicknameBottom.setText((forum.getNickname() + " 님의"));

        if(forum.getUnlikes()==0&&forum.getLikes()==0){
            stateLayout.setVisibility(View.GONE);
            nonelikeState.setVisibility(View.VISIBLE);
        }else {
            stateLayout.setVisibility(View.VISIBLE);
            nonelikeState.setVisibility(View.GONE);
            if (forum.getLikes() > 0) {
                likeStateIcon.setVisibility(View.VISIBLE);
                likeState.setVisibility(View.VISIBLE);
                likeState.setText((forum.getLikes() + "개"));
            } else {
                likeStateIcon.setVisibility(View.GONE);
                likeState.setVisibility(View.GONE);
            }
            if (forum.getUnlikes() > 0) {
                unlikeStateIcon.setVisibility(View.VISIBLE);
                unlikeState.setVisibility(View.VISIBLE);
                unlikeState.setText((forum.getUnlikes() + "개"));
            } else {
                unlikeStateIcon.setVisibility(View.GONE);
                unlikeState.setVisibility(View.GONE);
            }
        }

    }

    public class ForumCommentListAdapter extends RecyclerView.Adapter<ForumCommentListAdapter.CommentHolder>{

        ForumCommentListAdapter.CommentHolder commentHolder;
        private Context context;

        public ForumCommentListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            try {
                return forumCommentList.size();
            }catch (NullPointerException e){
                e.printStackTrace();
                return 0;
            }
        }

        @NonNull
        @Override
        public ForumCommentListAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View view = null;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem_anonymous_forum_view, parent, false);

            commentHolder = new ForumCommentListAdapter.CommentHolder(view);

            return commentHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ForumCommentListAdapter.CommentHolder commentHolder, int position) {

            Comment currentComment = forumCommentList.get(position);
            //원글에 대한 답글일 경우 (depth = 0)
            if(currentComment.getDepth()==0){
                commentHolder.nextCommentLayout.setVisibility(View.GONE);
                commentHolder.commentLayout.setVisibility(View.VISIBLE);
                commentHolder.commentNickname.setText(currentComment.getNickname());
                commentHolder.commentContent.setText(currentComment.getContent());
                commentHolder.commentUploadDatetime.setText(currentComment.getUpload_datetime());
            }
            //답글에 대한 답글일 경우 (depth = 1)
            else{
                commentHolder.nextCommentLayout.setVisibility(View.VISIBLE);
                commentHolder.commentLayout.setVisibility(View.GONE);
                commentHolder.nextCommentNickname.setText(currentComment.getNickname());
                commentHolder.nextCommentContent.setText(currentComment.getContent());
                commentHolder.nextCommentUploadDatetime.setText(currentComment.getUpload_datetime());
            }
            /*
            Forum convertForum = forumCommentList.get(position);
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
                forumHolder.uploadTime.setText(timeFormat.format(convertForum.getUpload_datetime()));*/

        }



        public class CommentHolder extends RecyclerView.ViewHolder{


            LinearLayout commentLayout;
            TextView commentNickname;
            ImageButton commentMoreButton;
            TextView commentContent;
            TextView commentUploadDatetime;

            LinearLayout nextCommentLayout;
            TextView nextCommentNickname;
            ImageButton nextCommentMoreButton;
            TextView nextCommentContent;
            TextView nextCommentUploadDatetime;

            public CommentHolder(View view){
                super(view);
                commentLayout = (LinearLayout)view.findViewById(R.id.comment_layout);
                commentNickname = (TextView)view.findViewById(R.id.anonymous_forum_comment_nickname);
                commentMoreButton = (ImageButton)view.findViewById(R.id.anonymous_forum_comment_more_button);
                commentContent = (TextView)view.findViewById(R.id.anonymous_forum_comment_content);
                commentUploadDatetime = (TextView)view.findViewById(R.id.anonymous_forum_comment_uplaod_datetime);


                nextCommentLayout = (LinearLayout)view.findViewById(R.id.next_comment_layout);
                nextCommentNickname = (TextView)view.findViewById(R.id.anonymous_forum_next_comment_nickname);
                nextCommentMoreButton = (ImageButton)view.findViewById(R.id.anonymous_forum_next_comment_more_button);
                nextCommentContent = (TextView)view.findViewById(R.id.anonymous_forum_next_comment_content);
                nextCommentUploadDatetime = (TextView)view.findViewById(R.id.anonymous_forum_next_comment_uplaod_datetime);
            }

        }

    }


    class LikeUploadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {

            String num = strings[0];
            String id = strings[1];
            String remain =  strings[2];
            String data = "";

            String postParameters = "num="+num+"&id="+id+"&remain="+remain;

            try{
                URL serverUrl = new URL("http://13.124.99.123/forum_like_upload.php");

                HttpURLConnection httpURLConnection =  (HttpURLConnection)serverUrl.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("LikeUploadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                    stringBuffer.append(line);

                data = stringBuffer.toString().trim();
                return data;

            }catch (Exception e){
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            String[] splitString = result.split("/");
            if (splitString[0].equals("OK")){
                try {
                    currentForum.setLikes(Integer.parseInt(splitString[1]));
                    if (likeClick % 2 == 0) {
                        likeIcon.setSelected(true);
                        likeText.setTextColor(Color.parseColor("#FF7F00"));
                        likeClick++;
                    } else {
                        likeIcon.setSelected(false);
                        likeText.setTextColor(Color.parseColor("#585858"));
                        likeClick++;
                    }
                    DataBinding(currentForum);
                }catch (Exception e) {
                    OrangeToast(getApplicationContext(),"Error : "+e.toString());
                }

            }
            else{
                OrangeToast(getApplicationContext(),"like upload server Error");
            }
        }
    }

    class UnlikeUploadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {

            String num = strings[0];
            String id = strings[1];
            String remain =  strings[2];
            String data = "";

            String postParameters = "num="+num+"&id="+id+"&remain="+remain;

            try{
                URL serverUrl = new URL("http://13.124.99.123/forum_unlike_upload.php");

                HttpURLConnection httpURLConnection =  (HttpURLConnection)serverUrl.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("LikeUploadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                    stringBuffer.append(line);

                data = stringBuffer.toString().trim();
                return data;

            }catch (Exception e){
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            String[] splitString = result.split("/");
            if (splitString[0].equals("OK")){
                try {
                    currentForum.setUnlikes(Integer.parseInt(splitString[1]));
                    if(unlikeClick%2==0) {
                        unlikeIcon.setSelected(true);
                        unlikeText.setTextColor(Color.parseColor("#FF7F00"));
                        unlikeClick++;
                    }
                    else{
                        unlikeIcon.setSelected(false);
                        unlikeText.setTextColor(Color.parseColor("#585858"));
                        unlikeClick++;
                    }
                    DataBinding(currentForum);
                }catch (Exception e) {
                    OrangeToast(getApplicationContext(),"Error : "+e.toString());
                }

            }
            else{
                OrangeToast(getApplicationContext(),"unlike upload server Error");
            }
        }
    }

    class CommentInsertTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {

            String num = strings[0];
            String depth =strings[1];
            String num_group =strings[2];
            String id=strings[3];
            String content = strings[4];
            String upload_datetime = strings[5];

            String data = "";

            String postParameters = "num="+num+"&depth="+depth+"&num_group="+num_group+"&id="+id+"&content="+content+"&upload_datetime="+upload_datetime;

            try{
                URL serverUrl = new URL("http://13.124.99.123/forum_comment_insert.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) serverUrl.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("CommentInsertTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line =  null;
                StringBuffer stringBuffer = new StringBuffer();
                while((line=bufferedReader.readLine())!=null){
                    stringBuffer.append(line);
                }
                data = stringBuffer.toString().trim();

                return data;

            }catch (Exception e){
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK")){
                forumCommentList = new ArrayList<Comment>();
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
                commentInput.setText("");
                startMyTask(new CommentLoadTask(),currentForum.getNum()+"");

            }
            else
                OrangeToast(getApplicationContext(),"서버 연결을 실패하였습니다.");
        }
    }

    class CommentLoadTask extends AsyncTask<String,Void,JSONArray>{

        JSONArray jArray = null;

        @Override
        protected JSONArray doInBackground(String... strings) {

            String num = strings[0];
            String postParameter = "num="+num;
            String data = "";

            try{
                URL serverUrl = new URL("http://13.124.99.123/forum_comment_load.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)serverUrl.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("CommentLoadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while((line =  bufferedReader.readLine())!=null)
                    stringBuffer.append(line+"\n");

                data = stringBuffer.toString();

            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                jArray = new JSONArray(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return jArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                //댓글을 제외한 게시판 데이터 불러오기
                DataBinding(currentForum);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Comment currentComment = new Comment(Integer.parseInt(jsonObject.getString("num")),
                            Integer.parseInt(jsonObject.getString("depth")),
                            Integer.parseInt(jsonObject.getString("num_group")),
                            jsonObject.getString("id"),
                            jsonObject.getString("nickname"),
                            jsonObject.getString("content"),
                            jsonObject.getString("upload_datetime"));
                    forumCommentList.add(currentComment);
                }
                commentList.setAdapter(new ForumCommentListAdapter(getApplicationContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(AnonymousForumActivity_View.this);
                commentList.setLayoutManager(layoutManager);

           }catch (Exception e){
                e.printStackTrace();
                OrangeToast(getApplicationContext(),"댓글 데이터 불러오기를 실패하였습니다.");
            }
        }
    }


    public void InitialSetting(){
        backButton = (ImageButton)findViewById(R.id.anonymous_forum_view_back_button);
        moreButton = (ImageButton)findViewById(R.id.anonymous_forum_view_more_button);
        topTitle = (TextView)findViewById(R.id.anonymous_forum_view_title_top);

        title = (TextView)findViewById(R.id.anonymous_forum_view_title);
        nickname = (TextView)findViewById(R.id.anonymous_forum_view_nickname);
        upload_datetime = (TextView)findViewById(R.id.anonymous_forum_view_upload_datetime);
        viewsAndComments = (TextView)findViewById(R.id.anonymous_forum_view_views_comments);
        zoomOut = (ImageButton)findViewById(R.id.anonymous_forum_view_zoom_out);
        zoomIn = (ImageButton)findViewById(R.id.anonymous_forum_view_zoom_in);

        content = (TextView)findViewById(R.id.anonymous_forum_view_content);
        nicknameBottom = (TextView)findViewById(R.id.anonymous_forum_view_nickname_bottom);
        nicknameSearch = (TextView)findViewById(R.id.anonymous_forum_view_nickname_search);

        likeButton = (LinearLayout)findViewById(R.id.anonymous_forum_view_like);
        likeIcon = (ImageView)findViewById(R.id.anonymous_forum_view_like_icon);
        likeText = (TextView)findViewById(R.id.anonymous_forum_view_like_text);
        unlikeButton = (LinearLayout)findViewById(R.id.anonymous_forum_view_unlike);
        unlikeIcon = (ImageView) findViewById(R.id.anonymous_forum_view_unlike_icon);
        unlikeText = (TextView)findViewById(R.id.anonymous_forum_view_unlike_text);
        commentButton = (LinearLayout)findViewById(R.id.anonymous_forum_view_comment);

        likeStateIcon = (ImageView)findViewById(R.id.anonymous_forum_view_likes_state_image);
        likeState = (TextView)findViewById(R.id.anonymous_forum_view_likes_state);
        unlikeStateIcon = (ImageView)findViewById(R.id.anonymous_forum_view_unlikes_state_image);
        unlikeState = (TextView)findViewById(R.id.anonymous_forum_view_unlikes_state);
        stateLayout = (LinearLayout)findViewById(R.id.anonymous_forum_view_state_layout);
        nonelikeState = (TextView)findViewById(R.id.anonymous_forum_view_none_state_textview);

        commentList = (RecyclerView)findViewById(R.id.anonymous_forum_view_comment_list);

        commentInput = (EditText)findViewById(R.id.anonymous_forum_view_comment_input);
        commentInputButton = (ImageButton)findViewById(R.id.anonymous_forum_view_comment_input_button);

        scrollView = (ScrollView)findViewById(R.id.anonymous_forum_view_scroll_view);
        fullScreen = (LinearLayout)findViewById(R.id.anonymous_forum_view_fullscreen);
        commentInputLayout = (LinearLayout)findViewById(R.id.anonymous_forum_view_comment_input_layout);
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

    public static int pixtosp(Context context, float pixel){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pixel/fontScale + 0.5f);
    }

    public static int sptopix(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale);
    }

}
