package com.allofgist.dell.allofgistlite;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AnonymousForumActivity_View extends AppCompatActivity {

    String id;
    Forum currentForum;

    ImageButton backButton;
    ImageButton moreButton;
    TextView topTitle;

    PopupWindow noticePopupWindow;
    View popupView;

    TextView noticeText;
    TextView noticeCancel;
    TextView noticeOk;

    TextView title;
    TextView nickname;
    TextView upload_datetime;
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.KOREA);
    SimpleDateFormat datetimeFormatDot = new SimpleDateFormat("yyyy.MM.dd. HH:mm",Locale.KOREA);
    SimpleDateFormat datetimeSecFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    SimpleDateFormat datetime_SecFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.KOREA);
    TextView comments;
    ImageButton zoomOut;
    ImageButton zoomIn;

    TextView content;
    TextView nicknameBottom;
    TextView nicknameSearch;

    LinearLayout likeButton;
    ImageView likeIcon;
    //TextView likeText;
    int likeClick=0;
    LinearLayout unlikeButton;
    ImageView unlikeIcon;
    //TextView unlikeText;
    int unlikeClick=0;
    LinearLayout commentButton;

    LinearLayout stateLayout;
    ImageView likeStateIcon;
    TextView likeState;
    ImageView unlikeStateIcon;
    TextView unlikeState;
    //TextView nonelikeState;


    RecyclerView commentList;
    private ArrayList<Comment> forumCommentList;

    private boolean editmode = false;
    private Comment comment;
    private Comment currentComment;
    EditText commentInput;
    ImageButton commentInputButton;

    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;
    LinearLayout fullScreen;
    LinearLayout commentInputLayout;


    ImageView nextCommentIcon;
    LinearLayout nextCommentLayout;
    TextView nextCommentNickname;
    TextView nextCommentNicknameExtra;
    ImageButton nextCommentCloseButton;
    View nextCommentLayoutBorderLine;


    CommentDeleteTask commentDeleteTask;
    CommentEditTask commentEditTask;
    CommentInsertTask commentInsertTask;
    CommentLoadTask commentLoadTask;

    ForumReloadTask forumReloadTask;
    ForumDeleteTask forumDeleteTask;

    LikeUploadTask likeUploadTask;
    UnlikeUploadTask unLikeUploadTask;

    @Override
    public void onBackPressed() {
        if(editmode)
            ShowCancelEditMode();
        else {
            if(nextCommentLayout.getVisibility()==View.VISIBLE){
                nextCommentIcon.setVisibility(View.GONE);
                nextCommentLayout.setVisibility(View.GONE);
                nextCommentLayoutBorderLine.setVisibility(View.GONE);
            }
            else
                super.onBackPressed();
        }
    }

    /*@Override
    public void onPause() {
        int count = commentList.getChildCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) commentList.getChildAt(i);
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
        commentDeleteTask.cancel(false);
        commentEditTask.cancel(false);
        commentInsertTask.cancel(false);
        commentLoadTask.cancel(false);

        forumDeleteTask.cancel(false);
        forumReloadTask.cancel(false);

        likeUploadTask.cancel(false);
        unLikeUploadTask.cancel(false);

        forumCommentList = null;
        super.onPause();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        forumCommentList = new ArrayList<Comment>();
        commentLoadTask = new CommentLoadTask();
        commentLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentForum.getNum()+"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_forum_view);

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
                likeClick++;
                break;
            }
        }

        for (int i=0;i<unlikeSelectSplit.length;i++){
            if(unlikeSelectSplit[i].equals(id)){
                unlikeIcon.setSelected(true);
                unlikeClick++;
                break;
            }
        }
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark
        );  //밑에 색깔 추가하면 한바퀴 돌떄 마다 색깔 변함

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                forumCommentList = new ArrayList<Comment>();
                forumReloadTask = new ForumReloadTask();
                forumReloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentForum.getNum()+"");
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //notice popup window cancel button
        noticeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticePopupWindow.dismiss();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    if (currentForum.getId().equals(id)) {
                        PopupMenu popupMine = new PopupMenu(getApplicationContext(), v);
                        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                        menuInflater.inflate(R.menu.anonymous_forum_view_mine_popup_menu, popupMine.getMenu());
                        popupMine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mine_delete:
                                        noticeText.setText(R.string.delete_question);

                                        nextCommentIcon.setVisibility(View.GONE);
                                        nextCommentLayout.setVisibility(View.GONE);
                                        nextCommentLayoutBorderLine.setVisibility(View.GONE);

                                        noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                        noticeOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                forumDeleteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentForum.getNum() + "");
                                                noticePopupWindow.dismiss();
                                            }
                                        });
                                        break;

                                    case R.id.mine_edit:
                                        noticeText.setText(R.string.edit_question);

                                        nextCommentIcon.setVisibility(View.GONE);
                                        nextCommentLayout.setVisibility(View.GONE);
                                        nextCommentLayoutBorderLine.setVisibility(View.GONE);

                                        noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                        noticeOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent AnonymousForumWrite = new Intent(getApplicationContext(), AnonymousForumActivity_Write.class);
                                                AnonymousForumWrite.putExtra("ForumLoadData", currentForum);
                                                AnonymousForumWrite.putExtra("ID", id);
                                                noticePopupWindow.dismiss();
                                                startActivity(AnonymousForumWrite);
                                            }
                                        });
                                        break;
                                    case R.id.mine_capture:
                                        File screenShot = ScreenShot();
                                        try {
                                            if (screenShot.isFile()) {
                                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                                                GrayToast(getApplicationContext(),"[AllofGist] 폴더에 저장되었습니다.");
                                            }
                                        }catch (NullPointerException e) {
                                            e.printStackTrace();
                                            GrayToast(getApplicationContext(),"캡쳐된 파일을 찾을 수 없습니다.");
                                        }
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMine.show();
                    } else {
                        PopupMenu popupOther = new PopupMenu(getApplicationContext(), v);
                        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                        menuInflater.inflate(R.menu.anonymous_forum_view_other_popup_menu, popupOther.getMenu());
                        popupOther.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {

                                    case R.id.other_capture:
                                        View screenview = getWindow().getDecorView().getRootView();
                                        //progress bar
                                        File screenShot = ScreenShot();
                                        try {
                                            if (screenShot.isFile()) {
                                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                                                GrayToast(getApplicationContext(),"[AllofGist] 폴더에 저장되었습니다.");
                                            }
                                        }catch (NullPointerException e) {
                                            e.printStackTrace();
                                            GrayToast(getApplicationContext(),"캡쳐된 파일을 찾을 수 없습니다.");
                                        }
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupOther.show();
                    }
                }
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    int spValue = pixtosp(getApplicationContext(), content.getTextSize());
                    if (spValue > 15) {
                        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue - 3);
                        zoomOut.setImageDrawable(getDrawable(R.drawable.zoom_out_grey));
                        zoomIn.setImageDrawable(getDrawable(R.drawable.zoom_in_grey));
                        if (spValue == 18)
                            zoomOut.setImageDrawable(getDrawable(R.drawable.zoom_out_grey));
                    }
                }

            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    int spValue = pixtosp(getApplicationContext(), content.getTextSize());
                    if (spValue < 24) {
                        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue + 3);
                        zoomIn.setImageDrawable(getDrawable(R.drawable.zoom_in_grey));
                        zoomOut.setImageDrawable(getDrawable(R.drawable.zoom_out_grey));
                        if (spValue == 21)
                            zoomIn.setImageDrawable(getDrawable(R.drawable.zoom_in_grey));
                    }
                }

            }
        });

        /*nicknameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    Intent searchActivity = new Intent(AnonymousForumActivity_View.this, AnonymousForumActivity_Search.class);
                    searchActivity.putExtra("ID", id);
                    searchActivity.putExtra("NICKNAME", currentForum.getNickname());
                    searchActivity.putExtra("ID_TAG",currentForum.getId());
                    startActivity(searchActivity);
                }
            }
        });*/

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    if (unlikeClick % 2 == 0)
                        likeUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (currentForum.getNum() + ""), id, ((likeClick % 2) + ""));
                }

            }
        });

        unlikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    if (likeClick % 2 == 0)
                        unLikeUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (currentForum.getNum() + ""), id, ((unlikeClick % 2) + ""));
                }

            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    nextCommentIcon.setVisibility(View.GONE);
                    nextCommentLayout.setVisibility(View.GONE);
                    nextCommentLayoutBorderLine.setVisibility(View.GONE);

                    commentInput.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        commentInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentInput.getText().toString().equals(""))
                    GrayToast(getApplicationContext(),"댓글을 입력해주세요.");
                else{
                    if(editmode){
                        commentEditTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentComment.getNum_primary()+"",commentInput.getText().toString());
                    }
                    else {
                        String num = currentForum.getNum() + "";
                        String id_input = id;
                        String content_input = commentInput.getText().toString();
                        String upload_datetime_input = datetimeSecFormat.format(Calendar.getInstance().getTime());
                        if (nextCommentLayout.getVisibility() == View.GONE) {
                            String depth_input = "0";
                            String num_group_input = "0";
                            commentInsertTask = new CommentInsertTask();
                            commentInsertTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, num, depth_input, num_group_input,
                                    id_input, content_input, upload_datetime_input);
                        } else if (comment != null) {
                            comment.setContent(commentInput.getText().toString());
                            comment.setUpload_datetime(upload_datetime_input);

                            commentInsertTask = new CommentInsertTask();
                            commentInsertTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, comment.getNum() + "",
                                    comment.getDepth() + "", comment.getNum_group() + "", comment.getId(),
                                    comment.getContent(), comment.getUpload_datetime());
                        }
                    }
                }
            }
        });

        /*commentList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), commentList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Comment currentComment = forumCommentList.get(position);
                if(currentComment.getDepth()==0) {
                    nextCommentIcon.setVisibility(View.VISIBLE);
                    nextCommentLayout.setVisibility(View.VISIBLE);
                    nextCommentLayoutBorderLine.setVisibility(View.VISIBLE);
                    nextCommentNickname.setText(currentComment.getNickname());
                    commentInput.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                    comment = new Comment(currentForum.getNum(),1,currentComment.getNum_group(),id,"default_nickname","default_content","default_upload_datetime");
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));*/

        nextCommentCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode)
                    ShowCancelEditMode();
                else {
                    nextCommentLayout.setVisibility(View.GONE);
                    nextCommentLayoutBorderLine.setVisibility(View.GONE);
                    nextCommentIcon.setVisibility(View.GONE);

                    commentInput.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentInput.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });
    }

    public void DataBinding(Forum forum) {
        //topTitle.setText(forum.getTitle());

        title.setText(forum.getTitle());
        nickname.setText("작성자 | "+forum.getNickname());

        upload_datetime.setText(datetimeFormatDot.format(forum.getUpload_datetime())+" 작성");
        comments.setText((""+forum.getComments()));

        content.setText(forum.getContent());
        //nicknameBottom.setText((forum.getNickname() + " 님의"));

        nextCommentIcon.setVisibility(View.GONE);
        nextCommentLayout.setVisibility(View.GONE);
        nextCommentLayoutBorderLine.setVisibility(View.GONE);
        nextCommentNicknameExtra.setText("에게 답글");

        commentInput.setText("");
        editmode = false;


        likeState.setText(forum.getLikes()+"");
        unlikeState.setText(forum.getUnlikes()+"");


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
        public void onBindViewHolder(@NonNull final ForumCommentListAdapter.CommentHolder commentHolder, final int position) {

            currentComment = forumCommentList.get(position);
            //원글에 대한 답글일 경우 (depth = 0)
            if(currentComment.getDepth()==0){
                commentHolder.nextCommentLayout.setVisibility(View.GONE);
                commentHolder.commentLayout.setVisibility(View.VISIBLE);
                commentHolder.commentNickname.setText(currentComment.getNickname());
                commentHolder.commentContent.setText(currentComment.getContent());
                try {
                    commentHolder.commentUploadDatetime.setText(datetimeFormat.format(datetimeSecFormat.parse(currentComment.getUpload_datetime()))+" 작성");
                }catch (ParseException e){
                    e.printStackTrace();
                    commentHolder.commentUploadDatetime.setText(currentComment.getUpload_datetime()+" 작성");
                }
            }
            //답글에 대한 답글일 경우 (depth = 1)
            else{
                commentHolder.nextCommentLayout.setVisibility(View.VISIBLE);
                commentHolder.commentLayout.setVisibility(View.GONE);
                commentHolder.nextCommentNickname.setText(currentComment.getNickname());
                commentHolder.nextCommentContent.setText(currentComment.getContent());
                try {
                    commentHolder.nextCommentUploadDatetime.setText(datetimeFormat.format(datetimeSecFormat.parse(currentComment.getUpload_datetime()))+" 작성");
                }catch (ParseException e){
                    e.printStackTrace();
                    commentHolder.nextCommentUploadDatetime.setText(currentComment.getUpload_datetime()+" 작성");
                }
            }

            commentHolder.commentMoreButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    if(editmode)
                        ShowCancelEditMode();
                    else {
                        currentComment = forumCommentList.get(position);
                        if (currentComment.getId().equals(id)) {
                            PopupMenu popupCommentMine = new PopupMenu(getApplicationContext(), v);
                            MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                            menuInflater.inflate(R.menu.anonymous_forum_comment_mine_popup_menu, popupCommentMine.getMenu());
                            popupCommentMine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.mine_comment_write:
                                            if (currentComment.getDepth() == 0) {
                                                nextCommentIcon.setVisibility(View.VISIBLE);
                                                nextCommentLayout.setVisibility(View.VISIBLE);
                                                nextCommentLayoutBorderLine.setVisibility(View.VISIBLE);
                                                nextCommentNicknameExtra.setText("에게 답글");
                                                nextCommentNickname.setText(currentComment.getNickname());
                                                commentInput.requestFocus();
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                                                comment = new Comment(currentForum.getNum(), 1, currentComment.getNum_group(), id, "default_nickname", "default_content", "default_upload_datetime");
                                            }
                                            break;

                                        case R.id.mine_comment_delete:
                                            nextCommentIcon.setVisibility(View.GONE);
                                            nextCommentLayout.setVisibility(View.GONE);
                                            nextCommentLayoutBorderLine.setVisibility(View.GONE);
                                            noticeText.setText(R.string.delete_question);
                                            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                            noticeOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    commentDeleteTask = new CommentDeleteTask();
                                                    commentDeleteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentComment.getNum_primary() + "", currentComment.getNum() + "", currentComment.getDepth() + "", currentComment.getNum_group() + "");
                                                    noticePopupWindow.dismiss();
                                                }
                                            });
                                            break;

                                        case R.id.mine_comment_edit:
                                            nextCommentLayoutBorderLine.setVisibility(View.GONE);
                                            noticeText.setText(R.string.edit_question);
                                            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                            noticeOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    noticePopupWindow.dismiss();
                                                    commentInput.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            nextCommentIcon.setVisibility(View.VISIBLE);
                                                            nextCommentLayoutBorderLine.setVisibility(View.VISIBLE);
                                                            nextCommentLayout.setVisibility(View.VISIBLE);
                                                            nextCommentNicknameExtra.setText("");
                                                            nextCommentNickname.setText("수정 모드");
                                                            commentInput.requestFocus();
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                                                            commentInput.setText(currentComment.getContent());
                                                            editmode = true;
                                                        }
                                                    });
                                                }
                                            });
                                            break;
                                        case R.id.mine_comment_search:
                                            Intent searchActivity = new Intent(AnonymousForumActivity_View.this, AnonymousForumActivity_Search.class);
                                            searchActivity.putExtra("ID", id);
                                            searchActivity.putExtra("NICKNAME", currentComment.getNickname());
                                            searchActivity.putExtra("ID_TAG",currentComment.getId());
                                            startActivity(searchActivity);
                                            break;
                                        case R.id.mine_comment_copy:
                                            setClipBoardLink(getApplicationContext(), commentHolder.commentContent.getText().toString());
                                            break;
                                        default:
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupCommentMine.show();
                        } else {
                            if (!currentComment.getNickname().isEmpty() && !currentComment.getId().isEmpty()) {
                                PopupMenu popupCommentOther = new PopupMenu(getApplicationContext(), v);
                                MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                                menuInflater.inflate(R.menu.anonymous_forum_comment_other_popup_menu, popupCommentOther.getMenu());
                                popupCommentOther.getMenu().getItem(1).setTitle("'"+currentComment.getNickname()+"'"+" 작성글 검색");
                                popupCommentOther.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.other_comment_write:
                                                if (currentComment.getDepth() == 0) {
                                                    nextCommentIcon.setVisibility(View.VISIBLE);
                                                    nextCommentLayout.setVisibility(View.VISIBLE);
                                                    nextCommentLayoutBorderLine.setVisibility(View.VISIBLE);
                                                    nextCommentNicknameExtra.setText("에게 답글");
                                                    nextCommentNickname.setText(currentComment.getNickname());
                                                    commentInput.requestFocus();
                                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                                                    comment = new Comment(currentForum.getNum(), 1, currentComment.getNum_group(), id, "default_nickname", "default_content", "default_upload_datetime");
                                                }
                                                break;

                                            case R.id.other_comment_search:
                                                Intent searchActivity = new Intent(AnonymousForumActivity_View.this, AnonymousForumActivity_Search.class);
                                                searchActivity.putExtra("ID", id);
                                                searchActivity.putExtra("NICKNAME", currentComment.getNickname());
                                                searchActivity.putExtra("ID_TAG",currentComment.getId());
                                                startActivity(searchActivity);
                                                break;

                                            case R.id.other_comment_copy:
                                                setClipBoardLink(getApplicationContext(), commentHolder.commentContent.getText().toString());
                                                break;
                                            default:
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                popupCommentOther.show();
                            }
                        }
                    }
                }
            });

            commentHolder.nextCommentMoreButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(editmode)
                        ShowCancelEditMode();
                    else {
                        currentComment = forumCommentList.get(position);
                        if (currentComment.getId().equals(id))   //본인
                        {
                            PopupMenu popupNextCommentMine = new PopupMenu(getApplicationContext(), v);
                            MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                            menuInflater.inflate(R.menu.anonymous_forum_next_comment_mine_popup_menu, popupNextCommentMine.getMenu());
                            popupNextCommentMine.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.mine_next_comment_delete:
                                            nextCommentIcon.setVisibility(View.GONE);
                                            nextCommentLayout.setVisibility(View.GONE);
                                            nextCommentLayoutBorderLine.setVisibility(View.GONE);
                                            noticeText.setText(R.string.delete_question);
                                            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                            noticeOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    commentDeleteTask = new CommentDeleteTask();
                                                    commentDeleteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentComment.getNum_primary() + "", currentComment.getNum() + "", currentComment.getDepth() + "", currentComment.getNum_group() + "");
                                                    noticePopupWindow.dismiss();
                                                }
                                            });
                                            break;

                                        case R.id.mine_next_comment_edit:
                                            noticeText.setText(R.string.edit_question);
                                            noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                            noticeOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    noticePopupWindow.dismiss();
                                                    commentInput.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            nextCommentIcon.setVisibility(View.VISIBLE);
                                                            nextCommentLayoutBorderLine.setVisibility(View.VISIBLE);
                                                            nextCommentLayout.setVisibility(View.VISIBLE);
                                                            nextCommentNicknameExtra.setText("");
                                                            nextCommentNickname.setText("수정 모드");
                                                            commentInput.requestFocus();
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                                                            commentInput.setText(currentComment.getContent());
                                                            editmode = true;
                                                        }
                                                    });
                                                }
                                            });
                                            break;
                                        case R.id.mine_next_comment_search:
                                            Intent searchActivity = new Intent(AnonymousForumActivity_View.this, AnonymousForumActivity_Search.class);
                                            searchActivity.putExtra("ID", id);
                                            searchActivity.putExtra("NICKNAME", currentComment.getNickname());
                                            searchActivity.putExtra("ID_TAG",currentComment.getId());
                                            startActivity(searchActivity);
                                            break;
                                        case R.id.mine_next_comment_copy:
                                            setClipBoardLink(getApplicationContext(), commentHolder.nextCommentContent.getText().toString());
                                            break;
                                        default:
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupNextCommentMine.show();
                        } else {
                            PopupMenu popupNextCommentOther = new PopupMenu(getApplicationContext(), v);
                            MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                            menuInflater.inflate(R.menu.anonymous_forum_next_comment_other_popup_menu, popupNextCommentOther.getMenu());
                            popupNextCommentOther.getMenu().getItem(1).setTitle("'"+currentComment.getNickname()+"'"+" 작성글 검색");
                            popupNextCommentOther.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.other_next_comment_search:
                                            Intent searchActivity = new Intent(AnonymousForumActivity_View.this, AnonymousForumActivity_Search.class);
                                            searchActivity.putExtra("ID", id);
                                            searchActivity.putExtra("NICKNAME", currentComment.getNickname());
                                            searchActivity.putExtra("ID_TAG",currentComment.getId());
                                            startActivity(searchActivity);
                                            break;
                                        case R.id.other_next_comment_copy:
                                            setClipBoardLink(getApplicationContext(), commentHolder.nextCommentContent.getText().toString());
                                            break;
                                        default:
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupNextCommentOther.show();
                        }
                    }
                }
            });

            commentHolder.commentLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    if(editmode)
                        ShowCancelEditMode();
                    else {
                        if (!currentComment.getNickname().isEmpty() && !currentComment.getId().isEmpty()) {
                            Comment currentComment = forumCommentList.get(position);
                            if (currentComment.getDepth() == 0) {
                                nextCommentIcon.setVisibility(View.VISIBLE);
                                nextCommentLayout.setVisibility(View.VISIBLE);
                                nextCommentLayoutBorderLine.setVisibility(View.VISIBLE);
                                nextCommentNicknameExtra.setText("에게 답글");
                                nextCommentNickname.setText(currentComment.getNickname());
                                commentInput.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                                comment = new Comment(currentForum.getNum(), 1, currentComment.getNum_group(), id, "default_nickname", "default_content", "default_upload_datetime");
                            }
                        }
                    }
                }
            });
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



        public class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


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
                commentUploadDatetime = (TextView)view.findViewById(R.id.anonymous_forum_comment_upload_datetime);


                nextCommentLayout = (LinearLayout)view.findViewById(R.id.next_comment_layout);
                nextCommentNickname = (TextView)view.findViewById(R.id.anonymous_forum_next_comment_nickname);
                nextCommentMoreButton = (ImageButton)view.findViewById(R.id.anonymous_forum_next_comment_more_button);
                nextCommentContent = (TextView)view.findViewById(R.id.anonymous_forum_next_comment_content);
                nextCommentUploadDatetime = (TextView)view.findViewById(R.id.anonymous_forum_next_comment_upload_datetime);
            }

            @Override
            public void onClick(View v) {

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
                URL serverUrl = new URL("https://server.allofgist.com/forum_like_upload.php");

                HttpsURLConnection httpsURLConnection =  (HttpsURLConnection)serverUrl.openConnection();
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("LikeUploadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == HttpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while ((line=bufferedReader.readLine())!=null) {
                    if(isCancelled())
                        return data;
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
            String[] splitString = result.split("/");
            if (splitString[0].equals("OK")){
                try {
                    currentForum.setLikes(Integer.parseInt(splitString[1]));
                    if (likeClick % 2 == 0) {
                        likeIcon.setSelected(true);
                        likeClick++;
                    } else {
                        likeIcon.setSelected(false);
                        likeClick++;
                    }
                    DataBinding(currentForum);
                }catch (Exception e) {
                    GrayToast(getApplicationContext(),"Error : "+e.toString());
                }

            }
            else{
                GrayToast(getApplicationContext(),"like upload server Error");
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
                URL serverUrl = new URL("https://server.allofgist.com/forum_unlike_upload.php");

                HttpsURLConnection httpsURLConnection =  (HttpsURLConnection)serverUrl.openConnection();
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("LikeUploadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);
                String line = null;

                StringBuffer stringBuffer = new StringBuffer();
                while ((line=bufferedReader.readLine())!=null) {

                    if(isCancelled())
                        return data;
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
            String[] splitString = result.split("/");
            if (splitString[0].equals("OK")){
                try {
                    currentForum.setUnlikes(Integer.parseInt(splitString[1]));
                    if(unlikeClick%2==0) {
                        unlikeIcon.setSelected(true);
                        unlikeClick++;
                    }
                    else{
                        unlikeIcon.setSelected(false);
                        unlikeClick++;
                    }
                    DataBinding(currentForum);
                }catch (Exception e) {
                    GrayToast(getApplicationContext(),"Error : "+e.toString());
                }

            }
            else{
                GrayToast(getApplicationContext(),"unlike upload server Error");
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
                URL serverUrl = new URL("https://server.allofgist.com/forum_comment_insert.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) serverUrl.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("CommentInsertTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line =  null;
                StringBuffer stringBuffer = new StringBuffer();
                while((line=bufferedReader.readLine())!=null){

                    if(isCancelled())
                        return data;
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
                commentLoadTask = new CommentLoadTask();
                commentLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentForum.getNum()+"");

            }
            else
                GrayToast(getApplicationContext(),"서버 연결을 실패하였습니다.");
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
                URL serverUrl = new URL("https://server.allofgist.com/forum_comment_load.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("CommentLoadTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode == httpsURLConnection.HTTP_OK)
                    inputStream = httpsURLConnection.getInputStream();
                else
                    inputStream = httpsURLConnection.getErrorStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while((line =  bufferedReader.readLine())!=null) {
                    if (isCancelled())
                        return jArray;
                    stringBuffer.append(line + "\n");
                }

                data = stringBuffer.toString();
                bufferedReader.close(); //

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
                    Comment currentComment = new Comment(Integer.parseInt(jsonObject.getString("num_primary")),
                            Integer.parseInt(jsonObject.getString("num")),
                            Integer.parseInt(jsonObject.getString("depth")),
                            Integer.parseInt(jsonObject.getString("num_group")),
                            jsonObject.getString("id"),
                            jsonObject.getString("nickname"),
                            jsonObject.getString("content"),
                            jsonObject.getString("upload_datetime"));
                    forumCommentList.add(currentComment);
                }
                commentList.setNestedScrollingEnabled(false);
                commentList.setAdapter(new ForumCommentListAdapter(getApplicationContext()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(AnonymousForumActivity_View.this);
                commentList.setLayoutManager(layoutManager);

                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

           }catch (Exception e){
                e.printStackTrace();
                GrayToast(getApplicationContext(),"댓글 데이터 불러오기를 실패하였습니다.");
            }
        }
    }

    class ForumDeleteTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            String num = strings[0];
            String postParameter = "num="+num;
            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_delete.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("ForumDeleteTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }else{
                    inputStream = httpsURLConnection.getErrorStream();
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();

                while((line = bufferedReader.readLine())!=null){
                    if(isCancelled())
                        return data;
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("")){
                GrayToast(getApplicationContext(),"서버 접속 에러");
            }
            if(result.equals("OK")){
                finish();
            }
        }
    }

    class ForumReloadTask extends AsyncTask<String,Void,JSONArray> {

        JSONArray jsonArray = null;
        @Override
        protected JSONArray doInBackground(String... strings) {
            String data ="";
            String num = strings[0];
            String postParameter = "num="+num;
            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_current_load.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();

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
                    if(isCancelled())
                        return jsonArray;
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

                    currentForum = forum;
                }
                if(swipeRefreshLayout.isRefreshing()) {
                    commentLoadTask = new CommentLoadTask();
                    commentLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentForum.getNum() + "");
                }

            }catch (Exception e){
                e.printStackTrace();
                GrayToast(getApplicationContext(),"게시판 업로드 에러!");
            }

        }
    }

    class CommentDeleteTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            String num_primary = strings[0];
            String num = strings[1];
            String depth = strings[2];
            String num_group = strings[3];
            String postParameter = "num_primary="+num_primary+"&num="+num+"&depth="+depth+"&num_group="+num_group;
            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_comment_delete.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("ForumDeleteTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }else{
                    inputStream = httpsURLConnection.getErrorStream();
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();

                while((line = bufferedReader.readLine())!=null){
                    if(isCancelled())
                        return data;
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK")){
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        forumCommentList = new ArrayList<Comment>();
                        forumReloadTask = new ForumReloadTask();
                        forumReloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentForum.getNum()+"");
                    }
                });
            }
            else
                GrayToast(getApplicationContext(),"서버 접속 에러");

        }
    }

    class CommentEditTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            String num_primary = strings[0];
            String content =  strings[1];
            String postParameter = "num_primary="+num_primary+"&content="+content;
            try{
                URL serverUrl = new URL("https://server.allofgist.com/forum_comment_edit.php");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)serverUrl.openConnection();
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("ForumDeleteTask","POST response code - "+responseStatusCode);

                InputStream inputStream;
                BufferedReader bufferedReader;

                if(responseStatusCode== httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }else{
                    inputStream = httpsURLConnection.getErrorStream();
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream),8*1024);

                String line = null;
                StringBuffer stringBuffer = new StringBuffer();

                while((line = bufferedReader.readLine())!=null){
                    if(isCancelled())
                        return data;
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            if(result.equals("OK")){
                editmode = false;
                commentInput.setText("");

                commentInput.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentInput.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        forumCommentList = new ArrayList<Comment>();
                        forumReloadTask = new ForumReloadTask();
                        forumReloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentForum.getNum()+"");
                    }
                });
            }
            else
                GrayToast(getApplicationContext(),"서버 접속 에러");

        }
    }

    public void InitialSetting(){
        backButton = (ImageButton)findViewById(R.id.anonymous_forum_view_back_button);
        moreButton = (ImageButton)findViewById(R.id.anonymous_forum_view_more_button);
        //topTitle = (TextView)findViewById(R.id.anonymous_forum_view_title_top);

        title = (TextView)findViewById(R.id.anonymous_forum_view_title);
        nickname = (TextView)findViewById(R.id.anonymous_forum_view_nickname);
        upload_datetime = (TextView)findViewById(R.id.anonymous_forum_view_upload_datetime);
        comments = (TextView)findViewById(R.id.anonymous_forum_view_views_comments);
        zoomOut = (ImageButton)findViewById(R.id.anonymous_forum_view_zoom_out);
        zoomIn = (ImageButton)findViewById(R.id.anonymous_forum_view_zoom_in);

        content = (TextView)findViewById(R.id.anonymous_forum_view_content);
        /*nicknameBottom = (TextView)findViewById(R.id.anonymous_forum_view_nickname_bottom);
        nicknameSearch = (TextView)findViewById(R.id.anonymous_forum_view_nickname_search);*/

        likeButton = (LinearLayout)findViewById(R.id.anonymous_forum_view_like);
        likeIcon = (ImageView)findViewById(R.id.anonymous_forum_view_like_icon);
        //likeText = (TextView)findViewById(R.id.anonymous_forum_view_like_text);
        unlikeButton = (LinearLayout)findViewById(R.id.anonymous_forum_view_unlike);
        unlikeIcon = (ImageView) findViewById(R.id.anonymous_forum_view_unlike_icon);
        //unlikeText = (TextView)findViewById(R.id.anonymous_forum_view_unlike_text);
        commentButton = (LinearLayout)findViewById(R.id.anonymous_forum_view_comment);

        likeState = (TextView)findViewById(R.id.anonymous_forum_view_likes_state);
        unlikeState = (TextView)findViewById(R.id.anonymous_forum_view_unlikes_state);
        stateLayout = (LinearLayout)findViewById(R.id.anonymous_forum_view_state_layout);
        //nonelikeState = (TextView)findViewById(R.id.anonymous_forum_view_none_state_textview);

        commentList = (RecyclerView)findViewById(R.id.anonymous_forum_view_comment_list);

        commentInput = (EditText)findViewById(R.id.anonymous_forum_view_comment_input);
        commentInputButton = (ImageButton)findViewById(R.id.anonymous_forum_view_comment_input_button);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.anonymous_forum_view_swipe_layout);
        scrollView = (NestedScrollView)findViewById(R.id.anonymous_forum_view_scroll_view);
        fullScreen = (LinearLayout)findViewById(R.id.anonymous_forum_view_fullscreen);
        commentInputLayout = (LinearLayout)findViewById(R.id.anonymous_forum_view_comment_input_layout);

        nextCommentIcon = (ImageView)findViewById(R.id.anonymous_forum_view_next_comment_icon);
        nextCommentLayout = (LinearLayout)findViewById(R.id.anonymous_forum_view_next_comment_layout);
        nextCommentNickname = (TextView)findViewById(R.id.anonymous_forum_view_next_comment_nickname);
        nextCommentNicknameExtra = (TextView)findViewById(R.id.anonymous_forum_view_next_comment_nickname_extra);
        nextCommentCloseButton = (ImageButton)findViewById(R.id.anonymous_forum_view_next_comment_close_button);
        nextCommentLayoutBorderLine = (View)findViewById(R.id.anonymous_forum_view_next_comment_border_line);

        popupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        noticePopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)popupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)popupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)popupView.findViewById(R.id.notice_plus_cancel_textview);
        noticePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));


        commentDeleteTask = new CommentDeleteTask();
        commentEditTask = new CommentEditTask();
        commentInsertTask = new CommentInsertTask();
        commentLoadTask = new CommentLoadTask();

        forumReloadTask = new ForumReloadTask();
        forumDeleteTask = new ForumDeleteTask();

        likeUploadTask = new LikeUploadTask();
        unLikeUploadTask = new UnlikeUploadTask();
    }

    public static int pixtosp(Context context, float pixel){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pixel/fontScale + 0.5f);
    }

    public static int sptopix(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale);
    }

    //클립보드 복사
    public static void setClipBoardLink(Context context , String link){
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", link);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static Bitmap loadBitmapFromView(View view, int width, int height)
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    //스크린샷 저장
    public File ScreenShot(){
        //view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        //Bitmap screenBitmap = view.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);   //캐시를 비트맵으로 변환
        //view.destroyDrawingCache();
        Bitmap screenBitmap =  loadBitmapFromView(scrollView, scrollView.getChildAt(0).getWidth(),scrollView.getChildAt(0).getHeight());

        String filename = "AllofGist_capture_"+datetime_SecFormat.format(Calendar.getInstance(Locale.KOREA).getTimeInMillis())+".jpg";
        String directory_uri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "AllofGist" + File.separator;
        File directory = new File(directory_uri);
        File file = new File(directory_uri+filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        if(!directory.exists()){
            try{
                directory.mkdirs();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);   //비트맵을 PNG파일로 변환
            os.flush();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
            GrayToast(getApplicationContext(),"캡쳐된 파일을 찾을 수 없습니다.");
            return null;
        }
        //view.setDrawingCacheEnabled(false);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA,
                directory_uri + filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return file;
    }




    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

    public void ShowCancelEditMode(){
        noticeText.setText(R.string.edit_cancel_question);
        noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
        noticeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticePopupWindow.dismiss();
                nextCommentIcon.setVisibility(View.GONE);
                nextCommentLayout.setVisibility(View.GONE);
                nextCommentLayoutBorderLine.setVisibility(View.GONE);
                commentInput.post(new Runnable() {
                    @Override
                    public void run() {
                        commentInput.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(commentInput.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        commentInput.setText("");
                        editmode = false;
                    }
                });
            }
        });
    }
}
