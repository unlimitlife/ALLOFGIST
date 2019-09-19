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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreditCalculatorMain extends AppCompatActivity {

    //로그인 정보
    private String id = "LOGIN_ERROR";

    public int completedCredits_main = 0;

    double completedCredits;

    double totalgpa;

    RecyclerView watchLogList;
    ImageButton backButton;
    ImageButton deleteButton;
    TextView totalGPA;
    TextView completedCreditsmain_tv;
    ImageButton addButton;

    PopupWindow noticePopupWindow;
    View popupView;

    TextView noticeText;
    TextView noticeCancel;
    TextView noticeOk;

    public static ArrayList<Calculator> calculatorData;
    public CalculatorLogAdapter calculatorLogAdapter;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> logTitleList;


    @Override
    protected void onResume() {
        super.onResume();
        completedCredits_main = 0;
        totalgpa = 0;
        logTitleList = getStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST");

        try {
            if (!logTitleList.isEmpty()) {        ///////////NULL POINETER EXCEPTION  처리  확실히
                calculatorLogAdapter = new CalculatorLogAdapter();
                watchLogList.setAdapter(calculatorLogAdapter);
                watchLogList.setLayoutManager(new LinearLayoutManager(CreditCalculatorMain.this));

                for(int i=0; i< logTitleList.size(); i++){
                    calculatorData = getCalculatorArrayPref(CreditCalculatorMain.this,logTitleList.get(i));
                    int total_credit_log = 0;

                    for (int j = 0; j < calculatorData.size(); j++)
                        total_credit_log += Integer.parseInt(calculatorData.get(j).getCredits());
                    completedCredits_main += total_credit_log;
                    totalgpa += calculateGPA();
                }
                completedCreditsmain_tv.setText("Completed Credits : " + completedCredits_main + " / 130");
                totalgpa /= completedCredits_main;
                totalGPA.setText("Total GPA :    " + ((float)Math.round(totalgpa*100)/100) + " / 4.5");
            }else {

                totalGPA.setText("Total GPA :    0.0 / 4.5");
                completedCreditsmain_tv.setText("Completed Credits : 000 / 130");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_calculator_main);

        //로그인 정보 가져오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(CreditCalculatorMain.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();



        logTitleList = getStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST");

        try {
            if (!logTitleList.isEmpty()) {        ///////////NULL POINETER EXCEPTION  처리  확실히
                calculatorLogAdapter = new CalculatorLogAdapter();
                watchLogList.setAdapter(calculatorLogAdapter);
                watchLogList.setLayoutManager(new LinearLayoutManager(this));
            } else {

                totalGPA.setText("Total GPA :    0.0 / 4.5");
                completedCreditsmain_tv.setText("Completed Credits : 000 / 130");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }





        noticeCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                noticePopupWindow.dismiss();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCalculator = new Intent(CreditCalculatorMain.this, CreditCalculatorAddMain.class);
                addCalculator.putExtra("ID",id);
                startActivity(addCalculator);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calculatorLogAdapter.isDeleteVisibility()) {
                    calculatorLogAdapter.updateDeleteVisibility(true);
                    calculatorLogAdapter.updateLayoutSelected(true);
                    deleteButton.setSelected(true);
                    calculatorLogAdapter.notifyDataSetChanged();
                }
                else {
                    calculatorLogAdapter.updateDeleteVisibility(false);
                    calculatorLogAdapter.updateLayoutSelected(false);
                    deleteButton.setSelected(false);
                    calculatorLogAdapter.notifyDataSetChanged();
                }

            }
        });


        watchLogList.addOnItemTouchListener(new RecyclerItemClickListener(CreditCalculatorMain.this, watchLogList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final int i = position;
                if(!calculatorLogAdapter.isDeleteVisibility()){
                    calculatorData = getCalculatorArrayPref(CreditCalculatorMain.this,logTitleList.get(position));
                    Intent editList = new Intent(CreditCalculatorMain.this, CreditCalculatorAddMain.class);
                    editList.putExtra("EDIT_CALCULATOR_LOG",1);
                    editList.putExtra("CALCULATOR_LOAD_DATA",calculatorData);
                    editList.putExtra("TITLE",logTitleList.get(position));
                    editList.putExtra("ID",id);
                    startActivity(editList);
                }
                else{
                    noticeText.setText(R.string.notice_delete_log);
                    noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);

                    noticeOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logTitleList = getStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST");
                            sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.remove(logTitleList.get(i));
                            editor.apply();

                            if(logTitleList.isEmpty())
                                logTitleList = new ArrayList<String>();

                            logTitleList.remove(logTitleList.get(i));
                            setStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST",logTitleList);


                            calculatorLogAdapter.notifyDataSetChanged();
                            noticePopupWindow.dismiss();
                        }
                    });

                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                /*noticeText.setText(R.string.notice_delete_log);
                noticePopupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);

                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logTitleList = getStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST");
                        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.remove(logTitleList.get(position));
                        editor.apply();

                        if(logTitleList.isEmpty())
                            logTitleList = new ArrayList<String>();

                        logTitleList.remove(logTitleList.get(position));
                        setStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST",logTitleList);


                        calculatorLogAdapter.notifyDataSetChanged();
                        noticePopupWindow.dismiss();
                    }
                });*/
            }
        }));


        /*calculatorList.addOnItemTouchListener(new RecyclerItemClickListener(CreditCalculatorMain.this, calculatorList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent editList = new Intent(CreditCalculatorMain.this, AddCreditCalculator.class);
                editList.putExtra("EDIT_CALCULATOR",1);
                editList.putExtra("CALCULATOR_LOAD_DATA",calculatorData.get(position));
                editList.putExtra("CALCULATOR_LOAD_DATA_POSITION",position);
                startActivity(editList);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Intent editList = new Intent(CreditCalculatorMain.this, AddCreditCalculator.class);
                editList.putExtra("EDIT_CALCULATOR",1);
                editList.putExtra("CALCULATOR_LOAD_DATA",calculatorData.get(position));
                editList.putExtra("CALCULATOR_LOAD_DATA_POSITION",position);
                startActivity(editList);
            }
        }));*/

    }

    class CalculatorLogAdapter extends RecyclerView.Adapter<CalculatorLogAdapter.LogHolder>{

        LogHolder logHolder;
        Boolean isVisible = false;
        Boolean isSelected = false;

        private CalculatorLogAdapter() {
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
            view = LayoutInflater.from(CreditCalculatorMain.this).inflate(R.layout.recyclerviewitem_gpa_calculator_main,viewGroup,false);
            logHolder = new LogHolder(view);
            return logHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull LogHolder logHolder, int i) {
            logHolder.title_LOG.setText(logTitleList.get(i));
            calculatorData = getCalculatorArrayPref(CreditCalculatorMain.this,logTitleList.get(i));
            int total_credit_log = 0;
            int total_major_credit_log = 0;

            for (int j = 0; j < calculatorData.size(); j++) {
                total_credit_log += Integer.parseInt(calculatorData.get(j).getCredits());
                if(calculatorData.get(j).getType().equals("전공"))
                    total_major_credit_log += Integer.parseInt(calculatorData.get(j).getCredits());
            }
            //completedCredits_main += total_credit_log;

            logHolder.total_credit_LOG.setText(total_credit_log+"");
            logHolder.major_num_LOG.setText(total_major_credit_log+"");

            double gpa = 0;
            gpa = (double) calculateGPA();
            gpa = (Math.round(gpa/completedCredits*100.00)/100.00);
            logHolder.gpa_LOG.setText(gpa+"");

            if(isVisible)
                logHolder.deleteButton.setVisibility(View.VISIBLE);
            else
                logHolder.deleteButton.setVisibility(View.GONE);

            logHolder.layout.setSelected(isSelected);


            final int position = i;

            logHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    noticeText.setText(R.string.notice_delete_log);
                    noticePopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);

                    noticeOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logTitleList = getStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST");
                            sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.remove(logTitleList.get(position));
                            editor.apply();

                            if(logTitleList.isEmpty())
                                logTitleList = new ArrayList<String>();

                            logTitleList.remove(logTitleList.get(position));
                            setStringArrayPref(CreditCalculatorMain.this,"LOG_TITLE_LIST",logTitleList);


                            calculatorLogAdapter.notifyDataSetChanged();
                            noticePopupWindow.dismiss();
                        }
                    });

                }
            });
        }

        public void updateDeleteVisibility(boolean newValue){
            isVisible = newValue;
        }

        public void updateLayoutSelected(boolean newValue){
            isSelected = newValue;
        }

        public boolean isDeleteVisibility(){
            return isVisible;
        }

        private class LogHolder extends RecyclerView.ViewHolder{
            TextView title_LOG;
            TextView major_num_LOG;
            TextView total_credit_LOG;
            TextView gpa_LOG;
            ImageButton deleteButton;
            LinearLayout layout;
            private LogHolder(View view){
                super(view);
                title_LOG = (TextView)view.findViewById(R.id.title_log_gpa_calculator);
                major_num_LOG = (TextView)view.findViewById(R.id.major_num_log_gpa_calculator);
                total_credit_LOG = (TextView)view.findViewById(R.id.total_credit_log_gpa_calculator);
                gpa_LOG = (TextView)view.findViewById(R.id.gpa_log_gpa_calculator);
                deleteButton = (ImageButton)view.findViewById(R.id.log_item_delete_gpa);
                layout = (LinearLayout)view.findViewById(R.id.layout_gpa_item);
            }
        }
    }

    private void InitialSetting(){

        calculatorData = new ArrayList<Calculator>();
        backButton = (ImageButton)findViewById(R.id.gpa_calculator_back_button);
        deleteButton = (ImageButton)findViewById(R.id.gpa_calculator_delete_button);
        watchLogList = (RecyclerView)findViewById(R.id.log_list_gpa_calculator);
        totalGPA =  (TextView)findViewById(R.id.total_gpa_credit_main_gpa_calculator);
        completedCreditsmain_tv = (TextView)findViewById(R.id.total_credit_main_gpa_calculator);
        addButton = (ImageButton)findViewById(R.id.plus_button_gpa_calculator_main);


        popupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        noticePopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)popupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)popupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)popupView.findViewById(R.id.notice_plus_cancel_textview);
        noticePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));
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

    /*public void calculateGPA(){
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
    }*/

    public int calculateGPA(){
        int gpa = 0;
        try {
            completedCredits = 0;
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
            return gpa;
        }catch (ArithmeticException e){
            e.printStackTrace();
            return gpa;
        }
    }
}
