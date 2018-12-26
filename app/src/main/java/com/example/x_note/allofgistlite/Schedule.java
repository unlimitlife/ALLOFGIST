package com.example.x_note.allofgistlite;

import java.util.Date;

public class Schedule {

    private int num;
    private String context;
    private String completeDateTime_text;
    private Date completeDateTime_numeric;
    private String alarm;
    private String alarm_calculate;
    private String repeat_period;
    private java.sql.Date repeat_start_date;
    private java.sql.Date repeat_end_date;
    private String repeat_date;
    private String repeat_cycle;


    public Schedule(int num, String context, String completeDateTime_text, Date completeDateTime_numeric,
                    String alarm, String alarm_calculate, String repeat_period, java.sql.Date repeat_start_date, java.sql.Date repeat_end_date,String repeat_date, String repeat_cycle){

        this.num = num;
        this.context = context;
        this.completeDateTime_text = completeDateTime_text;
        this.completeDateTime_numeric = completeDateTime_numeric;
        this.alarm = alarm;
        this.alarm_calculate = alarm_calculate;
        this.repeat_period = repeat_period;
        this.repeat_start_date = repeat_start_date;
        this.repeat_end_date = repeat_end_date;
        this.repeat_date = repeat_date;
        this.repeat_cycle = repeat_cycle;

    }
    public int getNum(){return num;}

    public void setNum(int num){this.num = num;}

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCompleteDateTime_text() {
        return completeDateTime_text;
    }

    public void setCompleteDateTime_text(String completeDateTime_text) {
        this.completeDateTime_text = completeDateTime_text;
    }

    public Date getCompleteDateTime_numeric() {
        return completeDateTime_numeric;
    }

    public void setCompleteDateTime_numeric(Date completeDateTime_numeric) {
        this.completeDateTime_numeric = completeDateTime_numeric;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAlarm_calculate() {
        return alarm_calculate;
    }

    public void setAlarm_calculate(String alarm_calculate) {
        this.alarm_calculate = alarm_calculate;
    }

    public String getRepeat_period() {
        return repeat_period;
    }

    public void setRepeat_period(String repeat_period) {
        this.repeat_period = repeat_period;
    }

    public java.sql.Date getRepeat_start_date() {
        return repeat_start_date;
    }

    public void setRepeat_start_date(java.sql.Date repeat_start_date) {
        this.repeat_start_date = repeat_start_date;
    }

    public java.sql.Date getRepeat_end_date() {
        return repeat_end_date;
    }

    public void setRepeat_end_date(java.sql.Date repeat_end_date) {
        this.repeat_end_date = repeat_end_date;
    }

    public String getRepeat_date(){
        return repeat_date;
    }

    public void setRepeat_date(String repeat_date){
        this.repeat_date = repeat_date;
    }

    public String getRepeat_cycle(){
        return repeat_cycle;
    }

    public void setRepeat_cycle(String repeat_cycle){
        this.repeat_cycle = repeat_cycle;
    }
}
