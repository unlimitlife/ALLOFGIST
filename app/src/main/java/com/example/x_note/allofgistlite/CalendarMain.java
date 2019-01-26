package com.example.x_note.allofgistlite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SafeBrowsingResponse;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

public class CalendarMain extends AppCompatActivity {

    private String id;
    private MaterialCalendarView calendarView;
    private Button watchAcademic;

    //요일 저장하는 배열
    String[] day = {"일", "월", "화", "수", "목", "금", "토"};

    private TextView textDate;

    //캘린더 하단 리스트뷰 항목들
    private ListView calendarList;
    private CalendarListAdapter calendarListAdapter;
    private ArrayList<Schedule> calendarListDate_pre;
    private Hashtable<Date, ArrayList<Schedule>> calendarListData;

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    SimpleDateFormat repeatDate = new SimpleDateFormat("yyyy.MM.dd",Locale.KOREA);
    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss",Locale.KOREA);
    SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.KOREA);

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogDot;
    private int colors;
    private Hashtable<CalendarDay, Integer> dotData;
    private ArrayList<Date> isDot;

    private FloatingActionButton gotoToday;

    //일정 추가 관련
    private FloatingActionButton addSchedule;
    private PopupWindow addScheduleScreen;
    private PopupWindow addScheduleRepeatScreen;

    private ImageButton clearBack;
    private ImageButton check;
    private PopupWindow deleteSchedulePopupWindow;
    private LinearLayout repeatDeleteLayout;
    private LinearLayout noRepeatDeleteLayout;
    private TextView allDeleteTextView;
    private TextView noRepeatOkTextView;
    private TextView noRepeatBackTextView;

    private EditText edit_diary;
    private DatePicker datePicker;
    private Button dateButton;
    private TimePicker timePicker;
    private Button timeButton;
    private TableLayout alarmTable;
    private Button alarmButton;
    private Button repeatButton;

    private InputMethodManager imm;

    //일정 삭제 관련
    private ImageButton deleteButton;

    //알람 버튼 선언
    private Button zeroAgo;
    private Button fiveMinuteAgo;
    private Button tenMinuteAgo;
    private Button fifteenMinuteAgo;
    private Button thirteenMinuteAgo;
    private Button oneHourAgo;
    private Button twoHourAgo;
    private Button threeHourAgo;
    private Button twelveHourAgo;
    private Button oneDayAgo;
    private Button twoDayAgo;
    private Button noAlarm;

    //반복 설정 화면 요소들
    private ImageButton backButton;
    private ImageButton checkRButton;

    private Button noRepeat;
    private Button dayRepeat;
    private Button weekRepeat;
    private Button monthRepeat;
    private Button yearRepeat;

    private LinearLayout noRepeatWindow;
    private LinearLayout dayRepeatWindow;
    private LinearLayout weekRepeatWindow;
    private LinearLayout monthRepeatWindow;
    private LinearLayout yearRepeatWindow;

    private TextView dayRepeatTextWindow;
    private TextView weekRepeatTextWindow;
    private TextView monthRepeatTextWindow;
    private TextView yearRepeatTextWindow;

    private String selectedRepeatTag = null;

    private EditText dayRepeatEditText;
    private EditText weekRepeatEditText;
    private String WEEK_REPEAT_TEXT;
    private EditText monthRepeatEditText;

    private RelativeLayout endDateTextLayout;
    private DatePicker endDatePicker;
    private TextView endDateTextView;
    private ImageButton endDatePickerButton;

    private Button dayEndButton;
    private Button weekEndButton;
    private Button monthEndButton;
    private Button yearEndButton;

    //반복창 하단 테이블들
    private TableLayout weekRepeatUnderTable;

    private Button weekRepeatMon;
    private Button weekRepeatTue;
    private Button weekRepeatWed;
    private Button weekRepeatThu;
    private Button weekRepeatFri;
    private Button weekRepeatSat;
    private Button weekRepeatSun;

    private ArrayList<String> dayOfTheWeekCheck;
    private String[] dayOfTheWeekCheckBase = {"월", "화", "수", "목", "금", "토", "일"};
    private String WEEK_OF_DAY_TEXT;

    ArrayList<Integer> oneColor = new ArrayList<Integer>();
    ArrayList<Integer> twoColor = new ArrayList<Integer>();
    ArrayList<Integer> threeColor = new ArrayList<Integer>();
    ArrayList<Integer> fourColor = new ArrayList<Integer>();
    ArrayList<Integer> fiveColor = new ArrayList<Integer>();

    @Override
    protected void onResume() {    //일단 응급처치(두개의 popupwindow 순서 바뀌는 문제)
        if (addScheduleRepeatScreen.isShowing())
            addScheduleRepeatScreen.dismiss();
        if (deleteSchedulePopupWindow.isShowing())
            deleteSchedulePopupWindow.dismiss();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (addScheduleRepeatScreen.isShowing()) {
            addScheduleRepeatScreen.dismiss();
        } else if (addScheduleScreen.isShowing()) {
            addScheduleScreen.dismiss();
        } else if (deleteSchedulePopupWindow.isShowing()) {
            deleteSchedulePopupWindow.dismiss();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_main);


        //Intent intent = getIntent();
        //id = intent.getStringExtra("ID");
        SharedPreferences loginPrefs = getSharedPreferences("LOGIN_ID",MODE_PRIVATE);
        id = loginPrefs.getString("ID","LOGIN_ERROR");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(CalendarMain.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .commit();

        calendarView.setDynamicHeightEnabled(true);

        calendarView.addDecorators(
                new OneDayDecorator(),
                new SundayDecorator(),
                new SaturdayDecorator());


        oneColor.add(Color.rgb(255,193,7));

        twoColor.add(Color.rgb(255,193,7));
        twoColor.add(Color.rgb(255,179,0));

        threeColor.add(Color.rgb(255,193,7));
        threeColor.add(Color.rgb(255,179,0));
        threeColor.add(Color.rgb(255,160,0));

        fourColor.add(Color.rgb(255,193,7));
        fourColor.add(Color.rgb(255,179,0));
        fourColor.add(Color.rgb(255,160,0));
        fourColor.add(Color.rgb(255,143,0));

        fiveColor.add(Color.rgb(255,193,7));
        fiveColor.add(Color.rgb(255,179,0));
        fiveColor.add(Color.rgb(255,160,0));
        fiveColor.add(Color.rgb(255,143,0));
        fiveColor.add(Color.rgb(255,111,0));


        /*calendarView.addDecorators(
                new OneDotDecorator(),
                new TwoDotDecorator(),
                new ThreeDotDecorator(),
                new FourDotDecorator(),
                new FiveDotDecorator());   */ //  달이 바뀔떄마다 refresh decorator 해줘야 함

        //날짜 제목
        textDate = (TextView) findViewById(R.id.date_text);

        //하단 리스트 관련
        calendarList = (ListView) findViewById(R.id.calendar_list);
        isDot = new ArrayList<Date>();

        //나중에 옯겨야함 month selected로
        dotData = new Hashtable<CalendarDay, Integer> ();

        //첫화면에 dot랑 일정표시 작업
        SettingCalendarScreen();

        final View popupView = getLayoutInflater().inflate(R.layout.add_schedule_popup_window, null);
        addScheduleScreen = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        final View popupViewR = getLayoutInflater().inflate(R.layout.add_schedule_repeat_button_popup_window, null);
        addScheduleRepeatScreen = new PopupWindow(popupViewR, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        final View popupViewD = getLayoutInflater().inflate(R.layout.delete_schedule_popup_window, null);
        deleteSchedulePopupWindow = new PopupWindow(popupViewD, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        repeatDeleteLayout = (LinearLayout)popupViewD.findViewById(R.id.repeat_delete_layout);
        allDeleteTextView = (TextView)popupViewD.findViewById(R.id.all_delete);
        noRepeatDeleteLayout = (LinearLayout)popupViewD.findViewById(R.id.no_repeat_delete_layout);
        noRepeatBackTextView = (TextView)popupViewD.findViewById(R.id.no_repeat_delete_back_textview);
        noRepeatOkTextView = (TextView)popupViewD.findViewById(R.id.no_repeat_delete_ok_textview);


        //일정 추가 창 설정
        setAddSchedulePopup(popupView);

        //반복 세부 창 설정
        setAddScheduleRepeatPopup(popupViewR);

        //addScheduleScreen.setFocusable(true);
        //addScheduleScreen.update();

        addScheduleScreen.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        addScheduleRepeatScreen.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        deleteSchedulePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        calendarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int dateClick;
            int timeClick;
            int alarmClick;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id1) {
                final Schedule schedule = (Schedule) calendarList.getItemAtPosition(position);

                if(schedule.getContext().equals("일정이 없습니다.")){

                }
                else {
                    //키보드 바로 뜨게(EditText)
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    edit_diary.setText(schedule.getContext());

                    dateClick = 0;
                    timeClick = 0;
                    alarmClick = 0;
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                    alarmTable.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.VISIBLE);

                    noRepeat.setSelected(false);
                    dayRepeat.setSelected(false);
                    weekRepeat.setSelected(false);
                    monthRepeat.setSelected(false);
                    yearRepeat.setSelected(false);

                    noRepeatWindow.setVisibility(View.VISIBLE);
                    dayRepeatWindow.setVisibility(View.GONE);
                    weekRepeatWindow.setVisibility(View.GONE);
                    monthRepeatWindow.setVisibility(View.GONE);
                    yearRepeatWindow.setVisibility(View.GONE);

                    endDateTextLayout.setVisibility(View.GONE);
                    endDatePicker.setVisibility(View.GONE);

                    Log.v("CalendarMain", "context : " + schedule.getContext() + "\ncompleteDateTime_text : " + schedule.getCompleteDateTime_text()
                            + "\ncompleteDateTime_numeric : " + schedule.getCompleteDateTime_numeric() + "\nalarm : " + schedule.getAlarm() + "\nalarm_calculate : "
                            + schedule.getAlarm_calculate() + "\nrepeat_period : " + schedule.getRepeat_period() + "\nrepeat_start_date : " + schedule.getRepeat_start_date()
                            + "\nrepeat_end_date : " + schedule.getRepeat_end_date() + "\nrepeat_date : " + schedule.getRepeat_date() + "\nrepeat_cycle : " + schedule.getRepeat_cycle());

                    try {
                        if (schedule.getRepeat_end_date() == null) {
                            repeatButton.setText(schedule.getRepeat_period());
                        } else {
                            repeatButton.setText(schedule.getRepeat_period() + "\n" + repeatDate.format(schedule.getCompleteDateTime_numeric()) + " - " + repeatDate.format(schedule.getRepeat_end_date()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        repeatButton.setText(schedule.getRepeat_period());
                    }

                    weekRepeatUnderTable.setVisibility(View.GONE);
                    dayOfTheWeekCheck = new ArrayList<String>();

                    if (calendarView.getSelectedDate() != null)
                        endDatePicker.setMinDate(calendarView.getSelectedDate().getCalendar().getTimeInMillis());
                    else
                        endDatePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

                    if (!addScheduleScreen.isShowing()) {
                        addScheduleScreen.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        final Calendar calendar = Calendar.getInstance();
                        if (calendarView.getSelectedDate() != null)
                            calendar.set(calendarView.getSelectedDate().getYear(), calendarView.getSelectedDate().getMonth(), calendarView.getSelectedDate().getDay());
                        final int daynum = calendar.get(Calendar.DAY_OF_WEEK);
                        dateButton.setText(schedule.getCompleteDateTime_text().split("\\)")[0] + ")");
                        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker datePicker, int year, int month, int day1) {
                                Calendar calendarN = Calendar.getInstance();
                                calendarN.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                int daynum_datepicker = calendarN.get(Calendar.DAY_OF_WEEK);
                                dateButton.setText(year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                endDatePicker.setMinDate(calendarN.getTimeInMillis());
                            }
                        });

                        String am_pm = null;
                        if (Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "오전 ";
                        else
                            am_pm = "오후 ";
                        timeButton.setText(schedule.getCompleteDateTime_text().split("\\) ")[1]);
                        setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1, 0);
                        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                            @Override
                            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                                String am_pmN = null;
                                if (hourOfDay > 12) {
                                    am_pmN = "오후 ";
                                    hourOfDay = hourOfDay - 12;
                                } else if (hourOfDay == 12) {
                                    am_pmN = "오후 ";
                                } else if (hourOfDay == 0) {
                                    am_pmN = "오전 ";
                                    hourOfDay += 12;
                                } else
                                    am_pmN = "오전 ";
                                String minuteS = null;
                                minuteS = "" + minute;
                                if (minute < 10)
                                    minuteS = "0" + minuteS;
                                timeButton.setText(am_pmN + hourOfDay + ":" + minuteS);
                            }
                        });
                        dateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dateClick % 2 == 0)
                                    datePicker.setVisibility(View.VISIBLE);
                                else
                                    datePicker.setVisibility(View.GONE);
                                dateClick++;
                            }
                        });
                        timeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (timeClick % 2 == 0)
                                    timePicker.setVisibility(View.VISIBLE);
                                else
                                    timePicker.setVisibility(View.GONE);
                                timeClick++;
                            }
                        });
                        alarmButton.setText(schedule.getAlarm());   // 설정해주어야 함
                        alarmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (alarmClick % 2 == 0) {
                                    alarmTable.setVisibility(View.VISIBLE);

                                    //알람 버튼 설정
                                    zeroAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("정시");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    fiveMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("5분 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    tenMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("10분 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    fifteenMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("15분 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    thirteenMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("30분 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    oneHourAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("1시간 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    twoHourAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("2시간 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    threeHourAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("3시간 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    twelveHourAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("12시간 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    oneDayAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("1일 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    twoDayAgo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("2일 전");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });

                                    noAlarm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alarmButton.setText("없음");
                                            alarmTable.setVisibility(View.GONE);
                                            alarmClick++;
                                        }
                                    });
                                } else
                                    alarmTable.setVisibility(View.GONE);
                                alarmClick++;
                            }
                        });
                        //반복 버튼
                        repeatButton.setOnClickListener(new View.OnClickListener() {

                            Calendar calendarN;
                            int daynum_datepicker;

                            @Override
                            public void onClick(View view) {
                                calendarN = Calendar.getInstance();
                                calendarN.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                daynum_datepicker = calendarN.get(Calendar.DAY_OF_WEEK);

                                if (!addScheduleRepeatScreen.isShowing()) {
                                    addScheduleRepeatScreen.showAtLocation(popupViewR, Gravity.CENTER, 0, 0);

                                    noRepeat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            InitiallizeRepeatSetting(noRepeat, noRepeatWindow);

                                            selectedRepeatTag = "없음";
                                        }
                                    });

                                    dayRepeat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            InitiallizeRepeatSetting(dayRepeat, dayRepeatWindow);
                                            selectedRepeatTag = dayRepeatTextWindow.getText().toString();

                                            dayRepeatEditText.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                                                    if (charSequence.toString().equals("1") || charSequence.toString().equals(""))
                                                        dayRepeatTextWindow.setText("매일 반복");
                                                    else if (charSequence.toString().equals("0")) {
                                                        Toast.makeText(getApplicationContext(), "잘못된 반복 주기 입니다.", Toast.LENGTH_SHORT).show();
                                                        dayRepeatEditText.setText("");
                                                        dayRepeatTextWindow.setText("매일 반복");
                                                    } else
                                                        dayRepeatTextWindow.setText("매" + charSequence + "일 마다 반복");
                                                    if (!TextUtils.isEmpty(endDateTextView.getText()))
                                                        selectedRepeatTag = dayRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    else
                                                        selectedRepeatTag = dayRepeatTextWindow.getText().toString();
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {

                                                }
                                            });
                                            //종료일 버튼
                                            dayEndButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    endDateTextLayout.setVisibility(View.VISIBLE);
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                    int month = ((calendarN.get(Calendar.MONTH) + 1) + 1);
                                                    month = month>12 ? month-12 : month;
                                                    endDateTextView.setText("종료일 " + calendarN.get(Calendar.YEAR) + "년 " + month+ "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                    endDatePicker.init(calendarN.get(Calendar.YEAR), calendarN.get(Calendar.MONTH) + 1, calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                        @Override
                                                        public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                            Calendar calendarE = Calendar.getInstance();
                                                            calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                            int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                            endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                            selectedRepeatTag = dayRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                        }
                                                    });
                                                    selectedRepeatTag = dayRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            selectedRepeatTag = dayRepeatTextWindow.getText().toString();
                                                            endDateTextLayout.setVisibility(View.GONE);
                                                            endDatePicker.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }
                                            });
                                            //text 제목
                                            endDateTextView.setOnClickListener(new View.OnClickListener() {
                                                int endClick = 0;

                                                @Override
                                                public void onClick(View view) {
                                                    if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                        endDatePicker.setVisibility(View.VISIBLE);
                                                    } else {
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                        }
                                    });
                                    weekRepeat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            WEEK_OF_DAY_TEXT = day[daynum_datepicker - 1] + "요일";
                                            WEEK_REPEAT_TEXT = "주 ";
                                            InitiallizeRepeatSetting(weekRepeat, weekRepeatWindow);
                                            selectedRepeatTag = weekRepeatTextWindow.getText().toString();
                                            weekRepeatUnderTable.setVisibility(View.VISIBLE);
                                            dayOfTheWeekCheck.add(day[daynum_datepicker - 1]);

                                            //하단 버튼 테이블 초기화 설정
                                            switch (day[daynum_datepicker - 1]) {
                                                case "월":
                                                    weekRepeatMon.setSelected(true);
                                                    break;
                                                case "화":
                                                    weekRepeatTue.setSelected(true);
                                                    break;
                                                case "수":
                                                    weekRepeatWed.setSelected(true);
                                                    break;
                                                case "목":
                                                    weekRepeatThu.setSelected(true);
                                                    break;
                                                case "금":
                                                    weekRepeatFri.setSelected(true);
                                                    break;
                                                case "토":
                                                    weekRepeatSat.setSelected(true);
                                                    break;
                                                case "일":
                                                    weekRepeatSun.setSelected(true);
                                                    break;
                                                default:
                                                    break;
                                            }

                                            //요일 버튼 클릭 설정
                                            weekRepeatMon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatMon, "월");
                                                }
                                            });

                                            weekRepeatTue.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatTue, "화");
                                                }
                                            });

                                            weekRepeatWed.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatWed, "수");
                                                }
                                            });

                                            weekRepeatThu.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatThu, "목");
                                                }
                                            });

                                            weekRepeatFri.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatFri, "금");
                                                }
                                            });

                                            weekRepeatSat.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatSat, "토");
                                                }
                                            });

                                            weekRepeatSun.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkWeekOfTheDayProcess(weekRepeatSun, "일");
                                                }
                                            });

                                            weekRepeatEditText.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                                                    if (charSequence.toString().equals("1") || charSequence.toString().equals("")) {
                                                        WEEK_REPEAT_TEXT = "주 ";
                                                        weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                                                    } else if (charSequence.toString().equals("0")) {
                                                        Toast.makeText(getApplicationContext(), "잘못된 반복 주기 입니다.", Toast.LENGTH_SHORT).show();
                                                        weekRepeatEditText.setText("");
                                                        WEEK_REPEAT_TEXT = "주 ";
                                                        weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                                                    } else {
                                                        WEEK_REPEAT_TEXT = charSequence + "주 마다 ";
                                                        weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                                                    }
                                                    if (!TextUtils.isEmpty(endDateTextView.getText()))
                                                        selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    else
                                                        selectedRepeatTag = weekRepeatTextWindow.getText().toString();
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {

                                                }
                                            });

                                            weekEndButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    endDateTextLayout.setVisibility(View.VISIBLE);
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                    int month = ((calendarN.get(Calendar.MONTH) + 1) + 1);
                                                    month = month>12 ? month-12 : month;
                                                    endDateTextView.setText("종료일 " + calendarN.get(Calendar.YEAR) + "년 " + month+ "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                    endDatePicker.init(calendarN.get(Calendar.YEAR), calendarN.get(Calendar.MONTH) + 1, calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                        @Override
                                                        public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                            Calendar calendarE = Calendar.getInstance();
                                                            calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                            int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                            endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                            selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                        }
                                                    });
                                                    selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            selectedRepeatTag = weekRepeatTextWindow.getText().toString();
                                                            endDateTextLayout.setVisibility(View.GONE);
                                                            endDatePicker.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }

                                            });

                                            //text 제목
                                            endDateTextView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                        endDatePicker.setVisibility(View.VISIBLE);
                                                    } else {
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    monthRepeat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            InitiallizeRepeatSetting(monthRepeat, monthRepeatWindow);
                                            selectedRepeatTag = monthRepeatTextWindow.getText().toString();
                                            monthRepeatEditText.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                                                    if (charSequence.toString().equals("1") || charSequence.toString().equals(""))
                                                        monthRepeatTextWindow.setText("매월 " + datePicker.getDayOfMonth() + "일 반복");
                                                    else if (charSequence.toString().equals("0")) {
                                                        Toast.makeText(getApplicationContext(), "잘못된 반복 주기 입니다.", Toast.LENGTH_SHORT).show();
                                                        weekRepeatEditText.setText("");
                                                        monthRepeatTextWindow.setText("매월 " + datePicker.getDayOfMonth() + "일 반복");
                                                    } else
                                                        monthRepeatTextWindow.setText("매" + charSequence + "개월 마다 " + datePicker.getDayOfMonth() + "일 반복");

                                                    if (!TextUtils.isEmpty(endDateTextView.getText()))
                                                        selectedRepeatTag = monthRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    else
                                                        selectedRepeatTag = monthRepeatTextWindow.getText().toString();
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {

                                                }
                                            });

                                            monthEndButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    endDateTextLayout.setVisibility(View.VISIBLE);
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                    endDateTextView.setText("종료일 " + (calendarN.get(Calendar.YEAR) + 1) + "년 " + (calendarN.get(Calendar.MONTH) + 1) + "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                    endDatePicker.init(calendarN.get(Calendar.YEAR) + 1, calendarN.get(Calendar.MONTH), calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                        @Override
                                                        public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                            Calendar calendarE = Calendar.getInstance();
                                                            calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                            int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                            endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                            selectedRepeatTag = monthRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                        }
                                                    });
                                                    selectedRepeatTag = monthRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            selectedRepeatTag = monthRepeatTextWindow.getText().toString();
                                                            endDateTextLayout.setVisibility(View.GONE);
                                                            endDatePicker.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }

                                            });

                                            //text 제목
                                            endDateTextView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                        endDatePicker.setVisibility(View.VISIBLE);
                                                    } else {
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    yearRepeat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            InitiallizeRepeatSetting(yearRepeat, yearRepeatWindow);
                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString();

                                            yearEndButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    endDateTextLayout.setVisibility(View.VISIBLE);
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                    endDateTextView.setText("종료일 " + (calendarN.get(Calendar.YEAR) + 1) + "년 " + (calendarN.get(Calendar.MONTH) + 1) + "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                    endDatePicker.init(calendarN.get(Calendar.YEAR) + 1, calendarN.get(Calendar.MONTH), calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                        @Override
                                                        public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                            Calendar calendarE = Calendar.getInstance();
                                                            calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                            int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                            endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                        }
                                                    });
                                                    selectedRepeatTag = yearRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString();
                                                            endDateTextLayout.setVisibility(View.GONE);
                                                            endDatePicker.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }

                                            });

                                            //text 제목
                                            endDateTextView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                        endDatePicker.setVisibility(View.VISIBLE);
                                                    } else {
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        /*if(endDateTextView.getText().toString().equals(null)||endDateTextView.getText().toString().equals(""))
                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString();
                                        else
                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString()+"\n"+datePicker.getYear()+"."+(datePicker.getMonth()+1)+"."+datePicker.getDayOfMonth()+" - "+endDatePicker.getYear()+"."+(endDatePicker.getMonth()+1)+"."+endDatePicker.getDayOfMonth();
*/
                                        }
                                    });

                                    //뒤로가기 버튼
                                    backButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            addScheduleRepeatScreen.dismiss();
                                        }
                                    });

                                    //확인 버튼
                                    checkRButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (selectedRepeatTag != null)
                                                repeatButton.setText(selectedRepeatTag);
                                            endDatePicker.setVisibility(View.GONE);
                                            addScheduleRepeatScreen.dismiss();
                                        }
                                    });

                                }
                            }

                        });

                        //종료 버튼
                        clearBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addScheduleScreen.dismiss();
                            }
                        });

                        //확인 버튼
                        check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditDiary editDiary = new EditDiary();
                                String completeDatetime = dateButton.getText().toString() + " " + timeButton.getText().toString();
                                String completeDate = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                                String completeTime;
                                if (Build.VERSION.SDK_INT < 23) {
                                    int getHour = timePicker.getCurrentHour();
                                    int getMinute = timePicker.getCurrentMinute();
                                    completeTime = getHour + ":" + getMinute;
                                } else {
                                    int getHour = timePicker.getHour();
                                    int getMinute = timePicker.getMinute();
                                    completeTime = getHour + ":" + getMinute;
                                }
                                if (repeatButton.getText().toString() != "없음" && repeatButton.getText().toString().contains(" - ")) {

                                    String[] repeatText = repeatButton.getText().toString().split("\n");
                                    String[] repeatDate = repeatText[1].split(" - ");
                                    String startDate = repeatDate[0].replaceAll("\\.", "-");
                                    String endDate = repeatDate[1].replaceAll("\\.", "-");

                                    startMyTask(editDiary,"http://13.124.99.123/diaryedit.php", id, edit_diary.getText().toString(), completeDatetime, (completeDate + " " + completeTime + ":" + "00"), alarmButton.getText().toString(), repeatText[0], startDate, endDate, schedule.getNum()+"");

                                } else
                                    startMyTask(editDiary,"http://13.124.99.123/diaryedit.php", id, edit_diary.getText().toString(), completeDatetime, (completeDate + " " + completeTime + ":" + "00"), alarmButton.getText().toString(), repeatButton.getText().toString(), null, null, schedule.getNum()+"");

                                //DateClassificationTask dateClassificationTask = new DateClassificationTask();
                                //dateClassificationTask.execute(id);
                                //need notifydatachanged
                                addScheduleScreen.dismiss();
                            }
                        });
                        //삭제 버튼
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                noRepeatDeleteLayout.setVisibility(View.GONE);
                                repeatDeleteLayout.setVisibility(View.GONE);
                                if (!deleteSchedulePopupWindow.isShowing()) {
                                    if(schedule.getRepeat_period().equals("없음")) {
                                        noRepeatDeleteLayout.setVisibility(View.VISIBLE);
                                        deleteSchedulePopupWindow.showAtLocation(popupViewD, Gravity.CENTER, 0, 0);
                                        noRepeatBackTextView.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view) {
                                                deleteSchedulePopupWindow.dismiss();
                                            }
                                        });
                                        noRepeatOkTextView.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view) {
                                                startMyTask(new DeleteDiaryTask(),id,schedule.getNum()+"");
                                                deleteSchedulePopupWindow.dismiss();
                                                addScheduleScreen.dismiss();
                                            }
                                        });
                                    }
                                    else {
                                        repeatDeleteLayout.setVisibility(View.VISIBLE);
                                        deleteSchedulePopupWindow.showAtLocation(popupViewD, Gravity.CENTER, 0, 0);
                                        allDeleteTextView.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view) {
                                                startMyTask(new DeleteDiaryTask(),id,schedule.getNum()+"");
                                                deleteSchedulePopupWindow.dismiss();
                                                addScheduleScreen.dismiss();
                                            }
                                        });

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        //시작날짜를 현재 날짜로 설정
        textDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ". " + day[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date1, boolean selected) {
                int dayNum = calendarView.getSelectedDate().getCalendar().get(Calendar.DAY_OF_WEEK);
                textDate.setText(calendarView.getSelectedDate().getDay() + ". " + day[dayNum - 1]);
                calendarListDate_pre = new ArrayList<Schedule>();
                calendarListData = new Hashtable<Date, ArrayList<Schedule>>();
                String completedate = date.format(calendarView.getSelectedDate().getDate());
                Log.e("completedate","taskinput"+completedate);
                ScheduleLoadTask scheduleLoadTask = new ScheduleLoadTask();
                startMyTask(scheduleLoadTask,id,completedate);

            }
        });



        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date1) {
                Log.v("CalendarMain",""+date1.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH));
                try {
                    if(!isDot.contains(date.parse(date1.getYear()+"-"+(date1.getMonth()+1)+"-"+1))) {
                        isDot.add(date.parse(date1.getYear() + "-" + (date1.getMonth() + 1) + "-" + 1));
                        EventDotTask eventDotTask = new EventDotTask();
                        startMyTask(eventDotTask, id, date1.getYear() + "", (date1.getMonth() + 1) + "");

                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //학사 일정
        watchAcademic = (Button) findViewById(R.id.watchAcademic);
        watchAcademic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CalendarMain.this, AcademicCalendar.class));
            }
        });

        gotoToday = (FloatingActionButton) findViewById(R.id.gotoToday);
        gotoToday.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendarView.setSelectedDate(CalendarDay.from(calendar.getTime()));
                calendarView.setCurrentDate(CalendarDay.from(calendar.getTime()),true);
                String completedate = date.format(calendar.getTime());
                calendarListAdapter.calendarAdapterData.clear();
                calendarListAdapter.notifyDataSetChanged();
                textDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ". " + day[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]);
                startMyTask(new ScheduleLoadTask(),id,completedate);
            }
        });


        addSchedule = (FloatingActionButton) findViewById(R.id.addSchedule);
        addSchedule.setOnClickListener(new View.OnClickListener() {
            int dateClick;
            int timeClick;
            int alarmClick;

            @Override
            public void onClick(View view) {

                //키보드 바로 뜨게(EditText)
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                edit_diary.setText(null);

                dateClick = 0;
                timeClick = 0;
                alarmClick = 0;
                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);
                alarmTable.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);

                noRepeat.setSelected(false);
                dayRepeat.setSelected(false);
                weekRepeat.setSelected(false);
                monthRepeat.setSelected(false);
                yearRepeat.setSelected(false);

                noRepeatWindow.setVisibility(View.VISIBLE);
                dayRepeatWindow.setVisibility(View.GONE);
                weekRepeatWindow.setVisibility(View.GONE);
                monthRepeatWindow.setVisibility(View.GONE);
                yearRepeatWindow.setVisibility(View.GONE);

                endDateTextLayout.setVisibility(View.GONE);
                endDatePicker.setVisibility(View.GONE);
                repeatButton.setText("없음");

                weekRepeatUnderTable.setVisibility(View.GONE);
                dayOfTheWeekCheck = new ArrayList<String>();

                if (calendarView.getSelectedDate() != null)
                    endDatePicker.setMinDate(calendarView.getSelectedDate().getCalendar().getTimeInMillis());
                else
                    endDatePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

                if (!addScheduleScreen.isShowing()) {
                    addScheduleScreen.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    final Calendar calendar = Calendar.getInstance();
                    if (calendarView.getSelectedDate() != null)
                        calendar.set(calendarView.getSelectedDate().getYear(), calendarView.getSelectedDate().getMonth(), calendarView.getSelectedDate().getDay());
                    final int daynum = calendar.get(Calendar.DAY_OF_WEEK);
                    dateButton.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum - 1] + ")");
                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker datePicker, int year, int month, int day1) {
                            Calendar calendarN = Calendar.getInstance();
                            calendarN.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            int daynum_datepicker = calendarN.get(Calendar.DAY_OF_WEEK);
                            dateButton.setText(year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                            endDatePicker.setMinDate(calendarN.getTimeInMillis());
                        }
                    });

                    String am_pm = null;
                    if (Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM)
                        am_pm = "오전 ";
                    else
                        am_pm = "오후 ";
                    timeButton.setText(am_pm + (Calendar.getInstance().get(Calendar.HOUR) + 1) + ":" + "00");
                    setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1, 0);
                    timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                            String am_pmN = null;
                            if (hourOfDay > 12) {
                                am_pmN = "오후 ";
                                hourOfDay = hourOfDay - 12;
                            } else if (hourOfDay == 12) {
                                am_pmN = "오후 ";
                            } else if (hourOfDay == 0) {
                                am_pmN = "오전 ";
                                hourOfDay += 12;
                            } else
                                am_pmN = "오전 ";
                            String minuteS = null;
                            minuteS = "" + minute;
                            if (minute < 10)
                                minuteS = "0" + minuteS;
                            timeButton.setText(am_pmN + hourOfDay + ":" + minuteS);
                        }
                    });
                    dateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dateClick % 2 == 0)
                                datePicker.setVisibility(View.VISIBLE);
                            else
                                datePicker.setVisibility(View.GONE);
                            dateClick++;
                        }
                    });
                    timeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (timeClick % 2 == 0)
                                timePicker.setVisibility(View.VISIBLE);
                            else
                                timePicker.setVisibility(View.GONE);
                            timeClick++;
                        }
                    });
                    alarmButton.setText("없음");
                    alarmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (alarmClick % 2 == 0) {
                                alarmTable.setVisibility(View.VISIBLE);

                                //알람 버튼 설정
                                zeroAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("정시");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                fiveMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("5분 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                tenMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("10분 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                fifteenMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("15분 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                thirteenMinuteAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("30분 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                oneHourAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("1시간 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                twoHourAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("2시간 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                threeHourAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("3시간 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                twelveHourAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("12시간 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                oneDayAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("1일 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                twoDayAgo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("2일 전");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });

                                noAlarm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alarmButton.setText("없음");
                                        alarmTable.setVisibility(View.GONE);
                                        alarmClick++;
                                    }
                                });
                            } else
                                alarmTable.setVisibility(View.GONE);
                            alarmClick++;
                        }
                    });
                    //반복 버튼
                    repeatButton.setOnClickListener(new View.OnClickListener() {

                        Calendar calendarN;
                        int daynum_datepicker;

                        @Override
                        public void onClick(View view) {
                            calendarN = Calendar.getInstance();
                            calendarN.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            daynum_datepicker = calendarN.get(Calendar.DAY_OF_WEEK);

                            if (!addScheduleRepeatScreen.isShowing()) {
                                addScheduleRepeatScreen.showAtLocation(popupViewR, Gravity.CENTER, 0, 0);

                                noRepeat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        InitiallizeRepeatSetting(noRepeat, noRepeatWindow);

                                        selectedRepeatTag = "없음";
                                    }
                                });

                                dayRepeat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        InitiallizeRepeatSetting(dayRepeat, dayRepeatWindow);
                                        selectedRepeatTag = dayRepeatTextWindow.getText().toString();

                                        dayRepeatEditText.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                                                if (charSequence.toString().equals("1") || charSequence.toString().equals(""))
                                                    dayRepeatTextWindow.setText("매일 반복");
                                                else if (charSequence.toString().equals("0")) {
                                                    Toast.makeText(getApplicationContext(), "잘못된 반복 주기 입니다.", Toast.LENGTH_SHORT).show();
                                                    dayRepeatEditText.setText("");
                                                    dayRepeatTextWindow.setText("매일 반복");
                                                } else
                                                    dayRepeatTextWindow.setText("매" + charSequence + "일 마다 반복");
                                                if (!TextUtils.isEmpty(endDateTextView.getText()))
                                                    selectedRepeatTag = dayRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                else
                                                    selectedRepeatTag = dayRepeatTextWindow.getText().toString();
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {

                                            }
                                        });
                                        //종료일 버튼
                                        dayEndButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                endDateTextLayout.setVisibility(View.VISIBLE);
                                                endDatePicker.setVisibility(View.VISIBLE);
                                                int month = ((calendarN.get(Calendar.MONTH) + 1) + 1);
                                                month = month > 12 ? month-12 : month;
                                                endDateTextView.setText("종료일 " + calendarN.get(Calendar.YEAR) + "년 " + month + "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                endDatePicker.init(calendarN.get(Calendar.YEAR), calendarN.get(Calendar.MONTH) + 1, calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                    @Override
                                                    public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                        Calendar calendarE = Calendar.getInstance();
                                                        calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                        int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                        endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                        selectedRepeatTag = dayRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    }
                                                });
                                                selectedRepeatTag = dayRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        selectedRepeatTag = dayRepeatTextWindow.getText().toString();
                                                        endDateTextLayout.setVisibility(View.GONE);
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                        });
                                        //text 제목
                                        endDateTextView.setOnClickListener(new View.OnClickListener() {
                                            int endClick = 0;

                                            @Override
                                            public void onClick(View view) {
                                                if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                } else {
                                                    endDatePicker.setVisibility(View.GONE);
                                                }
                                            }
                                        });

                                    }
                                });
                                weekRepeat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        WEEK_OF_DAY_TEXT = day[daynum_datepicker - 1] + "요일";
                                        WEEK_REPEAT_TEXT = "주 ";
                                        InitiallizeRepeatSetting(weekRepeat, weekRepeatWindow);
                                        selectedRepeatTag = weekRepeatTextWindow.getText().toString();
                                        weekRepeatUnderTable.setVisibility(View.VISIBLE);
                                        dayOfTheWeekCheck.add(day[daynum_datepicker - 1]);

                                        //하단 버튼 테이블 초기화 설정
                                        switch (day[daynum_datepicker - 1]) {
                                            case "월":
                                                weekRepeatMon.setSelected(true);
                                                break;
                                            case "화":
                                                weekRepeatTue.setSelected(true);
                                                break;
                                            case "수":
                                                weekRepeatWed.setSelected(true);
                                                break;
                                            case "목":
                                                weekRepeatThu.setSelected(true);
                                                break;
                                            case "금":
                                                weekRepeatFri.setSelected(true);
                                                break;
                                            case "토":
                                                weekRepeatSat.setSelected(true);
                                                break;
                                            case "일":
                                                weekRepeatSun.setSelected(true);
                                                break;
                                            default:
                                                break;
                                        }

                                        //요일 버튼 클릭 설정
                                        weekRepeatMon.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatMon, "월");
                                            }
                                        });

                                        weekRepeatTue.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatTue, "화");
                                            }
                                        });

                                        weekRepeatWed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatWed, "수");
                                            }
                                        });

                                        weekRepeatThu.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatThu, "목");
                                            }
                                        });

                                        weekRepeatFri.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatFri, "금");
                                            }
                                        });

                                        weekRepeatSat.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatSat, "토");
                                            }
                                        });

                                        weekRepeatSun.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkWeekOfTheDayProcess(weekRepeatSun, "일");
                                            }
                                        });

                                        weekRepeatEditText.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                                                if (charSequence.toString().equals("1") || charSequence.toString().equals("")) {
                                                    WEEK_REPEAT_TEXT = "주 ";
                                                    weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                                                } else if (charSequence.toString().equals("0")) {
                                                    Toast.makeText(getApplicationContext(), "잘못된 반복 주기 입니다.", Toast.LENGTH_SHORT).show();
                                                    weekRepeatEditText.setText("");
                                                    WEEK_REPEAT_TEXT = "주 ";
                                                    weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                                                } else {
                                                    WEEK_REPEAT_TEXT = charSequence + "주 마다 ";
                                                    weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                                                }
                                                if (!TextUtils.isEmpty(endDateTextView.getText()))
                                                    selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                else
                                                    selectedRepeatTag = weekRepeatTextWindow.getText().toString();
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {

                                            }
                                        });

                                        weekEndButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                endDateTextLayout.setVisibility(View.VISIBLE);
                                                endDatePicker.setVisibility(View.VISIBLE);
                                                int month = ((calendarN.get(Calendar.MONTH) + 1) + 1);
                                                month = month > 12 ? month-12 : month;
                                                endDateTextView.setText("종료일 " + calendarN.get(Calendar.YEAR) + "년 " + month + "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                endDatePicker.init(calendarN.get(Calendar.YEAR), calendarN.get(Calendar.MONTH) + 1, calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                    @Override
                                                    public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                        Calendar calendarE = Calendar.getInstance();
                                                        calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                        int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                        endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                        selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    }
                                                });
                                                selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        selectedRepeatTag = weekRepeatTextWindow.getText().toString();
                                                        endDateTextLayout.setVisibility(View.GONE);
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                        });

                                        //text 제목
                                        endDateTextView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                } else {
                                                    endDatePicker.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                });
                                monthRepeat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        InitiallizeRepeatSetting(monthRepeat, monthRepeatWindow);
                                        selectedRepeatTag = monthRepeatTextWindow.getText().toString();
                                        monthRepeatEditText.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                                                if (charSequence.toString().equals("1") || charSequence.toString().equals(""))
                                                    monthRepeatTextWindow.setText("매월 " + datePicker.getDayOfMonth() + "일 반복");
                                                else if (charSequence.toString().equals("0")) {
                                                    Toast.makeText(getApplicationContext(), "잘못된 반복 주기 입니다.", Toast.LENGTH_SHORT).show();
                                                    weekRepeatEditText.setText("");
                                                    monthRepeatTextWindow.setText("매월 " + datePicker.getDayOfMonth() + "일 반복");
                                                } else
                                                    monthRepeatTextWindow.setText("매" + charSequence + "개월 마다 " + datePicker.getDayOfMonth() + "일 반복");

                                                if (!TextUtils.isEmpty(endDateTextView.getText()))
                                                    selectedRepeatTag = monthRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                else
                                                    selectedRepeatTag = monthRepeatTextWindow.getText().toString();
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {

                                            }
                                        });

                                        monthEndButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                endDateTextLayout.setVisibility(View.VISIBLE);
                                                endDatePicker.setVisibility(View.VISIBLE);
                                                endDateTextView.setText("종료일 " + (calendarN.get(Calendar.YEAR) + 1) + "년 " + (calendarN.get(Calendar.MONTH) + 1) + "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                endDatePicker.init(calendarN.get(Calendar.YEAR) + 1, calendarN.get(Calendar.MONTH), calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                    @Override
                                                    public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                        Calendar calendarE = Calendar.getInstance();
                                                        calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                        int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                        endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                        selectedRepeatTag = monthRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    }
                                                });
                                                selectedRepeatTag = monthRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        selectedRepeatTag = monthRepeatTextWindow.getText().toString();
                                                        endDateTextLayout.setVisibility(View.GONE);
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                        });

                                        //text 제목
                                        endDateTextView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                } else {
                                                    endDatePicker.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                });
                                yearRepeat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        InitiallizeRepeatSetting(yearRepeat, yearRepeatWindow);
                                        selectedRepeatTag = yearRepeatTextWindow.getText().toString();

                                        yearEndButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                endDateTextLayout.setVisibility(View.VISIBLE);
                                                endDatePicker.setVisibility(View.VISIBLE);
                                                endDateTextView.setText("종료일 " + (calendarN.get(Calendar.YEAR) + 1) + "년 " + (calendarN.get(Calendar.MONTH) + 1) + "월 " + calendarN.get(Calendar.DAY_OF_MONTH) + "일 " + "(" + day[daynum_datepicker - 1] + ")");
                                                endDatePicker.init(calendarN.get(Calendar.YEAR) + 1, calendarN.get(Calendar.MONTH), calendarN.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                                    @Override
                                                    public void onDateChanged(DatePicker datePickerD, int year, int month, int day1) {
                                                        Calendar calendarE = Calendar.getInstance();
                                                        calendarE.set(datePickerD.getYear(), datePickerD.getMonth(), datePickerD.getDayOfMonth());
                                                        int daynum_enddatepicker = calendarE.get(Calendar.DAY_OF_WEEK);
                                                        endDateTextView.setText("종료일 " + year + "년 " + (month + 1) + "월 " + day1 + "일 " + "(" + day[daynum_enddatepicker - 1] + ")");
                                                        selectedRepeatTag = yearRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                    }
                                                });
                                                selectedRepeatTag = yearRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                                                endDatePickerButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        selectedRepeatTag = yearRepeatTextWindow.getText().toString();
                                                        endDateTextLayout.setVisibility(View.GONE);
                                                        endDatePicker.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                        });

                                        //text 제목
                                        endDateTextView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (endDateTextLayout.getVisibility() == View.VISIBLE && endDatePicker.getVisibility() == View.GONE) {
                                                    endDatePicker.setVisibility(View.VISIBLE);
                                                } else {
                                                    endDatePicker.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                        /*if(endDateTextView.getText().toString().equals(null)||endDateTextView.getText().toString().equals(""))
                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString();
                                        else
                                            selectedRepeatTag = yearRepeatTextWindow.getText().toString()+"\n"+datePicker.getYear()+"."+(datePicker.getMonth()+1)+"."+datePicker.getDayOfMonth()+" - "+endDatePicker.getYear()+"."+(endDatePicker.getMonth()+1)+"."+endDatePicker.getDayOfMonth();
*/
                                    }
                                });

                                //뒤로가기 버튼
                                backButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        addScheduleRepeatScreen.dismiss();
                                    }
                                });

                                //확인 버튼
                                checkRButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (selectedRepeatTag != null)
                                            repeatButton.setText(selectedRepeatTag);
                                        endDatePicker.setVisibility(View.GONE);
                                        addScheduleRepeatScreen.dismiss();
                                    }
                                });
                            }
                        }
                    });

                    //종료 버튼
                    clearBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addScheduleScreen.dismiss();
                        }
                    });

                    //확인 버튼
                    check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddDiary addDiary = new AddDiary();
                            String completeDatetime = dateButton.getText().toString() + " " + timeButton.getText().toString();
                            String completeDate = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                            String completeTime;
                            if (Build.VERSION.SDK_INT < 23) {
                                int getHour = timePicker.getCurrentHour();
                                int getMinute = timePicker.getCurrentMinute();
                                completeTime = getHour + ":" + getMinute;
                            } else {
                                int getHour = timePicker.getHour();
                                int getMinute = timePicker.getMinute();
                                completeTime = getHour + ":" + getMinute;
                            }
                            if (repeatButton.getText().toString() != "없음"&&repeatButton.getText().toString().contains(" - ")) {
                                String[] repeatText = repeatButton.getText().toString().split("\n");
                                String[] repeatDate = repeatText[1].split(" - ");
                                String startDate = repeatDate[0].replaceAll("\\.", "-");
                                String endDate = repeatDate[1].replaceAll("\\.", "-");
                                startMyTask(addDiary,"http://13.124.99.123/diaryinsert.php", id, edit_diary.getText().toString(), completeDatetime, (completeDate + " " + completeTime + ":" + "00"), alarmButton.getText().toString(), repeatText[0], startDate, endDate);
                            } else
                                startMyTask(addDiary,"http://13.124.99.123/diaryinsert.php", id, edit_diary.getText().toString(), completeDatetime, (completeDate + " " + completeTime + ":" + "00"), alarmButton.getText().toString(), repeatButton.getText().toString(), null, null);

                            //DateClassificationTask dateClassificationTask = new DateClassificationTask();
                            //dateClassificationTask.execute(id);
                            //need notifydatachanged
                            addScheduleScreen.dismiss();
                        }
                    });

                }
            }
        });


        //위아래 제스쳐
        //ListView calendarList = (ListView)findViewById(R.id.calendar_list);

        /*watchAcademic.setOnTouchListener(new View.OnTouchListener(){
            private float initialY, finalY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        finalY = motionEvent.getY();

                        //위로 스와이프
                        if (finalY>initialY) {
                            Toast.makeText(CalendarMain.this,"위로 스와이프",Toast.LENGTH_SHORT).show();
                        }
                        if (finalY<initialY) {
                            Toast.makeText(CalendarMain.this,"아래로 스와이프",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });*/


    }
    public void SettingCalendarScreen(){
        Calendar calendar = Calendar.getInstance();
        EventDotTask initialEventDotTask = new EventDotTask();
        ScheduleLoadTask initialScheduleLoadTask = new ScheduleLoadTask();
        calendarListDate_pre = new ArrayList<Schedule>();
        calendarListData = new Hashtable<Date, ArrayList<Schedule>>();
        try {
            isDot.add(date.parse(calendarView.getCurrentDate().getYear() + "-" + (calendarView.getCurrentDate().getMonth() + 1) + "-" + "1"));
        }catch (Exception e){
            e.printStackTrace();
        }
        startMyTask(initialEventDotTask,id,calendarView.getCurrentDate().getYear()+"",(calendarView.getCurrentDate().getMonth()+1)+"");
        startMyTask(initialScheduleLoadTask,id,(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)));
    }

    public void UpdateCalendarScreen() {
        isDot = new ArrayList<Date>();
        calendarView.removeDecorators();
        calendarView.addDecorators(
                new OneDayDecorator(),
                new SundayDecorator(),
                new SaturdayDecorator());
        EventDotTask updateEventDotTask = new EventDotTask();
        ScheduleLoadTask updateScheduleLoadTask = new ScheduleLoadTask();
        try {
            calendarListAdapter.calendarAdapterData.clear();
            calendarListAdapter.notifyDataSetChanged();
            isDot.add(date.parse(calendarView.getSelectedDate().getYear() + "-" + (calendarView.getSelectedDate().getMonth() + 1) + "-" + "1"));
            startMyTask(updateEventDotTask, id, calendarView.getSelectedDate().getYear() + "", (calendarView.getSelectedDate().getMonth() + 1) + "");
            startMyTask(updateScheduleLoadTask, id, calendarView.getSelectedDate().getYear() + "-" + (calendarView.getSelectedDate().getMonth() + 1) + "-" + (calendarView.getSelectedDate().getDay()));
        }catch(Exception e){
            Calendar calendar = Calendar.getInstance();
            calendarListAdapter.calendarAdapterData.clear();
            calendarListAdapter.notifyDataSetChanged();
            startMyTask(updateEventDotTask,id,calendarView.getCurrentDate().getYear()+"",(calendarView.getCurrentDate().getMonth()+1)+"");
            startMyTask(updateScheduleLoadTask,id,(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)));
            e.printStackTrace();
        }
    }

    //asynctask 병렬처리
    public void startMyTask(AsyncTask asyncTask, String... params){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    public void InitiallizeRepeatSetting(Button button, LinearLayout linearLayout) {

        Calendar calendarN;
        int daynum_datepicker;
        calendarN = Calendar.getInstance();
        calendarN.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        daynum_datepicker = calendarN.get(Calendar.DAY_OF_WEEK);


        noRepeat.setSelected(false);
        dayRepeat.setSelected(false);
        weekRepeat.setSelected(false);
        monthRepeat.setSelected(false);
        yearRepeat.setSelected(false);
        button.setSelected(true);

        noRepeatWindow.setVisibility(View.GONE);
        dayRepeatWindow.setVisibility(View.GONE);
        weekRepeatWindow.setVisibility(View.GONE);
        monthRepeatWindow.setVisibility(View.GONE);
        yearRepeatWindow.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        dayRepeatEditText.setText("");
        weekRepeatEditText.setText("");
        monthRepeatEditText.setText("");

        dayRepeatTextWindow.setText("매일 반복");
        weekRepeatTextWindow.setText("매주 " + day[daynum_datepicker - 1] + "요일 반복");
        monthRepeatTextWindow.setText("매월 " + datePicker.getDayOfMonth() + "일 반복");
        yearRepeatTextWindow.setText("매년 " + (datePicker.getMonth() + 1) + "월 " + datePicker.getDayOfMonth() + "일 반복");

        endDateTextLayout.setVisibility(View.GONE);

        endDatePicker.setVisibility(View.GONE);
        weekRepeatUnderTable.setVisibility(View.GONE);

        weekRepeatMon.setSelected(false);
        weekRepeatTue.setSelected(false);
        weekRepeatWed.setSelected(false);
        weekRepeatThu.setSelected(false);
        weekRepeatFri.setSelected(false);
        weekRepeatSat.setSelected(false);
        weekRepeatSun.setSelected(false);

        dayOfTheWeekCheck = new ArrayList<String>();

    }

    private void checkWeekOfTheDayProcess(Button button, String dayoftheweek) {
        if (button.isSelected() && dayOfTheWeekCheck.size() > 1) {
            button.setSelected(false);
            dayOfTheWeekCheck.remove(dayoftheweek);
            if (dayOfTheWeekCheck.size() == 1) {
                WEEK_OF_DAY_TEXT = dayOfTheWeekCheck.get(0) + "요일";
                weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                if (!TextUtils.isEmpty(endDateTextView.getText()))
                    selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                else
                    selectedRepeatTag = weekRepeatTextWindow.getText().toString();
            } else {
                WEEK_OF_DAY_TEXT = "";
                for (int i = 0; i < 7; i++) {
                    if (dayOfTheWeekCheck.contains(dayOfTheWeekCheckBase[i]))
                        WEEK_OF_DAY_TEXT = WEEK_OF_DAY_TEXT + ("," + dayOfTheWeekCheckBase[i]);
                }
                WEEK_OF_DAY_TEXT = WEEK_OF_DAY_TEXT.replaceFirst(",", "");
                weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
                if (!TextUtils.isEmpty(endDateTextView.getText()))
                    selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
                else
                    selectedRepeatTag = weekRepeatTextWindow.getText().toString();
            }
        } else if (button.isSelected() && dayOfTheWeekCheck.size() <= 1) {
        } else {
            button.setSelected(true);
            dayOfTheWeekCheck.add(dayoftheweek);
            WEEK_OF_DAY_TEXT = "";
            for (int i = 0; i < 7; i++) {
                if (dayOfTheWeekCheck.contains(dayOfTheWeekCheckBase[i]))
                    WEEK_OF_DAY_TEXT = WEEK_OF_DAY_TEXT + ("," + dayOfTheWeekCheckBase[i]);
            }
            WEEK_OF_DAY_TEXT = WEEK_OF_DAY_TEXT.replaceFirst(",", "");
            weekRepeatTextWindow.setText("매" + WEEK_REPEAT_TEXT + WEEK_OF_DAY_TEXT + " 반복");
            if (!TextUtils.isEmpty(endDateTextView.getText()))
                selectedRepeatTag = weekRepeatTextWindow.getText().toString() + "\n" + datePicker.getYear() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getDayOfMonth() + " - " + endDatePicker.getYear() + "." + (endDatePicker.getMonth() + 1) + "." + endDatePicker.getDayOfMonth();
            else
                selectedRepeatTag = weekRepeatTextWindow.getText().toString();
        }
    }

    private void setTime(int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
    }

    //일요일에 빨간색
    public class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    //토요일에 파란색
    public class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    //현재 날짜에 초록색 색깔 넣기
    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(getDrawable(R.drawable.calendarview_circle));
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        }

        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }

    public class DotDecorator implements DayViewDecorator {

        private ArrayList<Integer> color;
        private CalendarDay date;

        public DotDecorator(ArrayList<Integer> color,CalendarDay date){
            this.color = color;
            this.date = date;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return (day.equals(date)&&date!=null);
        }

        @Override
        public void decorate(DayViewFacade view) {
            if(color!=null)
                view.addSpan(new CustomMultipleDotSpan(3, color));
        }
    }

    public class OneDotDecorator implements DayViewDecorator {

        private ArrayList<Integer> color;

        public OneDotDecorator(){
            color = new ArrayList<Integer>();
            color.add(Color.rgb(255,111,0));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            try {
                return (dotData.get(day) == 1);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new CustomMultipleDotSpan(3, color));
        }
    }

    public class TwoDotDecorator implements DayViewDecorator {

        private ArrayList<Integer> color;

        public TwoDotDecorator(){
            color = new ArrayList<Integer>();
            color.add(Color.rgb(255,143,0));
            color.add(Color.rgb(255,111,0));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            try {
                return (dotData.get(day) == 2);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new CustomMultipleDotSpan(3, color));
        }
    }

    public class ThreeDotDecorator implements DayViewDecorator {

        private ArrayList<Integer> color;

        public ThreeDotDecorator(){
            color = new ArrayList<Integer>();
            color.add(Color.rgb(255,160,0));
            color.add(Color.rgb(255,143,0));
            color.add(Color.rgb(255,111,0));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            try {
                return (dotData.get(day) == 3);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new CustomMultipleDotSpan(3, color));
        }
    }

    public class FourDotDecorator implements DayViewDecorator {

        private ArrayList<Integer> color;

        public FourDotDecorator(){
            color = new ArrayList<Integer>();
            color.add(Color.rgb(255,179,0));
            color.add(Color.rgb(255,160,0));
            color.add(Color.rgb(255,143,0));
            color.add(Color.rgb(255,111,0));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            try {
                return (dotData.get(day) == 4);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new CustomMultipleDotSpan(3, color));
        }
    }

    public class FiveDotDecorator implements DayViewDecorator {

        private ArrayList<Integer> color;

        public FiveDotDecorator(){
            color = new ArrayList<Integer>();
            color.add(Color.rgb(255,193,7));
            color.add(Color.rgb(255,179,0));
            color.add(Color.rgb(255,160,0));
            color.add(Color.rgb(255,143,0));
            color.add(Color.rgb(255,111,0));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            try {
                return (dotData.get(day) >= 5);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new CustomMultipleDotSpan(3, color));
        }
    }

    public class CustomMultipleDotSpan implements LineBackgroundSpan{

        private final float radius;
        private ArrayList<Integer> color;

        public CustomMultipleDotSpan(float radius, ArrayList<Integer> color){
            this.radius = radius;
            this.color = new ArrayList<Integer>();
            this.color = color;
        }

        @Override
        public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom,
                                   CharSequence charSequence,
                                   int start, int end, int lineNum) {

            int total = color.size() > 5 ? 5 : color.size();
            int leftMost = (total - 1) * -5;

            for(int i = 0; i < total; i++){
                int oldColor = paint.getColor();
                if(color.get(i) != 0) {
                    paint.setColor(color.get(i));
                }
                canvas.drawCircle((left+right) / 2 - leftMost, bottom + (2 * radius), radius, paint);
                paint.setColor(oldColor);
                leftMost = leftMost + 10;

            }
        }

    }

    public void setAddSchedulePopup(View view) {
        //알림 버튼 테이블 요소들
        zeroAgo = (Button) view.findViewById(R.id.zero_ago);
        fiveMinuteAgo = (Button) view.findViewById(R.id.fiveminute_ago);
        tenMinuteAgo = (Button) view.findViewById(R.id.tenminute_ago);
        fifteenMinuteAgo = (Button) view.findViewById(R.id.fifteenminute_ago);
        thirteenMinuteAgo = (Button) view.findViewById(R.id.thirteenminute_ago);
        oneHourAgo = (Button) view.findViewById(R.id.onehour_ago);
        twoHourAgo = (Button) view.findViewById(R.id.twohour_ago);
        threeHourAgo = (Button) view.findViewById(R.id.threehour_ago);
        twelveHourAgo = (Button) view.findViewById(R.id.twelvehour_ago);
        oneDayAgo = (Button) view.findViewById(R.id.oneday_ago);
        twoDayAgo = (Button) view.findViewById(R.id.twoday_ago);
        noAlarm = (Button) view.findViewById(R.id.no_alarm);

        //일정 추가
        clearBack = (ImageButton) view.findViewById(R.id.clear);
        check = (ImageButton) view.findViewById(R.id.check);
        edit_diary = (EditText) view.findViewById(R.id.edit_diary);
        datePicker = (DatePicker) view.findViewById(R.id.datepicker);
        dateButton = (Button) view.findViewById(R.id.datebutton);
        timePicker = (TimePicker) view.findViewById(R.id.timepicker);
        timeButton = (Button) view.findViewById(R.id.timebutton);
        alarmTable = (TableLayout) view.findViewById(R.id.alarmtable);
        alarmButton = (Button) view.findViewById(R.id.alarmbutton);
        repeatButton = (Button) view.findViewById(R.id.repeatbutton);

        //일정 삭제 관련
        deleteButton = (ImageButton) view.findViewById(R.id.delete_diary);
    }


    public void setAddScheduleRepeatPopup(View view) {
        //반복 창 버튼
        noRepeat = (Button) view.findViewById(R.id.no_repeat);
        dayRepeat = (Button) view.findViewById(R.id.day_repeat);
        weekRepeat = (Button) view.findViewById(R.id.week_repeat);
        monthRepeat = (Button) view.findViewById(R.id.month_repeat);
        yearRepeat = (Button) view.findViewById(R.id.year_repeat);


        noRepeatWindow = (LinearLayout) view.findViewById(R.id.no_repeat_window);
        dayRepeatWindow = (LinearLayout) view.findViewById(R.id.day_repeat_window);
        weekRepeatWindow = (LinearLayout) view.findViewById(R.id.week_repeat_window);
        monthRepeatWindow = (LinearLayout) view.findViewById(R.id.month_repeat_window);
        yearRepeatWindow = (LinearLayout) view.findViewById(R.id.year_repeat_window);

        //반복 설정 화면
        backButton = (ImageButton) view.findViewById(R.id.back);
        checkRButton = (ImageButton) view.findViewById(R.id.checkR);

        dayRepeatTextWindow = (TextView) view.findViewById(R.id.day_repeat_text_window);
        weekRepeatTextWindow = (TextView) view.findViewById(R.id.week_repeat_text_window);
        monthRepeatTextWindow = (TextView) view.findViewById(R.id.month_repeat_text_window);
        yearRepeatTextWindow = (TextView) view.findViewById(R.id.year_repeat_text_window);

        dayRepeatEditText = (EditText) view.findViewById(R.id.day_repeat_edittext_window);
        weekRepeatEditText = (EditText) view.findViewById(R.id.week_repeat_edittext_window);
        monthRepeatEditText = (EditText) view.findViewById(R.id.month_repeat_edittext_window);

        endDateTextLayout = (RelativeLayout) view.findViewById(R.id.end_date_text_layout);
        endDatePicker = (DatePicker) view.findViewById(R.id.end_date_datepicker);
        endDateTextView = (TextView) view.findViewById(R.id.end_date_text_window);
        endDatePickerButton = (ImageButton) view.findViewById(R.id.end_date_datepicker_close_button);

        //종료일 버튼
        dayEndButton = (Button) view.findViewById(R.id.day_end_date_button);
        weekEndButton = (Button) view.findViewById(R.id.week_end_date_button);
        monthEndButton = (Button) view.findViewById(R.id.month_end_date_button);
        yearEndButton = (Button) view.findViewById(R.id.year_end_date_button);

        //하단 테이블
        weekRepeatUnderTable = (TableLayout) view.findViewById(R.id.repeat_week_under_table);
        weekRepeatMon = (Button) view.findViewById(R.id.repeat_week_mon);
        weekRepeatTue = (Button) view.findViewById(R.id.repeat_week_tue);
        weekRepeatWed = (Button) view.findViewById(R.id.repeat_week_wed);
        weekRepeatThu = (Button) view.findViewById(R.id.repeat_week_thu);
        weekRepeatFri = (Button) view.findViewById(R.id.repeat_week_fri);
        weekRepeatSat = (Button) view.findViewById(R.id.repeat_week_sat);
        weekRepeatSun = (Button) view.findViewById(R.id.repeat_week_sun);


    }

    //일정 입력(DB)   AsyncTask 사용해도 OK!(수 초내 끝나는 작업이므로)
    class AddDiary extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String id = (String) strings[1];
            String context = (String) strings[2];
            String complete_datetime = (String) strings[3];
            String complete_datetime_numeric = (String) strings[4];
            String alarm = (String) strings[5];
            String repeat_period = (String) strings[6];
            String repeat_start_date = (String) strings[7];
            String repeat_end_date = (String) strings[8];

            String serverUrl = (String) strings[0];
            String postParameters = "id=" + id + "&context=" + context + "&complete_datetime=" + complete_datetime + "&complete_datetime_numeric=" + complete_datetime_numeric + "&alarm=" + alarm + "&repeat_period=" + repeat_period + "&repeat_start_date=" + repeat_start_date + "&repeat_end_date=" + repeat_end_date;

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

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

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d("phptest", "Signup: Error", e);

                return new String("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            UpdateCalendarScreen();
        }
    }

    //일정 편집 (DB)   AsyncTask 사용해도 OK!(수 초내 끝나는 작업이므로)
    class EditDiary extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String num = (String) strings[9];
            String id = (String) strings[1];
            String context = (String) strings[2];
            String complete_datetime = (String) strings[3];
            String complete_datetime_numeric = (String) strings[4];
            String alarm = (String) strings[5];
            String repeat_period = (String) strings[6];
            String repeat_start_date = (String) strings[7];
            String repeat_end_date = (String) strings[8];


            String serverUrl = (String) strings[0];
            String postParameters = "num=" + num + "&id=" + id + "&context=" + context + "&complete_datetime=" + complete_datetime + "&complete_datetime_numeric=" + complete_datetime_numeric + "&alarm=" + alarm + "&repeat_period=" + repeat_period + "&repeat_start_date=" + repeat_start_date + "&repeat_end_date=" + repeat_end_date;

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

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

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d("phptest", "Signup: Error", e);

                return new String("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            UpdateCalendarScreen();
        }
    }

    public class DeleteDiaryTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0] +"&num="+params[1];
            Log.e("POST", param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/user_diary_schedule_list_delete.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("euc-kr"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                int responseStatusCode = conn.getResponseCode();
                if (responseStatusCode == conn.HTTP_OK)
                    is = conn.getInputStream();
                else
                    is = conn.getErrorStream();

                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA", data);

                return null;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            UpdateCalendarScreen();
        }
    }

    //AsyncTask 사용은 가능 but, 두번이상 사용한다는 점에서 적절한 방법은 아님
    public class ScheduleLoadTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String data = "";
            String param = "id=" + params[0] +"&complete_date="+params[1];
            Log.e("POST", param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://13.124.99.123/user_diary_schedule_list_load.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("euc-kr"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                int responseStatusCode = conn.getResponseCode();
                if (responseStatusCode == conn.HTTP_OK)
                    is = conn.getInputStream();
                else
                    is = conn.getErrorStream();

                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA", data);
                Log.v("RECV DATA", data);
                return (params[1]+"|||||"+data);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return (params[1]+"|||||"+null);
        }

        @Override
        protected void onPostExecute(String result) {
            String[] resultSplit = result.split("\\|\\|\\|\\|\\|");
            Schedule schedule;
            if (resultSplit.length==1){
                calendarListDate_pre.add(new Schedule(0,"일정이 없습니다.", null,null,null, null,null,null,null,null,null));
                calendarListAdapter = new CalendarListAdapter(calendarListDate_pre);
                calendarList.setAdapter(calendarListAdapter);
            }
            else {
                String[] splitlist;
                splitlist = resultSplit[1].split("\n");
                java.sql.Date sqlStartDate;
                java.sql.Date sqlEndDate;
                try {
                    for(int i=0;i<splitlist.length;i++) {
                        String[] splitstring=splitlist[i].split("/");
                        java.util.Date transCompleteDateTime = datetime.parse(splitstring[3]);
                        if(splitstring.length<=7){
                            sqlStartDate = null;
                            sqlEndDate = null;
                        }
                        else{
                            java.util.Date transStartDate = date.parse(splitstring[7]);
                            java.util.Date transEndDate = date.parse(splitstring[8]);
                            sqlStartDate = new java.sql.Date(transStartDate.getTime());
                            sqlEndDate = new java.sql.Date(transEndDate.getTime());
                        }
                        if(splitstring.length<=9)
                            schedule = new Schedule(Integer.parseInt(splitstring[0]),splitstring[1],splitstring[2],transCompleteDateTime,splitstring[4],splitstring[5],splitstring[6],sqlStartDate,sqlEndDate,null,null);
                        else
                            schedule = new Schedule(Integer.parseInt(splitstring[0]),splitstring[1],splitstring[2],transCompleteDateTime,splitstring[4],splitstring[5],splitstring[6],sqlStartDate,sqlEndDate,splitstring[9],splitstring[10]);

                        calendarListDate_pre.add(schedule);
                        Date completeDate = date.parse(resultSplit[0]);
                        calendarListData.put(completeDate, calendarListDate_pre);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                try {
                    java.util.Date completedatekey = date.parse(resultSplit[0]);
                    calendarListAdapter = new CalendarListAdapter(calendarListData.get(completedatekey));
                    calendarList.setAdapter(calendarListAdapter);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    public class EventDotTask extends AsyncTask<String, Integer, JSONObject> {

        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           //progressDialogDot = progressDialogDot.show(CalendarMain.this, "일정을 불러오는 중입니다...", null, true, true);

        }


        @Override
        protected JSONObject doInBackground(String... params) {

            String data = "";
            try {
                /* 인풋 파라메터값 생성 */
                String param = "id=" + params[0] + "&complete_date=" + params[1]+"-"+params[2]+"-"+1;
                Log.e("POST", param);

                /* 서버연결 */
                URL url = new URL(
                            "http://13.124.99.123/dot_data_calculate_renewal.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */

                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("euc-kr"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                int responseStatusCode = conn.getResponseCode();
                if (responseStatusCode == conn.HTTP_OK)
                    is = conn.getInputStream();
                else
                    is = conn.getErrorStream();

                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                data = buff.toString();
                in.close();


                /* 서버에서 응답 */
                Log.e("RECV DATA", data);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try{
                jsonObject = new JSONObject(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //progressDialogDot.dismiss();

            if(jsonObject==null){

            }else{
                for(Iterator<String> dateKey = jsonObject.keys(); dateKey.hasNext();){
                    try {
                        String dateKeystr = dateKey.next();
                        Date calendarDate = date.parse(dateKeystr);
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(calendarDate);
                        CalendarDay calendarDay = CalendarDay.from(calendar);
                        View view = calendarView.getChildAt(Integer.parseInt(dayFormat.format(calendarDate)));
                        int dot_NUM = Integer.parseInt(jsonObject.getString(dateKeystr));
                        switch (dot_NUM){
                            case 0:
                                break;
                            case 1:
                                calendarView.addDecorator(new DotDecorator(oneColor,calendarDay));
                                break;
                            case 2:
                                calendarView.addDecorator(new DotDecorator(twoColor,calendarDay));
                                break;
                            case 3:
                                calendarView.addDecorator(new DotDecorator(threeColor,calendarDay));
                                break;
                            case 4:
                                calendarView.addDecorator(new DotDecorator(fourColor,calendarDay));
                                break;
                            default:
                                calendarView.addDecorator(new DotDecorator(fiveColor,calendarDay));
                                break;
                        }
                        /*if(dot_NUM>0&&dot_NUM<5)
                            dotData.put(calendarDay,dot_NUM);
                        else if (dot_NUM>=5)
                            dotData.put(calendarDay,5);*/

                    }catch(Exception e) {
                        e.printStackTrace();
                    }

                }
                /*calendarView.addDecorators(
                        new OneDotDecorator(),
                        new TwoDotDecorator(),
                        new ThreeDotDecorator(),
                        new FourDotDecorator(),
                        new FiveDotDecorator());*/

            }

        }
    }


    public class CalendarListAdapter extends BaseAdapter{

        private ArrayList<Schedule> calendarAdapterData;

        public CalendarListAdapter(ArrayList<Schedule> calendarAdapterData){
            this.calendarAdapterData = calendarAdapterData;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getCount() {
            return calendarAdapterData.size();
        }

        @Override
        public Schedule getItem(int position) {
            return calendarAdapterData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.calendar_list_item, parent, false);
            }

            TextView contextTextView = (TextView)convertView.findViewById(R.id.diary_context_textview);
            TextView dateTextView = (TextView)convertView.findViewById(R.id.diary_compelete_date_textview);

            contextTextView.setText(calendarAdapterData.get(position).getContext());
            if(calendarAdapterData.get(position).getCompleteDateTime_numeric()==null)
                dateTextView.setText("");
            else {
                String subtext = calendarAdapterData.get(position).getCompleteDateTime_text();
                String[] subtextSplit = subtext.split("년 ");
                dateTextView.setText(subtextSplit[1]);
            }
            return convertView;
        }
    }


}