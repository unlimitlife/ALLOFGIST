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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreditCalculatorAddMain extends AppCompatActivity {

    //로그인 정보
    private String id = "LOGIN_ERROR";
    private String previousTitle;
    double completedCredits;
    double gpa;

    EditText title;
    ImageButton title_delete;
    TextView sizeOfTitle;

    Button resetButton;
    Button addButton;
    Button saveButton;
    ImageButton backButton;

    public static ArrayList<Calculator> calculatorData;
    RecyclerView calculatorList;
    public static CreditCalculatorAdapter creditCalculatorAdapter;

    PopupWindow noticePopupWindow;
    View popupView;

    TextView noticeText;
    TextView noticeCancel;
    TextView noticeOk;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> logTitleList;

    TextView totalGPA;
    TextView completedCreditsmain_tv;

    private boolean isScrolling = false;
    ScrollView scrollView;

    private int editmode = 0;

    @Override
    public void onBackPressed() {
        if(noticePopupWindow.isShowing())
            noticePopupWindow.dismiss();
        else {
            if(saveButton.getText().equals(getString(R.string.edit_log_calculator))) {
                noticeText.setText(R.string.notice_edit_reset);
                noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        title.setText("");
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        saveButton.setText(R.string.save_log_calculator);
                        noticePopupWindow.dismiss();
                        finish();
                    }
                });
            }
            else if(saveButton.getText().equals(getString(R.string.save_log_calculator))) {
                noticeText.setText("추가 작업을 취소하시겠습니까?");
                noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        title.setText("");
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        saveButton.setText(R.string.save_log_calculator);
                        noticePopupWindow.dismiss();
                        finish();
                    }
                });
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculateGPA();

        totalGPA.setText("GPA : " + gpa + " / 4.5");
        completedCreditsmain_tv.setText("Completed Credits : " + (int)completedCredits + " / 21");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_calculator);

        //로그인 정보 가져오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(CreditCalculatorAddMain.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();
        String size = 0+"/50";
        sizeOfTitle.setText(size);

        title_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveButton.getText().equals(getString(R.string.edit_log_calculator))) {
                    noticeText.setText(R.string.notice_edit_reset);
                    noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    noticeOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            title.setText("");
                            calculatorData = new ArrayList<Calculator>();
                            creditCalculatorAdapter.notifyDataSetChanged();
                            saveButton.setText(R.string.save_log_calculator);
                            noticePopupWindow.dismiss();
                        }
                    });

                }else{
                    noticeText.setText(R.string.notice_reset);
                    noticePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    noticeOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            title.setText("");
                            calculatorData = new ArrayList<Calculator>();
                            creditCalculatorAdapter.notifyDataSetChanged();
                            noticePopupWindow.dismiss();
                        }
                    });
                }
            }
        });

        noticeCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                noticePopupWindow.dismiss();
            }
        });


        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String size = s.length()+"/50";
                sizeOfTitle.setText(size);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCalculator = new Intent(CreditCalculatorAddMain.this,AddCreditCalculator.class);
                startActivity(addCalculator);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty())
                    GrayToast(CreditCalculatorAddMain.this,"제목을 입력해주세요.");
                else if(calculatorData.isEmpty())
                    GrayToast(CreditCalculatorAddMain.this,"최소 하나의 과목을 추가해주세요.");
                else{
                    if(saveButton.getText().toString().equals(getString(R.string.save_log_calculator))) {
                        logTitleList = getStringArrayPref(CreditCalculatorAddMain.this,"LOG_TITLE_LIST");
                        if(logTitleList.isEmpty())
                            logTitleList = new ArrayList<String>();

                        logTitleList.add(title.getText().toString());

                        setStringArrayPref(CreditCalculatorAddMain.this,"LOG_TITLE_LIST",logTitleList);
                        setCalculatorArrayPref(CreditCalculatorAddMain.this,title.getText().toString(),calculatorData);

                        title.setText("");
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        GrayToast(CreditCalculatorAddMain.this, "기록이 저장되었습니다.\n학점 화면에서 보기, 수정, 삭제가 가능합니다.");
                        finish();
                    }else if(saveButton.getText().toString().equals(getString(R.string.edit_log_calculator))){
                        logTitleList = getStringArrayPref(CreditCalculatorAddMain.this,"LOG_TITLE_LIST");
                        sharedPreferences = getSharedPreferences("LOAD_CALCULATOR",MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.remove(previousTitle);
                        editor.apply();

                        if(logTitleList.isEmpty())
                            logTitleList = new ArrayList<String>();

                        logTitleList.remove(previousTitle);
                        logTitleList.add(title.getText().toString());

                        setStringArrayPref(CreditCalculatorAddMain.this,"LOG_TITLE_LIST",logTitleList);
                        setCalculatorArrayPref(CreditCalculatorAddMain.this,title.getText().toString(),calculatorData);

                        title.setText("");
                        calculatorData = new ArrayList<Calculator>();
                        creditCalculatorAdapter.notifyDataSetChanged();
                        saveButton.setText(R.string.save_log_calculator);
                        GrayToast(CreditCalculatorAddMain.this, "기록이 수정되었습니다.\n학점 화면에서 보기, 수정, 삭제가 가능합니다.");
                        finish();
                    }
                }
            }
        });


        editmode = getIntent().getIntExtra("EDIT_CALCULATOR_LOG",0);
        if (editmode==1){
            calculatorData = getIntent().getParcelableArrayListExtra("CALCULATOR_LOAD_DATA");
            String name = getIntent().getStringExtra("TITLE");
            saveButton.setText(R.string.edit_log_calculator);
            title.setText(name);
            previousTitle = name;
        }

        creditCalculatorAdapter = new CreditCalculatorAdapter();
        calculatorList.setAdapter(creditCalculatorAdapter);
        calculatorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        calculatorList.addOnItemTouchListener(new RecyclerItemClickListener(CreditCalculatorAddMain.this, calculatorList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent editList = new Intent(CreditCalculatorAddMain.this, AddCreditCalculator.class);
                editList.putExtra("EDIT_CALCULATOR",1);
                editList.putExtra("CALCULATOR_LOAD_DATA",calculatorData.get(position));
                editList.putExtra("CALCULATOR_LOAD_DATA_POSITION",position);
                startActivity(editList);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Intent editList = new Intent(CreditCalculatorAddMain.this, AddCreditCalculator.class);
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

    private void InitialSetting(){
        backButton = (ImageButton)findViewById(R.id.gpa_calculator_add_back_button);

        saveButton = (Button) findViewById(R.id.calculator_save_button);

        title = (EditText)findViewById(R.id.credit_calculator_title);
        title_delete = (ImageButton)findViewById(R.id.gpa_title_delete);
        sizeOfTitle = (TextView)findViewById(R.id.size_of_title);

        calculatorData = new ArrayList<Calculator>();
        calculatorList = (RecyclerView)findViewById(R.id.calculator_list);

        addButton = (Button)findViewById(R.id.calculator_add_button);
        resetButton = (Button)findViewById(R.id.calculator_reset_button);

        popupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        noticePopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)popupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)popupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)popupView.findViewById(R.id.notice_plus_cancel_textview);
        noticePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));

        totalGPA = (TextView)findViewById(R.id.total_gpa_credit_add_gpa_calculator);
        completedCreditsmain_tv = (TextView)findViewById(R.id.total_credit_add_gpa_calculator);

        scrollView = (ScrollView)findViewById(R.id.scroll_gpa);


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

    public void calculateGPA(){
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
            //result.setText(("Completed Credits : " + (int)completedCredits + " | GPA : " + gpa + "/4.5"));
        }catch (ArithmeticException e){
            e.printStackTrace();
            //result.setText(("Completed Credits : " + 0 + " | GPA : " + 0 + "/4.5"));
        }
    }
}
