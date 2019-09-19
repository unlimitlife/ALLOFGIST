package com.allofgist.dell.allofgistlite;

import android.content.Context;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.allofgist.dell.allofgistlite.CreditCalculatorAddMain.calculatorData;

public class AddCreditCalculator extends AppCompatActivity {

    TextView title;
    //View loadPopupView;
    //PopupWindow loadPopupWindow;
    //TextView titleTextView;
    //Button closeButton;
    //RecyclerView loadList;
    //LoadListAdapter typeLoadAdapter;
    //LoadListAdapter titleLoadAdapter;

    View deletePopupView;
    PopupWindow deletePopupWindow;
    TextView noticeText;
    TextView noticeOk;
    TextView noticeCancel;

    ImageButton backButton;

    String type;
    Button common;
    Button basic;
    Button major;
    Button minor;
    Button research;
    EditText titleAddPopup;
    FrameLayout gradeAddPopup;
    TextView gradeAddTV;
    PopupMenu gradePopupMenu;
    FrameLayout creditsAddPopup;
    TextView creditsAddTV;
    PopupMenu creditsPopupMenu;
    Button saveButton;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    /*ArrayList<String> typeList;
    ArrayList<String> titleList;*/

    ArrayList<String> DEFAULTSET;
    Calculator currentCalculator;
    int currentCalculatorPosition = 12345;
    Button deleteButton;

    private int editmode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_calculator);


        InitialSetting();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.setSelected(true);
                basic.setSelected(false);
                major.setSelected(false);
                minor.setSelected(false);
                research.setSelected(false);
                type="공통";
            }
        });

        basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.setSelected(false);
                basic.setSelected(true);
                major.setSelected(false);
                minor.setSelected(false);
                research.setSelected(false);
                type="기초";
            }
        });

        major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.setSelected(false);
                basic.setSelected(false);
                major.setSelected(true);
                minor.setSelected(false);
                research.setSelected(false);
                type="전공";
            }
        });

        minor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.setSelected(false);
                basic.setSelected(false);
                major.setSelected(false);
                minor.setSelected(true);
                research.setSelected(false);
                type="부전공";
            }
        });

        research.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.setSelected(false);
                basic.setSelected(false);
                major.setSelected(false);
                minor.setSelected(false);
                research.setSelected(true);
                type="연구";
            }
        });

        editmode = getIntent().getIntExtra("EDIT_CALCULATOR",0);
        if(editmode==1){

            title.setText(R.string.calculator_edit_popup_title);
            saveButton.setText(R.string.calculator_edit);
            deleteButton.setVisibility(View.VISIBLE);
            try {
                currentCalculator = getIntent().getExtras().getParcelable("CALCULATOR_LOAD_DATA");
                currentCalculatorPosition = getIntent().getIntExtra("CALCULATOR_LOAD_DATA_POSITION",123456789);

                switch (currentCalculator.getType()){
                    case "공통":
                        common.setSelected(true);
                        basic.setSelected(false);
                        major.setSelected(false);
                        minor.setSelected(false);
                        research.setSelected(false);
                        type="공통";
                        break;

                    case "기초":
                        common.setSelected(false);
                        basic.setSelected(true);
                        major.setSelected(false);
                        minor.setSelected(false);
                        research.setSelected(false);
                        type="기초";
                        break;

                    case "전공":
                        common.setSelected(false);
                        basic.setSelected(false);
                        major.setSelected(true);
                        minor.setSelected(false);
                        research.setSelected(false);
                        type="전공";
                        break;

                    case "부전공":
                        common.setSelected(false);
                        basic.setSelected(false);
                        major.setSelected(false);
                        minor.setSelected(true);
                        research.setSelected(false);
                        type="부전공";
                        break;

                    case "연구":
                        common.setSelected(false);
                        basic.setSelected(false);
                        major.setSelected(false);
                        minor.setSelected(false);
                        research.setSelected(true);
                        type="연구";
                        break;
                }
                titleAddPopup.setText(currentCalculator.getTitle());
                gradeAddTV.setText(currentCalculator.getGrade());
                creditsAddTV.setText(currentCalculator.getCredits());

            }catch (NullPointerException e){
                e.printStackTrace();
                GrayToast(AddCreditCalculator.this,"개발자에게 연락을 취해주세요.");
                finish();
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticeText.setText("목록에서 제거하시겠습니까?");
                    deletePopupWindow.showAtLocation(deletePopupView,Gravity.CENTER,0,0);
                    noticeOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CreditCalculatorAddMain.calculatorData.remove(currentCalculatorPosition);
                            CreditCalculatorAddMain.creditCalculatorAdapter.notifyDataSetChanged();
                            deletePopupWindow.dismiss();
                            finish();
                        }
                    });
                }
            });
        }else{
            title.setText(R.string.calculator_add_popup_title);
        }


        /*typeReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTextView.setText(R.string.load_type_top_bar);
                loadPopupWindow.showAtLocation(loadPopupView, Gravity.CENTER,0,0);
                GrayToast(AddCreditCalculator.this,"길게 눌러 기록을 삭제할 수 있습니다.");
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(loadPopupWindow.isShowing())
                            loadPopupWindow.dismiss();
                    }
                });
                typeLoadAdapter = new LoadListAdapter(typeList,"TYPE");
                loadList.setAdapter(typeLoadAdapter);
                loadList.setLayoutManager(new LinearLayoutManager(AddCreditCalculator.this));

            }
        });


        titleReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTextView.setText(R.string.load_title_top_bar);
                loadPopupWindow.showAtLocation(loadPopupView, Gravity.CENTER,0,0);
                GrayToast(AddCreditCalculator.this,"길게 눌러 기록을 삭제할 수 있습니다.");
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(loadPopupWindow.isShowing())
                            loadPopupWindow.dismiss();
                    }
                });
                titleLoadAdapter = new LoadListAdapter(titleList,"TITLE");
                loadList.setAdapter(titleLoadAdapter);
                loadList.setLayoutManager(new LinearLayoutManager(AddCreditCalculator.this));


            }
        });*/

        gradeAddPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradePopupMenu = new PopupMenu(AddCreditCalculator.this,v);
                MenuInflater menuInflater = new MenuInflater(AddCreditCalculator.this);
                menuInflater.inflate(R.menu.calculator_grade_popup_menu,gradePopupMenu.getMenu());
                gradePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        switch (itemId){
                            case R.id.grade_aplus :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_azero :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_bplus :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_bzero :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_cplus :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_czero :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_dplus :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_dzero :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            case R.id.grade_f :
                                gradeAddTV.setText(item.getTitle());
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                gradePopupMenu.show();
            }
        });

        creditsAddPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditsPopupMenu = new PopupMenu(AddCreditCalculator.this,v);
                MenuInflater menuInflater = new MenuInflater(AddCreditCalculator.this);
                menuInflater.inflate(R.menu.calculator_credits_popup_menu,creditsPopupMenu.getMenu());
                creditsPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        switch (itemId){
                            case R.id.credit_five :
                                creditsAddTV.setText(item.getTitle());
                                break;
                            case R.id.credit_four :
                                creditsAddTV.setText(item.getTitle());
                                break;
                            case R.id.credit_three :
                                creditsAddTV.setText(item.getTitle());
                                break;
                            case R.id.credit_two :
                                creditsAddTV.setText(item.getTitle());
                                break;
                            case R.id.credit_one :
                                creditsAddTV.setText(item.getTitle());
                                break;
                            case R.id.credit_zero :
                                creditsAddTV.setText(item.getTitle());
                                break;
                            default:
                                break;

                        }
                        return false;
                    }
                });
                creditsPopupMenu.show();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleAddPopup.getText().toString().isEmpty())
                    GrayToast(getApplicationContext(),"과목명을 입력해주세요.");
                else if(gradeAddTV.getText().toString().isEmpty())
                    GrayToast(getApplicationContext(),"점수를 입력해주세요.");
                else if(creditsAddTV.getText().toString().isEmpty())
                    GrayToast(getApplicationContext(),"학점을 입력해주세요.");
                else{
                    if(editmode==1) {
                        try {
                            CreditCalculatorAddMain.calculatorData.remove(currentCalculatorPosition);
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            GrayToast(AddCreditCalculator.this, "개발자에게 연락을 취해주세요.");
                        }
                    }
                    calculatorData.add(new Calculator(type,
                            titleAddPopup.getText().toString(),
                            creditsAddTV.getText().toString(),
                            gradeAddTV.getText().toString()));

                    /*if(!typeList.contains(typeAddPopup.getText().toString()))
                        typeList.add(typeAddPopup.getText().toString());
                    if(!titleList.contains(titleAddPopup.getText().toString()))
                        titleList.add(titleAddPopup.getText().toString());

                    setStringArrayPref(AddCreditCalculator.this,"TYPE_LIST",typeList);
                    setStringArrayPref(AddCreditCalculator.this,"TITLE_LIST",titleList);*/

                    CreditCalculatorAddMain.creditCalculatorAdapter.notifyDataSetChanged();
                    type="공통";
                    titleAddPopup.setText("");
                    gradeAddTV.setText("");
                    creditsAddTV.setText("");
                    finish();
                }
            }
        });



        noticeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePopupWindow.dismiss();
            }
        });

        /*loadList.addOnItemTouchListener(new RecyclerItemClickListener(AddCreditCalculator.this, loadList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    switch ((int) loadList.getAdapter().getItemId(position)) {
                        case 1:
                            typeAddPopup.setText(typeList.get(position));
                            break;
                        case 2:
                            titleAddPopup.setText(titleList.get(position));
                            break;
                        default:
                            GrayToast(AddCreditCalculator.this,"개발자에게 연락을 취해주세요.");
                            break;
                    }
                }catch (NullPointerException e){
                    GrayToast(AddCreditCalculator.this,"개발자에게 연락을 취해주세요.");
                    e.printStackTrace();
                }
                loadPopupWindow.dismiss();
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                noticeText.setText("기록을 삭제하시겠습니까?");
                deletePopupWindow.showAtLocation(deletePopupView,Gravity.CENTER,0,0);
                noticeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            switch ((int) loadList.getAdapter().getItemId(position)) {
                                case 1:
                                    typeList.remove(position);
                                    typeLoadAdapter.notifyDataSetChanged();
                                    setStringArrayPref(AddCreditCalculator.this,"TYPE_LIST",typeList);
                                    break;
                                case 2:
                                    titleList.remove(position);
                                    titleLoadAdapter.notifyDataSetChanged();
                                    setStringArrayPref(AddCreditCalculator.this,"TITLE_LIST",titleList);
                                    break;
                                default:
                                    GrayToast(AddCreditCalculator.this,"개발자에게 연락을 취해주세요.");
                                    break;

                            }
                        }catch (NullPointerException e){
                            GrayToast(AddCreditCalculator.this,"개발자에게 연락을 취해주세요.");
                            e.printStackTrace();
                        }
                        deletePopupWindow.dismiss();
                    }
                });
            }
        }));*/
    }
    /*public class LoadListAdapter extends RecyclerView.Adapter<LoadListAdapter.LoadHolder>{

        private LoadHolder loadHolder;
        ArrayList<String> list;
        String key;

        private LoadListAdapter(ArrayList<String> list, String key){
            this.list = list;
            this.key = key;
        }

        @Override
        public long getItemId(int position) {
            switch (key){
                case "TYPE" :
                    return 1;
                case "TITLE" :
                    return 2;
                default:
                    return 0;
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @NonNull
        @Override
        public LoadHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(AddCreditCalculator.this).inflate(R.layout.recyclerviewitem_load_list,viewGroup,false);
            loadHolder = new LoadHolder(view);
            return loadHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final LoadHolder loadHolder, int i) {
            loadHolder.loadText.setText(list.get(i));
        }

        private class LoadHolder extends RecyclerView.ViewHolder{
            TextView loadText;
            private LoadHolder(View view){
                super(view);
                loadText = (TextView)view.findViewById(R.id.load_list_text);
            }
        }
    }*/

    public void InitialSetting(){

        title = (TextView)findViewById(R.id.title_textView_add_calculator);

        /*loadPopupView = getLayoutInflater().inflate(R.layout.load_type_title_calculator_popup_window,null);
        loadPopupWindow = new PopupWindow(loadPopupView,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,true);
        titleTextView = (TextView)loadPopupView.findViewById(R.id.title_load_popup);
        closeButton = (Button)loadPopupView.findViewById(R.id.close_load_popup);
        loadList = (RecyclerView)loadPopupView.findViewById(R.id.load_list);

        loadPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));*/


        backButton = (ImageButton)findViewById(R.id.gpa_calculator_add_add_back_button);
        common = (Button)findViewById(R.id.type_common);
        basic = (Button)findViewById(R.id.type_basic);
        major = (Button)findViewById(R.id.type_major);
        minor = (Button)findViewById(R.id.type_minor);
        research = (Button)findViewById(R.id.type_research);
        titleAddPopup = (EditText)findViewById(R.id.title_add_calculator);
        gradeAddPopup = (FrameLayout)findViewById(R.id.grade_add_calculator);
        gradeAddTV = (TextView)findViewById(R.id.grade_add_calculator_tv);
        creditsAddPopup = (FrameLayout)findViewById(R.id.credits_add_calculator);
        creditsAddTV = (TextView)findViewById(R.id.credits_add_calculator_tv);

        saveButton = (Button)findViewById(R.id.save_calcuator);

        DEFAULTSET = new ArrayList<String>();
        DEFAULTSET.add("");

        /*typeList = new ArrayList<String>();
        titleList = new ArrayList<String>();
        typeList = getStringArrayPref(AddCreditCalculator.this,"TYPE_LIST");
        titleList = getStringArrayPref(AddCreditCalculator.this,"TITLE_LIST");*/

        deletePopupView = getLayoutInflater().inflate(R.layout.notice_plus_cancel_popup_window,null);
        deletePopupWindow = new PopupWindow(deletePopupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,true);

        noticeText = (TextView)deletePopupView.findViewById(R.id.notice_plus_text);
        noticeOk = (TextView)deletePopupView.findViewById(R.id.notice_plus_ok_textview);
        noticeCancel = (TextView)deletePopupView.findViewById(R.id.notice_plus_cancel_textview);
        deletePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));

        deleteButton = (Button)findViewById(R.id.delete_calculator);
        deleteButton.setVisibility(View.GONE);
        common.setSelected(true);
        type="공통";
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



    public void GrayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }
}
