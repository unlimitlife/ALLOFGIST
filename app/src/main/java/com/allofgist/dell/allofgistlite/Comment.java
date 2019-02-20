package com.allofgist.dell.allofgistlite;

public class Comment {
    private int num_primary;
    private int num;
    private int depth;
    private int num_group;
    private String id;
    private String nickname;
    private String content;
    private String upload_datetime;

    public Comment(int num, int depth, int num_group, String id, String nickname, String content, String upload_datetime) {
        this.num = num;
        this.depth = depth;
        this.num_group = num_group;
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.upload_datetime = upload_datetime;
    }

    public Comment(int num_primary, int num, int depth, int num_group, String id, String nickname, String content, String upload_datetime) {
        this.num_primary = num_primary;
        this.num = num;
        this.depth = depth;
        this.num_group = num_group;
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.upload_datetime = upload_datetime;
    }

    public int getNum_primary() {
        return num_primary;
    }

    public void setNum_primary(int num_primary) {
        this.num_primary = num_primary;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getNum_group() {
        return num_group;
    }

    public void setNum_group(int num_group) {
        this.num_group = num_group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpload_datetime() {
        return upload_datetime;
    }

    public void setUpload_datetime(String upload_datetime) {
        this.upload_datetime = upload_datetime;
    }
}
