package com.allofgist.dell.allofgistlite;


import android.os.Parcel;
import android.os.Parcelable;

public class Calculator implements Parcelable {
    private String type;
    private String title;
    private String credits;
    private String grade;


    public Calculator(String type, String title, String credits, String grade) {
        this.type = type;
        this.title = title;
        this.credits = credits;
        this.grade = grade;
    }

    public Calculator(Parcel in){
        readFromParcel(in);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Calculator> CREATOR = new Parcelable.Creator<Calculator>(){
        @Override
        public Calculator createFromParcel(Parcel source) {
            return new Calculator(source);
        }

        @Override
        public Calculator[] newArray(int size) {
            return new Calculator[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(credits);
        dest.writeString(grade);
    }


    public void readFromParcel(Parcel in) {
        type = in.readString();
        title = in.readString();
        credits = in.readString();
        grade = in.readString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
