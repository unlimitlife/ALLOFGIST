package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AcademicFragment extends Fragment {
    private int mPageNumber;
    private ViewGroup mLeak;
    private TextView month;
    private TextView year;
    private ImageButton back;
    private ImageButton foward;
    // get currentMonth
    private HashMap<Integer,ArrayList<CalendarContext>> calendarData;
    RecyclerView context;

    private MyAsyncTask setdata;
    ProgressBar progressBar;


    public static AcademicFragment create(int pageNumber) {
        AcademicFragment fragment = new AcademicFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLeak = (ViewGroup) inflater.inflate(R.layout.fragment_academic_list, container, false);

        //context = (RecyclerView) rootView.findViewById(R.id.calendar_context);
        //setdata = new MyAsyncTask(this);

        context = (RecyclerView) mLeak.findViewById(R.id.calendar_context);
        progressBar = (ProgressBar)mLeak.findViewById(R.id.academic_calendar_progress_bar);
        /*month = (TextView) mLeak.findViewById(R.id.month_tv_academic_list);
        year = (TextView) mLeak.findViewById(R.id.year_tv_academic_list);
        back = (ImageButton) mLeak.findViewById(R.id.back_academic_list);
        foward = (ImageButton) mLeak.findViewById(R.id.foward_academic_list);*/

        setdata = new MyAsyncTask();
        setdata.execute();
        progressBar.setVisibility(View.VISIBLE);

        return mLeak;
    }


    private class AcademicAdapter extends RecyclerView.Adapter<AcademicAdapter.CalendarHolder>{
        Context mcontext;
        private ArrayList<CalendarContext> calendarContext;
        private ArrayList<String> rawData;
        private HashMap<Integer,ArrayList<String>> data;

        public AcademicAdapter(Context context, ArrayList<CalendarContext> calendarContext) {
            mcontext = context;
            this.calendarContext = calendarContext;
        }

        @NonNull
        @Override
        public CalendarHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listviewitem_academic_calendar, viewGroup, false);

            CalendarHolder calendarHolder = new CalendarHolder(view);
            return calendarHolder;

        }

        @Override
        public void onBindViewHolder(@NonNull CalendarHolder calendarHolder, int position) {

            progressBar.setVisibility(View.GONE);
            String date = calendarContext.get(position).getDate_title();
            date = date.replaceAll("-",".");
            if(date.indexOf('~')!=-1) {
                String date_start = date.substring(0, date.indexOf('('));
                String date_end = date.substring(date.indexOf('~'),date.length()-3);
                calendarHolder.dateStartView.setText(date_start);
                calendarHolder.dateEndView.setText(date_end);
                calendarHolder.dateEndView.setVisibility(View.VISIBLE);
                calendarHolder.contextView.setText(calendarContext.get(position).getContext());
            }
            else{
                calendarHolder.dateStartView.setText(date.substring(0,date.indexOf('(')));
                calendarHolder.dateEndView.setVisibility(View.GONE);
                calendarHolder.contextView.setText(calendarContext.get(position).getContext());

            }
        }

        @Override
        public int getItemCount() {
            return calendarContext.size();
        }

        public class CalendarHolder extends RecyclerView.ViewHolder{
            public TextView contextView;
            public TextView dateStartView;
            public TextView dateEndView;

            public CalendarHolder(View view){
                super(view);
                contextView = (TextView)view.findViewById(R.id.diary_context_textview);
                dateStartView = (TextView)view.findViewById(R.id.diary_compelete_date_textview_start);
                dateEndView = (TextView)view.findViewById(R.id.diary_compelete_date_textview_end);

            }
        }
    }

    //크롤링 데이터 수집
    public class MyAsyncTask extends AsyncTask<Integer,Void,HashMap<Integer,ArrayList<CalendarContext>>> {

        /*private WeakReference<AcademicFragment> fragmentWeakReference;

        private MyAsyncTask(AcademicFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }*/

        @Override
        protected HashMap<Integer, ArrayList<CalendarContext>> doInBackground(Integer... params) {
            String Address;
            URL url;
            BufferedReader br;
            HttpsURLConnection conn;
            String protocol = "GET";
            HashMap<Integer,ArrayList<CalendarContext>> data = new HashMap<Integer,ArrayList<CalendarContext>>();
            try {
                Address = "https://www.gist.ac.kr/kr/html/sub05/0501.html";
                url = new URL(Address);
                conn = (HttpsURLConnection)url.openConnection();
                conn.setRequestMethod(protocol);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));

                ArrayList<String> rawData = new ArrayList<String>();
                String line;
                while((line = br.readLine())!=null) {
                    if(line.startsWith("					<li><b>")) {
                        line = line.replace("					 					 ", "");
                        line = line.replace("					","");
                        line = line.replaceAll("</b><span>","Y");
                        line = line.replaceAll("</span></li><li><b>","Z"+"X");
                        line = line.replaceAll("<li><b>","X");
                        line = line.replaceAll("</span></li>","ZK");
                        rawData.add(line);
                        //System.out.print(rawData);
                    }
                    //System.out.println(line);
                }
                int rawDataSize = rawData.size();
                ArrayList<String> Data = new ArrayList<String>();
                for(int i=0;i<rawDataSize;i++) {
                    int rawDatapositionSize = rawData.get(i).length();
                    String replaceData;
                    for(int j=0;j<rawDatapositionSize;j++) {
                        if(rawData.get(i).startsWith("X")) {
                            Data.add(rawData.get(i).substring(rawData.get(i).indexOf("X")+1, rawData.get(i).indexOf("Y")));

                            replaceData = Data.get(Data.size()-1).replace("(", "\\(");
                            replaceData = replaceData.replace(")", "\\)");

                            rawData.set(i, rawData.get(i).replaceFirst("X"+replaceData, ""));
                        }
                        else if(rawData.get(i).startsWith("Y")) {
                            Data.add(rawData.get(i).substring(rawData.get(i).indexOf("Y")+1, rawData.get(i).indexOf("Z")));

                            replaceData = Data.get(Data.size()-1).replace("(", "\\(");
                            replaceData = replaceData.replace(")", "\\)");

                            rawData.set(i, rawData.get(i).replaceFirst("Y"+replaceData+"Z", ""));
                        }
                        else if(rawData.get(i).startsWith("K")) {
                            Data.add("MonthChange");
                            break;
                        }
                    }
                }

                Log.v("AcademicFragment","data(hashmap)"+rawData);

                int num = 0;
                CalendarContext savedcontext = new CalendarContext(null,null);
                for(int i=0;i<rawDataSize;i++) {

                    data.put(i, new ArrayList<CalendarContext>());

                    while (Data.get(num) != null) {
                        if (Data.get(num).equals("MonthChange")) {
                            num++;
                            break;
                        }
                        savedcontext.setDate_title(Data.get(num));
                        num++;
                        savedcontext.setContext(Data.get(num));
                        if (savedcontext.getContext() != null && savedcontext.getDate_title() != null) {
                            data.get(i).add(new CalendarContext(savedcontext.getDate_title(),savedcontext.getContext()));
                        }
                        num++;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, ArrayList<CalendarContext>> integerArrayListHashMap) {

            int month = Calendar.getInstance(Locale.KOREA).get(Calendar.MONTH)+1;

            AcademicAdapter academicAdapter = new AcademicAdapter(getActivity(),integerArrayListHashMap.get(mPageNumber+month-1));
            try {
                context.setAdapter(academicAdapter);
                context.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            }catch (NullPointerException e) {
                e.printStackTrace();
                GrayToast(getActivity(), "학사 일정 불러오기를 실패하였습니다.");
            }
            /*AcademicFragment academicFragment = fragmentWeakReference.get();
            AcademicAdapter academicAdapter = new AcademicAdapter(academicFragment.getActivity(),integerArrayListHashMap.get(mPageNumber+month-1));
            try {
                if(academicFragment == null || academicFragment.isRemoving()) return;

                context = (RecyclerView) academicFragment.getActivity().findViewById(R.id.calendar_context);
                context.setAdapter(academicAdapter);
                context.setLayoutManager(new LinearLayoutManager(academicFragment.getActivity(), RecyclerView.VERTICAL, false));
            }catch (NullPointerException e){
                e.printStackTrace();
                GrayToast(academicFragment.getActivity(),"학사 일정 불러오기를 실패하였습니다.");
            }*/
        }
    }


    public void GrayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setPadding(8,8,8,8);
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLeak = null;
        if(setdata!=null)
            setdata.cancel(true);
    }

    /*@Override
    public void onPause() {
        int count = context.getChildCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) context.getChildAt(i);
                int childSize = viewGroup.getChildCount();
                for (int j = 0; j < childSize; j++) {
                    if (viewGroup.getChildAt(j) instanceof TextView) {
                        ((TextView) viewGroup.getChildAt(j)).setText(null);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        calendarData = null;
        setdata.cancel(false);
        super.onPause();
    }*/
}
