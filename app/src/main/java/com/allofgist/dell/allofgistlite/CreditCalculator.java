package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreditCalculator extends AppCompatActivity {

    //로그인 정보
    private String id = "LOGIN_ERROR";
    private String previousTitle;
    double completedCredits;
    double gpa;

    TextView watchLog;
    PopupWindow watchLogPopupWindow;
    View logPopupView;
    TextView watchLogCloseButton;
    RecyclerView watchLogList;

    EditText title;

    Button resetButton;
    Button addButton;
    Button saveButton;

    public static ArrayList<Calculator> calculatorData;
    RecyclerView calculatorList;
    public static CreditCalculatorAdapter creditCalculatorAdapter;
    public CalculatorLogAdapter calculatorLogAdapter;
    TextView result;

    PopupWindow noticePopupWindow;
    View popupView;

    TextView noticeText;
    TextView noticeCancel;
    TextView noticeOk;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> logTitleList;

    @Override
    public void onBackPressed() {
        if(watchLogPopupWindow.isShowing())
            watchLogPopupWindow.dismiss();
        else if(noticePopupWindow.isShowing())
            noticePopupWindow.dismiss();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            completedCredits = 0;
            gpa = 0;
            for (int i = 0; i < calculatorData.size(); i++) {
                double currentCredits = Double.parseDouble(calculatorData.get(i).getCredits());
                completedCredits += currentCredits;
                double grade = 0;
                switch (calculatorData.get(i).getGrade()) {
                    case "A+":
                        grade = 4.5;
                        break;
                    case "A0":
                        grade = 4.0;
                        break;
                    case "B+":
                        grade = 3.5;
                        break;
                    case "B0":
                        grade = 3.0;
                        break;
                    case "C+":
                        grade = 2.5;
                        break;
                    case "C0":
                        grade = 2.0;
                        break;
                    case "D+":
                        grade = 1.5;
                        break;
                    case "D0":
                        grade = 1.0;
                        break;
                    case "F":
                        grade = 0.0;
                        break;
                }
                gpa += (grade * currentCredits);
            }
            gpa = (Math.round(gpa/completedCredits*100.00)/100.00);
            result.setText(("Completed Credits : " + (int)completedCredits + " | GPA : " + gpa + "/4.5"));
        }catch (ArithmeticException e){
            e.printStackTrace();
            result.setText(("Completed Credits : " + 0 + " | GPA : " + 0 + "/4.5"));

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_calculator);

        //로그인 정보 가져오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(CreditCalculator.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeText.setText(R.string.notice_reset);
                noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        noticePopupWindow.dismiss();
                    }
                });
            }
        });

        noticeCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                noticePopupWindow.dismiss();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCalculator = new Intent(CreditCalculator.this,AddCreditCalculator.class);
                startActivity(addCalculator);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty())
                    GrayToast(CreditCalculator.this,"제목을 입력해주세요.");
                else if(calculatorData.isEmpty())
                    GrayToast(CreditCalculator.this,"최소 하나의 과목을 추가해주세요.");
                else{
                    if(saveButton.getText().toString().equals(getString(R.string.save_log_calculator))) {
                        logTitleList = getStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST");
                        if(logTitleList.isEmpty())
                            logTitleList = new ArrayList<String>();

                        logTitleList.add(title.getText().toString());

                        setStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST",logTitleList);
                        setCalculatorArrayPref(CreditCalculator.this,title.getText().toString(),calculatorData);

                        title.setText("");
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        GrayToast(CreditCalculator.this, "기록이 저장되었습니다.\n기록 보기에서 보기, 수정, 삭제가 가능합니다.");
                    }else if(saveButton.getText().toString().equals(getString(R.string.edit_log_calculator))){
                        logTitleList = getStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST");
                        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.remove(previousTitle);
                        editor.apply();

                        if(logTitleList.isEmpty())
                            logTitleList = new ArrayList<String>();

                        logTitleList.remove(previousTitle);
                        logTitleList.add(title.getText().toString());

                        setStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST",logTitleList);
                        setCalculatorArrayPref(CreditCalculator.this,title.getText().toString(),calculatorData);

                        title.setText("");
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        saveButton.setText(R.string.save_log_calculator);
                        GrayToast(CreditCalculator.this, "기록이 수정되었습니다.\n기록 보기에서 보기, 수정, 삭제가 가능합니다.");
                    }
                }
            }
        });

        watchLogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(watchLogPopupWindow.isShowing())
                    watchLogPopupWindow.dismiss();
            }
        });

        watchLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchLogPopupWindow.showAtLocation(logPopupView,Gravity.CENTER,0,0);
                GrayToast(CreditCalculator.this,"길게 눌러 기록을 삭제할 수 있습니다.");
                logTitleList = getStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST");
                if(!logTitleList.isEmpty()){        ///////////NULL POINETER EXCEPTION  처리  확실히
                    calculatorLogAdapter = new CalculatorLogAdapter();
                    watchLogList.setAdapter(calculatorLogAdapter);
                    watchLogList.setLayoutManager(new LinearLayoutManager(CreditCalculator.this));
                }
                watchLogList.addOnItemTouchListener(new RecyclerItemClickListener(CreditCalculator.this, watchLogList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        calculatorData = getCalculatorArrayPref(CreditCalculator.this,logTitleList.get(position));
                        title.setText(logTitleList.get(position));
                        previousTitle = logTitleList.get(position);
                        creditCalculatorAdapter.notifyDataSetChanged();
                        saveButton.setText(R.string.edit_log_calculator);
                        watchLogPopupWindow.dismiss();

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        noticeText.setText(R.string.notice_delete_log);
                        noticePopupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);

                        noticeOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                logTitleList = getStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST");
                                sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.remove(logTitleList.get(position));
                                editor.apply();

                                if(logTitleList.isEmpty())
                                    logTitleList = new ArrayList<String>();

                                logTitleList.remove(logTitleList.get(position));
                                setStringArrayPref(CreditCalculator.this,"LOG_TITLE_LIST",logTitleList);


                                calculatorLogAdapter.notifyDataSetChanged();
                                noticePopupWindow.dismiss();
                            }
                        });
                    }
                }));
            }
        });

        creditCalculatorAdapter = new CreditCalculatorAdapter();
        calculatorList.setAdapter(creditCalculatorAdapter);
        calculatorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        calculatorList.addOnItemTouchListener(new RecyclerItemClickListener(CreditCalculator.this, calculatorList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent editList = new Intent(CreditCalculator.this, AddCreditCalculator.class);
                editList.putExtra("EDIT_CALCULATOR",1);
                editList.putExtra("CALCULATOR_LOAD_DATA",calculatorData.get(position));
                editList.putExtra("CALCULATOR_LOAD_DATA_POSITION",position);
                startActivity(editList);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Intent editList = new Intent(CreditCalculator.this, AddCreditCalculator.class);
                editList.putExtra("EDIT_CALCULATOR",1);
                editList.putExtra("CALCULATOR_LOAD_DATA",calculatorData.get(position));
                editList.putExtra("CALCULATOR_LOAD_DATA_POSITION",position);
                startActivity(editList);
            }
        }));

    }

    class CreditCalculatorAdapter extends RecyclerView.Adapter<CreditCalculatorAdapter.CalculatorHolder>{

        CalculatorHolder calculatorHolder;
        public CreditCalculatorAdapter() {
            super();
        }

        @Override
        public int getItemCount() {
            return calculatorData.size();
        }

        @NonNull
        @Override
        public CalculatorHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.recyclerviewitem_credit_calculator,viewGroup,false);

            calculatorHolder = new CalculatorHolder(view);
            return calculatorHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CalculatorHolder calculatorHolder, int i) {

            Calculator currentCalculator = calculatorData.get(i);

            calculatorHolder.type.setText(currentCalculator.getType());
            calculatorHolder.title.setText(currentCalculator.getTitle());
            calculatorHolder.credits.setText(currentCalculator.getCredits());
            calculatorHolder.grade.setText(currentCalculator.getGrade());
        }

        public class CalculatorHolder extends RecyclerView.ViewHolder{

            TextView type;
            TextView title;
            TextView credits;
            TextView grade;

            public CalculatorHolder(View view){
                super(view);
                type = (TextView)view.findViewById(R.id.calculator_list_type);
                title = (TextView)view.findViewById(R.id.calculator_list_title);
                credits = (TextView)view.findViewById(R.id.calculator_list_credits);
                grade = (TextView)view.findViewById(R.id.calculator_list_grade);
            }
        }

    }

    class CalculatorLogAdapter extends RecyclerView.Adapter<CalculatorLogAdapter.LogHolder>{

        LogHolder logHolder;

        public CalculatorLogAdapter() {
            super();
        }

        @Override
        public int getItemCount() {
            try {
                if (logTitleList.size() > 0)
                    return logTitleList.size();
            }catch (NullPointerException e){
                e.printStackTrace();
                return 0;
            }
            return 0;
        }

        @NonNull
        @Override
        public LogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(CreditCalculator.this).inflate(R.layout.recyclerviewitem_watch_log_list,viewGroup,false);
            logHolder = new LogHolder(view);
            return logHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull LogHolder logHolder, int i) {
            logHolder.title_LOG.setText(logTitleList.get(i));
        }

        private class LogHolder extends RecyclerView.ViewHolder{
            TextView title_LOG;
            private LogHolder(View view){
                super(view);
                title_LOG = (TextView)view.findViewById(R.id.log_title);
            }
        }
    }

    private void InitialSetting(){
        watchLog = (TextView) findViewById(R.id.watch_log_calculator);
        saveButton = (Button) findViewById(R.id.calculator_save_button);

        title = (EditText)findViewById(R.id.credit_calculator_title);

        calculatorData = new ArrayList<Calculator>();
        calculatorList = (RecyclerView)findViewById(R.id.calculator_list);
        result = (TextView)findViewById(R.id.calculator_result);

        addButton = (Button)findViewById(R.id.calculator_add_button);
        resetButton = (Button)findViewById(R.id.calculator_reset_button);

        popupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        noticePopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)popupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)popupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)popupView.findViewById(R.id.notice_plus_cancel_textview);
        noticePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));



        logPopupView = getLayoutInflater().inflate(R.layout.watch_log_calculator_popup_window,null);
        watchLogPopupWindow = new PopupWindow(logPopupView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,true);

        watchLogCloseButton = (Button)logPopupView.findViewById(R.id.close_log_popup);
        watchLogList = (RecyclerView)logPopupView.findViewById(R.id.log_list);
        watchLogPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));


    }


    public void GrayToast(Context context,String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

    private void setCalculatorArrayPref(Context context, String key, ArrayList<Calculator> list){
        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < list.size(); i++){
            Calculator currentCalculator = list.get(i);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", currentCalculator.getType());
                jsonObject.put("title", currentCalculator.getTitle());
                jsonObject.put("credits", currentCalculator.getCredits());
                jsonObject.put("grade", currentCalculator.getGrade());
                jsonArray.put(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(!list.isEmpty()){
            editor.putString(key,jsonArray.toString());
        }else{
            editor.putString(key,null);
        }
        editor.apply();
    }


    private ArrayList<Calculator> getCalculatorArrayPref(Context context, String key){
        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
        String json = sharedPreferences.getString(key,null);
        ArrayList<Calculator> list = new ArrayList<Calculator>();
        if(json!=null){
            try{
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0; i < jsonArray.length(); i++){
                    Calculator calculator = new Calculator(jsonArray.getJSONObject(i).getString("type"),
                            jsonArray.getJSONObject(i).getString("title"),
                            jsonArray.getJSONObject(i).getString("credits"),
                            jsonArray.getJSONObject(i).getString("grade"));
                    list.add(calculator);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return list;
    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> list){
        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < list.size(); i++){
            jsonArray.put(list.get(i));
        }
        if(!list.isEmpty()){
            editor.putString(key,jsonArray.toString());
        }else{
            editor.putString(key,null);
        }
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context, String key){
        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
        String json = sharedPreferences.getString(key,null);
        ArrayList<String> list = new ArrayList<String>();
        if(json!=null){
            try{
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0; i < jsonArray.length(); i++){
                    String text = jsonArray.optString(i);
                    list.add(text);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return list;
    }

}
