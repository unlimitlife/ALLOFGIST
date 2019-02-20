package com.allofgist.dell.allofgistlite;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Forum implements Parcelable {

    private int num;
    private String id;
    private String title;
    private String content;
    private String nickname;
    private Date upload_datetime;
    private int views;
    private int likes;
    private int unlikes;
    private int comments;
    private String like_select;
    private String unlike_select;
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.KOREA);


    //글 등록 할때

    public Forum(String id, String title, String content, String nickname, Date upload_datetime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.upload_datetime = upload_datetime;
    }

    public Forum(int num, String id, String title, String content, String nickname, Date upload_datetime, int views, int likes, int unlikes, int comments, String like_select, String unlike_select) {
        this.num = num;
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.upload_datetime = upload_datetime;
        this.views = views;
        this.likes = likes;
        this.unlikes = unlikes;
        this.comments = comments;
        this.like_select = like_select;
        this.unlike_select = unlike_select;
    }

    public Forum(Parcel in){
        readFromParcel(in);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Forum> CREATOR = new Parcelable.Creator<Forum>(){
        @Override
        public Forum createFromParcel(Parcel source) {
            return new Forum(source);
        }

        @Override
        public Forum[] newArray(int size) {
            return new Forum[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(num);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(nickname);
        dest.writeString(datetimeFormat.format(upload_datetime));
        dest.writeInt(views);
        dest.writeInt(likes);
        dest.writeInt(unlikes);
        dest.writeInt(comments);
        dest.writeString(like_select);
        dest.writeString(unlike_select);

    }


    public void readFromParcel(Parcel in) {
        num =  in.readInt();
        id = in.readString();
        title = in.readString();
        content = in.readString();
        nickname = in.readString();
        try {
            upload_datetime = datetimeFormat.parse(in.readString());
        }catch (Exception e){
            e.printStackTrace();
        }
        views = in.readInt();
        likes = in.readInt();
        unlikes = in.readInt();
        comments = in.readInt();
        like_select = in.readString();
        unlike_select = in.readString();
    }



    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getUpload_datetime() {
        return upload_datetime;
    }

    public void setUpload_datetime(Date upload_datetime) {
        this.upload_datetime = upload_datetime;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(int unlikes) {
        this.unlikes = unlikes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getLike_select() {
        return like_select;
    }

    public void setLike_select(String like_select) {
        this.like_select = like_select;
    }

    public String getUnlike_select() {
        return unlike_select;
    }

    public void setUnlike_select(String unlike_select) {
        this.unlike_select = unlike_select;
    }
}
