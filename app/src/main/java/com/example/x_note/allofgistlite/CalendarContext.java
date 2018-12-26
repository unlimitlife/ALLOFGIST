package com.example.x_note.allofgistlite;

public class CalendarContext {
    private String date_title;
    private String context;

    public CalendarContext(String date, String context){
        this.date_title = date;
        this.context = context;
    }


    public String getDate_title() {
        return this.date_title;
    }

    public String getContext() {
        return this.context;
    }

    public void setDate_title(String date){
        this.date_title = date;
    }

    public void setContext(String context){
        this.context = context;
    }
}
